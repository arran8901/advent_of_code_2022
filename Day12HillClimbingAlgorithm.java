import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;

public class Day12HillClimbingAlgorithm {

  public static void main(String[] args) throws IOException {
    part(2);
  }

  private static void part(int part) throws IOException {
    Heightmap heightmap = getHeightmapFromInput();
    char[][] elevations = heightmap.elevations;
    Pos posS = heightmap.posS;
    Pos posE = heightmap.posE;

    // For part 1: stop BFS when reaching posS
    // For part 2: stop BFS when reaching a pos with elevation 'a'
    Predicate<Pos> goalTest = part == 1
        ? pos -> pos.equals(posS)
        : pos -> elevations[pos.row][pos.col] == 'a';

    Queue<Pos> queue = new ArrayDeque<>();
    Map<Pos, Integer> stepsToE = new HashMap<>();

    queue.add(posE);
    stepsToE.put(posE, 0);

    while (!queue.isEmpty()) {
      Pos pos = queue.poll();

      List<Pos> adjacent = List.of(
          new Pos(pos.row + 1, pos.col),
          new Pos(pos.row - 1, pos.col),
          new Pos(pos.row, pos.col + 1),
          new Pos(pos.row, pos.col - 1)
      );
      int stepsFromAdj = stepsToE.get(pos) + 1;

      for (Pos adj : adjacent) {
        if (adj.isValidIn(elevations)
            && isValidStep(elevations, adj, pos)
            && !stepsToE.containsKey(adj)) {
          stepsToE.put(adj, stepsFromAdj);

          if (goalTest.test(adj)) {
            System.out.println(stepsFromAdj);
            return;
          }

          queue.add(adj);
        }
      }
    }
    throw new IllegalArgumentException();
  }

  private static boolean isValidStep(char[][] elevations, Pos from, Pos to) {
    return elevations[to.row][to.col] <= elevations[from.row][from.col] + 1;
  }

  private static Heightmap getHeightmapFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    List<char[]> elevations = new ArrayList<>();
    Pos posS = null, posE = null;

    String line;
    for (int row = 0; (line = br.readLine()) != null; row++) {
      char[] rowChars = new char[line.length()];

      for (int col = 0; col < line.length(); col++) {
        final char c = line.charAt(col);
        if (c == 'S') {
          posS = new Pos(row, col);
          rowChars[col] = 'a';
        } else if (c == 'E') {
          posE = new Pos(row, col);
          rowChars[col] = 'z';
        } else {
          rowChars[col] = c;
        }
      }
      elevations.add(rowChars);
    }
    return new Heightmap(elevations.toArray(char[][]::new), posS, posE);
  }

  private static record Pos(int row, int col) {
    public boolean isValidIn(char[][] elevations) {
      return row >= 0 && row < elevations.length
          && col >= 0 && col < elevations[0].length;
    }
  }

  private static record Heightmap(char[][] elevations, Pos posS, Pos posE) {}
}
