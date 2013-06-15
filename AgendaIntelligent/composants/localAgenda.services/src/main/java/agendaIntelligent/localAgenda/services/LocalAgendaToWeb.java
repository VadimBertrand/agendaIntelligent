package agendaIntelligent.localAgenda.services;

import net.fortuna.ical4j.model.*;
import java.util.ArrayList;
import net.fortuna.ical4j.model.property.Uid;

/**
 * Local Agenda Interface
 * Service provides by LocalAgenda to WebServer
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */

public interface LocalAgendaToWeb {

	/*
	* return the Agendas numbers
    * @param void
    * @return the Agendas numbers
    */
	public int getAgendaNumber();

	/**
		 * return a calendar
    	 * @param name of the agenda you want to get
    	 * @return the agenda associated to the specified name
    	 */
    public Calendar sendAgenda(String name);
    
	/**
	* Insert a new Component on the calendar on which the method is called
	* Parameters are by order : component's start, component's end, the event's name and the component's attendees
	* Event's attendees are insert in the last parameters attendees
	* Note that attendees can be empty but not null
   * @param java.util.Calendar start, java.util.Calendar end, String eventName, ArrayList<String> attendees
   * @return void
   */
    public void createComponent(java.util.Calendar start, java.util.Calendar end,
     String eventName, ArrayList<String> attendees, long timeUntilStart);
    
	/**
	* Remove a Component on the calendar on which the method is called
    * @param net.fortuna.ical4j.model.property.Uid uid
    * @return void
    */    
    public void removeComponent(net.fortuna.ical4j.model.property.Uid uid);
    
	/**
	 * Call the checkAlarm() method of the brainModule
     * @param void
     * @return void
     */ 
    public void checkAlarm();

	public ArrayList<String[]> getUsersList();

	public void addUser(String infoUser[]);
}
