import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day5SupplyStacks {

  public static void main(String[] args) throws IOException {
    part(2);
  }

  private static void part(int part) throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int numStacks = 0;
    Deque<Character>[] stacks = null;

    String line;
    while (!(line = br.readLine()).isEmpty()) {
      if (stacks == null) {
        numStacks = (line.length() + 1) / 4;
        stacks = new Deque[numStacks];
        for (int i = 0; i < numStacks; i++) {
          stacks[i] = new ArrayDeque<>();
        }
      }

      for (int i = 0; i < numStacks; i++) {
        char letter = line.charAt(4 * i + 1);
        if (Character.isAlphabetic(letter)) {
          stacks[i].add(letter);
        }
      }
    }

    Deque<Character> temp = new ArrayDeque<>();
    final Pattern pattern = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");

    while ((line = br.readLine()) != null) {
      Matcher matcher = pattern.matcher(line);
      matcher.find();
      int moveQty = Integer.parseInt(matcher.group(1));
      int moveFrom = Integer.parseInt(matcher.group(2)) - 1;
      int moveTo = Integer.parseInt(matcher.group(3)) - 1;

      while (moveQty-- > 0) {
        if (part == 1)
          stacks[moveTo].push(stacks[moveFrom].pop());
        else {
          temp.push(stacks[moveFrom].pop());
        }
      }

      while (!temp.isEmpty())
        stacks[moveTo].push(temp.pop());
    }

    System.out.println(Arrays.stream(stacks)
        .map(Deque::peek)
        .map(String::valueOf)
        .collect(Collectors.joining()));
  }
}
