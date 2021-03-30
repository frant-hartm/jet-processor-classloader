package org.example.jet;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

public class Hz5QueueConsumer {
    public static void main(String[] args) throws InterruptedException {
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        IQueue<Person> dst = instance.getQueue("dst");
        for (;;) {
            Person take = dst.take();
            System.out.println(take);
        }
    }
}
