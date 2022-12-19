import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day19NotEnoughMinerals {

  public static void main(String[] args) throws IOException, InterruptedException {
    part2();
  }

  private static void part2() throws IOException, InterruptedException {
    Blueprint[] blueprints = getBlueprintsFromInput();
    int numBlueprints = Math.min(3, blueprints.length);

    Thread[] threads = new Thread[3];
    int[] maxGeodes = new int[3];

    for (int i = 0; i < numBlueprints; i++) {
      final int I = i;
      threads[i] = new Thread(() -> {
        maxGeodes[I] = maxGeodes(blueprints[I], 32, new Resources(0, 0, 0, 0, 1, 0, 0, 0));
        System.out.println("Max geodes for blueprint " + (I + 1) + ": " + maxGeodes[I]);
      });
      threads[i].start();
    }
    int product = 1;
    for (int i = 0; i < numBlueprints; i++) {
      threads[i].join();
      product *= maxGeodes[i];
    }
    System.out.println(product);
  }

  private static void part1() throws IOException {
    Blueprint[] blueprints = getBlueprintsFromInput();

    int qualitySum = 0;
    for (int id = 1; id <= blueprints.length; id++) {
      Blueprint bp = blueprints[id - 1];
      int r = maxGeodes(bp, 24, new Resources(0, 0, 0, 0, 1, 0, 0, 0));
      System.out.println("Max geodes for blueprint " + id + ": " + r);
      qualitySum += id * r;
    }
    System.out.println(qualitySum);
  }

  // Assumes at most one robot constructed per minute
  private static int maxGeodes(Blueprint bp, int minsLeft, Resources ress) {
    if (minsLeft == 0) return ress.geodeCount;

    int maxGeodes = ress.geodeCount + minsLeft * ress.geodeRate;

    int oreNeeded;
    int minsNeeded;

    // Try making geode robot next
    if (ress.obsidianRate > 0) {
      oreNeeded = bp.geodeRobotOreCost - ress.oreCount;
      int obsidianNeeded = bp.geodeRobotObsidianCost - ress.obsidianCount;
      minsNeeded = Math.max(0, Math.max(
          (oreNeeded + ress.oreRate - 1) / ress.oreRate,
          (obsidianNeeded + ress.obsidianRate - 1) / ress.obsidianRate
      )) + 1;
      if (minsNeeded <= minsLeft) {
        int rec = maxGeodes(bp, minsLeft - minsNeeded,
            ResourcesBuilder.fromResource(ress)
                .elapseTime(minsNeeded)
                .makeGeodeRobot(bp.geodeRobotOreCost, bp.geodeRobotObsidianCost)
                .build()
        );
        maxGeodes = Math.max(maxGeodes, rec);

        // Optimisation: if we could make a geode robot immediately, return immediately and
        // do not consider making any other robots.
        // This is only necessary for part 2.
        if (minsNeeded == 1) return maxGeodes;
      }
    }

    // Try making ore robot next
    oreNeeded = bp.oreRobotOreCost - ress.oreCount;
    minsNeeded = Math.max(0, (oreNeeded + ress.oreRate - 1) / ress.oreRate) + 1;
    if (minsNeeded <= minsLeft) {
      int rec = maxGeodes(bp, minsLeft - minsNeeded,
          ResourcesBuilder.fromResource(ress)
              .elapseTime(minsNeeded)
              .makeOreRobot(bp.oreRobotOreCost)
              .build()
      );
      maxGeodes = Math.max(maxGeodes, rec);
    }

    // Try making clay robot next
    oreNeeded = bp.clayRobotOreCost - ress.oreCount;
    minsNeeded = Math.max(0, (oreNeeded + ress.oreRate - 1) / ress.oreRate) + 1;
    if (minsNeeded <= minsLeft) {
      int rec = maxGeodes(bp, minsLeft - minsNeeded,
          ResourcesBuilder.fromResource(ress)
              .elapseTime(minsNeeded)
              .makeClayRobot(bp.clayRobotOreCost)
              .build()
      );
      maxGeodes = Math.max(maxGeodes, rec);
    }

    // Try making obsidian robot next
    if (ress.clayRate > 0) {
      oreNeeded = bp.obsidianRobotOreCost - ress.oreCount;
      int clayNeeded = bp.obsidianRobotClayCost - ress.clayCount;
      minsNeeded = Math.max(0, Math.max(
          (oreNeeded + ress.oreRate - 1) / ress.oreRate,
          (clayNeeded + ress.clayRate - 1) / ress.clayRate
      )) + 1;
      if (minsNeeded <= minsLeft) {
        int rec = maxGeodes(bp, minsLeft - minsNeeded,
            ResourcesBuilder.fromResource(ress)
                .elapseTime(minsNeeded)
                .makeObsidianRobot(bp.obsidianRobotOreCost, bp.obsidianRobotClayCost)
                .build()
        );
        maxGeodes = Math.max(maxGeodes, rec);
      }
    }

    return maxGeodes;
  }

  private static record Resources(
      int oreCount, int clayCount, int obsidianCount, int geodeCount,
      int oreRate, int clayRate, int obsidianRate, int geodeRate
  ) {}

  private static class ResourcesBuilder {
    private int oreCount;
    private int clayCount;
    private int obsidianCount;
    private int geodeCount;
    private int oreRate;
    private int clayRate;
    private int obsidianRate;
    private int geodeRate;

    public static ResourcesBuilder fromResource(Resources ress) {
      return new ResourcesBuilder(ress.oreCount, ress.clayCount, ress.obsidianCount, ress.geodeCount,
          ress.oreRate, ress.clayRate, ress.obsidianRate, ress.geodeRate);
    }

    public ResourcesBuilder(int oreCount, int clayCount, int obsidianCount, int geodeCount,
                            int oreRate, int clayRate, int obsidianRate, int geodeRate) {
      this.oreCount = oreCount;
      this.clayCount = clayCount;
      this.obsidianCount = obsidianCount;
      this.geodeCount = geodeCount;
      this.oreRate = oreRate;
      this.clayRate = clayRate;
      this.obsidianRate = obsidianRate;
      this.geodeRate = geodeRate;
    }

    public ResourcesBuilder elapseTime(int mins) {
      oreCount += oreRate * mins;
      clayCount += clayRate * mins;
      obsidianCount += obsidianRate * mins;
      geodeCount += geodeRate * mins;
      return this;
    }

    public ResourcesBuilder makeOreRobot(int oreCost) {
      oreCount -= oreCost;
      oreRate++;
      return this;
    }

    public ResourcesBuilder makeClayRobot(int oreCost) {
      oreCount -= oreCost;
      clayRate++;
      return this;
    }

    public ResourcesBuilder makeObsidianRobot(int oreCost, int clayCost) {
      oreCount -= oreCost;
      clayCount -= clayCost;
      obsidianRate++;
      return this;
    }

    public ResourcesBuilder makeGeodeRobot(int oreCost, int obsidianCost) {
      oreCount -= oreCost;
      obsidianCount -= obsidianCost;
      geodeRate++;
      return this;
    }

    public Resources build() {
      return new Resources(oreCount, clayCount, obsidianCount, geodeCount,
          oreRate, clayRate, obsidianRate, geodeRate);
    }
  }

  private static Blueprint[] getBlueprintsFromInput() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String regex = """
        Blueprint \\d+: \
        Each ore robot costs (?<oreRobotOreCost>\\d+) ore. \
        Each clay robot costs (?<clayRobotOreCost>\\d+) ore. \
        Each obsidian robot costs (?<obsidianRobotOreCost>\\d+) ore and (?<obsidianRobotClayCost>\\d+) clay. \
        Each geode robot costs (?<geodeRobotOreCost>\\d+) ore and (?<geodeRobotObsidianCost>\\d+) obsidian.\
        """;
    Pattern p = Pattern.compile(regex);

    List<Blueprint> blueprints = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      Matcher m = p.matcher(line);
      m.matches();
      int[] costs = IntStream.range(1, m.groupCount() + 1)
          .mapToObj(m::group)
          .mapToInt(Integer::parseInt)
          .toArray();
      blueprints.add(new Blueprint(costs));
    }
    return blueprints.toArray(Blueprint[]::new);
  }

  private static record Blueprint(
      int oreRobotOreCost,
      int clayRobotOreCost,
      int obsidianRobotOreCost, int obsidianRobotClayCost,
      int geodeRobotOreCost, int geodeRobotObsidianCost
  ) {
    public Blueprint(int[] costs) {
      this(costs[0], costs[1], costs[2], costs[3], costs[4], costs[5]);
    }
  }
}
