import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day2RockPaperScissors {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part1() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int totalScore = 0;

    String line;
    while ((line = br.readLine()) != null) {
      int opShape = line.charAt(0) - 'A';
      int myShape = line.charAt(2) - 'X';

      totalScore += (myShape + 1) + outcome(opShape, myShape);
    }

    System.out.println(totalScore);
  }

  private static int outcome(int opShape, int myShape) {
    switch ((3 + myShape - opShape) % 3) {
      case 0: return 3;  // Draw
      case 1: return 6;  // Win
      case 2: return 0;  // Loss
    }
    throw new IllegalArgumentException();
  }

  private static void part2() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int totalScore = 0;

    String line;
    while ((line = br.readLine()) != null) {
      int opShape = line.charAt(0) - 'A';
      int outcome = line.charAt(2) - 'X';

      totalScore += (myShape(opShape, outcome) + 1) + outcomeScore(outcome);
    }

    System.out.println(totalScore);
  }

  private static int myShape(int opShape, int outcome) {
    int diff = (outcome + 2) % 3;
    return (opShape + diff) % 3;
  }

  private static int outcomeScore(int outcome) {
    return outcome * 3;
  }
}
