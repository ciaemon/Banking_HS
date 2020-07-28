package banking;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

enum Menu {
    MAIN_MENU ("1. Create account\n" +
            "2. Log into account\n" +
            "0. Exit\n", true),
    ACCOUNT_MENU ("1. Balance\n" +
            "2. Log out\n" +
            "0. Exit\n", true),
    ENTERING_NUMBER ("Enter your card number: ", true),
    ENTERING_PIN("Enter your PIN: ", true),
    BALANCE("Balance: %s\n", false),
    ACCOUNT_CREATED("Your card have been created\n" +
            "Your card number:\n%s\n" +
            "Your card PIN:\n%s\n", false),
    EXITING("Bye!", false),
    LOGIN_SUCCESSFUL("You have successfully logged in!\n", false),
    LOGIN_FAILED("Wrong card number or PIN!\n", false),
    LOGOUT("You have successfully logged out!\n", false);


    String printedInfo;
    boolean inputNeeded;

    Menu(String printedInfo, boolean inputNeeded) {
        this.printedInfo = printedInfo;
        this.inputNeeded = inputNeeded;
    }


    public void print() {
        System.out.print(printedInfo);
    }
}

public class BankingUI {

    private Menu menu = Menu.MAIN_MENU;
    // boolean processing = true;
    final private Scanner scanner = new Scanner(System.in);
    private String input;
    private ArrayList<String> args = new ArrayList<>();
    private Credentials credentials = Credentials.emptyCredentials();

    public BankingUI(Banking banking) throws SQLException {
        if (banking == null) throw new NullPointerException();
        while (true) {
            printMenu();

            if (menu.inputNeeded) {
                //System.out.print("Input:  ");
                this.input = scanner.nextLine();
            }
            switch (menu) {
                case MAIN_MENU:
                    switch (this.input) {
                        case "1": // Create account
                            credentials = banking.createAccount().getCredentials();
                            menu = Menu.ACCOUNT_CREATED;
                            args.add(credentials.getNumber());
                            args.add(credentials.getPin());
                            credentials.reset();
                            break;
                        case "2": // Log into account
                            menu = Menu.ENTERING_NUMBER;
                            break;
                        case "0": // Exit
                            menu = Menu.EXITING;
                            break;
                        default:
                            System.out.println("Incorrect input!");
                    }
                    break;
                case ACCOUNT_MENU:
                    switch (input) {
                        case "1": // Balance
                            menu = Menu.BALANCE;
                            args.add(String.valueOf(banking.currentAccount().getBalance()));
                            break;
                        case "2": // Logout
                            menu = Menu.LOGOUT;
                            break;
                        case "0": // Exit
                            menu = Menu.EXITING;
                            break;
                        default:
                            System.out.println("Incorrect input!");
                    }
                    break;
                case ENTERING_NUMBER:
                    credentials.setNumber(input);
                    menu = Menu.ENTERING_PIN;
                    break;
                case ENTERING_PIN:
                    credentials.setPin(input);
                    if (banking.login(credentials)) {
                        menu = Menu.LOGIN_SUCCESSFUL;
                    } else {
                        menu = Menu.LOGIN_FAILED;
                    }
                    break;

                case LOGIN_SUCCESSFUL:
                case BALANCE:
                    menu = Menu.ACCOUNT_MENU;
                    break;
                case LOGIN_FAILED:
                case LOGOUT:
                    credentials.reset();
                    banking.logout();
                    menu = Menu.MAIN_MENU;
                    break;
                case ACCOUNT_CREATED:
                    menu = Menu.MAIN_MENU;
                    break;
                case EXITING:
                    credentials.reset();
                    return;
            }
        }


    }

    private void printMenu() {
       System.out.printf(menu.printedInfo, args.toArray());
        args.clear();
    }



}
