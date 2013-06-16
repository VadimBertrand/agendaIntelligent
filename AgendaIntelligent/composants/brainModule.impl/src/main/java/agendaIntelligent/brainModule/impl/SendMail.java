package agendaIntelligent.brainModule.impl;



import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Transport;
 
import javax.mail.internet.AddressException;
import javax.mail.NoSuchProviderException;
import javax.mail.MessagingException;


public class SendMail {

    /*
     * send a mail to the adress 'email' with the object 'object' and containing 'corps'
     * the source adress is set to projetfablab2@gmail.com
     * @param String email, String objet, String corps)
     * @return void
     */
    public void envoi(String email, String objet, String corps) throws Exception {
		// Mail adress and password of the emetteur
		final String address = "agendaintelligent@gmail.com";
		final String password = "projetfablab";
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		Session session = Session.getInstance(props,
						      new javax.mail.Authenticator() {
							  protected PasswordAuthentication getPasswordAuthentication() {
							      return new PasswordAuthentication(address, password);
							  }
						      });
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("FROM_ADDRESS"));
		message.setRecipients(Message.RecipientType.TO,
				      InternetAddress.parse(email));
		message.setSubject(objet);
		message.setText(corps);
		Transport.send(message);
    }
    
    /*
	* send a mail to the adress 'email' with a predefined object and corps
	* @param String email : the destination adress, String eventName : the event to remind
	* @return void
	*/
    public void envoiAlerte(String email, String eventName, String timeStart) {
    if(!email.equals("")) {
		String objet = "Alerte Agenda";
		String corps = "Rappel : la tâche "+eventName+" débute à la date suivante : "+timeStart;
		try {
		    envoi(email,objet,corps);
		    System.out.println(eventName+" ENVOIE DU MAIL !!!!");
		}
		catch(Exception e) {
		    System.out.println("Error : the mail send failed");
		}	
	    }
    }

    
    /*
	* send a mail to the adress 'email' with a predefined object and corps
	* @param String email : the destination adress, String eventName : the event to remind
	* @return void
	*/
    public void envoiConflit(String email, String eventName, String timeStart) {
		String objet = "Conflict dans votre agenda";
		String corps = "Attention : la tâche " + eventName + " qui débute à la date suivante : " + timeStart +
					" a générée un conflit dans votre agenda.";
		try {
		    envoi(email,objet,corps);
		} catch(Exception e) {
		    System.out.println("Error : the mail send failed");
		}
	}

}
