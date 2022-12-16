import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day16ProboscideaVolcanium {

  public static void main(String[] args) throws IOException {
    part2();
  }

  // O(N^3 + M!).
  //   - O(N^3) for Floyd-Warshall
  //   - O(M!)  orderings of interesting valves to consider. We get all 2^M subsets and consider orderings of each
  // N = number of valves, M = number of non-zero flow rate valves.
  // Inspired by u/NORMIE101 (https://www.reddit.com/r/adventofcode/comments/zn6k1l/comment/j0gep14/?utm_source=share&utm_medium=web2x&context=3)
  private static void part2() throws IOException {
    Map<String, Valve> network = getNetworkFromInput();

    Valve[] valves = network.values().toArray(Valve[]::new);
    Map<String, Integer> valveToIndex = IntStream.range(0, network.size()).boxed()
        .collect(Collectors.toMap(i -> valves[i].label, i -> i));

    // Floyd-Warshall
    int[][] shortestPaths = new int[network.size()][network.size()];
    for (int i = 0; i < network.size(); i++) {
      for (int j = 0; j < network.size(); j++) {
        if (valves[i].tunnels.contains(valves[j].label)) {
          shortestPaths[i][j] = 1;
        } else if (i != j) {
          shortestPaths[i][j] = network.size();
        }
      }
    }
    for (int k = 0; k < network.size(); k++) {
      for (int i = 0; i < network.size(); i++) {
        for (int j = 0; j < network.size(); j++) {
          shortestPaths[i][j] = Math.min(
              shortestPaths[i][j],
              shortestPaths[i][k] + shortestPaths[k][j]
          );
        }
      }
    }

    // Get indices of interesting (non-zero flow rate) valves
    int[] interesting = IntStream.range(0, network.size())
        .filter(i -> valves[i].flowRate > 0)
        .toArray();

    int maxPressure = 0;

    // For each subset of interesting valves, find the max pressure among all orderings of visiting the subset's valves
    Map<Integer, Integer> maxPressures = new HashMap<>();
    for (int bitset = 0; bitset < 1 << interesting.length; bitset++) {
      List<Integer> subset = new ArrayList<>();
      int bitsetCpy = bitset;
      for (int bit = 0; bitsetCpy != 0; bit++, bitsetCpy >>>= 1) {
        if ((bitsetCpy & 1) != 0) {
          subset.add(interesting[bit]);
        }
      }

      int maxPressureForSet = maxPressureForSet(subset, valveToIndex.get("AA"), 26, 0, new BitSet(subset.size()), valves, shortestPaths);

      // Find a disjoint set with the highest summed max pressure
      for (Map.Entry<Integer, Integer> otherSetMaxPressure : maxPressures.entrySet()) {
        if ((bitset & otherSetMaxPressure.getKey()) == 0) {
          maxPressure = Math.max(maxPressure, maxPressureForSet + otherSetMaxPressure.getValue());
        }
      }

      maxPressures.put(bitset, maxPressureForSet);
    }

    System.out.println(maxPressure);
  }

  private static int maxPressureForSet(List<Integer> subset, int fromIndex, int timeRemaining, int currentPressure, BitSet usedIndices, Valve[] valves, int[][] shortestPaths) {
    if (subset.size() == 0) {
      return 0;
    }

    int maxPressure = timeRemaining * currentPressure;

    for (int index : subset) {
      int timeToOpen = shortestPaths[fromIndex][index] + 1;

      if (!usedIndices.get(index) && timeToOpen <= timeRemaining) {
        // Go to valve[index] and open it.
        usedIndices.set(index);
        maxPressure = Math.max(maxPressure,
            (timeToOpen * currentPressure)
                + maxPressureForSet(subset, index, timeRemaining - timeToOpen,
                currentPressure + valves[index].flowRate, usedIndices, valves, shortestPaths));
        usedIndices.clear(index);
      }
    }
    return maxPressure;
  }

  // O(N * M! * 2^N).
  // Time complexity upper bounded by dp size:
  //   - N possibilities for currentValve
  //   - 30 possibilities for timeRemaining
  //   - M! possibilities for currentPressure
  //   - 2^N possibilities for openedValves
  // Search pruning optimisations make it manageable.
  // N = number of valves, M = number of non-zero flow rate valves.
  private static void part1() throws IOException {
    Map<String, Valve> network = getNetworkFromInput();

    // 4D memo of the max pressure for a given currentValve, timeRemaining, currentPressure, openedValves
    Map<LabelTimePressureOpened, Integer> dp = new HashMap<>();

    int maxPressure = dfsMaxPressure("AA", 30, network, dp, 0, new HashSet<>());
    System.out.println(maxPressure);
  }

  private static int dfsMaxPressure(String currentValve, int timeRemaining, Map<String, Valve> network,
                                    Map<LabelTimePressureOpened, Integer> dp, int currentPressure, Set<String> openedValves) {
    if (timeRemaining == 0 || openedValves.size() == network.size()) {
      return timeRemaining * currentPressure;
    }
    LabelTimePressureOpened quadruple = new LabelTimePressureOpened(currentValve, timeRemaining, currentPressure, new HashSet<>(openedValves));
    Integer memoValue = dp.get(quadruple);
    if (memoValue != null) {
      return memoValue;
    }

    Valve valve = network.get(currentValve);

    int maxPressure = Integer.MIN_VALUE;

    for (String connected : valve.tunnels) {
      if (valve.flowRate > 0 && timeRemaining >= 2 && !openedValves.contains(currentValve)) {
        // Try moving after opening this valve
        // Bug 1: doesn't allow opening valve in the last minute
        // Bug 2: always opens any unopened valve it finds with >0 flow rate, even though moving might be better
        int newPressure = currentPressure + valve.flowRate;
        openedValves.add(currentValve);
        maxPressure = Math.max(maxPressure,
            currentPressure + newPressure + dfsMaxPressure(connected, timeRemaining - 2, network, dp, newPressure, openedValves));
        openedValves.remove(currentValve);
      } else {
        // Try moving without opening this valve
        maxPressure = Math.max(maxPressure,
            currentPressure + dfsMaxPressure(connected, timeRemaining - 1, network, dp, currentPressure, openedValves));
      }
    }
    dp.put(quadruple, maxPressure);
    return maxPressure;
  }

  private static Map<String, Valve> getNetworkFromInput() throws IOException {
    Map<String, Valve> network = new HashMap<>();

    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    Pattern p = Pattern.compile(
        "Valve (?<label>\\w+) has flow rate=(?<flowRate>\\d+); tunnels? leads? to valves? (?<tunnels>(\\w+)(, (\\w+))*)");

    String line;
    while ((line = br.readLine()) != null) {
      Matcher m = p.matcher(line);
      m.matches();
      String label = m.group("label");
      int flowRate = Integer.parseInt(m.group("flowRate"));
      Set<String> tunnels = Arrays.stream(m.group("tunnels").split(", "))
          .collect(Collectors.toSet());
      network.put(label, new Valve(label, flowRate, tunnels));
    }

    return network;
  }

  private static class Valve {
    private final String label;
    private final int flowRate;
    private final Set<String> tunnels;

    public Valve(String label, int flowRate, Set<String> tunnels) {
      this.label = label;
      this.flowRate = flowRate;
      this.tunnels = tunnels;
    }

    @Override
    public String toString() {
      return "Valve{" +
          "label='" + label + '\'' +
          ", flowRate=" + flowRate +
          ", tunnels=" + tunnels +
          '}';
    }
  }

  private static record LabelTimePressureOpened(String label, int timeRemaining, int currentPressure, Set<String> openedValves) {}
}
