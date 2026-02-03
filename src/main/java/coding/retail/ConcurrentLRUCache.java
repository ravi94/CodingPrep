package coding.retail;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentLRUCache<K, V> {

    // Custom Node to allow O(1) removal and addition
    private class Node {
        K key;
        V value;
        Node prev, next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final ConcurrentHashMap<K, Node> map;
    private final Node head, tail;

    // ReadWriteLock to allow multiple readers but exclusive writers
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public ConcurrentLRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity);

        // Dummy Head and Tail to simplify boundary logic
        this.head = new Node(null, null);
        this.tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        readLock.lock();
        try {
            Node node = map.get(key);
            if (node == null) return null;

            // Move to head requires structural change -> Upgrade to Write Lock
            readLock.unlock();
            writeLock.lock();
            try {
                // Double check if node still exists after acquiring write lock
                if (map.containsKey(key)) {
                    moveToHead(node);
                }
                return node.value;
            } finally {
                writeLock.unlock();
                readLock.lock(); // Downgrade
            }
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, V value) {
        writeLock.lock();
        try {
            if (map.containsKey(key)) {
                Node node = map.get(key);
                node.value = value;
                moveToHead(node);
            } else {
                if (map.size() >= capacity) {
                    // Evict the Least Recently Used (Tail)
                    Node lru = tail.prev;
                    removeNode(lru);
                    map.remove(lru.key);
                }
                Node newNode = new Node(key, value);
                addNode(newNode);
                map.put(key, newNode);
            }
        } finally {
            writeLock.unlock();
        }
    }

    // --- Internal List Logic (Assumes Write Lock is held) ---
    private void addNode(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addNode(node);
    }

    /**
     * Returns the current number of items in the cache.
     * Useful for monitoring and unit testing.
     */
    public int getPendingCount() {
        // We use the readLock to ensure we aren't mid-eviction
        // if strict consistency is needed for the test.
        readLock.lock();
        try {
            return map.size();
        } finally {
            readLock.unlock();
        }
    }
}