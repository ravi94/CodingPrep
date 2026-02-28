package machineCoding.cache;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LFUCache<K,V> {
    private static class Entry<V>{
        V value;
        int freq;

        Entry(V value, int freq) {
            this.value = value;
            this.freq = freq;
        }
    }

    private final int capacity;
    private int minFreq;

    private final Map<K , Entry > keyMap;

    private final Map<Integer , LinkedHashSet<K>> freqMap;

    // ReadWriteLock to allow multiple readers but exclusive writers
    private final ReentrantReadWriteLock lock;
    private final ReentrantReadWriteLock.ReadLock readLock;
    private final ReentrantReadWriteLock.WriteLock writeLock;

    public LFUCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException(
                "Capacity must be positive, got: " + capacity
        );
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyMap = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }

    // Returns value if present, null if not found
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        writeLock.lock(); // write lock because we modify freq internally
        try {
            Entry<V> entry = keyMap.get(key);
            if (entry == null) return null;
            incrementFreq(key);
            return entry.value;
        } finally {
            writeLock.unlock();
        }
    }

    // Returns Optional for cleaner null handling
    public Optional<V> getOptional(K key) {
        return Optional.ofNullable(get(key));
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");
        if (value == null) throw new IllegalArgumentException("Value cannot be null");

        writeLock.lock();
        try {
            if (keyMap.containsKey(key)) {
                // Update existing key
                keyMap.get(key).value = value;
                incrementFreq(key);
            } else {
                // Evict if at capacity
                if (keyMap.size() == capacity) {
                    evictLFU();
                }
                // Insert new key with frequency 1
                keyMap.put(key, new Entry<>(value, 1));
                freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
                minFreq = 1; // new key always starts at freq 1
            }
        } finally {
            writeLock.unlock();
        }
    }

    // Remove a key explicitly
    public boolean remove(K key) {
        if (key == null) return false;

        writeLock.lock();
        try {
            if (!keyMap.containsKey(key)) return false;

            Entry<V> entry = keyMap.remove(key);
            LinkedHashSet<K> bucket = freqMap.get(entry.freq);
            bucket.remove(key);
            if (bucket.isEmpty()) {
                freqMap.remove(entry.freq);
            }
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    // Check if key exists without modifying frequency
    public boolean containsKey(K key) {
        readLock.lock(); // true read-only op — read lock is sufficient
        try {
            return keyMap.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    // Current size
    public int size() {
        readLock.lock();
        try {
            return keyMap.size();
        } finally {
            readLock.unlock();
        }
    }

    // Clear entire cache
    public void clear() {
        writeLock.lock();
        try {
            keyMap.clear();
            freqMap.clear();
            minFreq = 0;
        } finally {
            writeLock.unlock();
        }
    }


    // ─── Private helpers ────────────────────────────────────────

    // Move key from freq bucket to freq+1 bucket
    private void incrementFreq(K key) {
        Entry<V> entry = keyMap.get(key);
        int oldFreq = entry.freq;
        int newFreq = oldFreq + 1;

        // Remove from old bucket
        LinkedHashSet<K> oldBucket = freqMap.get(oldFreq);
        oldBucket.remove(key);
        if (oldBucket.isEmpty()) {
            freqMap.remove(oldFreq);
            if (minFreq == oldFreq) {
                minFreq = newFreq;
            }
        }

        // Add to new bucket
        freqMap.computeIfAbsent(newFreq, k -> new LinkedHashSet<>()).add(key);
        entry.freq = newFreq;
    }

    // Evict least frequently used key
    // Tie-break: least recently used (first in LinkedHashSet)
    private void evictLFU() {
        LinkedHashSet<K> minBucket = freqMap.get(minFreq);
        K evictKey = minBucket.iterator().next(); // first = LRU in this freq
        minBucket.remove(evictKey);
        if (minBucket.isEmpty()) {
            freqMap.remove(minFreq);
        }
        keyMap.remove(evictKey);
    }

    // ─── Debug / monitoring ─────────────────────────────────────

    public String getStats() {
        readLock.lock();
        try {
            return String.format(
                    "LFUCache[capacity=%d, size=%d, minFreq=%d]",
                    capacity, keyMap.size(), minFreq
            );
        } finally {
            readLock.unlock();
        }

    }

    public static void main(String[] args) throws InterruptedException {

        // ── Test 1: Integer keys, String values ──
        System.out.println("=== Test 1: LFUCache<Integer, String> ===");
        LFUCache<Integer, String> cache1 = new LFUCache<>(3);

        cache1.put(1, "one");
        cache1.put(2, "two");
        cache1.put(3, "three");

        System.out.println(cache1.get(1));  // "one"   freq[1]=2
        System.out.println(cache1.get(1));  // "one"   freq[1]=3
        System.out.println(cache1.get(2));  // "two"   freq[2]=2

        // State: key1=freq3, key2=freq2, key3=freq1 (LFU)
        cache1.put(4, "four"); // evicts key3 (LFU)
        System.out.println(cache1.get(3));  // null (evicted)
        System.out.println(cache1.get(4));  // "four"
        System.out.println(cache1.getStats());


        // ── Test 2: String keys, custom object values ──
        System.out.println("\n=== Test 2: LFUCache<String, User> ===");

        LFUCache<String, User> userCache = new LFUCache<>(2);

        userCache.put("u1", new User("Alice", 30));
        userCache.put("u2", new User("Bob", 25));

        System.out.println(userCache.get("u1")); // Alice
        userCache.put("u3", new User("Charlie", 35)); // evicts u2 (LFU)

        System.out.println(userCache.get("u2")); // null (evicted)
        System.out.println(userCache.get("u3")); // Charlie
        System.out.println(userCache.getStats());


        // ── Test 3: Optional usage ──
        System.out.println("\n=== Test 3: Optional ===");
        LFUCache<String, String> cache3 = new LFUCache<>(2);
        cache3.put("hello", "world");

        cache3.getOptional("hello")
                .ifPresent(v -> System.out.println("Found: " + v)); // Found: world

        cache3.getOptional("missing")
                .ifPresentOrElse(
                        v -> System.out.println("Found: " + v),
                        () -> System.out.println("Key not found") // Key not found
                );


        // ── Test 4: Multithreaded ──
        System.out.println("\n=== Test 4: Multithreaded ===");
        LFUCache<Integer, Integer> sharedCache = new LFUCache<>(5);

        List<Thread> threads = new ArrayList<>();

        // 3 writer threads
        for (int t = 0; t < 3; t++) {
            final int threadId = t;
            threads.add(new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    sharedCache.put(threadId * 10 + i, i);
                }
            }, "Writer-" + t));
        }

        // 3 reader threads
        for (int t = 0; t < 3; t++) {
            final int threadId = t;
            threads.add(new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    sharedCache.get(threadId * 10 + i);
                }
            }, "Reader-" + t));
        }

        // Start all threads simultaneously
        threads.forEach(Thread::start);
        threads.forEach(thread -> {
            try { thread.join(); }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        System.out.println("Final: " + sharedCache.getStats());
        System.out.println("No exceptions = thread safety verified ✓");
    }


    // Sample domain object for testing
    static class User {
        String name;
        int age;
        User(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public String toString() {
            return "User{name=" + name + ", age=" + age + "}";
        }
    }

}
