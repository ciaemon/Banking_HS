package banking;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI used for controlling Banking object. UI starts when object creates with Banking object as argument.
 * UI is an infinite loop with changing menu items represented by enum Menu
 */
public class BankingUIEnum implements BankingUI {

    /**
     * Current menu, starts from Main menu
     */
    private Menu menu = Menu.MAIN_MENU;
    final private Scanner scanner = new Scanner(System.in);
    /**
     * Input for menu if necessary
     */
    private String input;
    /**
     * Current arguments for formatted String in current menu
     */
    private List<String> args = new ArrayList<>();
    /**
     * Current credentials
     */
    private Credentials credentials = Credentials.emptyCredentials();

    public void run(Banking banking) throws SQLException {
        if (banking == null) throw new NullPointerException();
        while (true) {
            printMenu();

            if (menu.inputNeeded) {
                this.input = scanner.nextLine();
            }
            switch (menu) {
                case MAIN_MENU:
                    switch (this.input) { // Selecting menus
                        case "1": // Create account
                            credentials = banking.createAccount().getCredentials();
                            menu = Menu.ACCOUNT_CREATED; // This menu needs two arguments for printing: number and pin
                            args.add(credentials.getNumber());
                            args.add(credentials.getPin());
                            credentials.reset();
                            break;
                        case "2": // Log into account
                            menu = Menu.LOGIN_ENTERING_NUMBER;
                            break;
                        case "0": // Exit
                            menu = Menu.EXITING;
                            break;
                        default:
                            System.out.println("Incorrect input!");
                    }
                    break;
                case ACCOUNT_MENU:
                    switch (input) { // Selecting menus
                        case "1": // Balance
                            menu = Menu.BALANCE; // This menu needs balance as argument
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
                case LOGIN_ENTERING_NUMBER:
                    credentials.setNumber(input);
                    menu = Menu.LOGIN_ENTERING_PIN;
                    break;
                case LOGIN_ENTERING_PIN:
                    credentials.setPin(input);
                    if (banking.login(credentials)) { //attempt to login after inputting PIN
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
                    return; // exiting from UI
                case INCOME_ADD:

                    break;
                case INCOME_ADDED:
                    menu = Menu.ACCOUNT_MENU;
                    break;
                case INCOME_ADD_ERROR:
                    menu = Menu.ACCOUNT_MENU;
                    break;
                case TRANSFER_ENTER_CARD:

                    break;
                case TRANSFER_ENTER_AMOUNT:
                    break;
                case TRANSFER_ERROR:
                    break;
                case CLOSE_ACCOUNT:
                    break;
            }
        }


    }

    /**
     * Printing info from Menu with arguments and clearing.
     */
    private void printMenu() {
       System.out.printf(menu.printedInfo, args.toArray());
       args.clear();
    }

    /**
     * enum represents menu items in Banking UI. Each Menu object has two fields: information to be printed and
     * boolean value, which indicates if UI needs input from user in that Menu
     */
    private enum Menu {
        MAIN_MENU ("1. Create account\n" +
                "2. Log into account\n" +
                "0. Exit\n", true),
        ACCOUNT_MENU ("1. Balance\n" +
                "2. Log out\n" +
                "0. Exit\n", true),
        LOGIN_ENTERING_NUMBER("Enter your card number: ", true),
        LOGIN_ENTERING_PIN("Enter your PIN: ", true),
        BALANCE("Balance: %s\n", false),
        ACCOUNT_CREATED("Your card have been created\n" +
                "Your card number:\n%s\n" +
                "Your card PIN:\n%s\n", false),
        EXITING("Bye!", false),
        LOGIN_SUCCESSFUL("You have successfully logged in!\n", false),
        LOGIN_FAILED("Wrong card number or PIN!\n", false),
        LOGOUT("You have successfully logged out!\n", false),
        INCOME_ADD("Enter income: ", true),
        INCOME_ADDED("Income was added!\n", false),
        INCOME_ADD_ERROR("%s\n", false),
        TRANSFER_ENTER_CARD("Transfer\nEnter card number: ", true),
        TRANSFER_ENTER_AMOUNT("Enter how much money you want to transfer: ", true),
        TRANSFER_ERROR("%s\n", false),
        CLOSE_ACCOUNT("Your account was closed\n", false);


        /**
         * Info to be printed. It can be formatted String.
         */
        String printedInfo;
        /**
         * Is input is needed
         */
        boolean inputNeeded;

        Menu(String printedInfo, boolean inputNeeded) {
            this.printedInfo = printedInfo;
            this.inputNeeded = inputNeeded;
        }
    }

}



