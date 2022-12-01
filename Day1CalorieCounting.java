import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.PriorityQueue;

public class Day1CalorieCounting {

  public static void main(String[] args) throws IOException {
    part1();
  }

  private static void part1() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int currElfCals = 0;
    int maxElfCals = Integer.MIN_VALUE;

    String line;
    while ((line = br.readLine()) != null) {
      if (line.isEmpty()) {
        maxElfCals = Math.max(maxElfCals, currElfCals);
        currElfCals = 0;
      } else {
        currElfCals += Integer.parseInt(line);
      }
    }
    maxElfCals = Math.max(maxElfCals, currElfCals);

    System.out.println(maxElfCals);
  }

  private static void part2() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int currElfCals = 0;
    PriorityQueue<Integer> maxElfCals = new PriorityQueue<>();

    String line;
    while ((line = br.readLine()) != null) {
      if (line.isEmpty()) {
        maxElfCals.add(currElfCals);
        if (maxElfCals.size() > 3)
          maxElfCals.poll();
        currElfCals = 0;
      } else {
        currElfCals += Integer.parseInt(line);
      }
    }
    maxElfCals.add(currElfCals);
    if (maxElfCals.size() > 3)
      maxElfCals.poll();

    System.out.println(maxElfCals.stream().mapToInt(i -> i).sum());
  }
}
