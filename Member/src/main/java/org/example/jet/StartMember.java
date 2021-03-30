package org.example.jet;

import com.hazelcast.core.Hazelcast;

public class StartMember {
    public static void main(String[] args) {
        Hazelcast.newHazelcastInstance();
    }
}
