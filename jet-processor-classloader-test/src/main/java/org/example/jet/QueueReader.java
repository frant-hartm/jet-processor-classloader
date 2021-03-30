package org.example.jet;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.internal.serialization.Data;
import com.hazelcast.internal.serialization.impl.HeapData;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.core.metrics.Metrics;
import com.hazelcast.jet.pipeline.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Hello world!
 *
 */
public class QueueReader
{
    public static ClassLoader classLoader;
    public static final String path = "file:///home/frantisek/.m2/repository/com/hazelcast/hazelcast/3.12.11/hazelcast-3.12.11.jar";

    static {
        try {
            classLoader = new URLClassLoader(new URL[] { new URL(path)});
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public static StreamSource<Data> queue(String queueName,
                                           String clientConfigXml) {

        return SourceBuilder.stream("remoteQueue3Source", c -> new QueueContextObject(clientConfigXml, queueName))
                .<Data>fillBufferFn((c, b) -> {
                    byte[] blob = c.takeBytes();
                    Metrics.metric("taken").increment();
                    b.add(new HeapData(blob));
                })
                .build();
    }

    public static void main( String[] args )
    {
        Pipeline p = Pipeline.create();

        Class<?> dummySourceClass = null;
        Object invoked = null;
        try {
            dummySourceClass = classLoader.loadClass("org.example.jet.DummySource");
            Method sourceMethod = dummySourceClass.getMethod("source2");
            invoked = sourceMethod.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assert dummySourceClass != null;
        assert invoked != null;
        System.out.println(dummySourceClass.getMethods().length);

        p.readFrom((BatchSource) invoked)
                .writeTo(Sinks.logger());

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(new Config().setClusterName("frantisek"));

        JetInstance jet = hz.getJetInstance();
        JobConfig config = new JobConfig();
        jet.newJob(p, config).join();

        hz.shutdown();
    }
}