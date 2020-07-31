package banking;

import java.sql.*;
import java.util.HashSet;
import java.util.Random;

public class BankingSql implements Banking {
    private Account currentAccount;
    private String url;
    private String db;
    private String user;
    private String userPwd;
    private CredentialsGenerator generator;

    /**
     * Constructor initialize database and create table if database is new
     * @param url connection URL
     * @param db name of database
     * @param user user name
     * @param userPwd user password
     * @throws SQLException
     */
    public BankingSql(String url, String db, String user, String userPwd) throws SQLException {
        this.url = url;
        this.db = db;
        this.user = user;
        this.userPwd = userPwd;
        this.generator = new CredentialsGenerator("400000");
        Connection con = getConnection();
        String sql = "CREATE TABLE IF NOT EXISTS card (id INTEGER, number TEXT, pin TEXT, balance INTEGER DEFAULT 0);";
        con.createStatement().execute(sql);
    }

    /**
     * @return new Connection to database
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url + db);
    }

    /**
     * Testing connection
     * @return true if connection successful
     */
    public boolean testConnection() {
        try {
            getConnection();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("Connection failed");
            return false;
        }
        System.err.println("Connection successful");
        return true;
    }

    @Override
    public Account createAccount() throws SQLException {
        Connection conn = getConnection();
        String sqlGetAllNumbers = "SELECT number, id FROM card;";
        HashSet<String> numbers = new HashSet<>();
        ResultSet rs = conn.createStatement().executeQuery(sqlGetAllNumbers);
        while (rs.next()) {
            numbers.add(rs.getString(1));
        }
       // int lastIndex = rs.getInt(2);
        rs.close();
        String newNumber = generator.nextNumber();
        while (numbers.contains(newNumber)) {
            newNumber = generator.nextNumber();
        }
        String newPin = generator.nextPin();
        PreparedStatement prep = conn.prepareStatement("INSERT INTO card (number, pin) VALUES (?, ?);");
        //prep.setInt(1, lastIndex + 1);
        prep.setString(1, newNumber);
        prep.setString(2, newPin);
        prep.execute();
        prep.close();
        return new Account(newNumber, newPin);
    }

    @Override
    public boolean login(Credentials credentials) throws SQLException {
        Connection conn = getConnection();
        String sqlQuery = "SELECT number, pin, balance FROM card WHERE number = ? AND pin = ?;";
        PreparedStatement prep = conn.prepareStatement(sqlQuery);
        prep.setString(1, credentials.getNumber());
        prep.setString(2, credentials.getPin());
        ResultSet rs = prep.executeQuery();
        if (!rs.next()) {
            rs.getStatement().close();
            return false; }
        currentAccount = new Account(credentials, rs.getLong("balance"));
        rs.getStatement().close();
        return true;
    }

    @Override
    public Account currentAccount() {
        if (currentAccount == null) System.err.println("Current account is null!");
        return currentAccount;
    }

    @Override
    public void logout() {
        currentAccount = null;
    }

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
    @Override
    public int transfer(String recipient, long amount) throws SQLException {
        if (amount <= 0) return 5; // wrong amount
        if (currentAccount.getBalance() < amount) return 1; // not enough money
        if (!CredentialsGenerator.checkLuhn(recipient)) return 2; // invalid number
        if (currentAccount.getNumber().equals(recipient)) return 4; // recipient is sender
        Connection conn = getConnection();
        /*
        String sqlGetRec = "SELECT number, balance FROM card WHERE number = ?;";
        PreparedStatement prep = conn.prepareStatement(sqlGetRec);
        prep.setString(1, recipient);
        ResultSet recRS = prep.executeQuery();
        if (!recRS.next()) {
            prep.close();
            return 3;
        }

        long recBalance = recRS.getLong("balance");
         */
        Statement transfer = conn.createStatement();
        String formattedSql = "UPDATE card SET balance = balance + %d WHERE number = %s";
        transfer.addBatch(String.format(formattedSql, amount, recipient)); // add to recipient
        transfer.addBatch(String.format(formattedSql, -amount, currentAccount.getNumber())); // from sender

        if (transfer.executeBatch()[0] != 1) { // First query must affect exactly one row
            transfer.cancel();
            return 3; // No recipient in db
        }
        transfer.close();

        return 0; // success
    }

    /**
     * Closes current account
     */
    @Override
    public void closeAccount() throws SQLException {
        Connection conn = getConnection();
        PreparedStatement prep = conn.prepareStatement("DELETE FROM card WHERE number = ?;");
        prep.setString(1, currentAccount.getNumber());
        prep.execute();
        prep.close();
        currentAccount = null;
    }
}
