package ch.brogli.graalvmperformance.receiver;

import ch.brogli.graalvmperformance.common.ItemsPerSecondMovingAverage;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.function.Supplier;

@Service
public class RequestReceiverService {

    private final MeterRegistry meterRegistry;
    private final ItemsPerSecondMovingAverage requestsPerSecondMovingAverage;

    public RequestReceiverService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestsPerSecondMovingAverage = new ItemsPerSecondMovingAverage(5);
        Gauge.builder("api_requests_per_second", getRequestsPerSecond())
                .description("Requests per Second")
                .register(meterRegistry);
    }

    public int getPing() {
        requestsPerSecondMovingAverage.recordTime();
        return new Random().nextInt(1000);
    }

    private Supplier<Double> getRequestsPerSecond() {
        return requestsPerSecondMovingAverage::getRecordingsPerSecond;
    }
}
