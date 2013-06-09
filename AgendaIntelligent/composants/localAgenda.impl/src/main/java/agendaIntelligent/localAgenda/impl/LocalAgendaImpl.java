package agendaIntelligent.localAgenda.impl;

import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;
import agendaIntelligent.brainModule.services.BrainModuleToLocal;
import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;
import java.io.IOException;
import org.apache.felix.ipojo.annotations.*;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.*;
import net.fortuna.ical4j.model.component.*;
import  net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.component.VTimeZone;

import java.util.Iterator;
import java.util.Calendar;
import java.util.*;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@org.apache.felix.ipojo.annotations.Component(publicFactory=false)
@Provides
@Instantiate
public class LocalAgendaImpl implements LocalAgendaToWeb {
	
	@Requires
	private GoogleAgendaToLocal googleAgendaToLocal;
	@Requires
	private BrainModuleToLocal brainModuleToLocal;

	// Agenda local
	private net.fortuna.ical4j.model.Calendar calendar;

	
	protected void setAgenda() {		
		this.calendar = new net.fortuna.ical4j.model.Calendar();
		/*calendar.getProperties().add(new net.fortuna.ical4j.model.property.ProdId("-//Local Agenda//-"));
		calendar.getProperties().add(net.fortuna.ical4j.model.property.Version.VERSION_2_0);
		calendar.getProperties().add(net.fortuna.ical4j.model.property.CalScale.GREGORIAN);*/
		this.calendar = googleAgendaToLocal.getGoogleAgenda();
	}



    public void createComponent(java.util.Calendar start, java.util.Calendar end, String eventName) {
	DateTime startTime = null;
	DateTime endTime = null;
	VEvent event = null;

	System.out.println("========COUCOU===========");
	try {
	    // Create the event
	    startTime = new DateTime(start.getTime());
	    endTime = new DateTime(end.getTime());	    
	    event = new VEvent(startTime, endTime, eventName);
	    
	    // generate unique identifier..
	    UidGenerator ug = new UidGenerator("uidGen");
	    Uid uid = ug.generateUid();
	    event.getProperties().add(uid);
	    
	    /*	// add attendees..
		Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
		dev1.getParameters().add(Role.REQ_PARTICIPANT);
		dev1.getParameters().add(new Cn("Developer 1"));
		meeting.getProperties().add(dev1);
		
		Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
		dev2.getParameters().add(Role.OPT_PARTICIPANT);
		dev2.getParameters().add(new Cn("Developer 2"));
		meeting.getProperties().add(dev2);
	    */
	    
	    // Add the event
	    (this.calendar).getComponents().add(event);
	}
	catch(java.net.SocketException e) {
	    System.out.println("Error : java.net.SocketException");
	}
	catch(Exception E) {
	     System.out.println("Probleme lors de la creation de l'evenement");
	}
    }

    /*       protected void createComponent(int MONTH, int DAY_OF_MONTH, String eventName) {

		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
		cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

		// initialisation de l'event :
		net.fortuna.ical4j.model.component.VEvent event = new net.fortuna.ical4j.model.component.VEvent(new Date(cal.getTime()), eventName);

		// Generate a UID for the event..
		try {
			net.fortuna.ical4j.util.UidGenerator ug = new UidGenerator("1");
			event.getProperties().add(ug.generateUid());
		} catch (Exception e) {
			System.out.println("ERREUR lors de la creation du composant");
		}

		this.calendar.getComponents().add(event);
		}*/

        /**
     * Merge all properties and components from two specified calendars into one instance.
     * Note that the merge process is not very sophisticated, and may result in invalid calendar
     * data (e.g. multiple properties of a type that should only be specified once).
     * @param c1 the first calendar to merge
     * @param c2 the second calendar to merge
     * @return a Calendar instance containing all properties and components from both of the specified calendars
     */
    public static net.fortuna.ical4j.model.Calendar merge(final net.fortuna.ical4j.model.Calendar c1, final net.fortuna.ical4j.model.Calendar c2) {
        final net.fortuna.ical4j.model.Calendar result = new net.fortuna.ical4j.model.Calendar();
        result.getProperties().addAll(c1.getProperties());
        for (final Iterator i = c2.getProperties().iterator(); i.hasNext();) {
            final net.fortuna.ical4j.model.Property p = (net.fortuna.ical4j.model.Property) i.next();
            if (!result.getProperties().contains(p)) {
                result.getProperties().add(p);
            }
        }
        result.getComponents().addAll(c1.getComponents());
        for (final Iterator i = c2.getComponents().iterator(); i.hasNext();) {
            final net.fortuna.ical4j.model.Component c = (net.fortuna.ical4j.model.Component) i.next();
            if (!result.getComponents().contains(c)) {
                net.fortuna.ical4j.model.ComponentList componentList = result.getComponents();
		componentList.add((net.fortuna.ical4j.model.Component) c);
            }
        }
        return result;
    }


	/**
     	 * Returns concatenate chaines
     	 * @return the message
     	 * @see localAgenda.services.LocalAgendaToWeb#sendAgenda()
     	 */

	public net.fortuna.ical4j.model.Calendar sendAgenda() {
		String get="toto";
/*		try{
		get=googleAgendaToLocal.httpGet("http://www.google.fr");
		}
		catch(IOException e){
		System.out.println("ERREUR (IOException) unreachable url");
		}
		return googleAgendaToLocal.accountUser() + " et " + brainModuleToLocal.sendSmartMessage() 
			+ " via le Local Agenda. Test de la methode returnMonth: "+googleAgendaToLocal.returnMonth(15,07,1998)
			+ " test de la methode getUrl" +get ; */
		if (this.calendar == null) {
			this.setAgenda();

//////////////// TEPORAIRE //////////////////////////////////////////////////

/*			// Create a TimeZone
			net.fortuna.ical4j.model.TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			net.fortuna.ical4j.model.TimeZone timezone = registry.getTimeZone("Europe/Paris");
			VTimeZone tz = timezone.getVTimeZone();

			 // Start Date is on: April 1, 2008, 9:00 am
			 java.util.Calendar startDate = new java.util.GregorianCalendar();
			 startDate.setTimeZone(timezone);
			 startDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
			 startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
			 startDate.set(java.util.Calendar.YEAR, 2014);
			 startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
			 startDate.set(java.util.Calendar.MINUTE, 0);
			 startDate.set(java.util.Calendar.SECOND, 0);

			  // End Date is on: April 1, 2008, 13:00
			  java.util.Calendar endDate = new java.util.GregorianCalendar();
			  endDate.setTimeZone(timezone);
			  endDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
			  endDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
			  endDate.set(java.util.Calendar.YEAR, 2014);
			  endDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
			  endDate.set(java.util.Calendar.MINUTE, 0);	
			  endDate.set(java.util.Calendar.SECOND, 0);

			  // Create the event
			  String eventName = "Progress Meeting";
			  DateTime start = new DateTime(startDate.getTime());
			  DateTime end = new DateTime(endDate.getTime());
			  VEvent meeting = new VEvent(start, end, eventName);

			  // add timezone info..
			  meeting.getProperties().add(tz.getTimeZoneId());

			  // generate unique identifier..
			  try {
			  	UidGenerator ug = new UidGenerator("uidGen");
			 	Uid uid = ug.generateUid();
			 	meeting.getProperties().add(uid);
			 } catch (Exception e) {
			 	System.out.println("Erreur dans générationd e l'Uid");
			}

			// Add the event and print
			this.calendar.getComponents().add(meeting);
*/
/////////////////////// FIN TEMP ///////////////////////////////////////////
		}
		
		/*		// TEMPORAIRE : Test methode merge()
		  //Création d'un autre calendrier
		  net.fortuna.ical4j.model.Calendar otherCal;
		  otherCal = new net.fortuna.ical4j.model.Calendar();
		  otherCal.getProperties().add(new net.fortuna.ical4j.model.property.ProdId("-//Local Agenda//-"));
		  calendar.getProperties().add(net.fortuna.ical4j.model.property.Version.VERSION_2_0);
		  calendar.getProperties().add(net.fortuna.ical4j.model.property.CalScale.GREGORIAN);
		  java.util.Calendar cal1 = java.util.Calendar.getInstance();
		  cal1.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
		  cal1.set(java.util.Calendar.DAY_OF_MONTH, 14);
		  net.fortuna.ical4j.model.component.VEvent antoineBirthDay = new net.fortuna.ical4j.model.component.VEvent(new Date(cal1.getTime()), "antoine's BirthDay");
		  try {
			net.fortuna.ical4j.util.UidGenerator ug = new UidGenerator("1");
			antoineBirthDay.getProperties().add(ug.generateUid());
		} catch (Exception e) {
			System.out.println("ERREUR creation de composant");
		}

		otherCal.getComponents().add(antoineBirthDay);
		
		this.calendar = merge(this.calendar, otherCal);
		*/
		
		return this.calendar;
    }
}
