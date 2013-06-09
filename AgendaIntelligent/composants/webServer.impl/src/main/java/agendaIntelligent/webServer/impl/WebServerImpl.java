package agendaIntelligent.webServer.impl;

import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;

import java.lang.*;
import java.util.Timer;
import java.util.TimerTask;
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
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.util.*;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.model.property.*;

import org.apache.commons.io.IOUtils;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@org.apache.felix.ipojo.annotations.Component(publicFactory=false)
@Provides
@Instantiate
public class WebServerImpl implements Runnable {

	boolean active = false;
	Timer t;
	int delay = 5000;
	BundleContext context;
	net.fortuna.ical4j.model.Calendar calendar;
	
	@Requires
	private LocalAgendaToWeb localAgendaToWeb;


	// permet de recuperer le context du bundle
	public WebServerImpl(BundleContext bc) {
		this.context = bc;
	}
   

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
			t.schedule(new MiseAJour(),0,delay);
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
		try {
			this.calendar = this.localAgendaToWeb.sendAgenda();
		} 
		catch (Exception e) {
		    System.out.println("ERREUR de récupération de l'agenda");
		}
		System.out.println("======= Liste de composants =======");
		net.fortuna.ical4j.model.ComponentList comps = this.calendar.getComponents();
		//System.out.println(comp.toString());

		System.out.println("======= Liste de properties =======");
		// TODO : iterateur sur comp
		net.fortuna.ical4j.model.Component comp = (net.fortuna.ical4j.model.Component) comps.iterator().next();
		net.fortuna.ical4j.model.PropertyList props = comp.getProperties();
		System.out.println(props.toString());
		System.out.println("======= Properties interessante =======");
		net.fortuna.ical4j.model.Property propName = props.getProperty(net.fortuna.ical4j.model.Property.SUMMARY);
		net.fortuna.ical4j.model.Property propStart = props.getProperty(net.fortuna.ical4j.model.Property.DTSTART);
		net.fortuna.ical4j.model.Property propEnd = props.getProperty(net.fortuna.ical4j.model.Property.DTEND);
		System.out.println(propName.toString());
		System.out.println(propStart.toString());
		System.out.println(propEnd.toString());
		//System.out.println(this.calendar);
	}
	
	
	/**
	 * classe definissant l'action executee par le timer
	 */
	class MiseAJour extends TimerTask {
		public void run() {
			WebServerImpl.this.recupAgenda();
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
			throws ServletException, IOException 
		{
			resp.getWriter().write(WebServerImpl.this.calendar.toString()); 
		} 
	}
	
	/**
	 * classe definissant la servlet d'interface
	 */
	public class InterfaceServlet extends HttpServlet {
	
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {

			InputStream myStream = new FileInputStream("../WebContent/WEB-INF/create.html");
			String myString = IOUtils.toString(myStream);

			resp.getWriter().write(myString); 
		}

		protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
			
			// Recuperation des champs du formulaire :
			
			String eventName = getValeurChamp(request, "eventName");

			int startYear = Integer.parseInt(getValeurChamp(request, "startYear"));
			int startMonth = Integer.parseInt(getValeurChamp(request, "startMonth"));
			int startDay = 0;
			int startHour = 0;
			int startMin = 0;

			int endYear = 0;
			int endMonth = 0;
			int endDay = 0;
			int endHour = 0;
			int endMin = 0;
			
			if (startMonth == 2) {
				if (startYear == 2016) {
					startDay = Integer.parseInt(getValeurChamp(request, "startDay29"));
				} else {
					startDay = Integer.parseInt(getValeurChamp(request, "startDay28"));
				}
			} else if (startMonth == 4 || startMonth == 6 || startMonth == 9 || startMonth == 11) {
				startDay = Integer.parseInt(getValeurChamp(request, "startDay30"));
			} else {
				startDay = Integer.parseInt(getValeurChamp(request, "startDay31"));
			}

			if (getValeurChamp(request, "allDay").equals("notAllDay")) {

				startHour = Integer.parseInt(getValeurChamp(request, "startHour"));
				startMin = Integer.parseInt(getValeurChamp(request, "startMin"));			

				endYear = Integer.parseInt(getValeurChamp(request, "endYear"));
				endMonth = Integer.parseInt(getValeurChamp(request, "endMonth"));
				endDay = 0;
				endHour = Integer.parseInt(getValeurChamp(request, "endHour"));
				endMin = Integer.parseInt(getValeurChamp(request, "endMin"));

				if (endMonth == 2) {
					if (endYear == 2016) {
						endDay = Integer.parseInt(getValeurChamp(request, "endDay29"));
					} else {
						endDay = Integer.parseInt(getValeurChamp(request, "endDay28"));
					}
				} else if (endMonth == 4 || endMonth == 6 || endMonth == 9 || endMonth == 11) {
					endDay = Integer.parseInt(getValeurChamp(request, "endDay30"));
				} else {
					endDay = Integer.parseInt(getValeurChamp(request, "endDay31"));
				}
			} else { 
				
				startHour = 0;
				startMin = 0;			

				endYear = startYear;
				endMonth = startMonth;
				endDay = startDay;
				endHour = 24;
				endMin = 59;
			}

			// Creation des Calendar :

			// Creation de la TimeZone
			//TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			java.util.TimeZone timezone = java.util.TimeZone.getTimeZone("Europe/Paris");
			// net.fortuna.ical4j.model.VTimeZone tz = timezone.getVTimeZone();

			// Date de début de l'evenement
			java.util.Calendar startDate = java.util.Calendar.getInstance();
			startDate.setTimeZone(timezone);
			startDate.set(startYear, startMonth, startDay, startHour, startMin);

			// Date de fin de l'evenement
			java.util.Calendar endDate = java.util.Calendar.getInstance();
			endDate.setTimeZone(timezone);
			endDate.set(endYear, endMonth, endDay, endHour, endMin);

			// Ajout de l'evenement à l'agenda local :
			localAgendaToWeb.createComponent(startDate, endDate, eventName);

			// reaffichage du formulaire d'ajout de tache :
			doGet(request, response);
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
      			service.registerServlet("/chaine", new DisplayAgendaServlet(), null, null);
			}
  		}
  		
  		public void stop(BundleContext context) throws Exception {
  		}
	}

}
