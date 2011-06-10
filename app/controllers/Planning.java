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
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;

/**
 * @author florian
 */
@With(DefaultInterceptors.class)
public class Planning extends Controller {

    public static void index() {
        renderPlanning(0);
    }

    public static void deletePerson(long id, int weekNb) {
        Person person = Person.<Person>findById(id);
        if (!person.isDeletable) {
            error(401, "No right to delete " + person.getName());
        }
        person.delete();
        renderPlanning(weekNb);
    }

    public static void createPerson(@Valid Person person, int weekNb) {
        if (validation.hasErrors()) {
            flash.put("createError", true);
            index();
        }
        person.isDeletable = true;
        person.save();
        renderPlanning(weekNb);
    }

    public static void createPersonAjax(@Valid Person person) {
        if (validation.hasErrors()) {
            badRequest();
        }

        person.isDeletable = true;
        person.save();
        render("Planning/line.html");

    }

    public static void changeRelativeWeek(@Required int weekNb) {
        if (validation.hasErrors()) {
            badRequest();
        }
        if (weekNb > 30 || weekNb < -30) {
            forbidden("This week cannot be shown");
        }
        renderPlanning(weekNb);
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

    private static void renderPlanning(int weekNb) {
        //List<Person> personsInWeek = Person.getAllPersonInTimeSlot(range.getStartDate(), range.getEndDate());
        TimeSlotListRange range = new TimeSlotListRange(TimeSlot.getCurrentWeek());
        range.addWeek(weekNb);
        List<TimeSlot> curWeek = TimeSlot.findTimeSlotsForRange(range);
        List<PersonAndTimeSlot> patsInWeek = PersonAndTimeSlot.getPatsInTimeSlot(range.getStartDate(), range.getEndDate());
        render("Planning/index.html", curWeek, patsInWeek, weekNb);
    }
}
