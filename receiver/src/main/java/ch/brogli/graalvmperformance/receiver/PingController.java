package ch.brogli.graalvmperformance.receiver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class PingController {

    @GetMapping("/ping")
    public int ping() {
        return new Random().nextInt(1000);
    }
}
