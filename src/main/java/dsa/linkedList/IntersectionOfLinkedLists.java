package dsa.linkedList;
/*

Find Intersection of Two Linked Lists
Question
Given the heads of two singly linked lists, return the node at which the two lists intersect. If they don't intersect, return null.
Note: Intersection means same node in memory (same reference), not just same value.
Sample Input:
List A:  4 → 1 ↘
        8 → 4 → 5 → null
List B:  5 → 6 → 1 ↗

Intersection node = 8
Sample Output:
        8
*/

public class IntersectionOfLinkedLists {

    // Approach: Two pointer trick
    // Key insight: if we make both pointers travel equal distance,
    // they will meet at intersection
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) return null;

        ListNode pA = headA;
        ListNode pB = headB;

        // When pA reaches end, redirect to headB
        // When pB reaches end, redirect to headA
        // They will meet at intersection after at most (lenA + lenB) steps
        while (pA != pB) {
            pA = (pA == null) ? headB : pA.next;
            pB = (pB == null) ? headA : pB.next;
        }

        return pA; // null if no intersection, intersection node otherwise
    }

    public static void main(String[] args) {
        IntersectionOfLinkedLists sol = new IntersectionOfLinkedLists();

        // Build intersection: shared tail 8 → 4 → 5
        ListNode shared = new ListNode(8);
        shared.next = new ListNode(4);
        shared.next.next = new ListNode(5);

        // List A: 4 → 1 → 8 → 4 → 5
        ListNode headA = new ListNode(4);
        headA.next = new ListNode(1);
        headA.next.next = shared;

        // List B: 5 → 6 → 1 → 8 → 4 → 5
        ListNode headB = new ListNode(5);
        headB.next = new ListNode(6);
        headB.next.next = new ListNode(1);
        headB.next.next.next = shared;

        ListNode result = sol.getIntersectionNode(headA, headB);
        System.out.println(result != null ? result.val : "No intersection"); // 8

        // No intersection case
        ListNode c1 = new ListNode(1);
        ListNode c2 = new ListNode(2);
        System.out.println(sol.getIntersectionNode(c1, c2)); // null
    }
}


/*

### Why This Works (the math)
```
Let:
lenA = length of list A before intersection = a
lenB = length of list B before intersection = b
common = length of shared tail = c

pA travels: a + c + b steps to reach intersection
pB travels: b + c + a steps to reach intersection

a + c + b == b + c + a  ✓  (same distance!)


What the Interviewer Expects
The brute force is O(m×n) — for each node in A, scan all of B. They don't want that. The hash set approach (O(m+n) time, O(m) space) is acceptable but not optimal.
The two-pointer trick is the expected answer — elegant, O(1) space.
They check:

Do you arrive at the two-pointer insight or need a hint
Do you understand why redirecting pointers equalizes the distance
The condition pA != pB correctly handles both intersection and no-intersection (both become null together)
Edge cases: one list is empty, lists of same length, one list fully contained in other

Common follow-ups:

"What if the lists have a cycle?" (much harder — Floyd's cycle detection first)
"What if you can modify the lists?" (reverse one list, find intersection — destructive)
"Can you find the intersection point using length difference?" (yes — find lengths, advance longer list by difference, then walk together)

*/

