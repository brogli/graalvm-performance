package ch.brogli.graalvmperformance.receiver;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {

        this.pingService = pingService;
    }

    @GetMapping("/ping")
    public int ping() {
        return pingService.getPing();

    }

    @GetMapping("/threadname")
    public String getThreadName() {
        return Thread.currentThread().toString();
    }
}
