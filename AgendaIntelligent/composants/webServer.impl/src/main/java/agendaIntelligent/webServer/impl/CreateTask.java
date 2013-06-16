package agendaIntelligent.webServer.impl;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

/**
 * Class générant la page de creation d'une tâche
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
public class CreateTask {

	public CreateTask() {
		super();
	}

	/**
	 * create a calendar
	 */
	public java.util.Calendar[] createCalendars(HttpServletRequest request) {

		java.util.Calendar tablCal[] = new java.util.Calendar[2];

		int startYear = Integer.parseInt(getValeurChamp(request, "startYear"));
		int startMonth = Integer.parseInt(getValeurChamp(request, "startMonth"));
		int startDay = 0;
		int startHour = 0;
		int startMin = 0;
		int startSecond = 0;

		int endYear = 0;
		int endMonth = 0;
		int endDay = 0;
		int endHour = 23;
		int endMin = 59;
		int endSecond = 0;

		if (startMonth == 1) {
			if (startYear == 2016) {
				startDay = Integer.parseInt(getValeurChamp(request, "startDay29"));
			} else {
				startDay = Integer.parseInt(getValeurChamp(request, "startDay28"));
			}
		} else if (startMonth == 3 || startMonth == 5 || startMonth == 8 || startMonth == 10) {
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

			if (endMonth == 1) {
				if (endYear == 2016) {
					endDay = Integer.parseInt(getValeurChamp(request, "endDay29"));
				} else {
					endDay = Integer.parseInt(getValeurChamp(request, "endDay28"));
				}
			} else if (endMonth == 3 || endMonth == 5 || endMonth == 8 || endMonth == 10) {
				endDay = Integer.parseInt(getValeurChamp(request, "endDay30"));
			} else {
				endDay = Integer.parseInt(getValeurChamp(request, "endDay31"));
			}
		} else {
			endYear = startYear;
			endMonth = startMonth;
			endDay = startDay;
		}

		// creation of the calendars

		// creation of the TimeZone
		java.util.TimeZone timezone = java.util.TimeZone.getTimeZone("Europe/Paris");

		// start date of the event
		java.util.Calendar startDate = java.util.Calendar.getInstance();
		startDate.setTimeZone(timezone);
		startDate.set(startYear, startMonth, startDay, startHour, startMin, startSecond);

		// end date of the event
		java.util.Calendar endDate = java.util.Calendar.getInstance();
		endDate.setTimeZone(timezone);
		endDate.set(endYear, endMonth, endDay, endHour, endMin, endSecond);
		
		tablCal[0] = startDate;
		tablCal[1] = endDate;

		return tablCal;
	}

	/**
	 * Return the time in ms between the alarme and the evenement
	 */
	public long alarmToMs(HttpServletRequest request) {
		if (getValeurChamp(request, "alarmeOui")!=null && getValeurChamp(request, "alarmeOui").equals("oui")) {
			int alarmeJour = Integer.parseInt(getValeurChamp(request, "alarmeJour"));
			int alarmeHeure = Integer.parseInt(getValeurChamp(request, "alarmeHeure")); 
			int alarmeMin = Integer.parseInt(getValeurChamp(request, "alarmeMin"));

			long durAlarme = (alarmeMin*60 + alarmeHeure*3600 + alarmeJour*3600*23) * 1000;
			return durAlarme;
		} else {
			return -1;
		}
	}
	 	
	/**
	 * Return the content of a champ
	 * Or null
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
