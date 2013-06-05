package agendaIntelligent.webServer.impl;

import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;

import java.lang.*;

import org.apache.felix.ipojo.annotations.*;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@Component(publicFactory=false)
@Provides
@Instantiate
public class WebServerImpl implements Runnable {
	boolean active = false;
	
	@Requires
	private LocalAgendaToWeb localAgendaToWeb;
    
	public void run() {
		if(active) {
			this.recupChaine();
		}
	}
	
	public void recupChaine() {
		System.out.println("Le serveur web a communique avec " + localAgendaToWeb.concatChaines());
    	}
    
	@Validate
	public void starting() {
        	Thread thread = new Thread(this);
		active = true;
        	thread.start();
	}
	
	@Invalidate
	public void stopping() {
		active = false;
	}
}
