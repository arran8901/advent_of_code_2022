import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Day7NoSpaceLeftOnDevice {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part2() throws IOException {
    final Directory root = buildDirStructureFromInput();

    final PriorityQueue<Directory> dirsBySize = new PriorityQueue<>(Comparator.comparingInt(dir -> dir.size));
    listDirsBySize(root, dirsBySize);
    final int spaceNeeded = root.size - 40000000;

    int size;
    do {
      size = dirsBySize.poll().size;
    } while (size < spaceNeeded);

    System.out.println(size);
  }

  private static void listDirsBySize(Directory curr, PriorityQueue<Directory> dirs) {
    curr.size = 0;
    for (Directory subdirectory : curr.subdirectories.values()) {
      listDirsBySize(subdirectory, dirs);
      curr.size += subdirectory.size;
    }

    curr.size += curr.fileSizes.values().stream()
        .mapToInt(i -> i)
        .sum();

    dirs.add(curr);
  }

  private static void part1() throws IOException {
    final Directory root = buildDirStructureFromInput();

    int sumDirsWithAtMost100000 = sumDirsWithAtMost100000(root)[0];
    System.out.println(sumDirsWithAtMost100000);
  }

  // Return array [sumDirsWithAtMost100000, dirSize]
  private static int[] sumDirsWithAtMost100000(Directory curr) {
    int sumDirsWithAtMost100000 = 0;
    int dirSize = 0;

    for (Directory subdirectory : curr.subdirectories.values()) {
      int[] result = sumDirsWithAtMost100000(subdirectory);
      sumDirsWithAtMost100000 += result[0];
      dirSize += result[1];
    }

    dirSize += curr.fileSizes.values().stream()
        .mapToInt(i -> i)
        .sum();

    if (dirSize <= 100000) {
      sumDirsWithAtMost100000 += dirSize;
    }
    return new int[]{sumDirsWithAtMost100000, dirSize};
  }

  private static Directory buildDirStructureFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    final Directory root = new Directory(null);
    Directory curr = root;

    String line;
    while ((line = br.readLine()) != null) {
      if (line.startsWith("$ cd")) {
        String targetDir = line.substring(5);
        switch (targetDir) {
          case "/":
            curr = root;
            continue;
          case "..":
            curr = curr.parent;
            continue;
          default:
            curr = curr.subdirectories.get(targetDir);
        }

      } else if (line.charAt(0) != '$') {
        int space = line.indexOf(' ');
        String l = line.substring(0, space);
        String r = line.substring(space + 1);

        if (l.equals("dir")) {
          curr.subdirectories.putIfAbsent(r, new Directory(curr));
        } else {
          curr.fileSizes.putIfAbsent(r, Integer.parseInt(l));
        }
      }
    }
    return root;
  }

  private static class Directory {
    private final Directory parent;
    private final Map<String, Directory> subdirectories;
    private final Map<String, Integer> fileSizes;

    private int size;

    public Directory(Directory parent) {
      this.parent = parent;
      subdirectories = new HashMap<>();
      fileSizes = new HashMap<>();
    }
  }
}
