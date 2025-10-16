package ch.brogli.graalvmperformance.sender;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RequestSender {

    private final PingClient pingClient;

    public RequestSender(PingClient pingClient) {
        this.pingClient = pingClient;
    }

    @Scheduled(fixedRate = 500)
    public void send() {
        pingClient.getPing();
    }
}
