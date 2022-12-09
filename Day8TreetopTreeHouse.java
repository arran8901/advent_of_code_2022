import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day8TreetopTreeHouse {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part2() throws IOException {
    int[][] heights = getHeightsFromInput();
    final int N = heights.length;
    final int M = heights[0].length;

    int[][] scenicScores = new int[N][M];
    for (int i = 0; i < N; i++) {
      for (int j = 0; j < M; j++) {
        scenicScores[i][j] = 1;
      }
    }

    // Left and right viewing distances
    for (int row = 0; row < N; row++) {
      // Monotonic stack of cols
      Deque<Integer> stack = new ArrayDeque<>();

      for (int col = 0; col < M; col++) {
        final int height = heights[row][col];

        int previousPoppedCol = -1;
        while (!stack.isEmpty() && heights[row][stack.peek()] < height) {
          int poppedCol = stack.poll();

          // If previous popped col had the same height, that will be the right obstruction of poppedCol
          // and the right viewing distance of poppedCol will be 1. There is no need to multiply 1.
          // Otherwise, the right obstruction of poppedCol is the current col.
          if (previousPoppedCol != -1 && heights[row][poppedCol] == heights[row][previousPoppedCol]) {
            scenicScores[row][poppedCol] *= previousPoppedCol - poppedCol;
          } else {
            scenicScores[row][poppedCol] *= col - poppedCol;
          }
          previousPoppedCol = poppedCol;
        }

        // Stack top is the left obstruction of current col. If empty, left view is not blocked.
        scenicScores[row][col] *= col - (stack.isEmpty() ? 0 : stack.peek());

        stack.push(col);
      }

      // Process remaining cols in stack
      int previousPoppedCol = -1;
      while (!stack.isEmpty()) {
        int poppedCol = stack.poll();

        // If previous popped col had the same height, that will be the right obstruction of poppedCol
        // and the right viewing distance of poppedCol will be 1. There is no need to multiply 1.
        // Otherwise, the right view is not blocked.
        if (previousPoppedCol != -1 && heights[row][poppedCol] == heights[row][previousPoppedCol]) {
          scenicScores[row][poppedCol] *= previousPoppedCol - poppedCol;
        } else {
          scenicScores[row][poppedCol] *= M - poppedCol - 1;
        }
        previousPoppedCol = poppedCol;
      }
    }

    // Top and bottom viewing distances
    for (int col = 0; col < M; col++) {
      // Monotonic stack of rows
      Deque<Integer> stack = new ArrayDeque<>();

      for (int row = 0; row < N; row++) {
        final int height = heights[row][col];

        int previousPoppedRow = -1;
        while (!stack.isEmpty() && heights[stack.peek()][col] < height) {
          int poppedRow = stack.poll();

          if (previousPoppedRow != -1 && heights[poppedRow][col] == heights[previousPoppedRow][col]) {
            scenicScores[poppedRow][col] *= previousPoppedRow - poppedRow;
          } else {
            scenicScores[poppedRow][col] *= row - poppedRow;
          }
          previousPoppedRow = poppedRow;
        }

        scenicScores[row][col] *= row - (stack.isEmpty() ? 0 : stack.peek());

        stack.push(row);
      }

      int previousPoppedRow = -1;
      while (!stack.isEmpty()) {
        int poppedRow = stack.poll();

        if (previousPoppedRow != -1 && heights[poppedRow][col] == heights[previousPoppedRow][col]) {
          scenicScores[poppedRow][col] *= previousPoppedRow - poppedRow;
        } else {
          scenicScores[poppedRow][col] *= N - poppedRow - 1;
        }
        previousPoppedRow = poppedRow;
      }
    }

    int maxScenicScore = Arrays.stream(scenicScores)
        .mapToInt(row -> Arrays.stream(row).max().getAsInt())
        .max()
        .getAsInt();
    System.out.println(maxScenicScore);
  }

  private static void part1() throws IOException {
    int[][] heights = getHeightsFromInput();
    final int N = heights.length;
    final int M = heights[0].length;

    // tree at (row, col) is visible <==> visible[row * M + col] is set
    BitSet visible = new BitSet();

    // Left & right visibility
    for (int row = 0; row < N; row++) {
      int maxHeightSeen = -1;
      // Monotonic stack of cols
      Deque<Integer> stack = new ArrayDeque<>();

      for (int col = 0; col < M; col++) {
        final int height = heights[row][col];

        if (height > maxHeightSeen) {
          maxHeightSeen = height;
          visible.set(row * M + col);
        }

        while (!stack.isEmpty() && heights[row][stack.peek()] <= height) {
          stack.poll();
        }
        stack.push(col);
      }

      // Any cols left in the stack are visible from the right
      while (!stack.isEmpty()) {
        int col = stack.poll();
        visible.set(row * M + col);
      }
    }

    // Top & bottom visibility
    for (int col = 0; col < M; col++) {
      int maxHeightSeen = -1;
      // Monotonic stack of rows
      Deque<Integer> stack = new ArrayDeque<>();

      for (int row = 0; row < N; row++) {
        final int height = heights[row][col];

        if (height > maxHeightSeen) {
          maxHeightSeen = height;
          visible.set(row * M + col);
        }

        while (!stack.isEmpty() && heights[stack.peek()][col] <= height) {
          stack.poll();
        }
        stack.push(row);
      }

      // Any rows left in the stack are visible from the bottom
      while (!stack.isEmpty()) {
        int row = stack.poll();
        visible.set(row * M + col);
      }
    }

    // Number of visible trees is the number of set bits in the visible bitset
    System.out.println(visible.cardinality());
  }

  private static int[][] getHeightsFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    List<String> inputLines = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      inputLines.add(line);
    }

    return inputLines.stream()
        .map(inputLine -> inputLine.chars()
            .mapToObj(c -> (char) c)
            .mapToInt(Character::getNumericValue)
            .toArray())
        .toArray(int[][]::new);
  }
}
