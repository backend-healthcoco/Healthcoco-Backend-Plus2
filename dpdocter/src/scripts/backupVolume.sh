#!/bin/bash

usage() { echo "Usage: $0 [-i <instanceID>] [-d <device>] [-z <availabilityZone>] [-t <retention_days>] [-b <backupType-log/mongodb/elastic...>] [-v <create volume true/false>] " 1>&2; exit 1; }

while getopts ":i:d:z:t:b:v:" o; do
    case "${o}" in
        i)
            instanceID=${OPTARG}
            ;;
        d)
            device=${OPTARG}
            ;;
	z)
	    availabilityZone=${OPTARG}
	    ;;
	t)
	    retention_days=${OPTARG}
	    ;;
	b)
            backupType=${OPTARG}
            ;;
        v)
            volume=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${instanceID}" ] || [ -z "${device}" ] || [ -z "${availabilityZone}" ] || [ -z "${retention_days}" ] || [ -z "${backupType}" ] || [ -z "${volume}" ]; then
    usage
fi


# Set Logging Options
logfile="/var/log/ebs-snapshot-`date +%m-%d-%Y`.log"
logfile_max_lines="5000"

# How many days do you wish to retain backups for? Default: 7 days
retention_date_in_seconds=$(date +%s --date "$retention_days days ago")

#Create log file
if [ ! -e "$logfile" ] ; then
    sudo touch "$logfile"
    sudo chmod 666 "$logfile"	
fi

# Check if logfile exists and is writable.
( [ -e "$logfile" ] ) && [ ! -w "$logfile" ] && echo "ERROR: Cannot write to $logfile. Check permissions or sudo access." && exit 1

tmplog=$(tail -n $logfile_max_lines $logfile 2>/dev/null) && echo "${tmplog}" > $logfile
exec > >(tee -a $logfile)
exec 2>&1

# Function: Log an event.
log() {
    echo "[$(date +"%Y-%m-%d"+"%T")]: $*"
}

#Get the VOlume for which snapshot is required
VOLUME_ID=(`aws ec2 describe-volumes --query 'Volumes[*].{ID:VolumeId,InstanceId:Attachments[0].InstanceId,Device:Attachments[0].Device}' --output text | grep $instanceID | grep $device  | awk '{print $2}'`) 

VOLUME_ID_RESULT=$?

if [ $VOLUME_ID_RESULT -eq 0 ]; then
  log "The volume id is :${VOLUME_ID}"
else
  log "Unable to generate volume_id"
  exit 1;
fi

description="$backupType-snapshot-`date +%x_%H:%M:%S`"

log "Name of Snapshot:$description"

log "Starting to create snapshot"
SNAPSHOT_ID=(`aws ec2 create-snapshot --volume-id $VOLUME_ID --description $description --output text | awk '{print $4}'`)

SNAPSHOT_ID_RESULT=$?

if [ $SNAPSHOT_ID_RESULT -eq 0 ]; then           
	log "Snapshot creation started for $SNAPSHOT_ID" 
else
	log "Issue Creating Snapshot"
	exit 1;
fi

SNAPSHOT_PROGRESS="Progress"
COUNTER=40


while [[ "$SNAPSHOT_PROGRESS" != "completed" && $COUNTER -ne 0 ]]
do
  SNAPSHOT_PROGRESS=(`aws ec2 describe-snapshots --snapshot-ids $SNAPSHOT_ID --query 'Snapshots[*].{State:State}' --output text`)
  SNAPSHOT_PROGRESS_RESULT=$?
  if [ $SNAPSHOT_PROGRESS_RESULT -eq 0 ]; then
        log "Snapshot creation in progress for $SNAPSHOT_ID and state is $SNAPSHOT_PROGRESS"
  else
        log "Issue Creating Snapshot"
        exit 1;
  fi
  COUNTER=$((COUNTER-1))
  sleep 15
done

if [ "$volume" = true ]; then
	log "Starting to create volume"

	CREATE_VOLUME_ID=(`aws ec2 create-volume --snapshot $SNAPSHOT_ID --availability-zone $availabilityZone`)
	CREATE_VOLUME_ID_RESULT=$?


	if [ $CREATE_VOLUME_ID_RESULT -eq 0 ]; then
        	log "Volume created successfully"
	else
        	log "Issue Creating Volume"
        	exit 1;
	fi
fi
exit 0;
