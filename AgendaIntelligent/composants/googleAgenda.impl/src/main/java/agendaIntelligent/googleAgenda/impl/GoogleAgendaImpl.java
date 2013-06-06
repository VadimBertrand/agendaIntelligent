package agendaIntelligent.googleAgenda.impl;

import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;
import net.fortuna.ical4j.model.*; 
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

    /**
     * Returns a message like : "Hello $user_name"
     * @param name and password of the google account
     * @return the hello message
     * @see googleAgenda.services.GoogleAgendaToLocal#accountUser()
     */
    public String accountUser() {
        return "google agenda ";
    }
   

   public String returnMonth(int day,int month, int year){
	net.fortuna.ical4j.model.Date date=new net.fortuna.ical4j.model.Date();
	date.setMonth(month);
	date.setYear(year);
	return date.toGMTString();    
   }
	
    public String httpGet(String urlStr) throws IOException {
	  URL url = new URL(urlStr);
	  HttpURLConnection conn =
	      (HttpURLConnection) url.openConnection();

  	if (conn.getResponseCode() != 200) {
    	throw new IOException(conn.getResponseMessage());
  	}

	  // Buffer the result into a string
	  BufferedReader rd = new BufferedReader(
	      new InputStreamReader(conn.getInputStream()));
	  StringBuilder sb = new StringBuilder();
	  String line;
	  while ((line = rd.readLine()) != null) {
	    sb.append(line);
	  }
	  rd.close();

	  conn.disconnect();
	  return sb.toString();
	}

 /*
    public String CreateCalendar (){
	Calendar calendar = new Calendar();
	calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
	calendar.getProperties().add(Version.VERSION_2_0);
	calendar.getProperties().add(CalScale.GREGORIAN);    
	return calendar.getProperties()
    }
*/
}

