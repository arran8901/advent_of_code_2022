import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class Day9RopeBridge {

  public static void main(String[] args) throws IOException {
    part(2);
  }

  private static void part(int part) throws IOException {
    final int nKnots = part == 1 ? 2 : 10;

    Pos[] knots = IntStream.range(0, nKnots)
        .mapToObj(i -> new Pos(0, 0))
        .toArray(Pos[]::new);

    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    Set<Pos> tailPositions = new HashSet<>();
    tailPositions.add(knots[knots.length - 1]);

    String line;
    while ((line = br.readLine()) != null) {
      final int nMoves = Integer.parseInt(line.substring(2));

      for (int i = 0; i < nMoves; i++) {
        processMove(line.charAt(0), knots);
        tailPositions.add(knots[knots.length - 1]);
      }
    }

    System.out.println(tailPositions.size());
  }

  // Returns updated knot positions
  private static void processMove(char direction, Pos[] knots) {
    // Move head according to direction
    knots[0] = switch (direction) {
      case 'L' -> new Pos(knots[0].x - 1, knots[0].y);
      case 'R' -> new Pos(knots[0].x + 1, knots[0].y);
      case 'U' -> new Pos(knots[0].x, knots[0].y + 1);
      case 'D' -> new Pos(knots[0].x, knots[0].y - 1);
      default  -> throw new IllegalArgumentException();
    };

    // Update the other knots in order
    for (int knot = 1; knot < knots.length; knot++) {
      int x = knots[knot].x;
      int y = knots[knot].y;

      //    H H H
      //  H . . . H
      //  H . T . H
      //  H . . . H
      //    H H H
      int dx = Math.abs(knots[knot - 1].x - knots[knot].x);
      int dy = Math.abs(knots[knot - 1].y - knots[knot].y);
      if (dx >= 2 || dy >= 2) {
        x += Math.signum(knots[knot - 1].x - knots[knot].x);
        y += Math.signum(knots[knot - 1].y - knots[knot].y);
      }

      knots[knot] = new Pos(x, y);
    }
  }

  private static record Pos(int x, int y) {}
}
