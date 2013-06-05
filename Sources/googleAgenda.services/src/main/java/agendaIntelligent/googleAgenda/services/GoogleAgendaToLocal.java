package agendaIntelligent.googleAgenda.services;

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
    public String accountUser();
}
