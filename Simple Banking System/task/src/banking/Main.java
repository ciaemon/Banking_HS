package banking;

import java.sql.SQLException;
import java.util.*;


public class Main {

   public static void main(String[] args) throws SQLException {
     Banking b = new BankingSql("jdbc:sqlite:", "test.db", "", "");
     BankingUI bui = new BankingUIEnum();
     bui.run(b);
   }
}


