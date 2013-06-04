package agendaIntelligent.googleAgenda.service1;

import agendaIntelligent.googleAgenda.GoogleAgenda;

import org.apache.felix.ipojo.annotations.*;

/**
 * Component implementing the Google Agenda service
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@Component
@Provides
@Instantiate
public class GoogleAgendaService1 implements GoogleAgenda {

    /**
     * Returns a message like : "Hello $user_name"
     * @param name and password of the google account
     * @return the hello message
     * @see googleAgenda.GoogleAgenda#accountUser(java.lang.String, java.lang.String)
     */
    public String accountUser(String name, String password) {
        return "Hello " + name;
    }
}
