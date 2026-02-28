package dsa.linkedList;

public class ReverseLinkedList {

    // Iterative approach
    public ListNode reverse(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode nextNode = curr.next; // save next before breaking link
            curr.next = prev;             // reverse the link
            prev = curr;                  // move prev forward
            curr = nextNode;              // move curr forward
        }

        return prev; // prev is the new head
    }

    // Helper to print list
    public void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val);
            if (head.next != null) System.out.print(" → ");
            head = head.next;
        }
        System.out.println(" → null");
    }

    public static void main(String[] args) {
        ReverseLinkedList sol = new ReverseLinkedList();

        // Build: 1 → 2 → 3 → 4 → 5
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        sol.printList(head);               // 1 → 2 → 3 → 4 → 5 → null
        ListNode reversed = sol.reverse(head);
        sol.printList(reversed);           // 5 → 4 → 3 → 2 → 1 → null

        // Edge case: single node
        ListNode single = new ListNode(1);
        sol.printList(sol.reverse(single)); // 1 → null

        // Edge case: null
        System.out.println(sol.reverse(null)); // null
    }
}
