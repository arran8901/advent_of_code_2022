import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day3RucksackReorganization {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part1() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int prioritySum = 0;

    String line;
    while ((line = br.readLine()) != null) {
      String compartment1 = line.substring(0, line.length() / 2);
      String compartment2 = line.substring(line.length() / 2);

      Set<Character> chars = compartment1.chars()
          .mapToObj(c -> (char) c)
          .collect(Collectors.toCollection(HashSet::new));

      char appearsInBoth = '\0';
      for (int i = 0; i < compartment2.length(); i++) {
        if (chars.contains(compartment2.charAt(i))) {
          appearsInBoth = compartment2.charAt(i);
          break;
        }
      }

      prioritySum += priority(appearsInBoth);
    }

    System.out.println(prioritySum);
  }

  private static int priority(char c) {
    if (c >= 'a' && c <= 'z')
      return c - 'a' + 1;

    return c - 'A' + 27;
  }

  private static void part2() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int prioritySum = 0;

    String line;
    while ((line = br.readLine()) != null) {
      Set<Character> chars = line.chars()
          .mapToObj(c -> (char) c)
          .collect(Collectors.toCollection(HashSet::new));

      for (int i = 0; i < 2; i++) {
        line = br.readLine();
        chars = line.chars()
            .mapToObj(c -> (char) c)
            .filter(chars::contains)
            .collect(Collectors.toCollection(HashSet::new));
      }

      prioritySum += priority(chars.iterator().next());
    }

    System.out.println(prioritySum);
  }
}
