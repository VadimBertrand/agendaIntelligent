package agendaIntelligent.localAgenda.impl;

import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;
import agendaIntelligent.brainModule.services.BrainModuleToLocal;
import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;

import org.apache.felix.ipojo.annotations.*;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@Component(publicFactory=false)
@Provides
@Instantiate
public class LocalAgendaImpl implements LocalAgendaToWeb {
	
	@Requires
	private GoogleAgendaToLocal googleAgendaToLocal;
	@Requires
	private BrainModuleToLocal brainModuleToLocal;
	
	/**
     	 * Returns concatenate chaines
     	 * @return the message
     	 * @see localAgenda.services.LocalAgendaToWeb#concatChaines()
     	 */
	public String concatChaines() {
		return googleAgendaToLocal.accountUser() + " et " + brainModuleToLocal.sendSmartMessage() 
			+ " via le Local Agenda.";
    	}
}
