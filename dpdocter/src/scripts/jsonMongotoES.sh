#!/bin/bash

usage() { echo "Usage: $0 [-i <index>] [-t <type>] [-f <InputFileName>]" 1>&2; exit 1; }

while getopts ":i:t:f:" o; do
    case "${o}" in
        i)
            index=${OPTARG}
            ;;
        t)
            type=${OPTARG}
            ;;
        f)  
            inputFile=${OPTARG}
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${index}" ] || [ -z "${type}" ] || [ -z "${inputFile}" ]; then
    usage
fi

filename=$inputFile 
while read -r line
do
    echo $line | sed 's/\$//g'  >  tempFile
    keys=( $(jq 'keys[]' tempFile) )
    idValue=($(jq  '._id.oid' tempFile))
    firstLine="{\"index\":{\"_index\":\"${index}\",\"_type\":\"${type}\",\"_id\":$idValue}}"
    secondLine="{"	
    for key in "${keys[@]}"
    do
    	if [[ $key = "\"updatedTime\"" ]]
	then
            updateTimeValue=($(jq  '.updatedTime.date' tempFile))
            secondLine+=$key:$updateTimeValue
    	elif [[ $key != "\"_id\"" ]]
	then
            jqKey=$(echo $key | sed 's\"\\g')
            keyValue=$(jq ".$jqKey" tempFile)
	    secondLine+=$key:$keyValue,	
     	fi
    done
    secondLine+=}
    echo $firstLine
    echo $secondLine
done < "$filename"
