package agendaIntelligent.googleAgenda.impl;

import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;

import org.apache.felix.ipojo.annotations.*;

/**
 * Component implementing the Google Agenda service
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Implementation of the services provided by GoogleAgenda to LocalAgenda
 */
@Component
@Provides
@Instantiate
public class GoogleAgendaImpl implements GoogleAgendaToLocal {

    /**
     * Returns a message like : "GoogleAgenda"
     * @return the hello message
     * @see googleAgenda.services.GoogleAgendaToLocal#accountUser()
     */
    public String accountUser() {
        return "GoogleAgenda";
    }
}
