package agendaIntelligent.webServer.impl;

import net.fortuna.ical4j.model.*;

import java.lang.*;
import java.util.*;
import java.io.*;
import org.apache.commons.io.IOUtils;

/**
 * Component implementing the Local Agenda services
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
public class RemoveWrite {

	public RemoveWrite() {
		super();
	}


	public String fileToString(net.fortuna.ical4j.model.Calendar cal) {
		String chaine = null;
		String startDate = null;
		String startDateYear = null;
		String startDateMonth = null;
		String startDateDay = null;
		String startDateHour = null;
		String startDateMin = null;
		String endDate = null;
		String endDateYear = null;
		String endDateMonth = null;
		String endDateDay = null;
		String endDateHour = null;
		String endDateMin = null;
		String eventName = null;
		String uid = null;
		String stringAttendees = "";
		InputStream myStream = null;
		
		net.fortuna.ical4j.model.ComponentList compList = cal.getComponents();
		net.fortuna.ical4j.model.Component comp = null;

		PropertyList props = null;

		chaine = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 5.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> " + 
				"<html> " + 
				"<title>Remove a task</title> " + 
				"<head> " +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/> "  +
				"<meta name=\"title\" content=\"remove a task from your agenda\"/> " + 
				"</head> " + 
				
				"<body> " + 
				"<h2>Liste des evenements dans votre agenda :</h2> "; 

		if (compList == null) {
			chaine = chaine + "<p>Votre agenda est vide.</p>";
		} else {
			chaine = chaine + "<ul> ";

			Iterator<Component> it = compList.iterator();

			while (it.hasNext()) {
				comp = it.next();

				props = comp.getProperties();
				startDate = getValue(props.getProperty(Property.DTSTART).toString());
				startDate = startDate.trim();
				startDateYear = startDate.substring(0,4);
				startDateMonth = startDate.substring(4,6);
				startDateDay = startDate.substring(6,8);
				startDateHour = startDate.substring(9,11);
				startDateMin = startDate.substring(11,13);
				endDate = getValue(props.getProperty(Property.DTEND).toString());
				endDate = endDate.trim();
				endDateYear = endDate.substring(0,4);
				endDateMonth = endDate.substring(4,6);
				endDateDay = endDate.substring(6,8);
				endDateHour = endDate.substring(9,11);
				endDateMin = endDate.substring(11,13);
				eventName = getValue(props.getProperty(Property.SUMMARY).toString());
				eventName = eventName.trim();
				uid = props.getProperty(Property.UID).toString();
				uid = uid.substring(0, uid.length() - 2);

				ArrayList<Property> listeAttendees = props.getProperties(Property.ATTENDEE);
				Iterator<Property> it1 = listeAttendees.iterator();

				stringAttendees = "";
				while (it1.hasNext()) {
					stringAttendees = stringAttendees + getValue(it1.next().getParameter("CN").toString()) + ", ";
				}
				stringAttendees = stringAttendees.substring(0, stringAttendees.length() - 2);

				chaine = chaine +
						"<li> " + 
						"<form method=\"post\"> " + 
						"<p> Du " +  startDateDay + " " + startDateMonth + " " + startDateYear + " (" + startDateHour + "h" + startDateMin + ") au " + endDateDay + " " + endDateMonth + " " + endDateYear + " (" + endDateHour + "h" + endDateMin + ")" + " </br>" + 
						"Nom de l'evenement : " + eventName + " &nbsp &nbsp " +
						"</br> " + 
						"Participant(s) : " +  stringAttendees +
						"</br> " +
						"<button name=\"remove\" id=\"remove\" type=\"submit\" value=\"1\">Supprimer</button> " + 
						"<input type=\"hidden\" id=\"eventUid\" name=\"eventUid\" value=\"" + uid + "\" />" +
						"<input type=\"button\" name=\"modify\" id=\"modify\" value=\"Modifier\" ";
						
				stringAttendees = stringAttendees.replaceAll("\\s", "");
	
				chaine = chaine +
						"onclick=\"self.location.href='/create?uid=" + echap(uid) + "&amp;start=" + startDate + 
						"&amp;end=" + endDate + "&amp;eventName=" + eventName + "&amp;users=" + stringAttendees + "'\" />" +
						"</p>" +
						"</form>" +
						"</li>";
			}
		}

		chaine = chaine + "</ul>" +
						"<h3>Retour a la page de configuration :</h3>" +
						"<input type=\"button\" name=\"config\" id=\"config\" value=\"Configuration\" " + 
						"onclick=\"self.location.href='/config'\" />" +
						"</body>" + 
						"</html>";

		return chaine;
	}


	/*
   	 * Methode retourne la valeur d'une propriété sans sa description
   	 */
  	 private String getValue(String chaine) {
	 	if (chaine.indexOf(":") != -1) {
	 		chaine = chaine.substring(chaine.lastIndexOf(":") + 1);
		}
		if (chaine.indexOf("=") != -1) {
	 		chaine = chaine.substring(chaine.lastIndexOf("=") + 1);
		}
		return chaine;
	}

	private String echap(String chaine) {
		if (chaine.indexOf("%") != -1) {
			chaine = chaine.substring(0, chaine.indexOf("%") + 1) + "25" + chaine.substring(chaine.indexOf("%") + 1);
		}
		return chaine;
	}
}
