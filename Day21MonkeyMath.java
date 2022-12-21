import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.LongBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day21MonkeyMath {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part2() throws IOException {
    getMonkeysFromInput();
    List<String> pathToHumn = new ArrayList<>();
    findPath("root", "humn", pathToHumn);

    Monkey root = Monkey.graph.get("root");
    long humn;
    if (root.deps[0].equals(pathToHumn.get(1))) {
      long target = numberYelled(root.deps[1]);
      humn = calculateHumn(pathToHumn, 1, target);
    } else {
      long target = numberYelled(root.deps[0]);
      humn = calculateHumn(pathToHumn, 1, target);
    }
    System.out.println(humn);
  }

  private static long calculateHumn(List<String> pathToHumn, int currIndex, long currVal) {
    String currName = pathToHumn.get(currIndex);
    if (currName.equals("humn")) return currVal;

    Monkey monkey = Monkey.graph.get(currName);
    currIndex++;

    String goesToHumn = pathToHumn.get(currIndex);
    String other = monkey.deps[0];
    boolean firstOperandGoesToHumn = false;
    if (monkey.deps[0].equals(goesToHumn)) {
      other = monkey.deps[1];
      firstOperandGoesToHumn = true;
    }

    long otherNumber = numberYelled(other);
    long newVal = inverseOps.get(new Pair(monkey.op, firstOperandGoesToHumn)).applyAsLong(currVal, otherNumber);
    return calculateHumn(pathToHumn, currIndex, newVal);
  }

  private static boolean findPath(String src, String dst, List<String> path) {
    path.add(src);
    Monkey srcMonkey = Monkey.graph.get(src);
    for (String dep : srcMonkey.deps) {
      if (dep.equals(dst)) {
        path.add(dst);
        return true;
      }
      if(findPath(dep, dst, path)) {
        return true;
      }
    }
    path.remove(path.size() - 1);
    return false;
  }

  private static void part1() throws IOException {
    getMonkeysFromInput();

    long res = numberYelled("root");
    System.out.println(res);
  }

  private static long numberYelled(String monkeyName) {
    Monkey monkey = Monkey.graph.get(monkeyName);
    for (String dep : monkey.deps) {
      numberYelled(dep);
    }
    monkey.calculate();
    return monkey.numberYelled;
  }

  private static void getMonkeysFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    final Pattern pNum = Pattern.compile("(\\w+): (\\d+)");
    final Pattern pMath = Pattern.compile("(\\w+): (\\w+) ([+\\-*/]) (\\w+)");

    String line;
    while ((line = br.readLine()) != null) {
      Matcher m = pNum.matcher(line);
      if (m.matches()) {
        String name = m.group(1);
        long num = Long.parseLong(m.group(2));
        Monkey.graph.put(name, new Monkey(new String[0], num));

      } else {
        m = pMath.matcher(line);
        m.matches();
        String name = m.group(1);
        String dep1 = m.group(2);
        String dep2 = m.group(4);
        char op = m.group(3).charAt(0);
        Monkey.graph.put(name, new Monkey(new String[]{dep1, dep2}, opFunctions.get(op)));
      }
    }
  }

  private static final LongBinaryOperator PLUS = Long::sum;
  private static final LongBinaryOperator MINUS = (x, y) -> x - y;
  private static final LongBinaryOperator TIMES = (x, y) -> x * y;
  private static final LongBinaryOperator DIVIDE = (x, y) -> x / y;
  private static final Map<Character, LongBinaryOperator> opFunctions = Map.of(
      '+', PLUS,
      '-', MINUS,
      '*', TIMES,
      '/', DIVIDE
  );
  private static final Map<Pair, LongBinaryOperator> inverseOps = Map.of(
      new Pair(PLUS, false), MINUS,
      new Pair(PLUS, true), MINUS,
      new Pair(MINUS, false), (long res, long op1) -> op1 - res,
      new Pair(MINUS, true), PLUS,
      new Pair(TIMES, false), DIVIDE,
      new Pair(TIMES, true), DIVIDE,
      new Pair(DIVIDE, false), (long res, long op1) -> op1 / res,
      new Pair(DIVIDE, true), TIMES
  );

  private static record Pair(LongBinaryOperator op, boolean calculatingFirstOperand) {}

  private static class Monkey {
    private final String[] deps;
    private final LongBinaryOperator op;
    private long numberYelled;

    public static final Map<String, Monkey> graph = new HashMap<>();

    public Monkey(String[] deps, long numberYelled) {
      this.deps = deps;
      this.numberYelled = numberYelled;
      op = null;
    }

    public Monkey(String[] deps, LongBinaryOperator op) {
      this.deps = deps;
      this.op = op;
    }

    public void calculate() {
      if (op != null) {
        long n1 = graph.get(deps[0]).numberYelled;
        long n2 = graph.get(deps[1]).numberYelled;
        numberYelled = op.applyAsLong(n1, n2);
      }
    }
  }
}
