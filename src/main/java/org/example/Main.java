package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
/*
        // Array and List
        Integer[] arr = new Integer[]{1, 2, 3, 4, 5};
        List<Integer> list = Arrays.stream(arr).collect(Collectors.toList());
        Collections.sort(list);
        Arrays.sort(arr);

        // Map usage
        Map<Integer, Integer> map = new HashMap<>();
        for (int key : map.keySet()) {
            // process key
        }
        for (int val : map.values()) {
            // process value
        }
        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            int key = e.getKey();
            int value = e.getValue();
        }
        map.put(2, map.getOrDefault(2, 0) + 1); // for frequency count

        // Stack usage
        Stack<Integer> st = new Stack<>();
        st.push(10);
        st.pop();
        st.peek();
        st.isEmpty();

        // Queue usage
        Queue<Integer> q = new LinkedList<>();
        q.add(10);
        q.poll();
        q.peek();

        // Deque usage
        Deque<Integer> dq = new ArrayDeque<>();
        dq.addLast(10);
        dq.removeFirst();
        dq.peekFirst();
        dq.peekLast();

        // PriorityQueue (max-heap)
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        pq.add(5);
        pq.add(1);
        pq.add(10);
        pq.poll(); // removes the largest element
        pq.peek(); // gets the largest element

        // PriorityQueue with custom comparator for int[]
        PriorityQueue<int[]> pqArr = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pqArr.add(new int[]{1, 5});
        pqArr.add(new int[]{2, 3});
        pqArr.add(new int[]{3, 4});
        int[] minSecond = pqArr.poll(); // array with smallest value at index 1

        // String operations
        String s = "abc";
        s.length();
        s.charAt(0);
        s.substring(0, 2);
        s.equals("abc");
        s.equalsIgnoreCase("ABC");
        s.contains("a");
        s.indexOf("a");
        s.toCharArray();


        public record Shift(String role, int start, int end) {}


*/


        System.out.println("Hello and welcome!");

        record Shift(String role, int start, int end) {}

        Shift s = new Shift("Manager", 9, 17);
        System.out.println(s.role());


        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }
    }
}