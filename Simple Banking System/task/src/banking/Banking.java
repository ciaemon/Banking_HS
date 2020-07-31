package banking;

import java.sql.SQLException;

/**
 * Interface represents banking system
 */
public interface Banking {
    /**
     * Creates new accounts in system
     *
     * @return created Account
     * @throws SQLException
     */
    Account createAccount() throws SQLException;

    /**
     * Attempts to log into the system using Credentials object and set current account if login is successful
     *
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
     * Transfer amount of money to recipient
     *
     * @param recipient
     * @param amount
     * @return code of completion.\n
     * 0 - success,\n
     * 1 - not enough money,\n
     * 2 - invalid recipient number,\n
     * 3 - recipient does not exist
     * 4 - recipient is current account
     * 5 - amount is less or equal to zero
     */
    int transfer(String recipient, long amount) throws SQLException;

    /**
     * Closes current account
     */
    void closeAccount() throws SQLException;

    /**
     * Adding income to current account
     */
    void addIncome(long amount) throws SQLException;

    /**
     * Checks if account is exist
     * @param number
     * @return
     * @throws SQLException
     */
    boolean isAccountExist(String number) throws SQLException;

    /**
     * Update currentAccount data from banking system
     */
    void refreshCurrentAccount() throws SQLException;
}
