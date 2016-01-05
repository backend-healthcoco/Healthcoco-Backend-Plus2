package common.util.web;

import java.util.ArrayList;
import java.util.List;

import com.dpdocter.beans.Slot;

public class DateAndTimeUtility {

	public static List<Slot> sliceTime(int begin, int end, int interval) {
	  List<Slot> slots = null;
  	  for (int time = begin*60; time <= end*60; time += interval) {
  		  if(slots == null)slots = new ArrayList<Slot>();
  		  Slot slot = new Slot();
  		  slot.setTime(String.format("%02d:%02d", time / 60, time % 60));
  		  slots.add(slot);
  	  }
  	  return slots;
  	}
}
