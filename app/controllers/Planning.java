/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllers.ajax.NewStatus;
import java.util.List;
import models.Person;
import models.PersonAndTimeSlot;
import models.TimeSlot;
import models.utils.TimeSlotListRange;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author florian
 */
@With(DefaultInterceptors.class)
public class Planning extends Controller {

    public static void index() {
        List<TimeSlot> curWeek = TimeSlot.getCurrentWeek();
        TimeSlotListRange range = new TimeSlotListRange(curWeek);
        renderPlanning(range, 0);
    }

    public static void deletePerson(long id) {
        Person person = Person.<Person>findById(id);
        if (!person.isDeletable) {
            error(401, "No right to delete " + person.getName());
        }
        person.delete();
        index();
    }

    public static void createPerson(@Valid Person person) {
        if (validation.hasErrors()) {
            flash.put("createError", true);
            index();
        }
        person.isDeletable = true;
        person.save();
        index();
    }

    public static void createPersonAjax(@Valid Person person) {
        if (validation.hasErrors()) {
            badRequest();
        }

        person.isDeletable = true;
        person.save();
        String hyper = "bob sponge";
        render("Planning/line.html", hyper);

    }

    public static void changeRelativeWeek(@Required int weekNb) {
        if (validation.hasErrors()) {
            badRequest();
        }
        if (weekNb > 30 || weekNb < -30) {
            forbidden("This week cannot be shown");

        }
        List<TimeSlot> curWeek = TimeSlot.getCurrentWeek();
        TimeSlotListRange range = new TimeSlotListRange(curWeek);
        range.addWeek(weekNb);
        renderPlanning(range, weekNb);
    }

    public static void nextStatus(@Required long id) {
        if (validation.hasErrors()) {
            badRequest();
        }
        PersonAndTimeSlot.nextStatus(id);
        index();
    }

    public static void nextStatusAjax(@Required long id) {
        if (validation.hasErrors()) {
            badRequest();
        }

        NewStatus json = new NewStatus();
        PersonAndTimeSlot pats = PersonAndTimeSlot.<PersonAndTimeSlot>findById(id);
        PersonAndTimeSlot.nextStatus(id);
        json.oldStatus = pats.status.getValue();
        json.newStatus = PersonAndTimeSlot.TimeSlotStatus.findNextStatus(pats.status).getValue();
        json.color = pats.status.getHtmlColor();

        renderJSON(json);
    }

    private static void renderPlanning(TimeSlotListRange range, int weekNb) {
        //List<Person> personsInWeek = Person.getAllPersonInTimeSlot(range.getStartDate(), range.getEndDate());
        List<TimeSlot> curWeek = TimeSlot.findTimeSlotsForRange(range);
        List<PersonAndTimeSlot> patsInWeek = PersonAndTimeSlot.getPatsInTimeSlot(range.getStartDate(), range.getEndDate());
        render("Planning/index.html", curWeek, patsInWeek, weekNb);
    }
}
