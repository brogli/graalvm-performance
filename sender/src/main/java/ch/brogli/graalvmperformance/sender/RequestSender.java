package ch.brogli.graalvmperformance.sender;

import ch.brogli.graalvmperformance.common.ItemsPerSecondMovingAverage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

@Service
@Slf4j
public class RequestSender {

    private final PingClient pingClient;
    private final TaskScheduler taskScheduler;
    private ScheduledFuture<?> scheduledTask;
    private final ItemsPerSecondMovingAverage requestsSent;

    public RequestSender(PingClient pingClient, TaskScheduler taskScheduler) {
        this.pingClient = pingClient;
        this.taskScheduler = taskScheduler;
        this.requestsSent = new ItemsPerSecondMovingAverage(5);
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


        try {
            pingClient.getPing();
        } catch (Exception e) {
            log.error("request failed", e);
        }
    }
}
