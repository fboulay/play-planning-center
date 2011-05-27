package controllers;

import java.util.List;

import models.Person;
import models.PersonAndTimeSlot;
import play.mvc.Controller;
import play.mvc.With;

@With(DefaultInterceptors.class)
public class Home extends Controller {

    public static void index() {
        List<Person> freePersons = Person.getAllPerson(PersonAndTimeSlot.TimeSlotStatus.AVAILABLE);
        List<Person> onCallPersons = Person.getAllPerson(PersonAndTimeSlot.TimeSlotStatus.ON_CALL);
        render(freePersons, onCallPersons);
    }

    public static void deletePerson(long id) {
        Person.<Person>findById(id).delete();
        index();
    }

    public static void onCallPerson(long id) {
        Person.<Person>findById(id).onCall().save();
        index();
    }

    public static void freePerson(long id) {
        Person.<Person>findById(id).free().save();
        index();
    }
}
