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
    Account createAccount() throws SQLException;

    /**
     * Attempts to log into the system using Credentials object and set current account if login is successful
     * @param credentials
     * @return true, if login successful; false, if not
     * @throws SQLException
     */
    boolean login(Credentials credentials) throws SQLException;

    /**
     * @return current account if logged in; null - if not
     */
    Account currentAccount();

    /**
     * Logs out the system and clearing current account
     */
    void logout();

     /**
     * Add amount of money to recipient
     * @param recipient
     * @param amount
     * @return code of completion.\n
     * 1 - success,\n
     * 0 - not enough money,\n
     * -1 - invalid recipient,\n
     * 2 - amount is less or equal to zero
     */
    int transfer(String recipient, long amount) throws SQLException;

    /**
     * Closes current account
     */
    void closeAccount() throws SQLException;
}
