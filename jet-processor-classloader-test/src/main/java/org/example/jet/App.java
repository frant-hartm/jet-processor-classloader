package org.example.jet;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.BatchSource;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Hello world!
 *
 */
public class App
{
    public static ClassLoader classLoader;
    public static final String path1 = "file:///home/frantisek/work/hz/repos/projectx_march_2021/jet-processor-classloader-v1/target/jet-processor-classloader-v1-1.0-SNAPSHOT.jar";
    public static final String path2 = "file:///home/frantisek/work/hz/repos/projectx_march_2021/jet-processor-classloader-v2/target/jet-processor-classloader-v2-1.0-SNAPSHOT.jar";

    static {
        try {
            classLoader = new URLClassLoader(new URL[] { new URL(path1), new URL(path2) });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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

//        ReplicatedMap<Object, String> replicatedMap = hz.getReplicatedMap("rmap");
        JetInstance jet = hz.getJetInstance();
        JobConfig config = new JobConfig();
        jet.newJob(p, config).join();

        hz.shutdown();
    }
}