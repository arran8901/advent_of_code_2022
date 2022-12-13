import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;

public class Day11MonkeyInTheMiddle {

  public static void main(String[] args) throws IOException {
    part(2);
  }


  private static void part(int part) throws IOException {
    Monkey[] monkeys = getMonkeysFromInput();

    // x is divisible by d1,d2,...,dn <==> (x % (d1*d2*...*dn)) is divisible by d1,d2,...,dn
    // Use this to manage worry levels so they do not overflow the long type
    long productOfDivisors = Arrays.stream(monkeys)
        .mapToLong(monkey -> monkey.divisor)
        .reduce((d1, d2) -> d1 * d2)
        .getAsLong();

    int numRounds = part == 1 ? 20 : 10000;
    LongUnaryOperator worryManager = part == 1
        ? worry -> worry / 3
        : worry -> worry % productOfDivisors;

    for (int round = 0; round < numRounds; round++) {
      for (final Monkey monkey : monkeys) {
        while (!monkey.items.isEmpty()) {
          long item = monkey.items.poll();
          long worry = monkey.operation.applyAsLong(item);
          worry = worryManager.applyAsLong(worry);
          int target = monkey.test.test(worry) ? monkey.trueTarget : monkey.falseTarget;
          monkeys[target].items.add(worry);
          monkey.timesInspected++;
        }
      }
    }

    System.out.println(Arrays.toString(Arrays.stream(monkeys).mapToLong(m -> m.timesInspected).toArray()));

    List<Long> top2TimesInspected = Arrays.stream(monkeys)
        .map(monkey -> monkey.timesInspected)
        .sorted(Collections.reverseOrder())
        .limit(2)
        .toList();
    long monkeyBusiness = top2TimesInspected.get(0) * top2TimesInspected.get(1);
    System.out.println(monkeyBusiness);
  }

  private static Monkey[] getMonkeysFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    final List<Monkey> monkeys = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      if (line.startsWith("Monkey ")) {
        // Assume monkey numbers are single digits only
        int monkeyNum = Character.getNumericValue(line.charAt(7));

        // Get starting items
        line = br.readLine();
        assert line.startsWith("  Starting items:");
        List<Long> startingItems = Arrays.stream(line.substring(18).split(", "))
            .map(Long::parseLong)
            .toList();

        // Get operation
        line = br.readLine();
        assert line.startsWith("  Operation:");
        // Assume format 'new = old <op> <otherOperand>'
        // where 'op' is either '+' or '*' and 'otherOperand' is a number or 'old'
        char op = line.charAt(23);
        String otherOperand = line.substring(25);

        LongUnaryOperator operation;
        if (otherOperand.equals("old")) {
          operation = op == '+'
              ? old -> old + old
              : old -> old * old;
        } else {
          long otherNumber = Long.parseLong(otherOperand);
          operation = op == '+'
              ? old -> old + otherNumber
              : old -> old * otherNumber;
        }

        // Get test
        line = br.readLine();
        assert line.startsWith("  Test:");
        // Assume format 'divisible by <number>'
        long divisor = Long.parseLong(line.substring(21));
        LongPredicate test = worry -> worry % divisor == 0;

        // Get trueTarget
        line = br.readLine();
        assert line.startsWith("    If true:");
        // Assume format 'throw to monkey <number>'
        int trueTarget = Integer.parseInt(line.substring(29));

        // Get falseTarget
        line = br.readLine();
        assert line.startsWith("    If false:");
        // Assume format 'throw to monkey <number>'
        int falseTarget = Integer.parseInt(line.substring(30));

        monkeys.add(new Monkey(startingItems, operation, test, divisor, trueTarget, falseTarget));
      }
    }
    return monkeys.toArray(Monkey[]::new);
  }

  private static class Monkey {
    private final Queue<Long> items;
    private final LongUnaryOperator operation;
    private final LongPredicate test;
    private final long divisor;
    private final int trueTarget;
    private final int falseTarget;
    private long timesInspected;

    public Monkey(List<Long> startingItems, LongUnaryOperator operation, LongPredicate test, long divisor,
                  int trueTarget, int falseTarget) {
      items = new ArrayDeque<>(startingItems);
      this.operation = operation;
      this.test = test;
      this.divisor = divisor;
      this.trueTarget = trueTarget;
      this.falseTarget = falseTarget;
      timesInspected = 0;
    }

    @Override
    public String toString() {
      return "Monkey{" +
          "items=" + items +
          ", operation=" + operation +
          ", test=" + test +
          ", trueTarget=" + trueTarget +
          ", falseTarget=" + falseTarget +
          '}';
    }
  }
}
