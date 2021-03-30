package org.example.jet;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JobConfig;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.StreamSource;
import org.example.jet.hz3sources.HzSources;
import org.example.jet.queuesink.QueueSink;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class ClientToSubmit
{
    public static ClassLoader classLoader;
    public static final String path1 = "file:////home/jara/devel/oss/jet-processor-classloader/jet-processor-classloader-v1/target/jet-processor-classloader-v1-1.0-SNAPSHOT.jar";
    public static final String path2 = "file:////home/jara/devel/oss/jet-processor-classloader/jet-processor-classloader-v2/target/jet-processor-classloader-v2-1.0-SNAPSHOT.jar";

    private static final String SRC_CLIENT_CONFIG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<hazelcast-client xmlns=\"http://www.hazelcast.com/schema/client-config\"\n"
            + "                  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "                  xsi:schemaLocation=\"http://www.hazelcast.com/schema/client-config\n"
            + "                  http://www.hazelcast.com/schema/client-config/hazelcast-client-config-3.12.xsd\">\n"
            + "\n"
            + "    <network>\n"
            + "        <cluster-members>\n"
            + "            <address>127.0.0.1:3120</address>\n"
            + "        </cluster-members>\n"
            + "    </network>\n"
            + "</hazelcast-client>\n";

    static {
        try {
            classLoader = new URLClassLoader(new URL[] { new URL(path1), new URL(path2) });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args )
    {
        StreamSource<Object> src = HzSources.hz3QueueSource(SRC_CLIENT_CONFIG, "myQueue");
        Pipeline p = Pipeline.create();
        p.readFrom(src)
                .withoutTimestamps()
                .writeTo(QueueSink.localSink("dst"));

        HazelcastInstance hz = HazelcastClient.newHazelcastClient();

//        ReplicatedMap<Object, String> replicatedMap = hz.getReplicatedMap("rmap");
        JetInstance jet = hz.getJetInstance();
        JobConfig config = new JobConfig();
        List<String> jars = new ArrayList<>();
        jars.add("file:///home/jara/devel/oss/jet-processor-classloader/hz3sources/target/hz3sources-1.0-SNAPSHOT.jar");
        jars.add("file:///home/jara/devel/oss/jet-processor-classloader/hz3context/target/hz3context-1.0-SNAPSHOT.jar");
        jars.add("file:///home/jara/.m2/repository/com/hazelcast/hazelcast/3.12.11/hazelcast-3.12.11.jar");
        jars.add("file:///home/jara/.m2/repository/com/hazelcast/hazelcast-client/3.12.11/hazelcast-client-3.12.11.jar");
        config.addCustomClasspath("source", jars);
        jet.newJob(p, config).join();

        hz.shutdown();
    }
}