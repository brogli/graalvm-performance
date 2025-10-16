package ch.brogli.graalvmperformance.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RequestSender {

    private final PingClient pingClient;

    public RequestSender(PingClient pingClient) {
        this.pingClient = pingClient;
    }

    @Scheduled(fixedRate = 100)
    public void send() {
        var t = Thread.currentThread();
        log.info("tick on {} (id={}, virtual={})", t.getName(), t.threadId(), t.isVirtual());


        try {
            pingClient.getPing();
        } catch (Exception e) {
            log.error("request failed", e);
        }
    }
}
