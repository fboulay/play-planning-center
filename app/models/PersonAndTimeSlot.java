package models;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Model;

/**
 * Association of a time slot with a person and a status.
 * 
 * @author florian
 */
@Entity
public class PersonAndTimeSlot extends Model implements Comparable<PersonAndTimeSlot> {

    public static enum TimeSlotStatus {

        AVAILABLE("Available", "green"),
        UNAVAILABLE("Unavailable", "grey"),
        ON_CALL("On call", "red");
        private String value;
        private String htmlcolor;

        private TimeSlotStatus(String value, String htmlColor) {
            this.value = value;
            this.htmlcolor = htmlColor;
        }

        public static TimeSlotStatus findNextStatus(TimeSlotStatus timeSlotStatus) {
            TimeSlotStatus[] values = TimeSlotStatus.values();
            int index = -1;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == timeSlotStatus) {
                    index = i;
                    break;
                }
            }

            return values[(index + 1) % values.length];
        }

        public String getValue() {
            return value;
        }

        public String getHtmlColor() {
            return htmlcolor;
        }
    }
    @ManyToOne
    public Person person;
    @ManyToOne(cascade = CascadeType.PERSIST)
    public TimeSlot timeSlot;
    public TimeSlotStatus status;

    public static List<PersonAndTimeSlot> getPatsInTimeSlot(Date startDate, Date endDate) {
        createMissingPats(startDate, endDate);
        return PersonAndTimeSlot.find("select pats from PersonAndTimeSlot pats"
                + " where pats.timeSlot.startDate >= ? and pats.timeSlot.endDate <= ? order by person, startDate",
                startDate, endDate).fetch();
    }

    public static void createDefaultPats(Person person) {
        List<TimeSlot> allSlots = TimeSlot.findAll();
        for (TimeSlot ts : allSlots) {
            new PersonAndTimeSlot(person, ts, TimeSlotStatus.AVAILABLE).save();
        }
    }

    public static long findMaxOnCallSessions(Date startDate, Date endDate) {
        Query allOnCallSession = JPA.em().createQuery("select pats.person.id, count(pats) from PersonAndTimeSlot pats where pats.status=?1 group by pats.person order by pats.person.id");
        allOnCallSession.setParameter(1, TimeSlotStatus.ON_CALL);
        List<Object[]> results = allOnCallSession.getResultList();
        long max = 0;
        for (Object[] result : results) {
            if (max < (Long) result[1]) {
                max = (Long) result[1];
            }
        }
        return max;
    }

    public static void nextStatus(long id) {
        PersonAndTimeSlot pats = PersonAndTimeSlot.findById(id);
        pats.status = TimeSlotStatus.findNextStatus(pats.status);
        pats.save();
    }

    public static long timeToNextStatus() {
        DateTime now = new DateTime();
        List<PersonAndTimeSlot> listToday = PersonAndTimeSlot.find("select pats from PersonAndTimeSlot pats where pats.timeSlot.startDate <= ? and pats.timeSlot.endDate >=? order by pats.id", now.toDate(), now.toDate()).fetch();

        // ten days max
        for (int i = 1; i < 10; i++) {

            DateTime dayToCompare = now.plusDays(i);
            List<PersonAndTimeSlot> listDayToCompare = PersonAndTimeSlot.find("select pats from PersonAndTimeSlot pats where pats.timeSlot.startDate <= ? and pats.timeSlot.endDate >=? order by pats.id", dayToCompare.toDate(), dayToCompare.toDate()).fetch();
            for (int j = 0; j < listToday.size(); j++) {
                if (listToday.get(j).status != listDayToCompare.get(j).status){
                    return (new DateMidnight().plusDays(i).toDate().getTime() - new Date().getTime()) / 1000;
                }
            }
        }

        // default dummy 100 days
        return  (new DateMidnight().plusDays(100).toDate().getTime() - new Date().getTime()) / 1000;
    }

    static void createMissingPats(Date startDate, Date endDate) {
        long nbSlots = TimeSlot.count("select count(ts) from TimeSlot ts where ts.startDate >= ? and ts.endDate <= ?",
                startDate, endDate);
        long nbPerson = Person.count();
        long nbPats = PersonAndTimeSlot.count("select count(distinct pats) from PersonAndTimeSlot pats "
                + "where pats.timeSlot.startDate >= ? and pats.timeSlot.endDate <= ?", startDate, endDate);

        // if there is missing pats, then we create them
        if (nbSlots * nbPerson != nbPats) {
            List<Person> listPerson = Person.findAll();
            List<TimeSlot> listTs = TimeSlot.find("select ts from TimeSlot ts where ts.startDate >= ? and ts.endDate <= ?",
                    startDate, endDate).fetch();
            for (Person person : listPerson) {
                for (TimeSlot ts : listTs) {
                    long count = PersonAndTimeSlot.count("select count(distinct pats) from PersonAndTimeSlot pats "
                            + "where pats.timeSlot = ? and pats.person = ?",
                            ts, person);
                    if (count == 0) {
                        new PersonAndTimeSlot(person, ts, TimeSlotStatus.AVAILABLE).save();
                    }
                }
            }
        }

        nbPats = PersonAndTimeSlot.count("select count(pats) from PersonAndTimeSlot pats "
                + "where pats.timeSlot.startDate >= ? and pats.timeSlot.endDate <= ?", startDate, endDate);

        if (nbSlots * nbPerson != nbPats) {
            throw new RuntimeException("It should have " + nbSlots * nbPerson + " pats and there is " + nbPats);
        }
    }

    public PersonAndTimeSlot(Person person, TimeSlot timeSlot, TimeSlotStatus status) {
        super();
        this.person = person;
        this.timeSlot = timeSlot;
        this.status = status;
    }

    public int compareTo(PersonAndTimeSlot o) {
        return id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "PersonAndTimeSlot{" + "person={" + person.firstName + " " + person.lastName + "}timeSlot=" + timeSlot + "status=" + status + '}';
    }
}
