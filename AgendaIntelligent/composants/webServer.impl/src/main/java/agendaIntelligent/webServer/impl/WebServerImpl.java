package agendaIntelligent.webServer.impl;

import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;

import java.lang.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;

import org.apache.felix.ipojo.annotations.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@org.apache.felix.ipojo.annotations.Component(publicFactory=false)
@Provides
@Instantiate
public class WebServerImpl implements Runnable {
	boolean active = false;
	String chaine = null;
	Timer t;
	int delay = 3000;
	BundleContext context;
	
	// permet de recuperer le context du bundle
	public WebServerImpl(BundleContext bc) {
		this.context = bc;
	}
	
	@Requires
	private LocalAgendaToWeb localAgendaToWeb;
    
	public void run() {
		// demarrage du serveur et activation de la servlet
		Activator serveurWeb = new Activator();
		try {
			serveurWeb.start(this.context);
		} catch(Exception e) {
			System.out.println("exception caught");
		}
		if(active) {
			// definition de la periode du timer
			t = new Timer();
			t.schedule(new MiseAJour(),0,3000);
		}
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
	
	public void recupChaine() {
		this.chaine = "Le serveur web a communique avec " + localAgendaToWeb.concatChaines();
		System.out.println(this.chaine);
    	}
	
	
	/**
	 * classe definissant l'action executee par le timer
	 */
	class MiseAJour extends TimerTask {
		public void run() {
			WebServerImpl.this.recupChaine();
		}
	}
	
	
	/**
	 * classe definissant la servlet
	 */
	public class PrintChaine extends HttpServlet {
	
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException 
		{
			resp.getWriter().write(WebServerImpl.this.chaine);      
		} 
	}
	
	/**
	 * Enregistre la servlet
	 * Definie son chemin d'acces
	 */
	public class Activator implements BundleActivator {
	
  		public void start(BundleContext context) throws Exception {
  			System.out.println("start");
    			ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
    			if (sRef != null) {
      			HttpService service = (HttpService) context.getService(sRef);
      			service.registerServlet("/chaine", new PrintChaine(), null, null);
    			}
  		}
  		
  		public void stop(BundleContext context) throws Exception {
  		}
	}

}
