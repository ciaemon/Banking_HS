package banking;

import java.sql.SQLException;

public interface Banking {
    public Account createAccount() throws SQLException;
    public boolean login(Credentials credentials) throws SQLException;
    public Account currentAccount();
    public void logout();



}
