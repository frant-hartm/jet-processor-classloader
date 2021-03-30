package org.example.jet;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Pipeline p = Pipeline.create();

        p.readFrom(DummySource.source())
                .writeTo(Sinks.logger());

        HazelcastInstance hz = Hazelcast.newHazelcastInstance();

        JetInstance jet = hz.getJetInstance();
        jet.newJob(p).join();

        hz.shutdown();
    }
}
