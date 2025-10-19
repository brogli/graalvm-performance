package ch.brogli.graalvmperformance.sender;

import ch.brogli.graalvmperformance.common.ItemsPerSecondMovingAverage;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

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

    public RequestSenderService(PingClient pingClient, TaskScheduler taskScheduler, MeterRegistry meterRegistry) {
        this.pingClient = pingClient;
        this.taskScheduler = taskScheduler;
        this.meterRegistry = meterRegistry;
        this.requestsSent = new ItemsPerSecondMovingAverage(5);
        this.responsesReceived = new ItemsPerSecondMovingAverage(5);
        Gauge.builder("sender_requests_sent_per_second", getRequestsSentPerSecond())
                .description("Requests per Second")
                .register(meterRegistry);
        Gauge.builder("sender_responses_received_per_second", getResponsesReceivedPerSecond())
                .description("Responses per Second")
                .register(meterRegistry);
    }

    public void startSending(int intervalMs) {
        if (scheduledTask == null || scheduledTask.isCancelled()) {
            log.info("start sending requests");
            scheduledTask = taskScheduler.scheduleAtFixedRate(
                    this::sendRequest,
                    Duration.ofMillis(intervalMs)
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
            log.error("request failed", e);
        }
    }

    private Supplier<Double> getRequestsSentPerSecond() {
        return requestsSent::getRecordingsPerSecond;
    }

    private Supplier<Double> getResponsesReceivedPerSecond() {
        return responsesReceived::getRecordingsPerSecond;
    }
}
