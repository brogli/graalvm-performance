package ch.brogli.graalvmperformance.receiver;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ItemsPerSecondMovingAverage {
    private final ConcurrentLinkedDeque<Long> timestamps = new ConcurrentLinkedDeque<>();

    private final int MAX_WINDOW_SECONDS;
    private final long MAX_WINDOW_NANOS;

    public ItemsPerSecondMovingAverage(int maxWindowSeconds) {
        this.MAX_WINDOW_SECONDS = maxWindowSeconds;
        this.MAX_WINDOW_NANOS  = TimeUnit.SECONDS.toNanos(MAX_WINDOW_SECONDS);
    }

    public void recordTime() {
        var now = System.nanoTime();
        var cutoffNanos = now - MAX_WINDOW_NANOS;
        // prune old entries
        while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoffNanos) {
            timestamps.pollFirst();
        }
        log.info("Pruned old entries. Remaining: {}", timestamps.size());

        timestamps.addLast(now);
    }

    public double getRecordingsPerSecond() {
        long now = System.nanoTime();
        long cutoffNanos = now - MAX_WINDOW_NANOS;

        long count = timestamps.stream()
                .filter(t -> t >= cutoffNanos).count();
        return count / (double) MAX_WINDOW_SECONDS;
    }
}
