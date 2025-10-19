package ch.brogli.graalvmperformance.receiver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    private final RequestReceiverService requestReceiverService;

    public PingController(RequestReceiverService requestReceiverService) {

        this.requestReceiverService = requestReceiverService;
    }

    @GetMapping("/ping")
    public int ping() throws InterruptedException {
        return requestReceiverService.getPing();
    }

    @GetMapping("/threadname")
    public String getThreadName() {
        return Thread.currentThread().toString();
    }
}
