package coding.retail.shiftMerge;

import java.util.*;

public class ShiftMerger {

    public record Shift(String role, int start, int end) {}

    public Map<String, List<int[]>> mergeShifts(List<Shift> shifts) {
        if (shifts == null || shifts.isEmpty()) return Collections.emptyMap();

        // Grouping by role
        Map<String, List<Shift>> grouped = new HashMap<>();
        for (Shift s : shifts) {
            grouped.computeIfAbsent(s.role(), k -> new ArrayList<>()).add(s);
        }

        Map<String, List<int[]>> result = new HashMap<>();

        for (String role : grouped.keySet()) {
            List<Shift> roleShifts = grouped.get(role);

            // Sort by start time - Critical for Merge Logic
            roleShifts.sort((s1,s2) -> Integer.compare(s1.start(), s2.start()));

            LinkedList<int[]> merged = new LinkedList<>();
            for (Shift s : roleShifts) {
                // If list is empty or current shift doesn't overlap with the last merged shift
                if (merged.isEmpty() || merged.getLast()[1] < s.start()) {
                    merged.add(new int[]{s.start(), s.end()});
                } else {
                    // There is an overlap or they touch, update the end time
                    merged.getLast()[1] = Math.max(merged.getLast()[1], s.end());
                }
            }
            result.put(role, merged);
        }
        return result;
    }



}
