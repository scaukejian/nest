package com.zhaofujun.nest.context.event.channel;

import com.zhaofujun.nest.context.event.message.MessageConverterFactory;
import com.zhaofujun.nest.core.BeanFinder;
import com.zhaofujun.nest.context.event.EventArgs;
import com.zhaofujun.nest.core.EventHandler;
import com.zhaofujun.nest.core.EventData;
import com.zhaofujun.nest.context.event.message.MessageConverter;
import com.zhaofujun.nest.context.event.message.MessageInfo;
import com.zhaofujun.nest.context.event.message.MessageRecord;
import com.zhaofujun.nest.context.event.store.MessageStore;
import com.zhaofujun.nest.context.event.store.MessageStoreFactory;

import java.util.Date;
import java.util.UUID;

public abstract class AbstractMessageConsumer implements MessageConsumer {

    private BeanFinder beanFinder;
    private MessageConverter messageConverter;

    public AbstractMessageConsumer(BeanFinder beanFinder) {
        this.beanFinder = beanFinder;
        messageConverter = new MessageConverterFactory(beanFinder).create();
    }

    public MessageConverter getMessageConverter() {
        return messageConverter;
    }

    public BeanFinder getBeanFinder() {
        return beanFinder;
    }

    public abstract void subscribe(EventHandler eventHandler);

    protected void onReceivedMessage(MessageInfo messageInfo, EventHandler eventHandler, Object context) {
        EventData eventData = messageInfo.getData();

        MessageRecord record = new MessageRecord();
        record.setHandlerName(eventHandler.getClass().getName());
        record.setHandleTime(new Date());
        record.setId(UUID.randomUUID().toString());
        record.setMessageInfo(messageInfo);
        record.setReceiveTime(new Date());

        EventArgs eventArgs = new EventArgs();
        eventArgs.setEventCode(eventHandler.getEventCode());
        eventArgs.setSource(messageInfo.getEventSource());
        eventArgs.setMessageId(messageInfo.getMessageId());
        eventArgs.setReceiveTime(new Date());
        eventArgs.setSendTime(messageInfo.getSendTime());


        MessageStoreFactory messageStoreFactory = new MessageStoreFactory(beanFinder);

        MessageStore messageStore = messageStoreFactory.create();
        if (messageStore.isSucceed(messageInfo.getMessageId(), eventHandler.getClass().getName()))
            return;


        try {

            eventHandler.handle(eventData, eventArgs);
            messageStore.save(record);
        } catch (Exception ex) {
            onFailed(eventHandler, context, ex);
            eventHandler.onFailed(context, ex);
        } finally {
            onEnds(eventHandler, context);
        }
    }


    protected void onFailed(EventHandler eventHandler, Object context, Exception ex) {
    }

    protected void onEnds(EventHandler eventHandler, Object context) {
    }
}
