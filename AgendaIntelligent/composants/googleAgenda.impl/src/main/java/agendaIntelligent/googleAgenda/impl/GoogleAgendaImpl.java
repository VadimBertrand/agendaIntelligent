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
	
	private java.net.URL urlAgenda = null;
	
	
	/** 
	* initialize the url of the google agenda
	*/
	public void setUrlAgenda() {
		try {
			urlAgenda = new java.net.URL("https://www.google.com/calendar/ical/vadim.bertrand%40gmail.com/" + 					"private-601dc676388413da20ea7b702d5716fa/basic.ics");
		} catch(Exception e) {
			System.out.println("Erreur au passage a l'URL");
		}
	}
	
    /**
     * Returns a message like : "Hello $user_name"
     * @param name and password of the google account
     * @return the hello message
     * @see googleAgenda.services.GoogleAgendaToLocal#accountUser()
     */
    public String accountUser() {
        return "google agenda ";
    }

    
     /**
     * Returns a calendar in ical format associated to a specified url
     * @param 
     * @return ical4j.calendar 
     * @see googleAgenda.services.GoogleAgendaToLocal#getGoogleAgenda()
     */
	public net.fortuna.ical4j.model.Calendar getGoogleAgenda() {
		this.setUrlAgenda();
		try {
			return net.fortuna.ical4j.util.Calendars.load(urlAgenda);
		} catch (Exception e) {
			System.out.println("Erreur a la recup de l'agenda");
		}
		return null;
	}

//	public String httpGet(String urlStr) throws IOException {
//	  	URL url = new URL(urlStr);
//	  	HttpURLConnection conn = (HttpURLConnection) url.openConnection();

//  		if (conn.getResponseCode() != 200) {
//    			throw new IOException(conn.getResponseMessage());
//  		}

//	  	// Buffer the result into a string
//	  	BufferedReader rd = new BufferedReader(
//	      	new InputStreamReader(conn.getInputStream()));
//	  	StringBuilder sb = new StringBuilder();
//	  	String line;
//	  	while ((line = rd.readLine()) != null) {
//	  		sb.append(line);
//	  	}
//	  	rd.close();

//	  	conn.disconnect();
//	  	return sb.toString();
//	}

}

