package ru.t1.java.demo.service.impl;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.t1.java.demo.model.enums.Metrics;
import ru.t1.java.demo.service.mterics.MetricCounter;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private MetricCounter metricCounter;

    @InjectMocks
    private MetricServiceImpl metricService;

    private static final String METRIC_NAME = Metrics.CLIENT_CONTROLLER_REQUEST_COUNT.getValue();

    @Test
    void testIncrementByName_Success() {
        Map<String, MetricCounter> counters = new HashMap<>();
        counters.put(METRIC_NAME, metricCounter);

        setCountersMap(metricService, counters);

        doNothing().when(metricCounter).increment();

        metricService.incrementByName(METRIC_NAME);

        verify(metricCounter, times(1)).increment();
        metricCounter.increment();
    }

    private void setCountersMap(MetricServiceImpl service, Map<String, MetricCounter> counters) {
        try {
            var field = MetricServiceImpl.class.getDeclaredField("counters");
            field.setAccessible(true);
            field.set(service, counters);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("failed to set counters map", e);
        }
    }
}
