package org.example.jet.hz3sources;

import com.hazelcast.jet.core.metrics.Metrics;
import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.SinkBuilder;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.StreamSource;
import org.example.jet.hz3context.Hz3Context;

public final class HzSources {
    public static StreamSource<Object> hz3QueueSource(String remoteClusterXmlConfig, String queueName) {
        return SourceBuilder.stream("my-source", c -> new Hz3Context(remoteClusterXmlConfig, queueName) )
                .fillBufferFn((c, b) -> b.add(c.takeObject()))
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
