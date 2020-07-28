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

    public BankingSql(String url, String db, String user, String userPwd) throws SQLException {
        this.url = url;
        this.db = db;
        this.user = user;
        this.userPwd = userPwd;
        Connection con = getConnection();
        String sql = "CREATE TABLE IF NOT EXISTS card (id INTEGER, number TEXT, pin TEXT, balance INTEGER DEFAULT 0);";
        con.createStatement().execute(sql);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url + db);
    }

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

    private String generateNumber() {
        int[] accNum = new int[16];
        accNum[0] = 4;
        int sum = 8;
        StringBuilder number = new StringBuilder("400000");
        for (int i = 6; i < 15; i++) {
            accNum[i] = random.nextInt(10);
            int digit = accNum[i];
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            number.append(accNum[i]);
        }
        // System.out.println(sum);

        int checkSum = (10 - sum % 10) % 10;
        // System.out.println(checkSum);
        number.append(checkSum);


        return number.toString();
    }

    private String generatePin() {
        StringBuilder cin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            cin.append(random.nextInt(10));
        }
        return cin.toString();
    }

    final Random random = new Random();

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
        String newNumber = generateNumber();
        while (numbers.contains(newNumber)) {
            newNumber = generateNumber();
        }
        String newPin = generatePin();
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
        currentAccount = new Account(credentials);
        currentAccount.setBalance(rs.getLong(3));
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
}
