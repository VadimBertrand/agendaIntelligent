package agendaIntelligent.brainModule.services;

import net.fortuna.ical4j.model.Calendar;

/**
 * Brain Module Interface
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Service provided by brainModule to localAgenda
 */

public interface BrainModuleToWeb {
	
	/**
	 * return an integer reprensenting the hour of the current time rounded down  
	 * @return the current hour rounded down
	 */ 
	public Integer getHourFilter();

   /**
	* return a string reprensenting the date of the current time rounded down  
	* @return the current time rounded down
	*/ 
   public long getDateFilter(); 
    
    
	/**
	* return a calendar filtered  
	* @param date reprensenting the beggining of the time intervalle we want to filter and
	*         the calendar to filter
	* @return the agenda associated the specific period
	*/
    public net.fortuna.ical4j.model.Calendar filterAgenda(net.fortuna.ical4j.model.Calendar calendar, long date);

    
    /**
	* return a calendar of CMD  
	* @param the 3 calendars we want to examine
	* @return an agenda where all events are CMD
	*/
   // public net.fortuna.ical4j.model.Calendar checkCMD(net.fortuna.ical4j.model.Calendar calendar1,
    //net.fortuna.ical4j.model.Calendar calendar2,net.fortuna.ical4j.model.Calendar calendar3);
}
