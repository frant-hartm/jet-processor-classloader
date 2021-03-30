package org.example.jet.hz3sources;

import com.hazelcast.internal.serialization.Data;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.jet.core.metrics.Metrics;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.SinkBuilder;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import org.example.jet.hz3context.Hz3Context;

import java.lang.reflect.Constructor;

public final class HzSources {
    private static final Constructor hz5HeapDataConstructor;

    static {
        try {
            // this is needed to make sure we use HeapData from Hazelcast 5
            hz5HeapDataConstructor = StreamSource.class.getClassLoader().loadClass("com.hazelcast.internal.serialization.impl.HeapData").getConstructor(byte[].class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public static StreamSource<Object> hz3QueueSource(String remoteClusterXmlConfig, String queueName) {
        return SourceBuilder.stream("source", c -> new Hz3Context(remoteClusterXmlConfig, queueName) )
                .fillBufferFn((c, b) -> {
                    byte[] bytes = c.takeObject();
                    Object o = hz5HeapDataConstructor.newInstance(bytes);
                    b.add(o);
                })
                .build();
    }

    public static Sink<Object> localClusterQueueSink(String queueName) {
        Sink<Object> queueSink = SinkBuilder.sinkBuilder("dst-queue-sink", (c) -> {
            return c.jetInstance().getHazelcastInstance().getQueue(queueName);
        }).receiveFn((objects, e) -> {
            objects.offer(e);
            Metrics.metric("offered").increment();
        }).build();
        return queueSink;
    }
}
