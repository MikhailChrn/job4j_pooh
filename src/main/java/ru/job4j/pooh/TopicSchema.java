package ru.job4j.pooh;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class TopicSchema implements Schema {

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Receiver>> receiverMap
            = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, BlockingQueue<String>> messagesMap
            = new ConcurrentHashMap<>();

    private final Condition condition
            = new Condition();

    @Override
    public void addReceiver(Receiver receiver) {
        receiverMap.putIfAbsent(receiver.name(), new CopyOnWriteArrayList<>());
        receiverMap.get(receiver.name()).add(receiver);
        condition.on();
    }

    @Override
    public void publish(Message message) {
        messagesMap.putIfAbsent(message.name(), new LinkedBlockingQueue<>());
        messagesMap.get(message.name()).add(message.text());
        condition.on();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (String messageKey : messagesMap.keySet()) {
                CopyOnWriteArrayList<Receiver> receiverList
                        = receiverMap.getOrDefault(messageKey, new CopyOnWriteArrayList<>());
                if (receiverList.isEmpty()) {
                    continue;
                }
                messagesMap.get(messageKey).forEach(
                        message -> receiverList.forEach(
                                receiver -> receiver.receive(message)
                        )
                );
            }

            condition.off();
            try {
                condition.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
