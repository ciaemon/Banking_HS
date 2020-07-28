package banking;


/**
 * Represents bank account
 */
public class Account {

    public Account(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }
    public Account(Credentials cred) {
        this.number = cred.getNumber();
        this.pin = cred.getPin();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPin() {
        return pin;
    }

    public Credentials getCredentials() {
        return new Credentials(number, pin);
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    private String number;
    private String pin;
    private long balance = 0;

}
