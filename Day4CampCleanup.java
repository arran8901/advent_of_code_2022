import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Day4CampCleanup {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part1() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int totalFullyContains = 0;

    String line;
    while ((line = br.readLine()) != null) {
      int commaIdx = line.indexOf(',');
      int dash1Idx = line.indexOf('-');
      int dash2Idx = line.indexOf('-', commaIdx + 1);

      int l1 = Integer.parseInt(line.substring(0, dash1Idx));
      int r1 = Integer.parseInt(line.substring(dash1Idx + 1, commaIdx));
      int l2 = Integer.parseInt(line.substring(commaIdx + 1, dash2Idx));
      int r2 = Integer.parseInt(line.substring(dash2Idx + 1));

      if (l1 <= l2 && r2 <= r1
          || l2 <= l1 && r1 <= r2)
        totalFullyContains++;
    }

    System.out.println(totalFullyContains);
  }

  private static void part2() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    int totalOverlaps = 0;

    String line;
    while ((line = br.readLine()) != null) {
      int commaIdx = line.indexOf(',');
      int dash1Idx = line.indexOf('-');
      int dash2Idx = line.indexOf('-', commaIdx + 1);

      int l1 = Integer.parseInt(line.substring(0, dash1Idx));
      int r1 = Integer.parseInt(line.substring(dash1Idx + 1, commaIdx));
      int l2 = Integer.parseInt(line.substring(commaIdx + 1, dash2Idx));
      int r2 = Integer.parseInt(line.substring(dash2Idx + 1));

      if (l1 <= r2 && l2 <= r1)
        totalOverlaps++;
    }

    // 4-5,2-3

    System.out.println(totalOverlaps);
  }
}
