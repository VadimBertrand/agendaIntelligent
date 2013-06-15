package agendaIntelligent.brainModule.impl;

import agendaIntelligent.brainModule.services.BrainModuleToLocal;
import agendaIntelligent.localAgenda.services.LocalAgendaToWeb;
import agendaIntelligent.brainModule.services.BrainModuleToWeb;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.*;

import java.util.*;
import java.util.ListIterator;

import org.apache.felix.ipojo.annotations.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.net.MalformedURLException;

/**
 * Component implementing the Brain Module service
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 * Implementation of the service provided by brain module to local agenda
 */

@org.apache.felix.ipojo.annotations.Component
@Provides
@Instantiate
public class BrainModuleImpl implements BrainModuleToLocal, BrainModuleToWeb {

    public class MyThread extends Thread {

	private long timeSleep;
	private String eventName;
	private String timeStart;
	private ArrayList<String> destUrl;
	private SendMail sendMail;
	
	public MyThread(long timeSleep, String eventName, String timeStart, ArrayList<String> destUrl) {
	    super();
	    this.timeSleep = timeSleep;
	    this.eventName = eventName;
	    this.timeStart = timeStart;
	    this.destUrl = destUrl;
	    this.sendMail = new SendMail();
	}	

	public void run() {
		if(this.destUrl!=null) {
		    try {    
				System.out.println(this.getName()+" "+this.eventName+" THREAD ASLEEP for : "+this.timeSleep+"ms");
				this.sleep(this.timeSleep);
				ListIterator it = destUrl.listIterator();
				while(it.hasNext()) {
					this.sendMail.envoiAlerte((String) it.next(), this.eventName, this.timeStart);					
				}
		    }
		    catch (InterruptedException e) {
				e.printStackTrace();
		    }
		    catch (NullPointerException e) {
			System.out.println("NullPointerException : Au niveau de l'endormissement du thread"); 		    
		    }
		}
	}
   }
   
    private MyThread myThread;
    

    public void checkAlarm(net.fortuna.ical4j.model.Calendar calendar, long delay, String destUrl) {
	System.out.println("Brain checkAlarm()");
	if(calendar!=null) {
	    //On itère sur tous les Component
	    ListIterator itComponent = calendar.getComponents().listIterator();
	    while(itComponent.hasNext()) {
	    	net.fortuna.ical4j.model.Component comp = (net.fortuna.ical4j.model.Component) itComponent.next();
	    	//On vérifie que le Component courant est de type VEvent
	    	if(comp.getName().equals("VEVENT")) {
		    //Si oui on récupère ses éventuelles alarmes
		    net.fortuna.ical4j.model.ComponentList alarmList = ((net.fortuna.ical4j.model.component.VEvent) comp).getAlarms();
		    //S'il y a effectivement des alarmes d'activées
	    		if(alarmList != null) {
			    ListIterator itAlarm = alarmList.listIterator();
			    while(itAlarm.hasNext()) {
				//Pour chaque alarme on récupère la date de l'alarme
				net.fortuna.ical4j.model.component.VAlarm alarm = (net.fortuna.ical4j.model.component.VAlarm) itAlarm.next();						 
				net.fortuna.ical4j.model.Dur dur = alarm.getTrigger().getDuration();
				net.fortuna.ical4j.model.property.DtStart dtStart = ((net.fortuna.ical4j.model.component.VEvent) comp).getStartDate();
				java.util.Date dateAlarm = dur.getTime((java.util.Date) dtStart.getDate());
				
				//Si la date de l'alarme est comprise entre l'heure courante et la prochaine mise à jour
				//on endort un thread qui enverra un mail à son reveil			    						    	
				if((dateAlarm.getTime() >= System.currentTimeMillis()) && (dateAlarm.getTime() <= System.currentTimeMillis()+delay))
				    
				    {
					//Calcul de la durée avant envoie du mail
					long timeSleep = dateAlarm.getTime() - System.currentTimeMillis();

					//Le thread créé s'endort jusqu'à l'envoie du mail
					try {
					    String eventName = ((net.fortuna.ical4j.model.component.VEvent) comp).getSummary().getValue();
					    DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
					    String timeStart = dateFormat.format(new java.util.Date(dtStart.getDate().getTime()));
					    //On créé une arraylist contenant les url des destinataires
					    ArrayList<String> destArray = new ArrayList();
					    
					    //Ajout du propriétaire de l'agenda
					    //Dans le cas de l'agenda commun destUrl est nulle
					    if(destUrl!=null) {
					    	destArray.add(destUrl);
					    }
					    //Ajout des participants potentiels
					    net.fortuna.ical4j.model.PropertyList properties = comp.getProperties();
					    net.fortuna.ical4j.model.PropertyList attendeeList = properties.getProperties(net.fortuna.ical4j.model.Property.ATTENDEE);
					    ListIterator itAttendee = attendeeList.listIterator();
					    while(itAttendee.hasNext()) {
							net.fortuna.ical4j.model.property.Attendee attendee = (net.fortuna.ical4j.model.property.Attendee) itAttendee.next();
							//Recupération de l'url
					    	String url = null;
					        try {
					            url = attendee.getCalAddress().toURL().getPath();
					            if(url!=null) {
					    			destArray.add(url);
					    		}
					            
					        } catch (IllegalArgumentException e) {
					            e.printStackTrace();
					        } catch (MalformedURLException e) {
					            e.printStackTrace();
					        }
	
					    }
					    
					    MyThread t = new MyThread(timeSleep, eventName, timeStart, destArray);
					    t.start();
					}
					catch(Exception e) {
					    System.out.println("Exception à l'utilisation du thread MYTHREAD");		
					}
				    }
			    }
			}
	    	}
	    }
	}
    }
    
    /**
     * return the hour of the current time rounded down
     * @return the current hour rounded down
     * @see brainModule.BrainModule.services#getHourFilter()
     */
    public Integer getHourFilter() {
    		java.util.GregorianCalendar calendar = new GregorianCalendar();
		Integer hourFilter = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		if (0<=hourFilter && hourFilter<8)
			return 0;
		else if (8<=hourFilter && hourFilter<16)
			return 8;
		return 16;
    }
    
	/**
	 * return a date reprensenting the date of the current time rounded down  
	 * @return the current time rounded down
	 * @see brainModule.BrainModule.services#getDateFilter()
	 */ 	
	public long getDateFilter() {
		//get the actual date
		java.util.GregorianCalendar calendar = new GregorianCalendar();
		Integer hourFilter = calendar.get(java.util.Calendar.HOUR_OF_DAY);
		if (0<=hourFilter && hourFilter<8) {
			calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
		} else if (8<=hourFilter && hourFilter<16) {
			calendar.set(java.util.Calendar.HOUR_OF_DAY, 8);
		} else {
			calendar.set(java.util.Calendar.HOUR_OF_DAY, 16);
		}
		calendar.set(java.util.Calendar.MINUTE , 00);
		calendar.set(java.util.Calendar.SECOND , 00);
		calendar.set(java.util.Calendar.MILLISECOND , 00);
		return calendar.getTime().getTime();
	}
		
	/**
	 * return a calendar filtered  
	 * @param date reprensenting the beggining of the time intervalle we want to filter and
	 *         the calendar to filter
	 * @return the agenda associated the specific period
	 * @see brainModule.BrainModule.services#filterAgenda(net.fortuna.ical4j.model.Calendar,java.lang.Long)
	 */
	public net.fortuna.ical4j.model.Calendar filterAgenda(net.fortuna.ical4j.model.Calendar calendar, long startDate) {
		
		// Filtered list to return
		net.fortuna.ical4j.model.ComponentList filteredEvents = new net.fortuna.ical4j.model.ComponentList();
		// Events to filter
		net.fortuna.ical4j.model.ComponentList unfilteredComponents = new net.fortuna.ical4j.model.ComponentList();
		if(calendar != null) {
			unfilteredComponents = calendar.getComponents();
		}
		
		// Current component to filter
		net.fortuna.ical4j.model.Component currentUnfilteredComponent = null;
		// Current event to filter
		net.fortuna.ical4j.model.component.VEvent currentUnfilteredEvent = null;
		
		// Iterator on unfiltered components
		ListIterator iteratorUnfiltered = unfilteredComponents.listIterator();
		
		long endDate = startDate + 8*3600000;
		long testedStartDate;
		long testedEndDate;
		
		String testedName = "";
		
		// process on the unfiltered list 
		while(iteratorUnfiltered.hasNext()) {
			currentUnfilteredComponent = (net.fortuna.ical4j.model.Component) iteratorUnfiltered.next();
			if(currentUnfilteredComponent.getName().equals("VEVENT")) {
				currentUnfilteredEvent = (net.fortuna.ical4j.model.component.VEvent) currentUnfilteredComponent;
				testedStartDate = ((java.util.Date) currentUnfilteredEvent.getStartDate().getDate()).getTime();
				testedEndDate = ((java.util.Date) currentUnfilteredEvent.getEndDate().getDate()).getTime();
				testedName = currentUnfilteredEvent.getSummary().getValue();
				
				if (testedEndDate>startDate && testedStartDate<endDate && !(testedName.length()>3 && 
						testedName.substring(0,3).equals("CMD"))) 
				{
					filteredEvents.add(currentUnfilteredEvent);
				}
			}	
		}	
		net.fortuna.ical4j.model.Calendar calendarReturn = new net.fortuna.ical4j.model.Calendar(filteredEvents);
		return calendarReturn ;
	}
		    
    
    public void checkCommonConflict(net.fortuna.ical4j.model.Calendar calendarCommun,
    			net.fortuna.ical4j.model.component.VEvent eventAdded, ArrayList<String[]> users) 
    	{
		net.fortuna.ical4j.model.PropertyList listAttendeesAdded;

		String email = "";
		Integer i = 0;
		Boolean stop = false;
		
		listAttendeesAdded = eventAdded.getProperties(net.fortuna.ical4j.model.Property.ATTENDEE);
		java.util.Date dateStartAdded =eventAdded.getStartDate().getDate();
		java.util.Date dateEndAdded =eventAdded.getEndDate().getDate();
		
		net.fortuna.ical4j.model.ComponentList comps;
		comps = calendarCommun.getComponents();
		
		if(comps != null) {
			Iterator<net.fortuna.ical4j.model.Component> it = comps.iterator();
			while(it.hasNext()) {
				net.fortuna.ical4j.model.Component comp = (net.fortuna.ical4j.model.Component) it.next();
				if (comp.getName().equals("VEVENT")){
					net.fortuna.ical4j.model.component.VEvent currentEvent = (net.fortuna.ical4j.model.component.VEvent) comp;
					java.util.Date dateStartEvent=(java.util.Date)currentEvent.getStartDate().getDate();
					java.util.Date dateEndEvent=(java.util.Date)currentEvent.getEndDate().getDate();
				
					if ((dateStartEvent.after(dateStartAdded) && dateStartEvent.before(dateEndAdded)) || 
						(dateEndEvent.after(dateStartAdded) && dateEndEvent.before(dateEndAdded)) || 
						(dateStartEvent.before(dateStartAdded) && dateEndEvent.after(dateEndAdded))) 
					{
					
						Iterator<net.fortuna.ical4j.model.Property> it1 = currentEvent.getProperties(net.fortuna.ical4j.model.Property.ATTENDEE).iterator();
						while (it1.hasNext()) {
							net.fortuna.ical4j.model.Parameter currentEventAttendee = it1.next().getParameter("CN");
							
							Iterator<net.fortuna.ical4j.model.Property> it2 = listAttendeesAdded.iterator();
							while (it2.hasNext()) {
								if (it2.next().getParameter("CN").equals(currentEventAttendee)) {
									System.out.println("CONFLIT ENTRE DEUX EVENT");
									while(i<users.size() && !stop) {
										if(users.get(i)[0].equals(currentEventAttendee)) {
											stop = true;
											email = users.get(i)[1];
										}
										i++;
									}
									System.out.println(i);
									System.out.println(email);
									SendMail mail = new SendMail();
									mail.envoiConflit(email, eventAdded.getProperty(net.fortuna.ical4j.model.Property.SUMMARY).getValue(), eventAdded.getStartDate().getValue());   
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Methode qui vérifie si un évenement entre en conflit avec les évenements déja présents dans un agenda
	 */
    public void checkGoogleAgenda(net.fortuna.ical4j.model.Calendar calendarGoogle,
    			net.fortuna.ical4j.model.component.VEvent eventAdded, String userMail) 	
    	{
		
		java.util.Date dateStartAdded = eventAdded.getStartDate().getDate();
		java.util.Date dateEndAdded = eventAdded.getEndDate().getDate();
		
		net.fortuna.ical4j.model.ComponentList comps = null;
		comps = calendarGoogle.getComponents();

		if (comps != null) {
			Iterator<net.fortuna.ical4j.model.Component> it = comps.iterator();
			while(it.hasNext()) {
				net.fortuna.ical4j.model.Component comp = (net.fortuna.ical4j.model.Component) it.next();
				if (comp.getName().equals("VEVENT")){
					net.fortuna.ical4j.model.component.VEvent currentEvent = (net.fortuna.ical4j.model.component.VEvent) comp;
					java.util.Date dateStartEvent = (java.util.Date)currentEvent.getStartDate().getDate();
					java.util.Date dateEndEvent = (java.util.Date)currentEvent.getEndDate().getDate();
				
					if ((dateStartEvent.after(dateStartAdded) && dateStartEvent.before(dateEndAdded)) || 
						(dateEndEvent.after(dateStartAdded) && dateEndEvent.before(dateEndAdded)) || 
						(dateStartEvent.before(dateStartAdded) && dateEndEvent.after(dateEndAdded))) 
					{
						System.out.println("CONFLIT AVEC UN EVENEMENT DU GOOGLE AGENDA");
						System.out.println(userMail);
						SendMail mail = new SendMail();
						mail.envoiConflit(userMail, eventAdded.getProperty(net.fortuna.ical4j.model.Property.SUMMARY).getValue(), eventAdded.getStartDate().getValue());   
					}
				}
			}
		}
	}
	
	
	/*
	    
     /**
	* return a calendar of CMD  
	* @param the 3 calendars we want to examine
	* @return an agenda where all events are CMD
	* @see brainModule.BrainModule.services#filterAgenda(net.fortuna.ical4j.model.Calendar,net.forxtuna.ical4j.model.Calendar,net.fortuna.ical4j.model.Calendar)
	*/
	public void checkCMD(net.fortuna.ical4j.model.Calendar calendar, long delay) {
	System.out.println("Brain checkCMD()");
	long startDateCMD;
	// Current event to check if CMD
	net.fortuna.ical4j.model.component.VEvent currentEvent = null;
	
	if(calendar!=null) {
		//On itère sur tous les Component
		ListIterator itComponent = calendar.getComponents().listIterator();
		while(itComponent.hasNext()) {
			net.fortuna.ical4j.model.Component comp = (net.fortuna.ical4j.model.Component) itComponent.next();
			//On vérifie que le Component courant est de type VEvent
			if(comp.getName().equals("VEVENT")) {
				currentEvent = (net.fortuna.ical4j.model.component.VEvent) comp;
				String summary=currentEvent.getSummary().getValue().substring(0,3);
			//	System.out.println(" name de l'event =  " + summary);
				if(summary.equals("CMD")){
					System.out.println("BrainModule:check CMD on a trouvé un event de type CMD");
					startDateCMD = ((java.util.Date) currentEvent.getStartDate().getDate()).getTime();
					
					//Si la date de début de la tache CMD est comprise entre l'heure courante et la prochaine mise à jour
					//on endort un thread qui enverra un mail à son reveil
					if( (startDateCMD >= System.currentTimeMillis()) && (startDateCMD <= System.currentTimeMillis()+15*1000) ){
						//Calcul de la durée avant envoie du mail
						long timeSleep = startDateCMD - System.currentTimeMillis();
						//Le thread créé s'endort jusqu'à l'envoie du mail
						try {
							String eventName = ((net.fortuna.ical4j.model.component.VEvent) comp).getSummary().toString();
							DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
							String timeStart = dateFormat.format(new java.util.Date(startDateCMD));
							ArrayList<String> destUrl = new ArrayList();
							destUrl.add("projetfablab2@gmail.com");
							MyThread t = new MyThread(timeSleep, eventName, timeStart, destUrl);
							t.start();
						}
						catch(Exception e) {
							System.out.println("Exception à l'utilisation du thread MYTHREAD");		
						}
					}
				}
			}
		}
	}
}
				
}





