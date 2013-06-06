package agendaIntelligent.brainModule.services;

/**
 * Brain Module Interface
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Service provided by brainModule to localAgenda
 */

public interface BrainModuleToLocal {

    /**
     * Returns a message like : " le brain module "
     * @return the message
     */
    public String sendSmartMessage();
}
