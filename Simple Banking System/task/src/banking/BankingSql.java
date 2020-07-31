package banking;

import java.sql.*;
import java.util.HashSet;

public class BankingSql implements Banking {
    private Account currentAccount;
    private String url;
    private String db;
    private String user;
    private String userPwd;
    private CredentialsGenerator generator;
    //private Connection conn;

    /**
     * Constructor initialize database and create table if database is new
     *
     * @param url     connection URL
     * @param db      name of database
     * @param user    user name
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
        con.close();
    }

    /**
     * @return new Connection to database
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        try {
           Connection conn = DriverManager.getConnection(url + db);
           return conn;
        } catch(SQLException e) {
          return getConnection();
        }

    }

    /**
     * Testing connection
     *
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
        conn.close();
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
            return false;
        }
        currentAccount = new Account(credentials, rs.getLong("balance"));
        rs.getStatement().close();
        conn.close();
        currentAccount.printInfo();
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
        conn.setAutoCommit(false);
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?;";
        PreparedStatement prepSend = conn.prepareStatement(sql);
        prepSend.setLong(1, -amount);
        prepSend.setString(2, currentAccount.getNumber());
        PreparedStatement prepReceive = conn.prepareStatement(sql);
        prepReceive.setLong(1, amount);
        prepReceive.setString(2, recipient);
        prepSend.executeUpdate();
        if (prepReceive.executeUpdate() != 1) {
            conn.rollback();
            prepReceive.close();
            prepSend.close();
            return 3; // No recipient in db
        }
        conn.commit();
        prepReceive.close();
        prepSend.close();
        conn.close();
        refreshCurrentAccount();
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
        conn.close();
        currentAccount = null;
    }

    /**
     * Adding income to current account
     *
     * @param amount
     */
    @Override
    public void addIncome(long amount) throws SQLException {
        if (amount <= 0) throw new IllegalArgumentException("Amount of income must be positive");
        Connection conn = getConnection();
        PreparedStatement prep = conn.prepareStatement("UPDATE card SET balance = balance + ? WHERE number = ?;");
        prep.setLong(1, amount);
        prep.setString(2, currentAccount.getNumber());
        prep.execute();
        prep.close();
        conn.close();
        refreshCurrentAccount();
    }

    @Override
    public boolean isAccountExist(String number) throws SQLException {
        boolean res = false;
        Connection conn = getConnection();
        PreparedStatement prep = conn.prepareStatement("SELECT number FROM card WHERE number = ?;");
        prep.setString(1, number);
        ResultSet rs = prep.executeQuery();
        if (rs.next()) {
            res = true;
        }
        prep.close();
        conn.close();
        return res;
    }

    /**
     * Update currentAccount data from banking system (only balance now)
     */
    @Override
    public void refreshCurrentAccount() throws SQLException {
        if (currentAccount != null) {
            Connection conn = getConnection();
            PreparedStatement prep = conn.prepareStatement("SELECT balance FROM card WHERE number = ?;");
            prep.setString(1, currentAccount.getNumber());
            ResultSet rs = prep.executeQuery();
            rs.next();
            currentAccount = new Account(currentAccount.getCredentials(), rs.getLong(1));
            prep.close();
            conn.close();
            currentAccount.printInfo();

        }
    }
}
