package models;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import models.PersonAndTimeSlot.TimeSlotStatus;
import play.data.validation.Required;

import play.db.jpa.Model;

/**
 * Represent a person in the system.
 * 
 * @author florian
 */
@Entity
public class Person extends Model {

    @Required
    public String firstName;
    @Required
    public String lastName;
//    @OrderBy("timeSlot.startDate")
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    public List<PersonAndTimeSlot> slots;
    public boolean isDeletable;


    public Person(String firstName, String lastName) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.save();
        PersonAndTimeSlot.createDefaultPats(this);
    }

    @Override
    public String toString() {
        return "Person{" + firstName + " " + lastName + " " + isDeletable + "}";
    }

    public Person onCall() {
//		isOnCall = true;
        return this;
    }

    public Person free() {
//		isOnCall = false;
        return this;
    }

    /**
     * Create the PersonAndTimeSlot object if necessary
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Person> getAllPersonInTimeSlot(Date startDate, Date endDate) {
        PersonAndTimeSlot.createMissingPats(startDate, endDate);

        return Person.find("select distinct p from Person p join p.slots pats "
                + "where pats.timeSlot.startDate >= ? and pats.timeSlot.endDate <= ?",
                startDate, endDate).fetch();
    }

    /**
     * Get all persons with the given status at the current time.
     * @param status
     * @return
     */
    public static List<Person> getAllPerson(TimeSlotStatus status) {
        List<Person> result = find("select pt.person from PersonAndTimeSlot pt where pt.status=? and ? between pt.timeSlot.startDate and pt.timeSlot.endDate",
                status, new Date(System.currentTimeMillis())).fetch();
        return result;
    }

    public String getName() {
        return firstName + " " + lastName;
    }
}
