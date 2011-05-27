/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.Person;
import models.PersonAndTimeSlot;
import models.TimeSlot;
import models.utils.TimeSlotListRange;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author florian
 */
@With(DefaultInterceptors.class)
public class Statistics extends Controller {

    public static void index() {
        boolean hasStats = PersonAndTimeSlot.count() > 0 ? true : false;
        TimeSlotListRange range = new TimeSlotListRange(TimeSlot.getCurrentWeek());
        long maxY = PersonAndTimeSlot.findMaxOnCallSessions(range.getStartDate(), range.getEndDate());
//        long rangeY = 0;
        List<Person> legendList = Person.find("order by id").fetch();
        String legend = "";
        for (Person person : legendList) {
            legend += person.firstName + "|";
        }
        String values = "";
        List<PersonAndTimeSlot> patsList = PersonAndTimeSlot.getPatsInTimeSlot(range.getStartDate(), range.getEndDate());
        // store person -> nb of on call sessions
        Map<Long, Integer> personByNbOnCall = new HashMap<Long, Integer>();
        for (PersonAndTimeSlot pats : patsList) {
            if(pats.status == PersonAndTimeSlot.TimeSlotStatus.ON_CALL){
                if (personByNbOnCall.containsKey(pats.person.id)){
                    personByNbOnCall.put(pats.person.id, personByNbOnCall.get(pats.person.id) + 1);
                } else {
                    personByNbOnCall.put(pats.person.id, 1);
                }
            }
        }
        // complete missing persons
        if (personByNbOnCall.size() != Person.count()){
            for (Person p : Person.<Person>findAll()) {
                if (! personByNbOnCall.containsKey(p.id)){
                    personByNbOnCall.put(p.id, 0);
                }
            }
        }
        for (Person person : legendList) {
            values += personByNbOnCall.get(person.id) + ",";
        }
        if (values.length() > 1){
            values = values.substring(0, values.length() - 1);
        }
//        Logger.debug("maxY %s", maxY);
//        Logger.debug("rangeY %s", rangeY);
        render(hasStats, legend, values, maxY);
    }
}
