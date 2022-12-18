import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day18BoilingBoulders {

  public static void main(String[] args) throws IOException {
    part(2);
  }

  // Part 1: O(n). For each lava cube check its 6 sides.
  // Part 2: O(X * Y * Z). trappedSides is O(X * Y * Z) in total across all calls. It does not re-explore
  //         visited cubes; so, each of the X*Y*Z cubes are only processed once. Each cube involves O(1) work.
  // n = number of lava cubes
  // X, Y, Z = range of possible x,y,z of lava cubes (xMax - xMin, etc.)
  private static void part(int part) throws IOException {
    List<Cube> lavaCubes = getLavaCubesFromInput();
    Map<Cube, Integer> sidesExposed = new HashMap<>();

    for (Cube lavaCube : lavaCubes) {
      int exposed = 6;
      for (Cube adj : lavaCube.adjacent()) {
        if (sidesExposed.containsKey(adj)) {
          sidesExposed.compute(adj, (k, v) -> v - 1);
          exposed--;
        }
      }
      sidesExposed.put(lavaCube, exposed);

      if (lavaCube.x < xMin) xMin = lavaCube.x;
      else if (lavaCube.x > xMax) xMax = lavaCube.x;

      if (lavaCube.y < yMin) yMin = lavaCube.y;
      else if (lavaCube.y > yMax) yMax = lavaCube.y;

      if (lavaCube.z < zMin) zMin = lavaCube.z;
      else if (lavaCube.z > zMax) zMax = lavaCube.z;
    }

    int totalSidesExposed = sidesExposed.values().stream()
        .mapToInt(i -> i)
        .sum();

    if (part == 2) {
      Set<Cube> visited = new HashSet<>();

      for (int x = xMin + 1; x < xMax; x++) {
        for (int y = yMin + 1; y < yMax; y++) {
          for (int z = zMin + 1; z < zMax; z++) {
            int trappedSides = trappedSides(new Cube(x, y, z), sidesExposed.keySet(), visited);
            if (trappedSides > 0) {
              totalSidesExposed -= trappedSides;
            }
          }
        }
      }
    }

    System.out.println(totalSidesExposed);
  }

  // DFS air pockets from `pos`. Return:
  //   -1  if air pocket is exposed (not trapped)
  //    0  if `pos` is lava
  //    0  if air pocket has been explored (`pos` has been visited)
  //   otherwise the number of trapped sides for the air pocket, after marking all its cubes as visited
  private static int trappedSides(Cube pos, Set<Cube> lavaCubes, Set<Cube> visited) {
    if (pos.x == xMin || pos.x == xMax
        || pos.y == yMin || pos.y == yMax
        || pos.z == zMin || pos.z == zMax) {
      return -1;
    }
    boolean visitedPos = !visited.add(pos);
    if (visitedPos || lavaCubes.contains(pos)) return 0;

    boolean exposed = false;
    int trappedSides = 0;

    for (Cube adj : pos.adjacent()) {
      if (lavaCubes.contains(adj)) {
        // adj has lava
        trappedSides++;
      } else {
        // adj is air; recurse
        int trappedFromAdj = trappedSides(adj, lavaCubes, visited);
        if (trappedFromAdj == -1) {
          exposed = true;
        }
        trappedSides += trappedFromAdj;
      }
    }

    return exposed ? -1 : trappedSides;
  }

  private static List<Cube> getLavaCubesFromInput() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    List<Cube> lavaCubes = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      int[] xyz = Arrays.stream(line.split(","))
          .mapToInt(Integer::parseInt)
          .toArray();
      lavaCubes.add(new Cube(xyz[0], xyz[1], xyz[2]));
    }

    return lavaCubes;
  }

  private record Cube(int x, int y, int z) {

    public List<Cube> adjacent() {
      return List.of(
          new Cube(x - 1, y, z),
          new Cube(x + 1, y, z),
          new Cube(x, y - 1, z),
          new Cube(x, y + 1, z),
          new Cube(x, y, z - 1),
          new Cube(x, y, z + 1)
      );
    }
  }

  private static int xMin = Integer.MAX_VALUE;
  private static int yMin = Integer.MAX_VALUE;
  private static int zMin = Integer.MAX_VALUE;
  private static int xMax = Integer.MIN_VALUE;
  private static int yMax = Integer.MIN_VALUE;
  private static int zMax = Integer.MIN_VALUE;
}
