package agendaIntelligent.brainModule.impl;

import agendaIntelligent.brainModule.services.BrainModuleToLocal;

import org.apache.felix.ipojo.annotations.*;

/**
 * Component implementing the Brain Module service
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Implementation of the service provided by brain module to local agenda
 */

@Component
@Provides
@Instantiate
public class BrainModuleImpl implements BrainModuleToLocal {

    /**
     * Returns a message like : " le brain module "
     * @return the message
     * @see brainModule.BrainModule.services#sendSmartMessage()
     */
    public String sendSmartMessage() {
        return " le brain module ";
    }
}
