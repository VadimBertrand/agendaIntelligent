package agendaIntelligent.webServer.impl;

import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;
import agendaIntelligent.brainModule.services.BrainModuleToWeb;
import agendaIntelligent.webServer.impl.RemoveWrite;
import agendaIntelligent.webServer.impl.ComponentsUtil;

import java.lang.*;
import java.util.*;
import java.io.*;

import org.apache.felix.ipojo.annotations.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.*;

import org.apache.commons.io.IOUtils;

import java.text.SimpleDateFormat;
import agendaIntelligent.webServer.impl.RemoveWrite;
import agendaIntelligent.webServer.impl.ConfigUsers;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@org.apache.felix.ipojo.annotations.Component(publicFactory=false)
@Provides
@Instantiate
public class WebServerImpl implements Runnable {

	/*	ATTRIBUTES	*/
	boolean active = false;
	Timer t;
	int delay = 10*1000;
	BundleContext context;
	// Agenda commun
	private net.fortuna.ical4j.model.Calendar calendarCommun;
	// Agendas Google
	private net.fortuna.ical4j.model.Calendar calendarGoogle1;
	private net.fortuna.ical4j.model.Calendar calendarGoogle2;
	@Requires
	private LocalAgendaToWeb localAgendaToWeb;
	@Requires
	private BrainModuleToWeb brainModuleToWeb;

	
	/*	METHODES		*/
	/*public int getDelay() {
		return this.delay;
	}*/	
	
	
	// Permet de recuperer le context du bundle
	public WebServerImpl(BundleContext bc) {
		this.context = bc;
	}

   
	public void run() {System.out.println("ENTREE DANS RUN");
		// Demarrage du serveur et activation de la servlet
		try {
			Activator serveurWeb = new Activator();
			serveurWeb.start(this.context);
		} catch(Exception e) {
			System.out.println("Exception Thread principal Run()");
		}
		if(active) {
			// Definition de la periode du timer
			try {
				t = new Timer();
				t.schedule(new MiseAJour(),0,delay);
			} catch(Exception e) {
				System.out.println("Exception Thread principal Timer");
			}
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

	
	
		
		
	// récupération du localAgenda 
	public void recupAgenda() {
		ArrayList<String[]> userList = null;
		try {
			userList = this.localAgendaToWeb.getUsersList();
			if (userList==null){
				return;
			}
			else if (userList.size()==1 && !(userList.get(0)[2].equals("")) ){
				this.calendarGoogle1 = this.localAgendaToWeb.sendAgenda("google1");
			}
			else if (userList.size()>=2) {
				if (!(userList.get(0)[2].equals(""))) {
					this.calendarGoogle1 = this.localAgendaToWeb.sendAgenda("google1");
				}
				if (!(userList.get(1)[2].equals(""))) {
					this.calendarGoogle2 = this.localAgendaToWeb.sendAgenda("google2");
				}
			}
			//this.calendarGoogle1 = this.localAgendaToWeb.sendAgenda("google1");
			//this.calendarGoogle2 = this.localAgendaToWeb.sendAgenda("google2");
			this.calendarCommun = this.localAgendaToWeb.sendAgenda("commun");
		
			/* ========== A COMMENTER POUR RECUPERER TOUS LES AGENDAS  ========== */
			/*this.calendarGoogle1 = null;
			this.calendarGoogle2 = null;
			this.calendarCommun = null;	*/
			
		} catch (Exception e) {
			e.printStackTrace();
	    		System.out.println("ERREUR de récupération de l'agenda");
		}
	}
	
	
	/**
	 * Classe definissant l'action executee par le timer
	 */
	class MiseAJour extends TimerTask {
		public void run() {
			WebServerImpl.this.recupAgenda();
			localAgendaToWeb.checkAlarm();
		}
	}
	 
	 

////////////////////////////////////////////////////////////////////
/////////////////////////// SERVLETS ///////////////////////////////
////////////////////////////////////////////////////////////////////

	 
	/**
	 * classe definissant la servlet d'affichage de l'agenda
	 */
	public class DisplayAgendaServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
			
			ArrayList<String[]> namesGoogle = WebServerImpl.this.localAgendaToWeb.getUsersList();
			
			Integer hourFilter = WebServerImpl.this.brainModuleToWeb.getHourFilter();
			long dateFilter = WebServerImpl.this.brainModuleToWeb.getDateFilter();
			
			String myString = PrintWrite.printWriteHtml(
				WebServerImpl.this.brainModuleToWeb.filterAgenda(WebServerImpl.this.calendarGoogle1, dateFilter), 
				WebServerImpl.this.brainModuleToWeb.filterAgenda(WebServerImpl.this.calendarGoogle2, dateFilter), 
				WebServerImpl.this.brainModuleToWeb.filterAgenda(WebServerImpl.this.calendarCommun, dateFilter),
				hourFilter, dateFilter, namesGoogle
			);

			resp.getWriter().write(myString); 
		}
	}
	
	/**
	 * classe definissant la servlet d'ajout de tâches à l'agenda commun
	 */
	public class InterfaceServlet extends HttpServlet {
	
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
			if (req.getParameter("uid") != null) {
				String uid = req.getParameter("uid");
				localAgendaToWeb.removeComponent(new net.fortuna.ical4j.model.property.Uid(uid));
			}	

			InputStream myStream = new FileInputStream("../WebContent/WEB-INF/create.html");
			String myString = IOUtils.toString(myStream);

			resp.getWriter().write(myString); 
		}

		protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
			
			// Recuperation des champs du formulaire :
			String eventName = getValeurChamp(request, "eventName");
			ArrayList<String> attendees = new ArrayList();

			// remplissage de la liste des attendees
			for (int i = 1; i <= 4; i++) {
				if (getValeurChamp(request, "attendee" + String.valueOf(i)) != null) {
					attendees.add(getValeurChamp(request, "attendee" + String.valueOf(i)));
				}
			}
			
			// verification de la présence de tout les attendees dans la liste des utilisateurs de l'appli
			Iterator<String> it = attendees.iterator();
			ArrayList<String[]> listeUsers = WebServerImpl.this.localAgendaToWeb.getUsersList();

			if (listeUsers != null) {

				while (it.hasNext()) {
					boolean match = false;
					String currentName = it.next();
					Iterator<String[]> it1 = listeUsers.iterator();

					while (it1.hasNext()) {
						if (it1.next()[0].equals(currentName)) {
							match = true;
						}
					}

					if (!match) {
						System.out.println("UN DES ATTENDEES N'EST PAS INSCRIT");

						String myString = "Erreur : one of the attendees is not registered in the application.";
						response.getWriter().write(myString); 
						return;
					}
				}

			} else {
				String myString = "Please Sign in to add tasks";
				response.getWriter().write(myString); 
				return;
			}
			

			CreateTask create = new CreateTask();
			java.util.Calendar tablCal[] = create.createCalendars(request);
			
			// Ajout de l'evenement à l'agenda local :
			localAgendaToWeb.createComponent(tablCal[0], tablCal[1], eventName, attendees, -1);

			// reaffichage du formulaire d'ajout de tache :
			doGet(request, response);
		 }
	}


	/**
	 * classe definissant la servlet de suppression/modification
	 * d'evenements dans l'agenda commun
	 */
	public class RemoveAgendaServlet extends HttpServlet {
	
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {

			RemoveWrite rem = new RemoveWrite();
			String myString = rem.fileToString(WebServerImpl.this.calendarCommun);
			
			resp.getWriter().write(myString); 
		}

		protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
			String uid = getValeurChamp(request, "eventUid");
			localAgendaToWeb.removeComponent(new net.fortuna.ical4j.model.property.Uid(uid));

			try {
		    	WebServerImpl.this.calendarCommun = WebServerImpl.this.localAgendaToWeb.sendAgenda("commun");
			} catch (Exception e) {
				System.out.println("Erreur de récupération de l'agenda après suppression de tâche");
			}

			doGet(request, response);	
		}
	}

	/*
	 * Servlet de config de l'appli
	 */
	public class ConfigServlet extends HttpServlet {
	
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
			
			ConfigUsers conf = new ConfigUsers();
			String myString = conf.fileToString(WebServerImpl.this.localAgendaToWeb.getUsersList());
			resp.getWriter().write(myString); 
		}

		protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {

			String newUser = getValeurChamp(req, "newUser");
			String newEmail = getValeurChamp(req, "newEmail");
			String newUrl = null;

			if (getValeurChamp(req, "newUrl") != null) {
				newUrl = getValeurChamp(req,"newUrl");
			} else {
				newUrl = "";
			}
				
			String infoUser[] = {newUser, newEmail, newUrl};

			WebServerImpl.this.localAgendaToWeb.addUser(infoUser);

			this.doGet(req, resp);
		}
	}


	/*
   	 * Methode utilitaire qui retourne null si un champ est vide, et son contenu
   	 * sinon.
   	 */
  	 private String getValeurChamp(HttpServletRequest request, String nomChamp) {
   		 String valeur = request.getParameter( nomChamp );
   	   	 if ( valeur == null || valeur.trim().length() == 0 ) {
   	   	  	return null;
   		 } else {
       		return valeur.trim();
       	 }
	}


	/**
	 * Enregistre la servlet
	 * Definie son chemin d'acces
	 */
	public class Activator implements BundleActivator {
	
  		public void start(BundleContext context) throws Exception {
    		ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
    		if (sRef != null) {
      			HttpService service = (HttpService) context.getService(sRef);
  				System.out.println("start servlet interface");
   				service.registerServlet("/create", new InterfaceServlet(), null, null);
  				System.out.println("start servlet affichage");
      			service.registerServlet("/print", new DisplayAgendaServlet(), null, null);
  				System.out.println("start servlet suppression");
      			service.registerServlet("/remove", new RemoveAgendaServlet(), null, null);
  				System.out.println("start servlet config");
      			service.registerServlet("/config", new ConfigServlet(), null, null);
			}
  		}
  		
  		public void stop(BundleContext context) throws Exception {
  		}
	}

}
