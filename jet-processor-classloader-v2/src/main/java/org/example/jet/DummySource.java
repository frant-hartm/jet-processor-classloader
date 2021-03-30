package org.example.jet;

import com.hazelcast.jet.pipeline.BatchSource;
import com.hazelcast.jet.pipeline.SourceBuilder;
import com.hazelcast.jet.pipeline.test.TestSources;

public class DummySource {

    public static BatchSource<String> source() {
        return TestSources.items("A", "B", "C");
    }

    public static BatchSource<String> source2() {
        return SourceBuilder.batch("source", context -> new int[1])
                .fillBufferFn((int[] obj, SourceBuilder.SourceBuffer<String> buffer) -> {
                    if (obj[0] < 10) {
                        buffer.add(Integer.toString(10 - obj[0]++));
                    } else {
                        buffer.close();
                    }
                }).build();
    }
}
