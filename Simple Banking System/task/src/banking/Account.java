package banking;


/**
 * Represents bank account
 */
public class Account {

    private String number;
    private String pin;
    private long balance = 0;

    public Account(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }

    public Account(Credentials cred) {
        this.number = cred.getNumber();
        this.pin = cred.getPin();
    }

    public Account(String number, String pin, long balance) {
        this.number = number;
        this.pin = pin;
        this.balance = balance;
    }

    public Account(Credentials cred, long balance) {
        this.number = cred.getNumber();
        this.pin = cred.getPin();
        this.balance = balance;

    }


    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }

    public Credentials getCredentials() {
        return new Credentials(number, pin);
    }

    public long getBalance() {
        return balance;
    }

    public void printInfo() {
        System.err.println("-----------Account info------------------------");
        System.err.printf("Number %s\n", number);
        System.err.printf("Pin %s\n", pin);
        System.err.printf("Balance %d\n", balance);
        System.err.println("----------------------------------------");
    }

}
