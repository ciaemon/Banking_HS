package banking;

import java.sql.SQLException;

/**
 * Interface represents banking system
 */
public interface Banking {
    /**
     * Creates new accounts in system
     * @return created Account
     * @throws SQLException
     */
    public Account createAccount() throws SQLException;

    /**
     * Attempts to log into the system using Credentials object and set current account if login is successful
     * @param credentials
     * @return true, if login successful; false, if not
     * @throws SQLException
     */
    public boolean login(Credentials credentials) throws SQLException;

    /**
     * @return current account if logged in; null - if not
     */
    public Account currentAccount();

    /**
     * Logs out the system and clearing current account
     */
    public void logout();



}
