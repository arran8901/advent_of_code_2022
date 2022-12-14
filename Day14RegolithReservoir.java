import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Day14RegolithReservoir {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part2() throws IOException {
    final Scan scan = getScanFromInput();
    final Set<Pos> blocked = scan.blocked;
    final int lowestRockY = scan.lowestY;
    final Pos source = new Pos(500, 0);
    final int bottomY = lowestRockY + 1;

    int sandCount = 0;

    int currX = source.x;
    int currY = source.y;
    while (!blocked.contains(source)) {
      // Try to move down
      if (currY < bottomY && !blocked.contains(new Pos(currX, currY + 1))) {
        currY++;
      }

      // Try to move down-left
      else if (currY < bottomY && !blocked.contains(new Pos(currX - 1, currY + 1))) {
        currX--;
        currY++;
      }

      // Try to move down-right
      else if (currY < bottomY && !blocked.contains(new Pos(currX + 1, currY + 1))) {
        currX++;
        currY++;
      }

      // Otherwise, sand comes to rest
      else {
        sandCount++;
        // Current position becomes blocked with this unit of sand
        blocked.add(new Pos(currX, currY));
        // Reset current position to try the next unit of sand
        currX = source.x;
        currY = source.y;
      }
    }

    System.out.println(sandCount);
  }

  private static void part1() throws IOException {
    final Scan scan = getScanFromInput();
    final Set<Pos> blocked = scan.blocked;
    final int lowestY = scan.lowestY;

    int sandCount = 0;

    int currX = 500;
    int currY = 0;
    while (currY <= lowestY) {
      // Try to move down
      if (!blocked.contains(new Pos(currX, currY + 1))) {
        currY++;
      }

      // Try to move down-left
      else if (!blocked.contains(new Pos(currX - 1, currY + 1))) {
        currX--;
        currY++;
      }

      // Try to move down-right
      else if (!blocked.contains(new Pos(currX + 1, currY + 1))) {
        currX++;
        currY++;
      }

      // Otherwise, sand comes to rest
      else {
        sandCount++;
        // Current position becomes blocked with this unit of sand
        blocked.add(new Pos(currX, currY));
        // Reset current position to try the next unit of sand
        currX = 500;
        currY = 0;
      }
    }

    System.out.println(sandCount);
  }

  private static Scan getScanFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    final Set<Pos> blocked = new HashSet<>();
    int lowestY = Integer.MIN_VALUE;

    String line;
    while ((line = br.readLine()) != null) {
      String[] coordsStr = line.split(" -> ");
      int[][] coords = Arrays.stream(coordsStr)
          .map(coordStr -> coordStr.split(","))
          .map(coordStr -> Arrays.stream(coordStr)
              .mapToInt(Integer::parseInt)
              .toArray())
          .toArray(int[][]::new);

      for (int i = 1; i < coords.length; i++) {
        // Mark line from coords[i - 1] to coords[i] as blocked
        final int x1 = coords[i - 1][0];
        final int y1 = coords[i - 1][1];
        final int x2 = coords[i][0];
        final int y2 = coords[i][1];
        final int signumDx = (int) Math.signum(x2 - x1);
        final int signumDy = (int) Math.signum(y2 - y1);

        if (signumDx != 0) {
          for (int x = x1; x != x2; x += signumDx) {
            blocked.add(new Pos(x, y1));
          }
          blocked.add(new Pos(x2, y1));
        } else {
          for (int y = y1; y != y2; y += signumDy) {
            blocked.add(new Pos(x1, y));
          }
          blocked.add(new Pos(x1, y2));
        }

        // Update lowestY
        lowestY = Math.max(lowestY, Math.max(y1, y2));
      }
    }

    return new Scan(blocked, lowestY);
  }

  private static record Pos(int x, int y) {}

  private static record Scan(Set<Pos> blocked, int lowestY) {}
}
