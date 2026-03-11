/*
 * Copyright (c) 2026. Gryphus Lab
 */
package ch.gryphus.chainvault.delegate;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import java.util.Collections;
import java.util.Map;

/**
 * The type O tel utils.
 */
public class OTelUtils {
    private OTelUtils() {
        /* This utility class should not be instantiated */
    }

    // Define the Getter explicitly
    private static final TextMapGetter<Map<String, String>> GETTER = new MapTextMapGetter();

    /**
     * Extract context from trace parent context.
     *
     * @param traceParent the trace parent
     * @return the context
     */
    public static Context extractContextFromTraceParent(String traceParent) {
        if (traceParent == null || traceParent.isEmpty()) {
            return Context.root();
        }

        Map<String, String> carrier = Collections.singletonMap("traceparent", traceParent);

        // Use the explicit GETTER instead of the lambda
        return GlobalOpenTelemetry.getPropagators()
                .getTextMapPropagator()
                .extract(Context.current(), carrier, GETTER);
    }

    private static class MapTextMapGetter implements TextMapGetter<Map<String, String>> {
        @Override
        public String get(Map<String, String> carrier, String s) {
            return carrier != null ? carrier.get(s) : null;
        }

        @Override
        public Iterable<String> keys(Map<String, String> carrier) {
            return carrier != null ? carrier.keySet() : Collections.emptyList();
        }
    }
}
