/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.List;
import models.Person;
import models.PersonAndTimeSlot;
import models.TimeSlot;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author florian
 */
@With(DefaultInterceptors.class)
public class Debug extends Controller {

    public static void index() {
        List<PersonAndTimeSlot> pats = PersonAndTimeSlot.findAll();
        List<TimeSlot> ts = TimeSlot.findAll();
        List<Person> persons = Person.findAll();
        render(pats, ts, persons);
    }
}
