package org.example.jet.queuesink;

import com.hazelcast.jet.pipeline.Sink;
import com.hazelcast.jet.pipeline.SinkBuilder;

import java.util.concurrent.BlockingQueue;

public class QueueSink {
    public static Sink<Object> localSink(String queueName) {
        return SinkBuilder.sinkBuilder("local-queue-sink", c -> c.jetInstance().getHazelcastInstance().getQueue(queueName))
                .receiveFn(BlockingQueue::put)
                .build();
    }
}
