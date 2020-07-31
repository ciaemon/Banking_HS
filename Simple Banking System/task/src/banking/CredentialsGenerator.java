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
        int sum = 0;
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < 15; i++) {
            int digit;
            if (i < prefix.size()) {
                digit = prefix.get(i);
            } else {
                digit = random.nextInt(10);
            }
            number.append(digit);

            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }

        int checkSum = 0;
        if (sum % 10 != 0) {
            checkSum = 10 - sum % 10;
        }
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

    /**
     * Check number for correct format
     * @param number - Number to check
     * @return code of completion. 1 - correct number, 0 - number doesn' t pass Luhn algorithm, -1 - incorrect format
     */
    public static boolean checkLuhn(String number) {
        if (!number.matches("\\d{16}")) {
            System.err.println("Regex mismatch");
            return false;
        }
        int checkSum = 0;
        for (int i = 0; i < 16; i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            checkSum += digit;
        }
        if (checkSum % 10 != 0) {
            System.err.println("Luhn error");
            return false;
        }
        return true;
    }
}
