package com.zhaofujun.nest.context.event.resend;

import com.zhaofujun.nest.context.event.message.MessageBacklog;
import com.zhaofujun.nest.core.BeanFinder;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class DefaultMessageResendStore implements MessageResendStore {

    private BeanFinder beanFinder;

    public DefaultMessageResendStore(BeanFinder beanFinder) {
        this.beanFinder = beanFinder;
    }

    public Queue<MessageBacklog> messageBacklogs = new LinkedBlockingDeque<>();

    public void add(MessageBacklog messageBacklog) {
        messageBacklogs.add(messageBacklog);
    }

    public void Resend() {
        MessageBacklog messageBacklog = messageBacklogs.poll();
        //todo resend
    }
}
