package agendaIntelligent.brainModule.services;
import net.fortuna.ical4j.model.Calendar;

import net.fortuna.ical4j.model.Calendar;

import java.util.*;

/**
 * Brain Module Interface
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Service provided by brainModule to localAgenda
 */

public interface BrainModuleToLocal {    
    
    /**
    * Send a mail to alert of the corresponding component start
    * the mail is sent to destUrl (if not null) and to the potential attendees of the event
    * @param void
    * @return void
    */ 
    public void checkAlarm(net.fortuna.ical4j.model.Calendar calendar, long delay, String destUrl);
    
    /**
    * check if there is no conflict between the common agenda and the event we want to add
    * @param net.fortuna.ical4j.model.Calendar calendarCommun, net.fortuna.ical4j.model.component.VEvent eventAdded
    */
    public void checkCommonConflict(net.fortuna.ical4j.model.Calendar calendarCommun,net.fortuna.ical4j.model.component.VEvent eventAdded, ArrayList<String[]> users);
    
    /**
    * check if there is no conflict between the google agendas and the event we want to add
    * @param net.fortuna.ical4j.model.Calendar calendarCommun, net.fortuna.ical4j.model.component.VEvent eventAdded
    */
    public void checkGoogleAgenda(net.fortuna.ical4j.model.Calendar calendarGoogle, net.fortuna.ical4j.model.component.VEvent eventAdded, String userMail);


    /**
    * Send mail to all the event begining by CMD
	* Get the delay until the next alarm sent
    * @param void
    * @return void
    */
    public void checkCMD(net.fortuna.ical4j.model.Calendar calendar, long delay);
    
}
