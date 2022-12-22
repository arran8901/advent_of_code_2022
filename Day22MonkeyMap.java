import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day22MonkeyMap {

  public static void main(String[] args) throws IOException {
    part(2);
  }

  private static void part(int part) throws IOException {
    BoardAndPath boardAndPath = getBoardAndPathFromInput();
    Board board = boardAndPath.board;
    List<String> path = boardAndPath.path;

    int row = 1;
    int col = board.leftEdges[row];
    int facing = 0;

    for (String instruction : path) {
      switch (instruction) {
        case "L" -> facing = (facing + 3) % 4;
        case "R" -> facing = (facing + 1) % 4;
        default -> {
          int steps = Integer.parseInt(instruction);

          while (steps-- > 0) {
            int[] nextTile = part == 1 ? nextTile(board, row, col, facing)
                : nextTile3D(board, row, col, facing);
            int nextRow = nextTile[0];
            int nextCol = nextTile[1];
            if (board.map[nextRow][nextCol] == '#') {
              break;
            }
            row = nextRow;
            col = nextCol;
            if (part == 2) facing = nextTile[2];
          }
        }
      }
    }
    int pwd = 1000 * row + 4 * col + facing;
    System.out.println(pwd);
  }

  private static int[] nextTile(Board board, int row, int col, int facing) {
    int[] nextTile = {row, col};
    switch (facing) {
      case 0 -> nextTile[1] = col + 1;
      case 1 -> nextTile[0] = row + 1;
      case 2 -> nextTile[1] = col - 1;
      case 3 -> nextTile[0] = row - 1;
    }
    if (board.map[nextTile[0]][nextTile[1]] == '\u0000') {
      switch (facing) {
        case 0 -> nextTile[1] = board.leftEdges[row];
        case 1 -> nextTile[0] = board.topEdges[col];
        case 2 -> nextTile[1] = board.rightEdges[row];
        case 3 -> nextTile[0] = board.bottomEdges[col];
      }
    }
    return nextTile;
  }

  private static int[] nextTile3D(Board board, int row, int col, int facing) {
    int[] nextTile = {row, col, facing};
    switch (facing) {
      case 0 -> nextTile[1] = col + 1;
      case 1 -> nextTile[0] = row + 1;
      case 2 -> nextTile[1] = col - 1;
      case 3 -> nextTile[0] = row - 1;
    }
    if (board.map[nextTile[0]][nextTile[1]] != '\u0000') {
      return nextTile;
    }

    if (facing == 2
        && row >= 1 && row <= 50
        && col == 51) {
      nextTile[0] = 151 - row;
      nextTile[1] = 1;
      nextTile[2] = 0;
    }
    else if (facing == 2
        && row >= 51 && row <= 100
        && col == 51) {
      nextTile[0] = 101;
      nextTile[1] = row - 50;
      nextTile[2] = 1;
    }
    else if (facing == 2
        && row >= 101 && row <= 150
        && col == 1) {
      nextTile[0] = 51 - (row - 100);
      nextTile[1] = 51;
      nextTile[2] = 0;
    }
    else if (facing == 2
        && row >= 151 && row <= 200
        && col == 1) {
      nextTile[0] = 1;
      nextTile[1] = 50 + (row - 150);
      nextTile[2] = 1;
    }
    else if (facing == 0
        && row >= 1 && row <= 50
        && col == 150) {
      nextTile[0] = 151 - row;
      nextTile[1] = 100;
      nextTile[2] = 2;
    }
    else if (facing == 0
        && row >= 51 && row <= 100
        && col == 100) {
      nextTile[0] = 50;
      nextTile[1] = 100 + (row - 50);
      nextTile[2] = 3;
    }
    else if (facing == 0
        && row >= 101 && row <= 150
        && col == 100) {
      nextTile[0] = 51 - (row - 100);
      nextTile[1] = 150;
      nextTile[2] = 2;
    }
    else if (facing == 0
        && row >= 151 && row <= 200
        && col == 50) {
      nextTile[0] = 150;
      nextTile[1] = 50 + (row - 150);
      nextTile[2] = 3;
    }
    else if (facing == 3
        && row == 101
        && col >= 1 && col <= 50) {
      nextTile[0] = 50 + col;
      nextTile[1] = 51;
      nextTile[2] = 0;
    }
    else if (facing == 3
        && row == 1
        && col >= 51 && col <= 100) {
      nextTile[0] = 150 + (col - 50);
      nextTile[1] = 1;
      nextTile[2] = 0;
    }
    else if (facing == 3
        && row == 1
        && col >= 101 && col <= 150) {
      nextTile[0] = 200;
      nextTile[1] = col - 100;
    }
    else if (facing == 1
        && row == 200
        && col >= 1 && col <= 50) {
      nextTile[0] = 1;
      nextTile[1] = 100 + col;
    }
    else if (facing == 1
        && row == 150
        && col >= 51 && col <= 100) {
      nextTile[0] = 150 + (col - 50);
      nextTile[1] = 50;
      nextTile[2] = 2;
    }
    else if (facing == 1
        && row == 50
        && col >= 101 && col <= 150) {
      nextTile[0] = 50 + (col - 100);
      nextTile[1] = 100;
      nextTile[2] = 2;
    } else {
      throw new RuntimeException(row + " " + col + " " + facing + " " + board.map[row][col]);
    }

    return nextTile;
  }

  private static BoardAndPath getBoardAndPathFromInput() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    List<String> boardLines = new ArrayList<>();
    int maxLineLength = Integer.MIN_VALUE;
    String path = null;

    while (true) {
      String line = br.readLine();
      if (line.isEmpty()) {
        path = br.readLine();
        break;
      }
      boardLines.add(line);
      maxLineLength = Math.max(maxLineLength, line.length());
    }

    final int mapRows = boardLines.size() + 2;
    final int mapCols = maxLineLength + 2;
    char[][] map = new char[mapRows][mapCols];
    int[] leftEdges = new int[mapRows];
    int[] rightEdges = new int[mapRows];
    int[] topEdges = new int[mapCols];
    int[] bottomEdges = new int[mapCols];

    for (int row = 1; row < mapRows - 1; row++) {
      final String line = boardLines.get(row - 1);

      for (int col = 1; col < mapCols - 1; col++) {
        if (col > line.length()) {
          continue;
        }
        final char c = line.charAt(col - 1);
        if (c == '.' || c == '#') {
          map[row][col] = c;

          if (leftEdges[row] == 0) leftEdges[row] = col;
          rightEdges[row] = col;
          if (topEdges[col] == 0) topEdges[col] = row;
          bottomEdges[col] = row;
        }
      }
    }

    List<String> pathInstructions = new ArrayList<>();
    int lastTurnIdx = -1;
    for (int i = 0; i < path.length(); i++) {
      if (path.charAt(i) == 'L' || path.charAt(i) == 'R') {
        // If there is a number between this turn and the last turn, add it
        if (i > lastTurnIdx + 1) {
          pathInstructions.add(path.substring(lastTurnIdx + 1, i));
        }

        // Add this turn
        pathInstructions.add(path.substring(i, i + 1));

        lastTurnIdx = i;
      }
    }
    if (lastTurnIdx < path.length() - 1) {
      // Add number at the end
      pathInstructions.add(path.substring(lastTurnIdx + 1));
    }

    return new BoardAndPath(new Board(map, leftEdges, rightEdges, topEdges, bottomEdges), pathInstructions);
  }

  private static class Board {
    private final char[][] map;
    private final int[] leftEdges;
    private final int[] rightEdges;
    private final int[] topEdges;
    private final int[] bottomEdges;

    private final int N;
    private final int M;

    public Board(char[][] map, int[] leftEdges, int[] rightEdges, int[] topEdges, int[] bottomEdges) {
      this.map = map;
      this.leftEdges = leftEdges;
      this.rightEdges = rightEdges;
      this.topEdges = topEdges;
      this.bottomEdges = bottomEdges;
      N = map.length - 1;
      M = map[0].length - 1;
    }

    @Override
    public String toString() {
      return "Board{" +
          "map=" + Arrays.deepToString(map) +
          ", leftEdges=" + Arrays.toString(leftEdges) +
          ", rightEdges=" + Arrays.toString(rightEdges) +
          ", topEdges=" + Arrays.toString(topEdges) +
          ", bottomEdges=" + Arrays.toString(bottomEdges) +
          '}';
    }
  }

  private record BoardAndPath(Board board, List<String> path) {}
}
