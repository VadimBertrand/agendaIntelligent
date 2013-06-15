package agendaIntelligent.localAgenda.impl;

import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;
import agendaIntelligent.brainModule.services.BrainModuleToLocal;
import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;
//import agendaIntelligent.webServer.services.WebServerToLocal;
import java.io.IOException;
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

import java.net.URI;

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
	/*@Requires
		private WebServerToLocal webServerToLocal;		   */
	
	//Agendas googles
	private net.fortuna.ical4j.model.Calendar calendarGoogle1;
	private net.fortuna.ical4j.model.Calendar calendarGoogle2;
	// Agenda commun
	private net.fortuna.ical4j.model.Calendar calendarCommun;
	
	private final static int AGENDANUMBER = 3;
	
	private ArrayList<Users> userList;

	public int getAgendaNumber() {
	    return AGENDANUMBER;
	}

	/**
	 * methode qui renvoi une liste de tableau de String
	 * correspondant aux utilisateur déja enregistrés
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

	// methode d'ajout d'un utilisateur à la liste
	public void addUser(String infoUser[]) {
		Users user = new Users(infoUser[0], infoUser[1], infoUser[2]);

		if (this.userList == null) {
			this.userList = new ArrayList<Users>();
			//c'est le 1er qu'on ajoute
			this.googleAgendaToLocal.setUrlAgenda(infoUser[0],infoUser[2]);
		} else if (this.userList.size()==1) {
			googleAgendaToLocal.setUrlAgenda(infoUser[0],infoUser[2]);
			this.googleAgendaToLocal.setUrlAgenda(infoUser[0],infoUser[2]);
		}
		this.userList.add(user);
	}


	protected void setAgenda(String calendarName) {
		if (calendarName.equals("commun")) {
			this.calendarCommun = new net.fortuna.ical4j.model.Calendar();

			/*			//////////////// TEPORAIRE //////////////////////////////////////////////////*/
			// Start Date is on: April 1, 2008, 9:00 am
			/*java.util.Calendar startDate = new java.util.GregorianCalendar();
			startDate.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
			startDate.set(java.util.Calendar.DAY_OF_MONTH, 14);
			startDate.set(java.util.Calendar.YEAR, 2013);
			startDate.set(java.util.Calendar.HOUR_OF_DAY, 16);
			startDate.set(java.util.Calendar.MINUTE, 18);
			startDate.set(java.util.Calendar.SECOND, 0);
			
			// End Date is on: April 1, 2008, 13:00
			java.util.Calendar endDate = new java.util.GregorianCalendar();
			endDate.set(java.util.Calendar.MONTH, java.util.Calendar.JUNE);
			endDate.set(java.util.Calendar.DAY_OF_MONTH, 14);
			endDate.set(java.util.Calendar.YEAR, 2014);
			endDate.set(java.util.Calendar.HOUR_OF_DAY, 14);
			endDate.set(java.util.Calendar.MINUTE, 48);	
			endDate.set(java.util.Calendar.SECOND, 0);
			
			ArrayList<String> attendees = new ArrayList();
			attendees.add("pere");
			
			this.createComponent(startDate, endDate, "TEST JU", attendees, -1);*/
/*			/////////////////////// FIN TEMP ///////////////////////////////////////////
*/			
		}/* else if (calendarName.equals("google1")) {
			this.calendarGoogle1 = googleAgendaToLocal.getGoogleAgenda("google1");
		} else if (calendarName.equals("google2")) {
			this.calendarGoogle2 = googleAgendaToLocal.getGoogleAgenda("google2");
		}*/
	}

    public void createComponent(java.util.Calendar start, java.util.Calendar end, String eventName, ArrayList<String> attendees, long timeUntilStart) {
	DateTime startTime = null;
	DateTime endTime = null;
	VEvent event = null;

	System.out.println("======== CREATE COMPONENT ===========");
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
	    	//On cherche attendeeName dans userList et on récupère l'adresse mail correspondante
	    	ListIterator itUser = this.userList.listIterator();
	    	while(itUser.hasNext()) {
	    		user = (Users) itUser.next();
	    		if(user.userName.equals(attendeeName)){
	    			break;
	    		}
	    	}
	    	//Si on a trouvé l'adresse mail correspondante on ajoute le participant
	    	if(user!=null && user.userName.equals(attendeeName)) {
	    		attendeeMail = user.userMail;
   				Attendee attendee = new Attendee(URI.create("mailto:"+attendeeMail));
				attendee.getParameters().add( new net.fortuna.ical4j.model.parameter.Role("REQ_PARTICIPANT") );
				attendee.getParameters().add(new net.fortuna.ical4j.model.parameter.Cn(attendeeName));
				event.getProperties().add(attendee);
	    	}
	    }	    
	    

	    // ========== TEMPORAIRE ==========
	    // add alarms
/*	    java.util.Calendar calAlarm = java.util.Calendar.getInstance();
		calAlarm.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
		calAlarm.set(java.util.Calendar.DAY_OF_MONTH, 25);*/
	    event.getAlarms().add(new VAlarm(new net.fortuna.ical4j.model.Dur(0, 0, -1, 0)));

	    //Check if no conflicts in common Agenda :
	    brainModuleToLocal.checkCommonConflict(this.calendarCommun,event,this.getUsersList());
	    
		// check if no conflict in google agenda :
		Iterator<String> it1 = attendees.iterator();
		net.fortuna.ical4j.model.Calendar googleAgenda = null;
		while (it1.hasNext()) {
			String attendee = (String) it1.next();
			if (attendee.equals(this.userList.get(0).userName)) {
				if (!(this.userList.get(0).url.equals(""))) {
					googleAgenda = this.googleAgendaToLocal.getGoogleAgenda("google1");
					brainModuleToLocal.checkGoogleAgenda(googleAgenda, event, this.getUsersList().get(0)[1]);
				}
			} else if (this.userList.size() >= 2) {
				if (attendee.equals(this.userList.get(1).userName) && !(this.userList.get(1).url.equals(""))) {
					googleAgenda = this.googleAgendaToLocal.getGoogleAgenda("google2");
					brainModuleToLocal.checkGoogleAgenda(googleAgenda, event, this.getUsersList().get(1)[1]);
				}
			}
		}
	
	    
	    // Add the event
	    (this.calendarCommun).getComponents().add(event);
	}
	catch(java.net.SocketException e) {
	    System.out.println("Error : java.net.SocketException");
	}
	catch(Exception e) {
		e.printStackTrace();
	     System.out.println("Probleme lors de la creation de l'evenement");
	}
    }


    public void removeComponent(Uid uid) {
	String UID = uid.getValue();
	UID = UID.substring(4);
	ListIterator it = this.calendarCommun.getComponents().listIterator();
	//On itère sur chaque component de l'agenda
	while(it.hasNext()) {
	    net.fortuna.ical4j.model.Component component = (net.fortuna.ical4j.model.Component) it.next();
	    //Pour chaque component on récupère l'uid dans la liste de Property
	    net.fortuna.ical4j.model.PropertyList listProperties = component.getProperties();
	    net.fortuna.ical4j.model.property.Uid currentUid = (net.fortuna.ical4j.model.property.Uid) listProperties.getProperty(net.fortuna.ical4j.model.Property.UID);

	    //Si on trouve le bon Uid on supprime le component
	    if(currentUid.getValue().equals(UID) ) {
			System.out.println("ENTREE DANS LE IF DE SUPPRESSION => UID TROUVE");
			it.remove();
	    }
	}	
    }


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
     	 * return a calendar
    	 * @param name of the agenda you want to get
    	 * @return the agenda associated to the specified name
     	 * @see localAgenda.services.LocalAgendaToWeb#sendAgenda(Java.lang.String)
     	 */
    public net.fortuna.ical4j.model.Calendar sendAgenda(String name) {
		if(name.equals("commun")) {
			if (this.calendarCommun == null) {
				this.setAgenda("commun");
			}
		// on regarde quel agenda on veut recuperer
		} else if(name.equals("google1")) {    
			this.calendarGoogle1 = googleAgendaToLocal.getGoogleAgenda("google1");
			this.brainModuleToLocal.checkCMD(this.calendarGoogle1,5000);
			return this.calendarGoogle1;
		}
		else if(name.equals("google2")){
			this.calendarGoogle2 = googleAgendaToLocal.getGoogleAgenda("google2");
			this.brainModuleToLocal.checkCMD(this.calendarGoogle2,5000);
			return this.calendarGoogle2;
		}	
			this.brainModuleToLocal.checkCMD(this.calendarCommun,5000);
			return this.calendarCommun;
    }
    
    
    public void checkAlarm() {	    
	    //On teste si des utilisateurs sont enregistrés
	    if(this.userList!=null) {
	    	//Si oui
	    	//On teste si il existe un googleAgenda1
	    	if(!this.userList.get(0).url.equals("")) {
	    		//Si oui
	    		//On check les alarmes de googleAgenda1
				brainModuleToLocal.checkAlarm(this.calendarGoogle1, 10*1000, this.userList.get(0).url);
			}
			//On teste si il existe un googleAgenda2
	    	//if(!this.userList.get(1).url.equals("")) {
	    	if(this.calendarGoogle2!=null) {
	    		//Si oui
	    		//On check les alarmes de googleAgenda2
				brainModuleToLocal.checkAlarm(this.calendarGoogle2, 10*1000, this.userList.get(1).url);
			}
			//On check les alarmes de googleAgendaCommun
		    brainModuleToLocal.checkAlarm(this.calendarCommun, 10*1000, null);
		}
    }
    
}
