import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day25FullOfHotAir {

  public static void main(String[] args) throws IOException {
    part1();
  }

  private static void part1() throws IOException {
    List<String> snafuNums = getNumsFromInput();

    long sum = snafuNums.stream()
        .mapToLong(Day25FullOfHotAir::snafuToDecimal)
        .sum();

    String snafuSum = decimalToSnafu(sum);
    System.out.println(snafuSum);
  }

  private static String decimalToSnafu(long decimalNum) {
    // Find highest pow of 5 that will correspond to the value of the first SNAFU digit for this decimal number
    // This is the pow of 5 for which a 1 in its digit position followed by all '=' digits <= decimal number
    // (1=, 1==, 1===, 1====, etc. while <= decimal number)
    long pow5 = 1;
    long lo = 1;
    long sub = 0;
    while (lo <= decimalNum) {
      sub += 2 * pow5;
      pow5 *= 5;
      lo = pow5 - sub;
    }
    pow5 /= 5;
    sub -= 2 * pow5;

    StringBuilder snafuNum = new StringBuilder();

    // First digit may be 1 or 2
    long firstDigit = (decimalNum + sub) / pow5;
    snafuNum.append(firstDigit);

    long rem = decimalNum - pow5 * firstDigit;
    pow5 /= 5;
    sub -= 2 * pow5;
    // Each subsequent digit is the largest among -2, -1, 0, 1, 2 for which its digit position's
    // pow of 5 value * the digit, minus the value of all '=' following digits (variable sub), is
    // <= the remaining difference between the input decimal number and the current value (variable rem)
    while (pow5 != 0) {
      int nextDigit = -2;
      while (nextDigit < 2 && pow5 * (nextDigit + 1) - sub <= rem) {
        nextDigit++;
      }
      snafuNum.append(switch(nextDigit) {
        case 0, 1, 2 -> (char) (nextDigit + '0');
        case -1 -> '-';
        case -2 -> '=';
        default -> throw new IllegalArgumentException();
      });

      rem -= pow5 * nextDigit;
      pow5 /= 5;
      sub -= 2 * pow5;
    }

    return snafuNum.toString();
  }

  private static long snafuToDecimal(String snafuNum) {
    long pow5 = 1;
    long decimalNum = 0;
    for (int i = snafuNum.length() - 1; i >= 0; i--) {
      final char c = snafuNum.charAt(i);
      decimalNum += pow5 * switch(c) {
        case '2', '1', '0' -> Character.getNumericValue(c);
        case '-' -> -1;
        case '=' -> -2;
        default -> throw new IllegalArgumentException();
      };
      pow5 *= 5;
    }
    return decimalNum;
  }

  private static List<String> getNumsFromInput() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    List<String> nums = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      nums.add(line);
    }

    return nums;
  }
}
