package agendaIntelligent.googleAgenda;

/**
 * Google Agenda Interface
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */

public interface GoogleAgenda {

    /**
     * Returns a message like : "Hello $user_name"
     * @param name and password of the google account
     * @return the hello message
     */
    String accountUser(String name, String password);
}
