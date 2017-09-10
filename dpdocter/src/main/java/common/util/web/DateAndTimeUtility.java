package common.util.web;

import java.util.ArrayList;
import java.util.List;

import com.dpdocter.beans.Slot;

public class DateAndTimeUtility {

    public static List<Slot> sliceTime(int begin, int end, int interval) {
	List<Slot> slots = null;
	for (int time = begin; time < end; time += interval) {
	    if (slots == null)
		slots = new ArrayList<Slot>();
	    Slot slot = new Slot();
//	    slot.setIsAvailable(isAvailable);
	    slot.setMinutesOfDay(time);
	    slot.setTime(String.format("%02d:%02d", time / 60, time % 60));
	    slots.add(slot);
	}
	return slots;
    }
}
