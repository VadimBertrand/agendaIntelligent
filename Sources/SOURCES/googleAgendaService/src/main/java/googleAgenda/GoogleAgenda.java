package googleAgenda;

/**
 * GoogleAgenda Interface
 * @author <a href="">Agenda Intelligent Project Team</a>
 */

public interface GoogleAgenda {

    /**
     * Returns a message like : "Hello $user_name"
     * @param name and password of the google account
     * @return the hello message
     */
    String accountUser(String name, String password);
}