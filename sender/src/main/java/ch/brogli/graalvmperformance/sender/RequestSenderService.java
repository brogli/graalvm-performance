package ch.brogli.graalvmperformance.sender;

import ch.brogli.graalvmperformance.common.ItemsPerSecondMovingAverage;
import com.sun.management.OperatingSystemMXBean;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

@Service
@Slf4j
public class RequestSenderService {

    private final PingClient pingClient;
    private final TaskScheduler taskScheduler;
    private final MeterRegistry meterRegistry;
    private ScheduledFuture<?> scheduledTask;
    private final ItemsPerSecondMovingAverage requestsSent;
    private final ItemsPerSecondMovingAverage responsesReceived;
    private final ItemsPerSecondMovingAverage exceptionsCaught;
    private final MemoryMXBean memoryMxBean;
    private final OperatingSystemMXBean osBean;


    public RequestSenderService(PingClient pingClient, TaskScheduler taskScheduler, MeterRegistry meterRegistry) {
        this.pingClient = pingClient;
        this.taskScheduler = taskScheduler;
        this.meterRegistry = meterRegistry;
        this.requestsSent = new ItemsPerSecondMovingAverage(5);
        this.responsesReceived = new ItemsPerSecondMovingAverage(5);
        this.exceptionsCaught = new ItemsPerSecondMovingAverage(5);
        this.memoryMxBean = ManagementFactory.getMemoryMXBean();

        this.osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class
        );
        Gauge.builder("sender_requests_sent_per_second", getRequestsSentPerSecond())
                .description("Requests per Second")
                .register(meterRegistry);
        Gauge.builder("sender_responses_received_per_second", getResponsesReceivedPerSecond())
                .description("Responses per Second")
                .register(meterRegistry);
        Gauge.builder("sender_exceptions_caught_per_second", getExceptionsCaughtPerSecond())
                .description("Responses per Second")
                .register(meterRegistry);


        Gauge.builder("receiver_memory_mx_used", getMemoryMxUsed())
                .description("MemoryMx Used")
                .register(meterRegistry);

        Gauge.builder("receiver_memory_mx_committed", getMemoryMxCommitted())
                .description("MemoryMx Committed")
                .register(meterRegistry);

        Gauge.builder("receiver_total_memory_size", getTotalMemorySize())
                .description("MemoryMx Committed")
                .register(meterRegistry);
    }

    public void startSending(int targetRequestsPerSecond) {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            log.info("start sending requests");
            double intervalMs = (double) 1000 / (targetRequestsPerSecond);
            long intervalNanos = (long) (intervalMs * 1_000_000);
            scheduledTask = taskScheduler.scheduleAtFixedRate(
                    this::sendRequest,
                    Duration.ofNanos(intervalNanos)
            );
        }
    }

    public void stopSending() {
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            log.info("stop sending requests");
            scheduledTask.cancel(false);
        }
    }

    public void sendRequest() {
        var t = Thread.currentThread();
        log.debug("tick on {} (id={}, virtual={})", t.getName(), t.threadId(), t.isVirtual());

        requestsSent.recordTime();
        try {
            String response = pingClient.getPing();
            if (response != null) {
                responsesReceived.recordTime();
            }
        } catch (Exception e) {
            exceptionsCaught.recordTime();
            log.error("request failed", e);
        }
    }

    private Supplier<Double> getRequestsSentPerSecond() {
        return requestsSent::getRecordingsPerSecond;
    }

    private Supplier<Double> getResponsesReceivedPerSecond() {
        return responsesReceived::getRecordingsPerSecond;
    }

    private Supplier<Double> getExceptionsCaughtPerSecond() {
        return exceptionsCaught::getRecordingsPerSecond;
    }

    private Supplier<Long> getMemoryMxUsed() {
        return () -> memoryMxBean.getHeapMemoryUsage().getUsed() + memoryMxBean.getNonHeapMemoryUsage().getUsed();
    }

    private Supplier<Long> getTotalMemorySize() {
        return osBean::getTotalMemorySize;
    }

    private Supplier<Long> getMemoryMxCommitted() {
        return () -> memoryMxBean.getHeapMemoryUsage().getCommitted() + memoryMxBean.getNonHeapMemoryUsage().getCommitted();
    }
}
