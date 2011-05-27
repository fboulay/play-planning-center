package models.utils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import models.TimeSlot;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Manage the max and the min of a list of TimeSlot
 * 
 * @author florian
 */
public class TimeSlotListRange {

    /**
     * Default range is one day
     */
    public static final long DEFAULT_RANGE = TimeUnit.DAYS.toMillis(1);

    private Date startDate;
    private Date endDate;

    public TimeSlotListRange(List<TimeSlot> tsList){
        for (TimeSlot ts : tsList) {
            if (startDate == null){
                startDate = ts.startDate;
            }
            if (endDate == null){
                endDate = ts.endDate;
            }

            if (startDate.after(ts.startDate)){
                startDate = ts.startDate;
            }
            if (endDate.before(ts.endDate)){
                endDate = ts.endDate;
            }
        }
        
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public int getWeekNumber(){
        LocalDate localDate = new LocalDate(startDate.getTime());
        return localDate.getWeekOfWeekyear();
    }

    public void addWeek(int weekNb) {
        DateMidnight start = new DateMidnight(startDate.getTime());
        DateMidnight end = new DateMidnight(endDate.getTime());
        startDate = start.plusWeeks(weekNb).toDate();
        endDate = end.plusWeeks(weekNb).toDate();
    }

    @Override
    public String toString() {
        return "TimeSlotListRange{" + "startDate=" + startDate + "endDate=" + endDate + '}';
    }

    
}
