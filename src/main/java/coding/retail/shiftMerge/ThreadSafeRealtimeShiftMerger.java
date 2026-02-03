package coding.retail.shiftMerge;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeRealtimeShiftMerger {
    // We map each role to its own sorted schedule and its own lock for fine-grained locking
    private final Map<String, TreeMap<Integer, Integer>> roleSchedules = new HashMap<>();
    private final Map<String, ReadWriteLock> roleLocks = new HashMap<>();

    public void addShift(String role, int start, int end) {
        // Double-checked locking or computeIfAbsent to initialize role structures
        ReadWriteLock lock = roleLocks.computeIfAbsent(role, k -> new ReentrantReadWriteLock());

        lock.writeLock().lock(); // Acquire exclusive write lock
        try {
            roleSchedules.putIfAbsent(role, new TreeMap<>());
            TreeMap<Integer, Integer> intervals = roleSchedules.get(role);

            Integer prevStart = intervals.floorKey(start);
            Integer nextStart = intervals.ceilingKey(start);

            // Logic to merge with previous
            if (prevStart != null && intervals.get(prevStart) >= start) {
                start = prevStart;
                end = Math.max(end, intervals.get(prevStart));
            }

            // Logic to merge with subsequent
            while (nextStart != null && nextStart <= end) {
                end = Math.max(end, intervals.get(nextStart));
                intervals.remove(nextStart);
                nextStart = intervals.ceilingKey(start);
            }

            intervals.put(start, end);
        } finally {
            lock.writeLock().unlock(); // Always unlock in finally
        }
    }

    public List<int[]> getMergedShifts(String role) {
        ReadWriteLock lock = roleLocks.get(role);
        if (lock == null) return Collections.emptyList();

        lock.readLock().lock(); // Multiple threads can read at once
        try {
            List<int[]> result = new ArrayList<>();
            TreeMap<Integer, Integer> intervals = roleSchedules.get(role);
            if (intervals != null) {
                intervals.forEach((s, e) -> result.add(new int[]{s, e}));
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
}
