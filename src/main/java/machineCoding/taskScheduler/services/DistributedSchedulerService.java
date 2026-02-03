package machineCoding.taskScheduler.services;

import lombok.Data;
import machineCoding.taskScheduler.entities.Task;
import machineCoding.taskScheduler.entities.TaskStatus;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class DistributedSchedulerService {
    private final PriorityBlockingQueue<Task> taskQueue ;
    private final ExecutorService  workerPool;
    private final AtomicInteger taskCounter ;



    // The Signaling Mechanism
    private final Lock lock = new ReentrantLock();
    private final Condition newNodeAdded = lock.newCondition();

    public DistributedSchedulerService(int corePoolSize){
        this.taskQueue = new PriorityBlockingQueue<>();
        this.workerPool = Executors.newFixedThreadPool(corePoolSize);
        this.taskCounter = new AtomicInteger(0);

        startMonitor();
    }

    public void schedule(Task task) {
        lock.lock();
        try {
            taskQueue.offer(task);
            // Signal the monitor thread that a new task is available.
            // It will wake up and re-evaluate if this new task is the earliest.
            newNodeAdded.signal();
            System.out.println("Scheduled Task: " + task.getId());
        } finally {
            lock.unlock();
        }
    }

    private void startMonitor(){
        Thread monitorThread = new Thread( ()->{
            while(true){
                lock.lock();
                try {
                    Task latestTask = taskQueue.peek();
                    if(latestTask == null){
                        newNodeAdded.await(); // wait till new task is added; and release the lock , hence no dead lock
                    }else  {

                        long delay = latestTask.getExecutionTimeInEpoch() -System.currentTimeMillis();
                        if (delay >=0) {
                            // time to run the task
                            Task taskToRun = taskQueue.poll();
                            if (taskToRun != null) {
                                workerPool.submit(() -> executeTask(taskToRun));
                            }
                        }else{
                            //Wait until the task is due OR until a new task is added
                            // awaitNanos returns 0 or less if the time elapsed
                            newNodeAdded.awaitNanos(TimeUnit.MILLISECONDS.toNanos(delay));
                        }
                    }
                }catch(Exception e){
                    Thread.currentThread().interrupt();
                    break;
                }finally {
                    lock.unlock();
                }
            }
        });
        monitorThread.setDaemon(true); // run in background
        monitorThread.start();
    };

    private void executeTask(Task taskToRun){
        try{
            taskToRun.setStatus(TaskStatus.RUNNING);
            System.out.println("Executing Task: " + taskToRun.getId() + " at " + System.currentTimeMillis());
            taskToRun.getAction().run();
            taskToRun.setStatus(TaskStatus.COMPLETED);
        }catch (Exception e){
            handleFailure(taskToRun);
        }
    }

    private void handleFailure(Task task){
        if(task.getRetryCount() < task.getMaxRetries()){
            task.incrementRetryCount();
            long nextRunTime = task.calculateNextRetryTime();

            Task retryTask = new Task(
                    task.getId() + "_retry_" + task.getRetryCount(),
                    nextRunTime,
                    task.getPriority(),
                    task.getAction()
            );

            System.out.println("Task " + task.getId() + " failed. Retrying in " +
                    (nextRunTime - System.currentTimeMillis()) + "ms");

            this.schedule(retryTask);
        }else{
            task.setStatus(TaskStatus.FAILED);
            System.err.println("Task " + task.getId() + " exceeded max retries. Dropping.");
        }

    }



}

/*However, Condition.await() (and awaitNanos) is designed specifically to prevent this:

Atomic Release: The moment a thread calls await(), it atomically releases the lock and goes to sleep in one single step.

Producer Entry: Because the lock is now free, the schedule() thread (the Producer) can successfully call lock.lock(), add a task, and call signal().

Atomic Re-acquisition: When the sleeping thread is "woken up" (by a signal or a timeout), it doesn't just start running. It stays in a waiting state until it can re-acquire the lock. Once the Producer calls unlock(), the Monitor grabs the key back and continues.*/
