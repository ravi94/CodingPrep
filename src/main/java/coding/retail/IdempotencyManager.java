package coding.retail;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdempotencyManager<T> {

    public enum Status { IN_PROGRESS, COMPLETED, FAILED }

    public record IdempotencyRecord<T>(Status status, T response) {}

    private final Map<String, IdempotencyRecord<T>> storage = new ConcurrentHashMap<>();

    public synchronized boolean startRequest(String key) {
        if (storage.containsKey(key)) {
            return false; // Already exists (in progress or finished)
        }
        storage.put(key, new IdempotencyRecord<>(Status.IN_PROGRESS, null));
        return true;
    }

    public void completeRequest(String key, T response) {
        storage.put(key, new IdempotencyRecord<>(Status.COMPLETED, response));
    }

    public void failRequest(String key) {
        storage.remove(key); // Allow retry
    }

    public IdempotencyRecord<T> getRecord(String key) {
        return storage.get(key);
    }
}