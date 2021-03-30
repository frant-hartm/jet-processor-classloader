package org.example.jet.hz3context;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

public class Hz3Context {
    private final String clientConfig;
    private final HazelcastInstance client;
    private final IQueue<Object> queue;

    public Hz3Context(String clientConfig, String queueName) {
        System.out.println("Hz3Context loaded by " + this.getClass().getClassLoader());
        this.clientConfig = clientConfig;
        this.client = HazelcastClient.newHazelcastClient(Utils.xmlToConfig(clientConfig));
        System.out.println("Created client: " + client + " loaded by " + client.getClass().getClassLoader());
        this.queue = client.getQueue(queueName);

    }

    public Object takeObject() throws InterruptedException {
        return queue.take();
    }
}
