package agendaIntelligent.webServer.impl;

import agendaIntelligent.webServer.impl.ComponentsUtil;

import net.fortuna.ical4j.model.*;

import java.util.*;
import java.io.*;
import java.lang.Math;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
public class PrintWrite {

	public static String printWriteHtml(net.fortuna.ical4j.model.Calendar googleCalendar1, 
		net.fortuna.ical4j.model.Calendar googleCalendar2,
		net.fortuna.ical4j.model.Calendar localCalendar,
		Integer startHour, long startDate, ArrayList<String[]> namesGoogle) 
	{
		/*	Variables	*/
		// Names
		String nameGoogle1 = "";
		String nameGoogle2 = "";
		if(namesGoogle != null) {
			if(namesGoogle.size()>=1) {
				nameGoogle1 = namesGoogle.get(0)[0];
			}
			if(namesGoogle.size()>=2) {
				nameGoogle2 = namesGoogle.get(1)[0];
			}
		}
		
		// Lists of components
		List<net.fortuna.ical4j.model.component.VEvent> componentsToPrintGoogle1 = new ArrayList<net.fortuna.ical4j.model.component.VEvent>();
		if(googleCalendar1 != null) {
			componentsToPrintGoogle1 = ComponentsUtil.sortByStartDate(googleCalendar1);
		}
		List<net.fortuna.ical4j.model.component.VEvent> componentsToPrintGoogle2 = new ArrayList<net.fortuna.ical4j.model.component.VEvent>();
		if(googleCalendar2 != null) {
			componentsToPrintGoogle2 = ComponentsUtil.sortByStartDate(googleCalendar2);
		}
		List<net.fortuna.ical4j.model.component.VEvent> componentsToPrintLocal = new ArrayList<net.fortuna.ical4j.model.component.VEvent>();
		if(localCalendar != null) {
			componentsToPrintLocal = ComponentsUtil.sortByStartDate(localCalendar);
		}
		
		// Iterators on each list
		Iterator<net.fortuna.ical4j.model.component.VEvent> iteratorGoogle1 = componentsToPrintGoogle1.iterator();
		Iterator<net.fortuna.ical4j.model.component.VEvent> iteratorGoogle2 = componentsToPrintGoogle2.iterator();
		Iterator<net.fortuna.ical4j.model.component.VEvent> iteratorLocal = componentsToPrintLocal.iterator();
		
		// Current components of each calendar
		net.fortuna.ical4j.model.component.VEvent componentCurrentGoogle1 = null;
		net.fortuna.ical4j.model.component.VEvent componentCurrentGoogle2 = null;
		net.fortuna.ical4j.model.component.VEvent componentCurrentLocal = null;
	 	
	 	// Events attributes
	 	String eventName = "";
	 	long eventStart;
	 	long eventEnd;
	 	
	 	// Attributes usefull to write the string
	 	Integer rowspanGoogle1 = 1;
	 	Integer rowspanGoogle2 = 1;
	 	Integer rowspanLocal = 1;
	 	Double rowspanTemp;
	 	Integer lineNumber = 1;
	 	Integer currentHour = startHour;
	 	long currentDate = startDate;
	 	String classHour = "";
	 	String classValue = "";
	 	String stringToWrite = "";
	 	String stringToReturn = "";
	 	FileWriter fw = null;
	 	BufferedWriter output = null;
	
	 	// HTML file invariable header
	 	String startFileHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 5.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n" +
"<html>\n" +
"	<title>Votre agenda</title>\n" +
"	<head>\n" +
"		<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n" +
"		<meta name=\"title\" content=\"Votre agenda\"/>\n" +
"		<style>\n" +
"			body {\n" +
"				background-color:#FFFFFF;\n" +
"				text-align:center;\n" +
"			}\n" +
"			#tableId {\n" +
"				border-collapse: collapse;\n" +
"				width:760px;\n" +
"				height:1020px;\n" +
"			}\n" +
"			.tableBody {\n" +
"				height:6%;\n" +
"				border-left:1px solid black;\n" +
"				border-right:1px solid black;\n" +
"			}\n" +
"			.hour {\n" +
"				width:1%;\n" +
"				vertical-align:top;\n" +
"				border-right:1px solid black;\n" +
"				border-top:1px solid black;\n" +
"			}\n" +
"			.halfHour {\n" +
"				width:1%;\n" +
"				vertical-align:top;\n" +
"				border-right:1px solid black;\n" +
"			}\n" +
"			.event {\n" +
"				width:33%;\n" +
"				border-top:1px solid black;\n" +
"				border-bottom:1px solid black;\n" +
"			}\n" +
"			.noEvent {\n" +
"				width:33%;\n" +
"				border:none;\n" +
"				background-color:#D0D0D0;\n" +
"			}\n" +
"			.column1 {\n" +
"				border-left:1px solid black;\n" +
"				border-right:1px solid black;\n" +
"			}\n" +
"			.column2 {\n" +
"				border-right:1px solid black;\n" +
"			}\n" +
"			.column3 {\n" +
"				border-right:1px solid black;\n" +
"			}\n" +
"			#tableHeader {\n" +
"				height:4%;\n" +
"				border-left:1px solid black;\n" +
"				border-right:1px solid black;\n" +
"			}\n" +
"			#tableHeader td {\n" +
"				border-top:1px solid black;\n" +
"				border-bottom:1px solid black;\n" +
"			}\n" +
"			#tableHeader .hour {\n" +
"				border-bottom:none;\n" +
"				border-top:none;\n" +
"			}\n" +
"			#line1 td {\n" +
"				border-top:none;\n" +
"			}\n" +
"			#line16 td {\n" +
"				border-bottom:1px solid black;\n" +
"			}\n" +
"			#line16 .hour {\n" +
"				border-bottom:none;\n" +
"			}\n" +
"		</style>\n" +
"	</head>\n" +
"	<body>\n" +
"		<table id=\"tableId\" cellspacing=\"0\" cellpadding=\"0\">\n" +
"			<tr id=\"tableHeader\">\n" +
"				<td></td>\n" +
"				<td class=\"column1\">" + nameGoogle1 +"</td>\n" +
"				<td class=\"column2\">" + nameGoogle2 +"</td>\n" +
"				<td class=\"column3\">Commun</td>\n" +
"			</tr>\n";
		
		// HTML file invariable footer
		String endFileHtml = "\n" +
"		</table>\n" +
"	</body>\n" +
"</html>\n";

		
		/*	IMPLEMENTATION	*/
		try {
			stringToReturn = startFileHtml;
			
	 		// Current components initialization
	 		if(iteratorGoogle1.hasNext()) {
	 			componentCurrentGoogle1 = iteratorGoogle1.next();
	 		}
	 		// Current component update
	 		if(iteratorGoogle2.hasNext()) {
	 			componentCurrentGoogle2 = iteratorGoogle2.next();
	 		}
	 		// Current component update
	 		if(iteratorLocal.hasNext()) {
	 			componentCurrentLocal = iteratorLocal.next();
	 		}
	 		
			// Process for each line of the table
			for(lineNumber=1; lineNumber<17; lineNumber++) {
				// String to write a the beginning of each line of the table
				if((lineNumber % 2) != 0) {
					stringToWrite = "" +
"			<tr class=\"tableBody\" id=\"line" + lineNumber.toString() + "\">\n" +
"				<td class=\"hour\">" + String.valueOf(currentHour) + "h</td>\n";
					// We change the hour
	 				currentHour++;
				} else {
					stringToWrite = "" +
"			<tr class=\"tableBody\" id=\"line" + lineNumber.toString() + "\">\n" +
"				<td class=\"halfHour\"></td>\n";
				}
				stringToReturn = stringToReturn + stringToWrite;
				
				/*	PROCESS FOR THE GOOGLE1	*/
				// An event can fill several line depending on his duration
				if(rowspanGoogle1!=1) {
	 				rowspanGoogle1--;
	 			} else {
	 				// If there is no event left in the list
	 				if(componentCurrentGoogle1==null) {
	 					classValue="noEvent";
	 					eventName = "";
	 				} else {
	 					// Recovery of the start date attributes
	 					eventStart = ((java.util.Date) componentCurrentGoogle1.getStartDate().getDate()).getTime();
	 					// Where is the event regarding to the current date ?
	 					if(!(eventStart<currentDate+1800000)) {
	 						classValue="noEvent";
	 						eventName = "";
	 					} else {
	 						eventName = componentCurrentGoogle1.getSummary().getValue();
	 						// If the event is a command, no need to display it
	 						if(eventName.length()>3 && eventName.substring(0,3).equals("CMD")) {
	 							classValue="noEvent";
	 							eventName = "";
	 						} else {
	 							// Recovery of the end date
	 							eventEnd = ((java.util.Date) componentCurrentGoogle1.getEndDate().getDate()).getTime();
	 							// Here, it's sure we have an event to display
	 							classValue="event";
	 							// How many slots the event occupied ?
	 							if(eventEnd>(startDate+15*1800000)) {
	 								rowspanGoogle1 = 16 - (lineNumber-1);
	 							} else if(eventStart<startDate)	{
	 								rowspanTemp = java.lang.Math.floor((eventEnd - startDate) / 1800000);
	 								rowspanGoogle1 = rowspanTemp.intValue();
	 							} else {
	 								rowspanTemp = java.lang.Math.floor((eventEnd - eventStart) / 1800000);
	 								rowspanGoogle1 = rowspanTemp.intValue();
	 							}
	 						}
					 		do {
					 			// Current component update
						 		if(iteratorGoogle1.hasNext()) {
						 			componentCurrentGoogle1 = iteratorGoogle1.next();
						 			eventStart = ((java.util.Date) componentCurrentGoogle1.getStartDate().getDate()).getTime();
						 		} else {
						 			componentCurrentGoogle1 = null;
						 			eventStart = currentDate+1;
						 		}
					 		} while(eventStart<=currentDate);
	 					}
	 				}
	 				// String to write for the corresponding line of the table
	 				stringToWrite = "" +
"				<td class=\"column1 " + classValue + "\" rowspan=\"" + rowspanGoogle1 + "\">" + eventName +"</td>\n";
	 				stringToReturn = stringToReturn + stringToWrite;
	 			}
	 			
	 			/*	PROCESS FOR THE GOOGLE2	*/
				// An event can fill several line depending on his duration
				if(rowspanGoogle2!=1) {
	 				rowspanGoogle2--;
	 			} else {
	 				// If there is no event left in the list
	 				if(componentCurrentGoogle2==null) {
	 					classValue="noEvent";
	 					eventName = "";
	 				} else {
	 					// Recovery of the start date attributes
	 					eventStart = ((java.util.Date) componentCurrentGoogle2.getStartDate().getDate()).getTime();
	 					// Where is the event regarding to the current date ?
	 					if(!(eventStart<currentDate+1800000)) {
	 						classValue="noEvent";
	 						eventName = "";
	 					} else {
	 						eventName = componentCurrentGoogle2.getSummary().getValue();
	 						// If the event is a command, no need to display it
	 						if(eventName.length()>3 && eventName.substring(0,3).equals("CMD")) {
	 							classValue="noEvent";
	 							eventName = "";
	 						} else {
	 							// Recovery of the end date
	 							eventEnd = ((java.util.Date) componentCurrentGoogle2.getEndDate().getDate()).getTime();
	 							// Here, it's sure we have an event to display
	 							classValue="event";
	 							// How many slots the event occupied ?
	 							if(eventEnd>(startDate+15*1800000)) {
	 								rowspanGoogle2 = 16 - (lineNumber-1);
	 							} else if(eventStart<startDate)	{
	 								rowspanTemp = java.lang.Math.floor((eventEnd - startDate) / 1800000);
	 								rowspanGoogle2 = rowspanTemp.intValue();
	 							} else {
	 								rowspanTemp = java.lang.Math.floor((eventEnd - eventStart) / 1800000);
	 								rowspanGoogle2 = rowspanTemp.intValue();
	 							}
	 						}
	 						do {
					 			// Current component update
						 		if(iteratorGoogle2.hasNext()) {
						 			componentCurrentGoogle2 = iteratorGoogle2.next();
						 			eventStart = ((java.util.Date) componentCurrentGoogle2.getStartDate().getDate()).getTime();
						 		} else {
						 			componentCurrentGoogle2 = null;
						 			eventStart = currentDate+1;
						 		}
					 		} while(eventStart<=currentDate);
	 					}
	 				}
	 				// String to write for the corresponding line of the table
	 				stringToWrite = "" +
"				<td class=\"column2 " + classValue + "\" rowspan=\"" + rowspanGoogle2 + "\">" + eventName +"</td>\n";
					stringToReturn = stringToReturn + stringToWrite;
	 			}
	 			
	 			/*	PROCESS FOR THE LOCAL	*/
				// An event can fill several line depending on his duration
				if(rowspanLocal!=1) {
	 				rowspanLocal--;
	 			} else {
	 				// If there is no event left in the list
	 				if(componentCurrentLocal==null) {
	 					classValue="noEvent";
	 					eventName = "";
	 				} else {
	 					// Recovery of the start date attributes
	 					eventStart = ((java.util.Date) componentCurrentLocal.getStartDate().getDate()).getTime();
	 					// Where is the event regarding to the current date ?
	 					if(!(eventStart<currentDate+1800000)) {
	 						classValue="noEvent";
	 						eventName = "";
	 					} else {
	 						eventName = componentCurrentLocal.getSummary().getValue();
	 						// If the event is a command, no need to display it
	 						if(eventName.length()>3 && eventName.substring(0,3).equals("CMD")) {
	 							classValue="noEvent";
	 							eventName = "";
	 						} else {
	 							// Recovery of the end date
	 							eventEnd = ((java.util.Date) componentCurrentLocal.getEndDate().getDate()).getTime();
	 							// Here, it's sure we have an event to display
	 							classValue="event";
	 							// How many slots the event occupied ?
	 							if(eventEnd>(startDate+15*1800000)) {
	 								rowspanLocal = 16 - (lineNumber-1);
	 							} else if(eventStart<startDate)	{
	 								rowspanTemp = java.lang.Math.floor((eventEnd - startDate) / 1800000);
	 								rowspanLocal = rowspanTemp.intValue();
	 							} else {
	 								rowspanTemp = java.lang.Math.floor((eventEnd - eventStart) / 1800000);
	 								rowspanLocal = rowspanTemp.intValue();
	 							}
	 						}
	 						do {
					 			// Current component update
						 		if(iteratorLocal.hasNext()) {
						 			componentCurrentLocal = iteratorLocal.next();
						 			eventStart = ((java.util.Date) componentCurrentLocal.getStartDate().getDate()).getTime();
						 		} else {
						 			componentCurrentLocal = null;
						 			eventStart = currentDate+1;
						 		}
					 		} while(eventStart<=currentDate);
	 					}
	 				}
	 				// String to write for the corresponding line of the table
	 				stringToWrite = "" +
"				<td class=\"column3 " + classValue + "\" rowspan=\"" + rowspanLocal + "\">" + eventName +"</td>\n";
	 				stringToReturn = stringToReturn + stringToWrite;
	 			}
	 			// End of the line
	 			stringToWrite = "" + 
"			</tr>\n";
				stringToReturn = stringToReturn + stringToWrite;
	 			// We change the current date
	 			currentDate+=1800000;
			}
			// End of the table and end of the file
			stringToReturn = stringToReturn + endFileHtml;
		} catch (Exception e) {
	 		System.out.println("Erreur pendant l'écriture dans le fichier HTML");
	 		e.printStackTrace();
	 	}
	 	/*	END OF THE IMPLEMENTATION	*/
	 	return stringToReturn;
	}
	
}
