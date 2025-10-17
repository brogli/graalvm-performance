package ch.brogli.graalvmperformance.sender;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class RequestSenderController {

    private final RequestSender sender;

    public RequestSenderController(RequestSender sender) {
        this.sender = sender;
    }
    public record StartRequest(int intervalMs) {}

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void start(@RequestBody StartRequest request) {
        sender.startSending(request.intervalMs());
    }

    @PostMapping("/stop")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    public void stop() {
        sender.stopSending();
    }
}
