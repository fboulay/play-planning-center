package bootstrap;

import models.Person;
import models.PersonAndTimeSlot;
import models.TimeSlot;
import org.joda.time.DateMidnight;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() throws Exception {
        if (Person.count() == 0) {
            Logger.info("Loading static database data...");
            Fixtures.load("data.yml");
            Logger.info("Loading successful");
            Logger.info("Loading static programmed data...");
            initAllSlots();
//            initTimeSlots();
            Logger.info("Loading successful");
        }
    }

    private void initAllSlots() {
        // current week
        DateMidnight todayDateMidnight = new DateMidnight(System.currentTimeMillis());
        DateMidnight curWeekMonday = todayDateMidnight.withDayOfWeek(1);
        DateMidnight curWeekTueday = todayDateMidnight.withDayOfWeek(2);
        DateMidnight curWeekWenesday = todayDateMidnight.withDayOfWeek(3);
        DateMidnight curWeekThursday = todayDateMidnight.withDayOfWeek(4);
        DateMidnight curWeekFriday = todayDateMidnight.withDayOfWeek(5);
        DateMidnight curWeekSaturday = todayDateMidnight.withDayOfWeek(6);
        DateMidnight curWeekSunday = todayDateMidnight.withDayOfWeek(7);
        DateMidnight nextWeekMonday = curWeekMonday.plusWeeks(1);

        TimeSlot monday = new TimeSlot(curWeekMonday.toDate(), curWeekTueday.toDate()).save();
        TimeSlot tuesday = new TimeSlot(curWeekTueday.toDate(), curWeekWenesday.toDate()).save();
        TimeSlot wenesday = new TimeSlot(curWeekWenesday.toDate(), curWeekThursday.toDate()).save();
        TimeSlot thursday = new TimeSlot(curWeekThursday.toDate(), curWeekFriday.toDate()).save();
        TimeSlot friday = new TimeSlot(curWeekFriday.toDate(), curWeekSaturday.toDate()).save();
        TimeSlot saturday = new TimeSlot(curWeekSaturday.toDate(), curWeekSunday.toDate()).save();
        TimeSlot sunday = new TimeSlot(curWeekSunday.toDate(), nextWeekMonday.toDate()).save();

        // set Florian schedule
        new PersonAndTimeSlot(getFlorian(), saturday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getFlorian(), sunday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getFlorian(), monday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getFlorian(), tuesday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();
        new PersonAndTimeSlot(getFlorian(), wenesday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getFlorian(), thursday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getFlorian(), friday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();

        // set Antonio schedule
        new PersonAndTimeSlot(getAntonio(), saturday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();
        new PersonAndTimeSlot(getAntonio(), sunday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();
        new PersonAndTimeSlot(getAntonio(), monday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();
        new PersonAndTimeSlot(getAntonio(), tuesday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getAntonio(), wenesday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();
        new PersonAndTimeSlot(getAntonio(), thursday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getAntonio(), friday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();

        // set Antoine schedule
        new PersonAndTimeSlot(getAntoine(), saturday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getAntoine(), sunday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getAntoine(), monday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getAntoine(), tuesday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();
        new PersonAndTimeSlot(getAntoine(), wenesday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getAntoine(), thursday, PersonAndTimeSlot.TimeSlotStatus.AVAILABLE).save();
        new PersonAndTimeSlot(getAntoine(), friday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();

        // set Laurent schedule
        new PersonAndTimeSlot(getLaurent(), saturday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getLaurent(), sunday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getLaurent(), monday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getLaurent(), tuesday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getLaurent(), wenesday, PersonAndTimeSlot.TimeSlotStatus.ON_CALL).save();
        new PersonAndTimeSlot(getLaurent(), thursday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
        new PersonAndTimeSlot(getLaurent(), friday, PersonAndTimeSlot.TimeSlotStatus.UNAVAILABLE).save();
    }

    private void initTimeSlots() {
        // current week
        DateMidnight todayDateMidnight = new DateMidnight(System.currentTimeMillis());
        DateMidnight curWeekMonday = todayDateMidnight.withDayOfWeek(1);
        DateMidnight curWeekTueday = todayDateMidnight.withDayOfWeek(2);
        DateMidnight curWeekWenesday = todayDateMidnight.withDayOfWeek(3);
        DateMidnight curWeekThursday = todayDateMidnight.withDayOfWeek(4);
        DateMidnight curWeekFriday = todayDateMidnight.withDayOfWeek(5);
        DateMidnight curWeekSaturday = todayDateMidnight.withDayOfWeek(6);
        DateMidnight curWeekSunday = todayDateMidnight.withDayOfWeek(7);
        DateMidnight nextWeekMonday = curWeekMonday.plusWeeks(1);

        new TimeSlot(curWeekMonday.toDate(), curWeekTueday.toDate()).save();
        new TimeSlot(curWeekTueday.toDate(), curWeekWenesday.toDate()).save();
        new TimeSlot(curWeekWenesday.toDate(), curWeekThursday.toDate()).save();
        new TimeSlot(curWeekThursday.toDate(), curWeekFriday.toDate()).save();
        new TimeSlot(curWeekFriday.toDate(), curWeekSaturday.toDate()).save();
        new TimeSlot(curWeekSaturday.toDate(), curWeekSunday.toDate()).save();
        new TimeSlot(curWeekSunday.toDate(), nextWeekMonday.toDate()).save();
    }

    private Person getFlorian() {
        return Person.find("byFirstName", "Florian").first();
    }

    private Person getAntoine() {
        return Person.find("byFirstName", "Antoine").first();
    }

    private Person getAntonio() {
        return Person.find("byFirstName", "Antonio").first();
    }

    private Person getLaurent() {
        return Person.find("byFirstName", "Laurent").first();
    }
}
