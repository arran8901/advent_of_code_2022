import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day17PyroclasticFlow {

  public static void main(String[] args) {
    part(2);
  }

  // Cycle detection idea from u/scratchisthebest (https://www.reddit.com/r/adventofcode/comments/znykq2/comment/j0kc9qp/?utm_source=share&utm_medium=web2x&context=3)
  // but cache key (configuration) uses the entire top of chamber down to the lowestHighestFilledY.
  // This makes this method robust to rocks sliding sideways under overhangs.
  private static void part(int part) {
    final long NUM_ROCKS = part == 1 ? 2022L : 1000000000000L;

    String jetPattern = getJetPatternFromInput();
    Set<Pos> filled = new HashSet<>();
    long highestFilledY = 0;
    long[] highestFilledYPerX = new long[7];
    long lowestHighestFilledY = 0;
    Map<Configuration, Snapshot> seenConfigurations = new HashMap<>();

    int jetIndex = 0;
    int rockVariant = 0;

    boolean fastForwarded = false;
    long fastForwardIncrease = 0;

    for (long rockNumber = 0; rockNumber < NUM_ROCKS; rockNumber++) {
      final long normaliser = lowestHighestFilledY;
      Set<Pos> chamberTop = filled.stream()
          .map(pos -> new Pos(pos.x, pos.y - normaliser))
          .collect(Collectors.toSet());
      Configuration configuration = new Configuration(jetIndex, rockVariant, chamberTop);

      if (!fastForwarded) {
        // Attempt to find cycle
        Snapshot snapshot;
        if ((snapshot = seenConfigurations.get(configuration)) != null) {
          // Cycle found, fast forward
          long cycleRockCount = rockNumber - snapshot.rockNumber;
          long timesToRepeatCycle = (NUM_ROCKS - rockNumber - 1) / cycleRockCount;

          long cycleYIncrease = highestFilledY - snapshot.highestFilledY;
          fastForwardIncrease = timesToRepeatCycle * cycleYIncrease;

          rockNumber += timesToRepeatCycle * cycleRockCount;
          fastForwarded = true;
        } else {
          seenConfigurations.put(configuration, new Snapshot(rockNumber, highestFilledY));
        }
      }

      Rock rock = Rock.generateVariant(rockVariant, CHAMBER_MIN_X + 2, highestFilledY + 4);
      rockVariant = (rockVariant + 1) % 5;

      boolean landed = false;
      do {
        char jetDirection = jetPattern.charAt(jetIndex);
        jetIndex = (jetIndex + 1) % jetPattern.length();

        // Try to move sideways
        rock.tryMove(pos -> new Pos(pos.x + (jetDirection == '<' ? -1 : 1), pos.y), filled);

        // Try to move down; land rock if unable to
        if (!rock.tryMove(pos -> new Pos(pos.x, pos.y - 1), filled)) {
          for (Pos pos : rock.occupied) {
            filled.add(pos);
            highestFilledY = Math.max(highestFilledY, pos.y);
            highestFilledYPerX[(int) pos.x] = Math.max(highestFilledYPerX[(int) pos.x], pos.y);
          }
          lowestHighestFilledY = Arrays.stream(highestFilledYPerX).min().getAsLong();
          landed = true;
        }
      } while (!landed);

      // Prune filled set to chamber top
      for (Iterator<Pos> it = filled.iterator(); it.hasNext();) {
        Pos filledPos = it.next();
        if (filledPos.y < lowestHighestFilledY) {
          it.remove();
        }
      }
    }

    // Uncomment for display

//    final int DISPLAY_HEIGHT = (int) highestFilledY;
//    char[][] chamber = new char[DISPLAY_HEIGHT + 1][7];
//    for (int y = 1; y <= DISPLAY_HEIGHT; y++) {
//      for (int x = 0; x < 7; x++) {
//        chamber[y][x] = filled.contains(new Pos(x, y)) ? '#' : '.';
//      }
//    }
//    for (int x = 0; x < 7; x++) chamber[0][x] = '-';
//    for (int y = DISPLAY_HEIGHT; y >= 0; y--) {
//      StringBuilder sb = new StringBuilder();
//      sb.append(String.format("%6s", y))
//          .append(" ")
//          .append('|');
//      for (char c : chamber[y]) sb.append(c);
//      sb.append('|');
//      System.out.println(sb);
//    }

    System.out.println(highestFilledY + fastForwardIncrease);
  }
  // y
  // 4 ..@@@@.
  // 3 .......
  // 2 .......
  // 1 .......
  // 0 -------
  //   0123456 x

  private static String getJetPatternFromInput() {
    try (Scanner sc = new Scanner(System.in)) {
      return sc.nextLine();
    }
  }

  private static record Pos(long x, long y) {}

  private static record Configuration(int jetIndex, int rockVariant, Set<Pos> chamberTop) {}
  private static record Snapshot(long rockNumber, long highestFilledY) {}

  private static final long CHAMBER_MIN_X = 0;
  private static final long CHAMBER_MAX_X = 6;
  private static final long CHAMBER_MIN_Y = 1;

  private static abstract class Rock {
    protected List<Pos> occupied;

    protected Rock(List<Pos> occupied) {
      this.occupied = occupied;
    }

    public static Rock generateVariant(int variant, long baseX, long baseY) {
      return switch (variant) {
        case 0 -> new HLineRock(baseX, baseY);
        case 1 -> new PlusRock(baseX, baseY);
        case 2 -> new LRock(baseX, baseY);
        case 3 -> new VLineRock(baseX, baseY);
        case 4 -> new SquareRock(baseX, baseY);
        default -> throw new IllegalArgumentException();
      };
    }

    public boolean tryMove(Function<Pos, Pos> movement, Set<Pos> filled) {
      List<Pos> newOccupied = occupied.stream()
          .map(movement)
          .toList();

      if (newOccupied.stream().anyMatch(pos ->
          pos.x < CHAMBER_MIN_X
          || pos.x > CHAMBER_MAX_X
          || pos.y < CHAMBER_MIN_Y
          || filled.contains(pos))) {
        return false;
      }
      occupied = newOccupied;
      return true;
    }
  }

  private static class HLineRock extends Rock {
    HLineRock(long baseX, long baseY) {
      super(Arrays.asList(
          new Pos(baseX, baseY),
          new Pos(baseX + 1, baseY),
          new Pos(baseX + 2, baseY),
          new Pos(baseX + 3, baseY)
      ));
    }
  }

  private static class PlusRock extends Rock {
    PlusRock(long baseX, long baseY) {
      super(Arrays.asList(
          new Pos(baseX, baseY + 1),
          new Pos(baseX + 1, baseY + 1),
          new Pos(baseX + 1, baseY + 2),
          new Pos(baseX + 1, baseY),
          new Pos(baseX + 2, baseY + 1)
      ));
    }
  }

  private static class LRock extends Rock {
    LRock(long baseX, long baseY) {
      super(Arrays.asList(
          new Pos(baseX, baseY),
          new Pos(baseX + 1, baseY),
          new Pos(baseX + 2, baseY),
          new Pos(baseX + 2, baseY + 1),
          new Pos(baseX + 2, baseY + 2)
      ));
    }
  }

  private static class VLineRock extends Rock {
    VLineRock(long baseX, long baseY) {
      super(Arrays.asList(
          new Pos(baseX, baseY),
          new Pos(baseX, baseY + 1),
          new Pos(baseX, baseY + 2),
          new Pos(baseX, baseY + 3)
      ));
    }
  }

  private static class SquareRock extends Rock {
    SquareRock(long baseX, long baseY) {
      super(Arrays.asList(
          new Pos(baseX, baseY),
          new Pos(baseX, baseY + 1),
          new Pos(baseX + 1, baseY),
          new Pos(baseX + 1, baseY + 1)
      ));
    }
  }
}
