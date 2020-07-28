package banking;

public class Credentials {
    public static Credentials emptyCredentials() {
        return new Credentials("", "");
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

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void reset() {
        number = "";
        pin = "";
    }

    String number;
    String pin;

    public Credentials(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }



}