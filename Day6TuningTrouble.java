import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day6TuningTrouble {

  public static void main(String[] args) {
    part(2);
  }

  private static void part(final int part) {
    final int numDistinctChars = part == 1 ? 4 : 14;

    final Scanner sc = new Scanner(System.in);
    final String buffer = sc.nextLine();

    final Map<Character, Integer> charCounts = new HashMap<>();
    for (int i = 0; i < numDistinctChars - 1; i++) {
      charCounts.merge(buffer.charAt(i), 1, Integer::sum);
    }

    for (int i = numDistinctChars - 1; i < buffer.length(); i++) {
      charCounts.merge(buffer.charAt(i), 1, Integer::sum);
      if (charCounts.size() == numDistinctChars) {
        System.out.println(i + 1);
        return;
      }
      charCounts.compute(buffer.charAt(i - numDistinctChars + 1), (k, v) -> v == 1 ? null : v - 1);
    }
  }
}
