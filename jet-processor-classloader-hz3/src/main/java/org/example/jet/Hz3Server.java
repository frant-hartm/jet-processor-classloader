package org.example.jet;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

/**
 * Hello world!
 *
 */
public class Hz3Server {

    public static void main(String[] args) throws InterruptedException {

        HazelcastInstance hz = Hazelcast.newHazelcastInstance(new Config().setGroupConfig(new GroupConfig("frantisek")));
        IQueue<String> queue = hz.getQueue("my-queue");

        int i = 0;
        while (true) {

            queue.offer("Item: " + i);

            Thread.sleep(500);
        }
    }
}