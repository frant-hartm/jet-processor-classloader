package org.example.jet;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class Hz3Producer {
    public static void main(String[] args) throws InterruptedException {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().addAddress("localhost:3120");
        HazelcastInstance instance = HazelcastClient.newHazelcastClient(config);

        IQueue<Person> myQueue = instance.getQueue("myQueue");
        for (int i = 0;; i++) {
            Person p = new Person();
            p.setId(i);
            p.setName("name " + i);
            myQueue.offer(p);
            Thread.sleep(1000);
        }
    }
}
