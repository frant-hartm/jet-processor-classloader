package org.example.jet;

import com.hazelcast.jet.pipeline.BatchSource;
import com.hazelcast.jet.pipeline.test.TestSources;

public class DummySource {

    public static BatchSource<String> source() {
        return TestSources.items("A", "B", "C");
    }
}
