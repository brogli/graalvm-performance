package ch.brogli.graalvmperformance.receiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RequestPerSecondCalculator {
    private final ConcurrentLinkedDeque<Long> requestTimestamps = new ConcurrentLinkedDeque<>();

    private static final int MAX_WINDOW_SECONDS = 5;
    private static final long MAX_WINDOW_NANOS = TimeUnit.SECONDS.toNanos(MAX_WINDOW_SECONDS);

    public void recordRequest() {
        requestTimestamps.addLast(System.nanoTime());
    }

    @Scheduled(fixedDelay = 5000) // Cleanup every 5 seconds
    public void pruneOldEntries() {
        long cutoff = System.nanoTime() - MAX_WINDOW_NANOS * 3;

        while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() < cutoff) {
            requestTimestamps.pollFirst();
        }

        log.info("Pruned old entries. Remaining: {}", requestTimestamps.size());
    }

    public double getRequestsPerSecond() {
        long now = System.nanoTime();
        long cutoffNanos = now - MAX_WINDOW_NANOS;

        long count = requestTimestamps.stream()
                .filter(t -> t >= cutoffNanos).count()
                ;
        return count / (double) MAX_WINDOW_SECONDS;
    }
}
