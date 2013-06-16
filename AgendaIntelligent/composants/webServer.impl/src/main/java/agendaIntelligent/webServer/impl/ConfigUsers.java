package agendaIntelligent.webServer.impl;

import java.util.*;

/**
 * Classe pour la Gestion de l'ajout d'utilisateurs
 * @author <a href="http://fablab.ensimag.fr/index.php/Service_Agenda_pour_Habitat_Intelligent">Agenda Intelligent Project Team</a>
 */
public class ConfigUsers {

	public ConfigUsers() {
		super();
	}


	public String fileToString(ArrayList<String[]> userList) {

		String chaine = null;
	
		chaine = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 5.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
				"<html>" + 
				"<title>Configure your Application</title>" + 
				"<head>" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>" + 
				"<meta name=\"title\" content=\"Configuration\"/>" +
				"</head>" +
				"<body>" + 
				"<h2>Configuration<h2>" + 
				"<h3>Liste des utilisateurs : </h3>";


		// Affichage des utilisateurs inscrits dans l'appli
		if (userList == null) {
			chaine = chaine + "<p>Aucun utilisateur n'est inscrit</p>";
		} else {
			chaine = chaine + "<ul>";

			Iterator<String[]> it = userList.iterator();
			String currentUser[];
			while (it.hasNext()) {
				currentUser = it.next();
				chaine = chaine + "<li>Name = " + currentUser[0] + "<br>" +
								"Email = " + currentUser[1] + "<br>";
				if (currentUser[2] != "") {
					chaine = chaine + "Url = " + currentUser[2] + "<br>";
				}
				chaine = chaine + "</li>";
			}
			
			chaine = chaine + "</ul>";
		}

		// formulaire d'ajout d'utilisateur
		if (userList == null || userList.size() < 2) {

			chaine = chaine + "<h3>Add a principal user :</h3>" +
							"<form method=\"post\" action=\"./config\">" + 
							"<p>" + 
							"Name : <input type=\"text\" id=\"newUser\" name=\"newUser\"/></br>" + 
							"Email : <input type=\"text\" id=\"newEmail\" name=\"newEmail\"/></br>" + 
							"Agenda URL : <input type=\"text\" id=\"newUrl\" name=\"newUrl\" /></br>" + 
							"<input type=\"submit\" value=\"Add\" />";
		} else {

			chaine = chaine + "<h3>Add a user :</h3>" +
							"<form method=\"post\" action=\"./config\">" + 
							"<p>" + 
							"Name : <input type=\"text\" id=\"newUser\" name=\"newUser\"/></br>" + 
							"Email : <input type=\"text\" id=\"newEmail\" name=\"newEmail\"/></br>" +  
							"<input type=\"submit\" value=\"Add\" />";
		}

		chaine = chaine + "<h3>Other actions :</h3>" +
						"<input type=\"button\" name=\"add\" id=\"add\" value=\"Add a task\" " + 
						"onclick=\"self.location.href='/create'\" />" +
						"<input type=\"button\" name=\"remove\" id=\"remove\" value=\"Remove/Modify a task\" " + 
						"onclick=\"self.location.href='/remove'\" />" +
						"<input type=\"button\" name=\"print\" id=\"print\" value=\"Display agendas\" " + 
						"onclick=\"self.location.href='/print'\" />" +
						"</body>"  +
						"</html>";

		return chaine;
	}
}
