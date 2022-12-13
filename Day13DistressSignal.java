import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day13DistressSignal {

  public static void main(String[] args) throws IOException {
    part2();
  }

  private static void part2() throws IOException {
    List<PacketPair> packetPairs = getPacketPairsFromInput();
    Stream<Packet> flattenedPairs = packetPairs.stream()
        .flatMap(packetPair -> Stream.of(packetPair.left, packetPair.right));

    Packet divider1 = new ListPacket(new Packet[]{
        new ListPacket(new Packet[]{
            new IntegerPacket(2)
        })
    });
    Packet divider2 = new ListPacket(new Packet[]{
        new ListPacket(new Packet[]{
            new IntegerPacket(6)
        })
    });

    Packet[] ordered = Stream.concat(flattenedPairs, Stream.of(divider1, divider2))
        .sorted(Day13DistressSignal::cmpPackets)
        .toArray(Packet[]::new);

    int decoderKey = 1;
    for (int i = 0; i < ordered.length; i++) {
      if (ordered[i] == divider1 || ordered[i] == divider2) {
        decoderKey *= i + 1;
      }
    }
    System.out.println(decoderKey);
  }

  private static void part1() throws IOException {
    List<PacketPair> packetPairs = getPacketPairsFromInput();

    int inOrderIndicesSum = 0;

    int pairIndex = 1;
    for (PacketPair pair : packetPairs) {
      if (cmpPackets(pair.left, pair.right) < 0) {
        inOrderIndicesSum += pairIndex;
      }
      pairIndex++;
    }

    System.out.println(inOrderIndicesSum);
  }

  private static int cmpPackets(Packet left, Packet right) {
    if (left instanceof IntegerPacket leftInteger) {
      if (right instanceof IntegerPacket rightInteger) {
        return Integer.compare(leftInteger.value, rightInteger.value);

      } else {
        return cmpPackets(new ListPacket(new Packet[]{leftInteger}), right);
      }

    } else {
      ListPacket leftList = (ListPacket) left;

      if (right instanceof IntegerPacket rightInteger) {
        return cmpPackets(left, new ListPacket(new Packet[]{rightInteger}));

      } else {
        ListPacket rightList = (ListPacket) right;
        final int leftLength = leftList.elements.length;
        final int rightLength = rightList.elements.length;

        for (int i = 0; i < Math.min(leftLength, rightLength); i++) {
          int cmp = cmpPackets(leftList.elements[i], rightList.elements[i]);
          if (cmp != 0) return cmp;
        }
        return Integer.compare(leftLength, rightLength);
      }
    }
  }

  private static List<PacketPair> getPacketPairsFromInput() throws IOException {
    final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    final List<PacketPair> packetPairs = new ArrayList<>();

    Packet left = null;

    String line;
    while ((line = br.readLine()) != null) {
      if (line.isEmpty()) continue;

      Packet current = parsePacket(line);
      if (left == null) {
        // This is the left packet
        left = current;
      } else {
        // This is the right packet
        packetPairs.add(new PacketPair(left, current));
        left = null;
      }
    }

    return packetPairs;
  }

  private static Packet parsePacket(String packetStr) {
    if (packetStr.charAt(0) == '[') {
      // List packet
      if (packetStr.length() == 2) {
        // Empty list
        return new ListPacket(new Packet[0]);
      }
      List<String> innerPackets = new ArrayList<>();
      int lastCommaIndex = 0;
      int nestLevel = 0;
      for (int i = 1; i < packetStr.length() - 1; i++) {
        if (packetStr.charAt(i) == '[') {
          nestLevel++;
        } else if (packetStr.charAt(i) == ']') {
          nestLevel--;
        } else if (packetStr.charAt(i) == ',' && nestLevel == 0) {
          innerPackets.add(packetStr.substring(lastCommaIndex + 1, i));
          lastCommaIndex = i;
        }
      }
      innerPackets.add(packetStr.substring(lastCommaIndex + 1, packetStr.length() - 1));

      return new ListPacket(innerPackets.stream()
          .map(Day13DistressSignal::parsePacket)
          .toArray(Packet[]::new));
    }

    // Integer packet
    return new IntegerPacket(Integer.parseInt(packetStr));
  }

  private interface Packet {}
  private static record ListPacket(Packet[] elements) implements Packet {}
  private static record IntegerPacket(int value) implements Packet {}

  private static record PacketPair(Packet left, Packet right) {}
}
