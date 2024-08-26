package com.wangyuanye.plugin.listeners;

import com.intellij.util.messages.Topic;

/**
 * @author wangyuanye
 * @date 2024/8/26
 **/
public interface MyTopic {
    String TOPIC_NAME = "MY_CUSTOM_TOPIC";
    // 定义一个Topic，使用泛型指定消息类型
    Topic<MyTopic> TOPIC = Topic.create(TOPIC_NAME, MyTopic.class);

    /**
     * 定义需要传递的消息方法
     *
     * @param dataLoaded 数据是否已加载
     */
    void onMessageReceived(Boolean dataLoaded);
}
