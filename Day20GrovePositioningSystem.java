import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day20GrovePositioningSystem {

  public static void main(String[] args) throws IOException {
    part(2);
  }

  private static void part(int part) throws IOException {
    List<Long> nums = getNumsFromInput();

    final int NUM_MIXES = part == 2 ? 10 : 1;
    if (part == 2) {
      nums = nums.stream()
          .map(num -> num * 811589153)
          .toList();
    }

    final int N = nums.size();
    final int MOD = nums.size() - 1;

    // Build circular list and get zero node
    ListNode[] listNodes = new ListNode[N];
    listNodes[0] = new ListNode(nums.get(0));
    ListNode zeroNode = null;
    for (int i = 1; i < N; i++) {
      listNodes[i] = new ListNode(nums.get(i));
      listNodes[i].prev = listNodes[i - 1];
      listNodes[i - 1].next = listNodes[i];
      if (nums.get(i) == 0L) {
        zeroNode = listNodes[i];
      }
    }
    listNodes[0].prev = listNodes[N - 1];
    listNodes[N - 1].next = listNodes[0];

    ListNode head = listNodes[0];

    for (int mix = 0; mix < NUM_MIXES; mix++) {
      for (int i = 0; i < N; i++) {
        final ListNode node = listNodes[i];
        int movement = ((int) (node.num % MOD) + MOD) % MOD;

        // If moving head, next node becomes the new head
        if (node == head) {
          head = node.next;
        }

        // Remove node
        node.prev.next = node.next;
        node.next.prev = node.prev;

        // Find position of insertion
        ListNode insertPos = node.next;
        while (movement-- > 0) {
          insertPos = insertPos.next;
        }

        // Insert node
        node.prev = insertPos.prev;
        node.next = insertPos;
        insertPos.prev.next = node;
        insertPos.prev = node;
      }
    }

    long groveCoordsSum = 0;

    for (int offset : new int[]{1000, 2000, 3000}) {
      int modOffset = offset % N;

      // Advance modOffset nodes from zero node
      ListNode n = zeroNode;
      for (int i = 0; i < modOffset; i++) n = n.next;

      groveCoordsSum += n.num;
    }
    System.out.println(groveCoordsSum);
  }

  private static List<Long> getNumsFromInput() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    List<Long> nums = new ArrayList<>();

    String line;
    while ((line = br.readLine()) != null) {
      nums.add(Long.parseLong(line));
    }
    return nums;
  }

  private static class ListNode {
    private final long num;
    private ListNode prev;
    private ListNode next;

    public ListNode(long num) {
      this.num = num;
    }
  }
}
