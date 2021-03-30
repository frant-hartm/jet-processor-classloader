package org.example.jet;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

public class Hz3MemberStart {
    public static void main(String[] args) {
        Config config = new Config();
        config.getNetworkConfig().setPort(3120);
        Hazelcast.newHazelcastInstance(config);
    }
}
