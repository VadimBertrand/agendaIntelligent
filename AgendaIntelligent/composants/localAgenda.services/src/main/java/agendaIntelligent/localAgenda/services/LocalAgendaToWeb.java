package agendaIntelligent.localAgenda.services;

import net.fortuna.ical4j.model.*;

/**
 * Local Agenda Interface
 * Service provides by LocalAgenda to WebServer
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */

public interface LocalAgendaToWeb {

	/**
    	 * Concatenate two chaines
    	 * @return the message
    	 */
    public Calendar sendAgenda();
    

    public void createComponent(java.util.Calendar start, java.util.Calendar end, String eventName);

}
