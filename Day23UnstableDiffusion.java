import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day23UnstableDiffusion {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part2() throws IOException {
    Set<Pos> elves = getElfPositionsFromInput();

    char[] directions = {'N', 'S', 'W', 'E'};
    int currDirection = 0;

    boolean elfMoved;
    int roundNum = 0;

    do {
      elfMoved = false;
      roundNum++;
      // map[new position -> list of positions of old elves wanting to move there]
      Map<Pos, List<Pos>> propositions = new HashMap<>();

      // First half: generate all propositions
      for (Pos elfPos : elves) {
        Pos newPos = propose(elfPos, directions, currDirection, elves);

        if (newPos.equals(elfPos)) continue;
        propositions.computeIfAbsent(newPos, k -> new ArrayList<>(directions.length)).add(elfPos);
        elfMoved = true;
      }

      // Second half: move elves with no conflicting propositions
      for (Map.Entry<Pos, List<Pos>> proposition : propositions.entrySet()) {
        Pos destPos = proposition.getKey();
        List<Pos> elvesWantingToMove = proposition.getValue();
        if (elvesWantingToMove.size() > 1) {
          // Conflicting proposition; none of them move
          continue;
        }
        Pos elfWantingToMove = elvesWantingToMove.get(0);
        elves.remove(elfWantingToMove);
        elves.add(destPos);
      }

      // Advance currDirection
      currDirection = (currDirection + 1) % directions.length;
    } while (elfMoved);

    System.out.println(roundNum);
  }

  private static void part1() throws IOException {
    final int NUM_ROUNDS = 10;
    Set<Pos> elves = getElfPositionsFromInput();

    char[] directions = {'N', 'S', 'W', 'E'};
    int currDirection = 0;

    for (int round = 0; round < NUM_ROUNDS; round++) {
      // map[new position -> list of positions of old elves wanting to move there]
      Map<Pos, List<Pos>> propositions = new HashMap<>();

      // First half: generate all propositions
      for (Pos elfPos : elves) {
        Pos newPos = propose(elfPos, directions, currDirection, elves);

        if (newPos.equals(elfPos)) continue;
        propositions.computeIfAbsent(newPos, k -> new ArrayList<>(directions.length)).add(elfPos);
      }

      // Second half: move elves with no conflicting propositions
      for (Map.Entry<Pos, List<Pos>> proposition : propositions.entrySet()) {
        Pos destPos = proposition.getKey();
        List<Pos> elvesWantingToMove = proposition.getValue();
        if (elvesWantingToMove.size() > 1) {
          // Conflicting proposition; none of them move
          continue;
        }
        Pos elfWantingToMove = elvesWantingToMove.get(0);
        elves.remove(elfWantingToMove);
        elves.add(destPos);
      }

      // Advance currDirection
      currDirection = (currDirection + 1) % directions.length;
    }

    // Get boundary rectangle
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxY = Integer.MIN_VALUE;
    for (Pos elfPos : elves) {
      if (elfPos.x < minX)
        minX = elfPos.x;
      else if (elfPos.x > maxX)
        maxX = elfPos.x;
      if (elfPos.y < minY)
        minY = elfPos.y;
      else if (elfPos.y > maxY)
        maxY = elfPos.y;
    }
    int rectangleNumTiles = (maxX - minX + 1) * (maxY - minY + 1);
    int rectangleNumEmptyTiles = rectangleNumTiles - elves.size();

    System.out.println(rectangleNumEmptyTiles);
  }

  private static Pos propose(Pos elfPos, char[] directions, int currDirection, Set<Pos> elves) {
    Pos[] adjacent = {
        new Pos(elfPos.x - 1, elfPos.y - 1),  // NW
        new Pos(elfPos.x, elfPos.y - 1),         // N
        new Pos(elfPos.x + 1, elfPos.y - 1),  // NE
        new Pos(elfPos.x + 1, elfPos.y),         // E
        new Pos(elfPos.x + 1, elfPos.y + 1),  // SE
        new Pos(elfPos.x, elfPos.y + 1),         // S
        new Pos(elfPos.x - 1, elfPos.y + 1),  // SW
        new Pos(elfPos.x - 1, elfPos.y),         // W
    };
    BitSet adjacentIsEmpty = new BitSet(adjacent.length);
    for (int i = 0; i < adjacent.length; i++) {
      adjacentIsEmpty.set(i, !elves.contains(adjacent[i]));
    }

    if (adjacentIsEmpty.cardinality() == adjacent.length) {
      // Don't move
      return elfPos;
    }

    // Return the first valid direction from currDirection
    for (int i = 0; i < directions.length; i++) {
      int[] toCheck = adjacentsToCheck.get(directions[currDirection]);
      if (Arrays.stream(toCheck).allMatch(adjacentIsEmpty::get)) {
        return adjacent[toCheck[0]];
      }
      currDirection = (currDirection + 1) % directions.length;
    }
    // Otherwise, can't move
    return elfPos;
  }

  private static final Map<Character, int[]> adjacentsToCheck = Map.of(
      'N', new int[]{1, 0, 2},
      'S', new int[]{5, 4, 6},
      'W', new int[]{7, 0, 6},
      'E', new int[]{3, 2, 4}
  );

  private static Set<Pos> getElfPositionsFromInput() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    Set<Pos> elves = new HashSet<>();

    String line;
    int y = 0;
    while ((line = br.readLine()) != null) {
      for (int x = 0; x < line.length(); x++) {
        if (line.charAt(x) == '#') {
          elves.add(new Pos(x, y));
        }
      }
      y++;
    }
    return elves;
  }

  private static record Pos(int x, int y) {}
}
