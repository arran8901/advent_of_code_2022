import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

public class Day10CathodeRayTube {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part1() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    final Set<Integer> cyclesToCheck = Set.of(20, 60, 100, 140, 180, 220);

    int cycle = 0;
    int x = 1;
    int sumSignalStrengths = 0;

    String line;
    while ((line = br.readLine()) != null) {
      int v = 0;
      if (line.startsWith("addx")) {
        if (cyclesToCheck.contains(++cycle)) {
          sumSignalStrengths += cycle * x;
        }
        v = Integer.parseInt(line.substring(5));
      }

      if (cyclesToCheck.contains(++cycle)) {
        sumSignalStrengths += cycle * x;
      }
      x += v;
    }

    System.out.println(sumSignalStrengths);
  }

  private static void part2() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    final StringBuilder pixels = new StringBuilder();

    int drawPixel = 0;
    int x = 1;

    String line;
    while ((line = br.readLine()) != null) {
      if (Math.abs(drawPixel - x) <= 1) {
        pixels.append('#');
      } else {
        pixels.append('.');
      }
      drawPixel = (drawPixel + 1) % 40;
      
      if (line.startsWith("addx")) {
        if (Math.abs(drawPixel - x) <= 1) {
          pixels.append('#');
        } else {
          pixels.append('.');
        }
        drawPixel = (drawPixel + 1) % 40;
        x += Integer.parseInt(line.substring(5));
      }
    }

    for (int i = 0; i < pixels.length(); i += 40) {
      System.out.println(pixels.substring(i, i + 40));
    }
  }
}
