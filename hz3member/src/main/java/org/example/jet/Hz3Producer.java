package org.example.jet;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.util.UuidUtil;

public class Hz3Producer {
    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().addAddress("localhost:3120");
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IQueue<String> myQueue = instance.getQueue("myQueue");
        for (;;) {
            myQueue.offer(UuidUtil.newSecureUuidString());
            Thread.sleep(1000);
        }
    }
}
