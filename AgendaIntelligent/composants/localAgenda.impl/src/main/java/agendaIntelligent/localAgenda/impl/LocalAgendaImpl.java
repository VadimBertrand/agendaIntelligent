package agendaIntelligent.localAgenda.impl;

import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;
import agendaIntelligent.brainModule.services.BrainModuleToLocal;
import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;

import org.apache.felix.ipojo.annotations.*;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.util.*;
import net.fortuna.ical4j.model.component.*;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.parameter.Cn;

import java.util.Iterator;
import java.util.Calendar;
import java.util.*;
import java.io.IOException;
import java.net.URI;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
@org.apache.felix.ipojo.annotations.Component(publicFactory=false)
@Provides
@Instantiate
public class LocalAgendaImpl implements LocalAgendaToWeb {
	
	/*	ATRRIBUTES	*/
	
	@Requires
	private GoogleAgendaToLocal googleAgendaToLocal;
	@Requires
	private BrainModuleToLocal brainModuleToLocal;	  
	
	//Agendas googles
	private net.fortuna.ical4j.model.Calendar calendarGoogle1;
	private net.fortuna.ical4j.model.Calendar calendarGoogle2;
	// Agenda commun
	private net.fortuna.ical4j.model.Calendar calendarCommun;
	
	private final static int AGENDANUMBER = 3;
	
	private ArrayList<Users> userList;
	
	
	/*	METHODES		*/
	
	/**
	 * Return this.AGENDANUMBER
	 */
	public int getAgendaNumber() {
	    return this.AGENDANUMBER;
	}

	/**
	 * Return a list of String[]
	 * Corresponding to the register users
	 */
	public ArrayList<String[]> getUsersList() {
		if (userList == null) {
			return null;
		} else {
			ArrayList<String[]> list = new ArrayList();
			Users currentUser = null;
			Iterator<Users> it = this.userList.iterator();
			while (it.hasNext()) {
				currentUser = it.next();
				String tab[] = {currentUser.userName, currentUser.userMail, currentUser.url};
				list.add(tab);
			}
			return list;
		}
	}

	/**
	 * Add an user to the userList
	 */
	public void addUser(String infoUser[]) {
		Users user = new Users(infoUser[0], infoUser[1], infoUser[2]);
		
		if (this.userList == null) {
		// First user
			this.userList = new ArrayList<Users>();
			this.googleAgendaToLocal.setUrlAgenda(infoUser[0],infoUser[2]);
		} else if (this.userList.size()==1) {
			googleAgendaToLocal.setUrlAgenda(infoUser[0],infoUser[2]);
			this.googleAgendaToLocal.setUrlAgenda(infoUser[0],infoUser[2]);
		}
		
		this.userList.add(user);
	}

	/**
	 * Initialize the common calendar
	 */
	protected void setAgenda(String calendarName) {
		if (calendarName.equals("commun")) {
			this.calendarCommun = new net.fortuna.ical4j.model.Calendar();
		}
	}

	/**
	 * Add an event
	 * 	and	attendee
	 *		alarm
	 */
    public void createComponent(java.util.Calendar start, java.util.Calendar end, 
    		String eventName, ArrayList<String> attendees, long timeUntilStart) 
    	{
		DateTime startTime = null;
		DateTime endTime = null;
		VEvent event = null;

		try {
			// Create a TimeZone
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			net.fortuna.ical4j.model.TimeZone timezone = registry.getTimeZone("Europe/Paris");
			net.fortuna.ical4j.model.component.VTimeZone tz = timezone.getVTimeZone();
			start.setTimeZone(timezone);
			end.setTimeZone(timezone);

			// Create the event
			startTime = new DateTime(start.getTime());
			endTime = new DateTime(end.getTime());	    
			event = new VEvent(startTime, endTime, eventName);

			// add timezone info..
			event.getProperties().add(tz.getTimeZoneId());

			// generate unique identifier..
			UidGenerator ug = new UidGenerator("uidGen");
			Uid uid = ug.generateUid();
			event.getProperties().add(uid);
			
			// add attendees..
			ListIterator it = attendees.listIterator();
			while(it.hasNext()) {
				String attendeeName = (String) it.next();
				String attendeeMail = null;
				Users user = null;
				ListIterator itUser = this.userList.listIterator();
				// we search the mail adress corresponding to the attendeeName
				while(itUser.hasNext()) {
					user = (Users) itUser.next();
					if(user.userName.equals(attendeeName)){
						break;
					}
				}
				// if we found one, we add the attendee to the event
				if(user!=null && user.userName.equals(attendeeName)) {
					attendeeMail = user.userMail;
	   				Attendee attendee = new Attendee(URI.create("mailto:"+attendeeMail));
					attendee.getParameters().add( new net.fortuna.ical4j.model.parameter.Role("REQ_PARTICIPANT") );
					attendee.getParameters().add(new net.fortuna.ical4j.model.parameter.Cn(attendeeName));
					event.getProperties().add(attendee);
				}
			}	    
			
			// add alarm if necessary
			if(timeUntilStart >= 0) {
			    net.fortuna.ical4j.model.Dur dur = new net.fortuna.ical4j.model.Dur(
			    		new java.util.Date(start.getTimeInMillis()), 
			    		new java.util.Date(start.getTimeInMillis() - timeUntilStart));
				event.getAlarms().add(new VAlarm(dur));
	    		}

			// check if no conflicts in common Agenda :
			if (this.calendarCommun != null) {
				brainModuleToLocal.checkCommonConflict(this.calendarCommun, event, this.getUsersList());
			}
	    
			// check if no conflict in google agenda :
			Iterator<String> it1 = attendees.iterator();
			net.fortuna.ical4j.model.Calendar googleAgenda = null;
			while (it1.hasNext()) {
				String attendee = (String) it1.next();
				if (attendee.equals(this.userList.get(0).userName)) {
					if (!(this.userList.get(0).url.equals(""))) {
						googleAgenda = this.googleAgendaToLocal.getGoogleAgenda("google1");
						brainModuleToLocal.checkGoogleAgenda(googleAgenda, event, this.getUsersList().get(0));
					}
				} else if (this.userList.size() >= 2) {
					if (attendee.equals(this.userList.get(1).userName) && !(this.userList.get(1).url.equals(""))) {
						googleAgenda = this.googleAgendaToLocal.getGoogleAgenda("google2");
						brainModuleToLocal.checkGoogleAgenda(googleAgenda, event, this.getUsersList().get(1));
					}
				}
			}
	
	  	  	// Add the event
			(this.calendarCommun).getComponents().add(event);
		} catch(java.net.SocketException e) {
	    		System.out.println("Error : java.net.SocketException");
	    		e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
	     	System.out.println("Probleme lors de la creation de l'evenement");
		}
    }

	/**
	 * Remove a component
	 */	
    public void removeComponent(Uid uid) {
		String UID = uid.getValue();
		UID = UID.substring(4);
		ListIterator it = this.calendarCommun.getComponents().listIterator();
		// process on each component of the calendar
		while(it.hasNext()) {
		    net.fortuna.ical4j.model.Component component = (net.fortuna.ical4j.model.Component) it.next();
		    // we look at the uid
		    net.fortuna.ical4j.model.PropertyList listProperties = component.getProperties();
		    net.fortuna.ical4j.model.property.Uid currentUid = (net.fortuna.ical4j.model.property.Uid) 
		    		listProperties.getProperty(net.fortuna.ical4j.model.Property.UID);

	    		// if the uid match we remove the event
	    		if(currentUid.getValue().equals(UID) ) {
				it.remove();
	    		}
		}	
    }

	/**
     * return a calendar
    	 * @param name of the agenda you want to get
    	 * @return the agenda associated to the specified name
     * @see localAgenda.services.LocalAgendaToWeb#sendAgenda(Java.lang.String)
     */
    public net.fortuna.ical4j.model.Calendar sendAgenda(String name) {
		// wich calendar we want to get
		if(name.equals("commun")) {
			if (this.calendarCommun == null) {
				this.setAgenda("commun");
			}
		} else if(name.equals("google1")) {    
			this.calendarGoogle1 = googleAgendaToLocal.getGoogleAgenda("google1");
			if (this.calendarCommun!=null) {
				net.fortuna.ical4j.model.ComponentList comps = this.calendarCommun.getComponents();
				Iterator<net.fortuna.ical4j.model.Component> it = comps.iterator();
				// we check for conflit on each event
				while(it.hasNext()) {	
					net.fortuna.ical4j.model.Component comp = (net.fortuna.ical4j.model.Component) it.next();
					if (comp.getName().equals("VEVENT")) {
						System.out.println("EVENT");
						net.fortuna.ical4j.model.component.VEvent currentEvent = (net.fortuna.ical4j.model.component.VEvent) comp;
						brainModuleToLocal.checkGoogleAgenda(calendarGoogle1, currentEvent, this.getUsersList().get(0));
					}
				}
			}
			// we check if there is command to program
			this.brainModuleToLocal.checkCMD(this.calendarGoogle1,5000);
			return this.calendarGoogle1;
		} else if(name.equals("google2")) {
			this.calendarGoogle2 = googleAgendaToLocal.getGoogleAgenda("google2");
			if (this.calendarCommun!=null) {
				net.fortuna.ical4j.model.ComponentList comps = this.calendarCommun.getComponents();
				Iterator<net.fortuna.ical4j.model.Component> it = comps.iterator();
				// we check for conflit on each event
				while(it.hasNext()) {	
					net.fortuna.ical4j.model.Component comp = (net.fortuna.ical4j.model.Component) it.next();
					if (comp.getName().equals("VEVENT")) {
						net.fortuna.ical4j.model.component.VEvent currentEvent = (net.fortuna.ical4j.model.component.VEvent) comp;
						brainModuleToLocal.checkGoogleAgenda(calendarGoogle2, currentEvent, this.getUsersList().get(1));
					}
				}
			}
			// we check if there is command to program
			this.brainModuleToLocal.checkCMD(this.calendarGoogle2,5000);
			return this.calendarGoogle2;
		}	
		// we check if there is command to program
		this.brainModuleToLocal.checkCMD(this.calendarCommun,5000);
		return this.calendarCommun;
    }
    
    /**
     * 
     */
    public void checkAlarm() {	    
	    // we check if users are register
	    if(this.userList!=null) {
	    	// yes
	    	// we check if he holds the calendar google1
	    		if(!this.userList.get(0).url.equals("")) {
	    		// yes
	    		// check alarm for google1
				brainModuleToLocal.checkAlarm(this.calendarGoogle1, 10*1000, this.userList.get(0).url);
			}
			// we check if he holds the calendar google2
	    		if(this.calendarGoogle2!=null) {
	    		// yes
	    		// check alarm for google2
				brainModuleToLocal.checkAlarm(this.calendarGoogle2, 10*1000, this.userList.get(1).url);
			}
			// check alarm for common
		    brainModuleToLocal.checkAlarm(this.calendarCommun, 10*1000, null);
		}
    }
    
}
