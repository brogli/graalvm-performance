package ch.brogli.graalvmperformance.receiver;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.function.Supplier;

@Service
public class PingService {

    private final MeterRegistry meterRegistry;
    private final RequestPerSecondCalculator requestPerSecondCalculator;


    public PingService(MeterRegistry meterRegistry, RequestPerSecondCalculator requestPerSecondCalculator) {
        this.meterRegistry = meterRegistry;
        this.requestPerSecondCalculator = requestPerSecondCalculator;
        Gauge.builder("api_requests_per_second", getRequestsPerSecond())
                .description("Requests per Second")
                .register(meterRegistry);
    }

    public int getPing() {
        requestPerSecondCalculator.recordRequest();
        return new Random().nextInt(1000);
    }

    private Supplier<Double> getRequestsPerSecond() {
        return requestPerSecondCalculator::getRequestsPerSecond;
    }
}
