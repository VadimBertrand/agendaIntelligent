package agendaIntelligent.localAgenda.impl;


import agendaIntelligent.googleAgenda.services.GoogleAgendaToLocal;
import net.fortuna.ical4j.model.*;


public class Users {
   String userName;
	String userMail;
	String url;
	//private net.fortuna.ical4j.model.Calendar calendarUser;
	

    Users(String userName, String userMail) {
		this.userName = userName;
		this.userMail = userMail;
    } 

	Users(String userName, String userMail, String url) {
		this.userName = userName;
		this.userMail = userMail;
		this.url = url;
		//this.calendarUser =googleAgendaToLocal.getGoogleAgenda(userName);
	}
}

