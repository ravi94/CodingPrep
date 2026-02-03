package machineCoding.taskScheduler;

import machineCoding.taskScheduler.entities.Task;
import machineCoding.taskScheduler.services.DistributedSchedulerService;

public class SchedulerDemoApp {
    public static void main(String[] args) throws InterruptedException {
        DistributedSchedulerService scheduler = new DistributedSchedulerService(4); // 4 worker threads

        long now = System.currentTimeMillis();

        // Schedule tasks with different offsets
        scheduler.schedule(new Task("T1", now + 5000, 1, () -> System.out.println("Running T1 (5s delay)")));
        scheduler.schedule(new Task("T2", now + 2000, 2, () -> System.out.println("Running T2 (2s delay)")));
        scheduler.schedule(new Task("T3", now + 2000, 10, () -> System.out.println("Running T3 (High Priority 2s delay)")));

        // Keep main thread alive to see execution
        Thread.sleep(7000);
    }
}


/*1. Thread Safety
PriorityBlockingQueue: You must use this instead of a standard PriorityQueue. The blocking version handles concurrent access to the "earliest task."

Worker Pool: You shouldn't run the task in the monitor thread; otherwise, a long-running task would block the entire scheduler from checking other tasks.

2. Precision vs. Efficiency
The interviewer will ask: "Why did you use Thread.sleep in the monitor?"

The SDE 4 Answer: "Sleeping for the exact duration until the next task is efficient. However, if a new task is added with an even earlier time, the monitor must wake up. In a production system, I'd use a Condition variable where schedule() calls signal() to wake the monitor early if a new 'earliest' task arrives."

3. Distributed Aspect (The "SDE 4" twist)
The prompt says "Distributed." In an LLD interview, they often let you code the in-memory version but will ask: "How do you make this work across 10 servers?"

The Answer: "I would move the PriorityQueue to a persistent store like Redis (using Sorted Sets/ZSET) or a DB. Each server would try to 'Lock' a task (using SETNX in Redis or SELECT FOR UPDATE in SQL) before executing it to ensure no two servers run the same task."

4. Error Handling & Retries
How do you handle a task that fails? An SDE 4 should suggest an Exponential Backoff retry strategy where the task is re-added to the queue with a new executionTime.*/
