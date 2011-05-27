/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;
import models.utils.TimeSlotListRange;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * Represent a time slot, tipically a day
 * 
 * @author florian
 */
@Entity
public class TimeSlot extends Model {

    @Required
    public Date startDate;
    @Required
    public Date endDate;

    /**
     * Return a list with a size of 7.
     * @return
     */
    public static List<TimeSlot> getCurrentWeek() {
        DateMidnight todayDateMidnight = new DateMidnight(System.currentTimeMillis());
        DateMidnight curWeekMonday = todayDateMidnight.withDayOfWeek(1);
        DateMidnight nextWeekMonday = curWeekMonday.plusWeeks(1);

        return findTimeSlotsForRange(curWeekMonday.toDate(), nextWeekMonday.toDate());
    }

    public static List<TimeSlot> findTimeSlotsForRange(TimeSlotListRange range) {
        return findTimeSlotsForRange(range.getStartDate(), range.getEndDate());
    }

    public static List<TimeSlot> findTimeSlotsForRange(Date startRange, Date endRange) {
        List<TimeSlot> tsList = find("select ts from TimeSlot ts where ts.startDate >= ? and endDate <= ? order by startDate",
                startRange, endRange).fetch();

        if (tsList.size() != 7) {
            DateMidnight previousDate = new DateMidnight(startRange);
            DateMidnight nextDate = previousDate.plusDays(1);

            while (nextDate.isBefore(new DateTime(endRange).plusMinutes(1))) {
                Long count = count("select count(ts) from TimeSlot ts where ts.startDate >= ? and endDate <= ?",
                        previousDate.toDate(), nextDate.toDate());

                if (count == 0) {
                    new TimeSlot(previousDate.toDate(), nextDate.toDate()).save();
                }

                previousDate = nextDate;
                nextDate = nextDate.plusDays(1);
            }
        }

        tsList = find("select ts from TimeSlot ts where ts.startDate >= ? and endDate <= ? order by startDate",
                startRange, endRange).fetch();

        if (tsList.size() != 7) {
            throw new RuntimeException("Impossible to create the time slots between " + startRange + " and " + endRange);
        }

        return tsList;
    }

    public TimeSlot(Date startDate, Date endDate) {
        super();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "TimeSlot{" + "startDate=" + startDate + "endDate=" + endDate + '}';
    }


}
