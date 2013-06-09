package agendaIntelligent.googleAgenda.services;

import java.io.IOException;
import net.fortuna.ical4j.model.*; 

/**
 * Google Agenda Interface
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Service provided by GoogleAgenda to LocalAgenda
 */

public interface GoogleAgendaToLocal {

    /**
     * Returns a message like : "Hello $user_name"
     * @param name and password of the google account
     * @return the hello message
     */
    String accountUser();
    
    /**
     * Returns a calendar in ical format associated to a specified url
     * @param 
     * @return ical4j.calendar 
     */
    public net.fortuna.ical4j.model.Calendar getGoogleAgenda();
    
    
//    String httpGet(String urlStr) throws IOException;
}

