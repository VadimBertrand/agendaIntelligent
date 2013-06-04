package localAgenda.impl;

import googleAgenda.GoogleAgenda;
import localAgenda.LocalAgenda;

import java.lang.*;

import org.apache.felix.ipojo.annotations.*;

@Component
@Provides
@Instantiate
public class LocalAgendaImpl implements LocalAgenda, Runnable {
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