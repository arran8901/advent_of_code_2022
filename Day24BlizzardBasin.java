import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day24BlizzardBasin {

  public static void main(String[] args) throws IOException {
    part(2);
  }

  private static void part(int part) throws IOException {
    BlizzardMap map = getBlizzardMapFromInput();
    final int N = map.height - 2;
    final int M = map.width - 2;
    Pos start = new Pos(0, 1);
    Pos end = new Pos(map.height - 1, map.width - 2);
    Set<Pos> walls = map.walls;
    Map<Pos, Character> blizzards = map.blizzards;

    // Blizzards cycle back to original positions after lcm(N, M) minutes
    int cycleLength = lcm(N, M);
    // iterations[i] = blizzard positions after i minutes
    Set<Pos>[] iterations = new Set[cycleLength];
    // Precompute blizzard positions for each iteration; put them into HashSets for fast lookup
    for (int it = 0; it < cycleLength; it++) {
      iterations[it] = new HashSet<>();

      for (Map.Entry<Pos, Character> blizzard : blizzards.entrySet()) {
        int row = blizzard.getKey().row;
        int col = blizzard.getKey().col;
        char dir = blizzard.getValue();

        switch (dir) {
          case '>' -> col = (col + it - 1) % M + 1;
          case '<' -> col = (col - it + cycleLength - 1) % M + 1;
          case 'v' -> row = (row + it - 1) % N + 1;
          case '^' -> row = (row - it + cycleLength - 1) % N + 1;
        }
        iterations[it].add(new Pos(row, col));
      }
    }

    int mins = bfsShortestPath(new PosAndMinute(start, 0), end, walls, iterations);
    if (part == 2) {
      mins = bfsShortestPath(new PosAndMinute(end, mins), start, walls, iterations);
      mins = bfsShortestPath(new PosAndMinute(start, mins), end, walls, iterations);
    }
    System.out.println(mins);
  }

  private static int bfsShortestPath(PosAndMinute from, Pos to, Set<Pos> walls, Set<Pos>[] iterations) {
    final int cycleLength = iterations.length;
    Queue<PosAndMinute> queue = new ArrayDeque<>();
    Set<PosAndIt> visited = new HashSet<>();
    queue.add(from);
    visited.add(new PosAndIt(from.pos, from.minute % cycleLength));

    while (!queue.isEmpty()) {
      PosAndMinute posAndMinute = queue.poll();
      Pos pos = posAndMinute.pos;
      int nextMinute = posAndMinute.minute + 1;
      int nextIt = nextMinute % cycleLength;

      for (Pos nextPos : List.of(
          pos,
          new Pos(pos.row - 1, pos.col),
          new Pos(pos.row + 1, pos.col),
          new Pos(pos.row, pos.col - 1),
          new Pos(pos.row, pos.col + 1)
      )) {
        if (!walls.contains(nextPos) && !iterations[nextIt].contains(nextPos) && !visited.contains(new PosAndIt(nextPos, nextIt))) {
          if (nextPos.equals(to)) {
            return nextMinute;
          }
          visited.add(new PosAndIt(nextPos, nextIt));
          queue.add(new PosAndMinute(nextPos, nextMinute));
        }
      }
    }
    throw new RuntimeException("Answer not found");
  }

  private static int gcd(int a, int b) {
    if (b == 0) return a;
    return gcd(b, a % b);
  }

  private static int lcm(int a, int b) {
    return a * b / gcd(a, b);
  }

  private static BlizzardMap getBlizzardMapFromInput() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    Set<Pos> walls = new HashSet<>();
    Map<Pos, Character> blizzards = new HashMap<>();
    int width = 0;

    String line;
    int row = 0;
    while ((line = br.readLine()) != null) {
      width = line.length();

      for (int col = 0; col < line.length(); col++) {
        char c = line.charAt(col);
        if (c == '#') {
          walls.add(new Pos(row, col));

        } else if (c != '.') {
          blizzards.put(new Pos(row, col), c);
        }
      }

      row++;
    }

    walls.add(new Pos(-1, 1));
    walls.add(new Pos(row, width - 2));

    return new BlizzardMap(width, row, walls, blizzards);
  }

  private static record BlizzardMap(
      int width,
      int height,
      Set<Pos> walls,
      Map<Pos, Character> blizzards
  ) {}

  private static record Pos(int row, int col) {}

  private static record PosAndMinute(Pos pos, int minute) {}

  private static record PosAndIt(Pos pos, int it) {}
}
