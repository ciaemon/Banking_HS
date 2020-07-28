package banking;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generator for card numbers and pins. Constructor argument is a constant digits at start of a number
 */
public class CredentialsGenerator {
    private List<Integer> prefix;
    private Random random = new Random();

    public CredentialsGenerator (String prefix) {
        if (!prefix.matches("\\d{0,15}")) {
            throw new InvalidParameterException("Prefix must contain only digits and not exceed 15 characters"); }
        List<Integer> pref = new ArrayList<>();
        for (var digit:prefix.toCharArray()) {
            pref.add(Character.getNumericValue(digit));
        }
        this.prefix = pref;
    }

    public Credentials next() {
        return new Credentials(nextNumber(), nextPin());
    }

    public String nextNumber() {
        int[] accNum = new int[16];
        int sum = 0;
        StringBuilder number = new StringBuilder();
        for (var digit:prefix) {
            sum += digit;
            number.append(digit);
        }

        for (int i = prefix.size(); i < 15; i++) {
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
        int checkSum = (10 - sum % 10) % 10;
        number.append(checkSum);
        return number.toString();
    }

    public String nextPin() {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }
}
