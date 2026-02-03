package machineCoding.taskScheduler.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
public class Task implements Comparable<Task> {
    private final String id;
    private final long executionTimeInEpoch;
    private final int priority;
    private final Runnable action;
    private TaskStatus status;

    //retry
    private int retryCount = 0;
    private final int maxRetries = 3;

    public Task(String id, long executionTimeInEpoch, int priority, Runnable action) {
        this.id = id;
        this.executionTimeInEpoch = executionTimeInEpoch;
        this.priority = priority;
        this.action = action;
        this.status = TaskStatus.PENDING;
    }


    @Override
    public int compareTo(Task other) {
        if(this.executionTimeInEpoch != other.executionTimeInEpoch)
            return Long.compare(this.executionTimeInEpoch,other.executionTimeInEpoch);
        else
            return Integer.compare(this.priority,other.priority);
    }

    public void incrementRetryCount() { this.retryCount++; }

    // New execution time = currentTime + (BaseDelay * 2^retryCount)
    public long calculateNextRetryTime() {
        long baseDelay = 1000; // 1 second
        long delay = baseDelay * (long) Math.pow(2, retryCount);

        // Add random jitter between 0 and 200ms
        //this is because if 1000 of thread fail since system goes down all with retry at same time.
        long jitter = ThreadLocalRandom.current().nextLong(0, 200);

        return System.currentTimeMillis() + delay + jitter;

    }
}
