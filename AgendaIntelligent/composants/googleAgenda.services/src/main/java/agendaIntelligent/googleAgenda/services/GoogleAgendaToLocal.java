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
     * Returns a calendar in ical format associated to a specified url
     * @param String url of the googleAgenda we want to get
     * @return ical4j.calendar 
     */
    public net.fortuna.ical4j.model.Calendar getGoogleAgenda(String name);
    
    public void setUrlAgenda(String nameUser, String url);
    
    
}

