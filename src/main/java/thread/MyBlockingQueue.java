package thread;

import java.util.*;
import java.util.concurrent.locks.*;

public class MyBlockingQueue<T> {

    private final Queue<T> queue;
    private final int capacity;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final Condition notFull = lock.newCondition();

    public MyBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

    // Producer method
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await();   // wait until space available
            }

            queue.add(item);
            notEmpty.signal();    // signal consumer
        } finally {
            lock.unlock();
        }
    }

    // Consumer method
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await();   // wait until item available
            }

            T item = queue.poll();
            notFull.signal();      // signal producer
            return item;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
