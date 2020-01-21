package ico.ico.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 对mqttMessage要发送的数据对象进行了封装,已便于存入数据缓冲区中
 */
public class MqttMessageIco {
    private String topic;
    private MqttMessage mqttMessage;

    public MqttMessageIco() {
    }

    public MqttMessageIco(String topic, MqttMessage mqttMessage) {
        this.topic = topic;
        this.mqttMessage = mqttMessage;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public MqttMessageIco setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
        return this;
    }

    public String getTopic() {
        return topic;
    }

    public MqttMessageIco setTopic(String topic) {
        this.topic = topic;
        return this;
    }
}
