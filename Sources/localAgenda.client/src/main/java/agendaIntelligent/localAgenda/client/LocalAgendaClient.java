package agendaIntelligent.localAgenda.client;

import agendaIntelligent.googleAgenda.GoogleAgenda;
import agendaIntelligent.localAgenda.LocalAgenda;

import java.lang.*;

import org.apache.felix.ipojo.annotations.*;

/**
 * Component implementing the Local Agenda client
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@Component
@Provides
@Instantiate
public class LocalAgendaClient implements LocalAgenda, Runnable {
	String login = "Thib";
	String mdp = "Dup's";
	boolean active = false;
	
	@Requires
	private GoogleAgenda googleAgenda;
    
	public void run() {
		if(active) {
			this.getLogin();
		}
	}
	
	public void getLogin() {
		String surprise = googleAgenda.accountUser(login, mdp);
		System.out.println(surprise);
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
