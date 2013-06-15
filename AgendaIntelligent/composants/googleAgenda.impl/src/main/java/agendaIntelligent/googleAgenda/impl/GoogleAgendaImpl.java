package agendaIntelligent.googleAgenda.impl;

import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;

import net.fortuna.ical4j.model.*; 
import net.fortuna.ical4j.util.*; 

import org.apache.felix.ipojo.annotations.*;

import java.io.*;
import java.net.*;

/**
 * Component implementing the Google Agenda service
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Implementation of the services provided by GoogleAgenda to LocalAgenda
 */
@org.apache.felix.ipojo.annotations.Component
@Provides
@Instantiate
public class GoogleAgendaImpl implements GoogleAgendaToLocal {
	
	private String user1;
	private String user2;
	
	private java.net.URL urlAgenda1 = null;
	private java.net.URL urlAgenda2 = null;
	//private Users user;
	
		/** 
	* initialize the url of the google agendas
	*/
	public void setUrlAgenda(String nameUser, String url) {
		try {
			if(this.urlAgenda1 == null){
				this.user1 = nameUser;
				if (url.equals("")) {
					urlAgenda1 = null;
				} else {
					this.urlAgenda1 = new java.net.URL(url);
				}
			} else {
				this.user2= nameUser;
				if (url.equals("")) {
					urlAgenda2 = null;
				} else {
					this.urlAgenda2 = new java.net.URL(url);
				}

				//urlAgenda1 = new java.net.URL("https://www.google.com/calendar/ical/projetfablab1%40gmail.com/private-2a002af9a99aba54ca9f63d4a8cd4604/basic.ics");
				//urlAgenda2 = new java.net.URL("https://www.google.com/calendar/ical/projetfablab2%40gmail.com/private-5861a3fece00ffed1a2e4398d673116b/basic.ics");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    
     /**
     * Returns a calendar in ical format associated to a specified url
     * @param String  representing the name of googleAgenda we want to get
     * @return ical4j.calendar 
     */
	public net.fortuna.ical4j.model.Calendar getGoogleAgenda(String name) {
	//	this.setUrlAgenda();
		if(name=="google1"){
			try {
				return net.fortuna.ical4j.util.Calendars.load(urlAgenda1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (name=="google2"){
			try {
				return net.fortuna.ical4j.util.Calendars.load(urlAgenda2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}

