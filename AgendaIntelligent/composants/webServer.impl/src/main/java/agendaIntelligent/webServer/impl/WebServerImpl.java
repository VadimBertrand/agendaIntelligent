package agendaIntelligent.webServer.impl;

import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;
import agendaIntelligent.brainModule.services.BrainModuleToWeb;
import agendaIntelligent.webServer.impl.RemoveWrite;
import agendaIntelligent.webServer.impl.ComponentsUtil;
import agendaIntelligent.webServer.impl.ConfigUsers;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

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
	
	/**
	 * Return the bundle context
	 */
	public WebServerImpl(BundleContext bc) {
		this.context = bc;
	}

	/**
	 * Run 
	 */   
	public void run() {
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
				e.printStackTrace();
				System.out.println("Exception Thread principal Timer");
			}
		}
	}
	
	/**
	 * If start
	 */
	@Validate
	public void starting() {
        	Thread thread = new Thread(this);
		active = true;
        	thread.start();
	}
	
	/**
	 * If stop
	 */
	@Invalidate
	public void stopping() {
		active = false;
	}

	/**
	 * Set all the calendar
	 */
	public void recupAgenda() {
		ArrayList<String[]> userList = null;
		try {
			userList = this.localAgendaToWeb.getUsersList();
			if (userList==null){
				return;
			} else if (userList.size()==1 && !(userList.get(0)[2].equals("")) ){
				this.calendarGoogle1 = this.localAgendaToWeb.sendAgenda("google1");
			} else if (userList.size()>=2) {
				if (!(userList.get(0)[2].equals(""))) {
					this.calendarGoogle1 = this.localAgendaToWeb.sendAgenda("google1");
				}
				if (!(userList.get(1)[2].equals(""))) {
					this.calendarGoogle2 = this.localAgendaToWeb.sendAgenda("google2");
				}
			}
			this.calendarCommun = this.localAgendaToWeb.sendAgenda("commun");
		} catch (Exception e) {
			e.printStackTrace();
	    		System.out.println("ERREUR de récupération de l'agenda");
		}
	}
	
	
	/**
	 * Class for the timer
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
	 * The print servlet
	 */
	public class DisplayAgendaServlet extends HttpServlet {
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
			WebServerImpl.this.recupAgenda();			

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
	 * The add event servlet
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
			
			// Get the form values
			String eventName = getValeurChamp(request, "eventName");
			ArrayList<String> attendees = new ArrayList();

			// Fill the attendees list
			for (int i = 1; i <= 4; i++) {
				if (getValeurChamp(request, "attendee" + String.valueOf(i)) != null) {
					attendees.add(getValeurChamp(request, "attendee" + String.valueOf(i)));
				}
			}
			
			// All the attendees are in the app ?
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
			long durAlarme = create.alarmToMs(request);
			
			// Add the event to the common agenda
			localAgendaToWeb.createComponent(tablCal[0], tablCal[1], eventName, attendees, durAlarme);

			doGet(request, response);
		 }
	}


	/**
	 * add / remove the event servlet
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
				e.printStackTrace();
				System.out.println("Erreur de récupération de l'agenda après suppression de tâche");
			}

			doGet(request, response);	
		}
	}

	/*
	 * app config servlet
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
   	 * Return the content of a champ
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
	 * Save and register servlets
	 */
	public class Activator implements BundleActivator {
	
  		public void start(BundleContext context) throws Exception {
    		ServiceReference sRef = context.getServiceReference(HttpService.class.getName());
    		if (sRef != null) {
      			HttpService service = (HttpService) context.getService(sRef);
   				service.registerServlet("/create", new InterfaceServlet(), null, null);
      			service.registerServlet("/print", new DisplayAgendaServlet(), null, null);
      			service.registerServlet("/remove", new RemoveAgendaServlet(), null, null);
      			service.registerServlet("/config", new ConfigServlet(), null, null);
			}
  		}
  		
  		public void stop(BundleContext context) throws Exception {
  		}
	}

}
