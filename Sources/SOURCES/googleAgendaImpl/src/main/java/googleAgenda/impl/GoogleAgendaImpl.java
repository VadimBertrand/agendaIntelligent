package googleAgenda.impl;

import googleAgenda.GoogleAgenda;

import org.apache.felix.ipojo.annotations.*;

/**
 * Component implementing the googleAgenda service
 * @author <a href="">Agenda Intelligent Project Team</a>
 */
@Component
@Provides
@Instantiate
public class GoogleAgendaImpl implements GoogleAgenda {

    /**
     * Returns a message like : "Hello $user_name"
     * @param name and password of the google account
     * @return the hello message
     * @see googleAgenda.GoogleAgenda#accountUser(java.lang.String, java.lang.String)
     */
    public String accountUser(String name, String password) {
        return "Hello " + name;
    }
}