import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15BeaconExclusionZone {

  public static void main(String[] args) throws IOException {
    part2();
  }

  // O(H * S log S).
  // findAndMergeCoveredIntervals is O(S log S); done H times. Calculating tuningFreq is O(1).
  // H = height (range of y values), S = number of sensors.
  private static void part2() throws IOException {
    List<SensorAndBeacon> map = getMapFromInput();

    final int MIN = 0;
    final int MAX = 4000000;

    Pos distressBeacon = null;
    for (int y = MIN; distressBeacon == null; y++) {
      assert y <= MAX;

      List<int[]> mergedIntervals = findAndMergeCoveredIntervals(map, y, true);

      if (mergedIntervals.get(0)[0] > MIN) {
        distressBeacon = new Pos(MIN, y);
      } else if (mergedIntervals.get(mergedIntervals.size() - 1)[1] < MAX) {
        distressBeacon = new Pos(MAX, y);
      } else {
        for (int i = 1; i < mergedIntervals.size(); i++) {
          if (mergedIntervals.get(i)[0] > mergedIntervals.get(i - 1)[1] + 1) {
            distressBeacon = new Pos(mergedIntervals.get(i)[0] - 1, y);
          }
        }
      }
    }

    System.out.println(distressBeacon);

    long tuningFreq = distressBeacon.x * 4000000L + distressBeacon.y;
    System.out.println(tuningFreq);
  }

  // O(S log S).
  // findAndMergeCoveredIntervals is O(S log S). Counting is O(S).
  // S = number of sensors.
  private static void part1() throws IOException {
    final int rowToCheck = 2_000_000;
    List<SensorAndBeacon> map = getMapFromInput();
    List<int[]> mergedIntervals = findAndMergeCoveredIntervals(map, rowToCheck, false);

    // Count total number of positions covered
    int totalCovered = mergedIntervals.stream()
        .mapToInt(interval -> interval[1] - interval[0] + 1)
        .sum();
    System.out.println(totalCovered);
  }

  // O(S log S).
  // Building coveredIntervals is O(S). Sorting is O(S log S). Merging is O(S).
  private static List<int[]> findAndMergeCoveredIntervals(List<SensorAndBeacon> map, int rowToCheck,
                                                          boolean includeDiscoveredBeacons) {
    List<int[]> coveredIntervals = new ArrayList<>();

    for (SensorAndBeacon sensorAndBeacon : map) {
      Pos sensor = sensorAndBeacon.sensor;
      Pos beacon = sensorAndBeacon.beacon;

      int distSensorToBeacon = Pos.manhattanDistance(sensor, beacon);
      int distSensorToRow = Math.abs(sensor.y - rowToCheck);
      int diff = distSensorToBeacon - distSensorToRow;

      if (diff < 0) {
        continue;
      }
      int leftmostCovered = sensor.x - diff;
      int rightmostCovered = sensor.x + diff;
      if (!includeDiscoveredBeacons && beacon.y == rowToCheck) {
        // Need to exclude the beacon's position
        if (leftmostCovered < beacon.x) {
          coveredIntervals.add(new int[]{leftmostCovered, beacon.x - 1});
        } else if (rightmostCovered > beacon.x) {
          coveredIntervals.add(new int[]{beacon.x + 1, rightmostCovered});
        }
      } else {
        coveredIntervals.add(new int[]{leftmostCovered, rightmostCovered});
      }
    }
    coveredIntervals.sort(Comparator.comparingInt(interval -> interval[0]));

    // Merge overlapping covered intervals
    List<int[]> mergedIntervals = new ArrayList<>(coveredIntervals.size());
    for (int[] interval : coveredIntervals) {
      int[] prevInterval = mergedIntervals.isEmpty() ? null : mergedIntervals.get(mergedIntervals.size() - 1);

      if (prevInterval == null || interval[0] > prevInterval[1]) {
        mergedIntervals.add(interval);
      } else {
        prevInterval[1] = Math.max(prevInterval[1], interval[1]);
      }
    }
    return mergedIntervals;
  }
  //    . . . . . . S . . . . . . .
  //    . . . . . . . . . . . . . .
  //    . . . . . . . . . . . B . .
  //    . . . . . . . . . . . . . .
  // 10 . . . . . . . . . . . . . .

  private static List<SensorAndBeacon> getMapFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    final Pattern linePat = Pattern.compile(
        "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)");

    List<SensorAndBeacon> result = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      Matcher m = linePat.matcher(line);
      m.matches();
      int sensorX = Integer.parseInt(m.group(1));
      int sensorY = Integer.parseInt(m.group(2));
      int beaconX = Integer.parseInt(m.group(3));
      int beaconY = Integer.parseInt(m.group(4));
      result.add(new SensorAndBeacon(new Pos(sensorX, sensorY), new Pos(beaconX, beaconY)));
    }
    return result;
  }

  private static record Pos(int x, int y) {
    static int manhattanDistance(Pos pos1, Pos pos2) {
      return Math.abs(pos2.x - pos1.x) + Math.abs(pos2.y - pos1.y);
    }
  }

  private static record SensorAndBeacon(Pos sensor, Pos beacon) {}
}
