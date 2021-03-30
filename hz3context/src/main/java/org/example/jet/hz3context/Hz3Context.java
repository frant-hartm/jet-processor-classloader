package org.example.jet.hz3context;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.QueueTakeCodec;
import com.hazelcast.client.proxy.ClientQueueProxy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Hz3Context {
    private final String clientConfig;
    private final HazelcastInstance client;
    private final IQueue<Object> queue;

    private static Method INVOKE_ON_PARTITION_METHOD = getMethod();
    private final String queueName;

    private static Method getMethod() {
        try {
            Method invokeOnPartitionInterruptibly = ClientQueueProxy.class.getSuperclass().getDeclaredMethod("invokeOnPartitionInterruptibly", ClientMessage.class);
            if (!invokeOnPartitionInterruptibly.isAccessible()) {
                invokeOnPartitionInterruptibly.setAccessible(true);
            }

            return invokeOnPartitionInterruptibly;
        } catch (NoSuchMethodException var1) {
            throw new AssertionError(var1);
        }
    }


    public Hz3Context(String clientConfig, String queueName) {
        this.clientConfig = clientConfig;
        this.client = HazelcastClient.newHazelcastClient(Utils.xmlToConfig(clientConfig));
        this.queue = client.getQueue(queueName);
        this.queueName = queueName;
    }

    public byte[] takeObject() throws InterruptedException {
        ClientMessage clientMessage = QueueTakeCodec.encodeRequest(this.queueName);

        try {
            ClientMessage response = (ClientMessage)INVOKE_ON_PARTITION_METHOD.invoke(this.queue, clientMessage);
            QueueTakeCodec.ResponseParameters resultParameters = QueueTakeCodec.decodeResponse(response);
            return resultParameters.response.toByteArray();
        } catch (InvocationTargetException | IllegalAccessException var4) {
            throw new RuntimeException(var4);
        }
    }
}
