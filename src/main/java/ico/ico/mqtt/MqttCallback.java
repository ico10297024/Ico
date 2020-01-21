package ico.ico.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by admin on 2015/5/8 0008.
 */
public interface MqttCallback {
    /**
     * 当接收到数据时调用该回调函数
     *
     * @param buffer
     */
    void receive(MqttSocket mqttSocket, String s, MqttMessage mqttMessage);

    /**
     * 当TCP连接失败次数超过设置时触发
     */
    void connectFail(MqttSocket mqttSocket, Exception e);

    /**
     * 当TCP连接成功时触发
     */
    void connectSuccess(MqttSocket mqttSocket, boolean b, String s);


    /**
     * 数据发送成功时触发
     *
     * @param mqttSocket
     * @param mqttMessage
     */
    void sendSuccess(MqttSocket mqttSocket, IMqttDeliveryToken iMqttDeliveryToken);

    /**
     * 当TCP连接失败次数超过设置时触发
     */
    void sendFail(MqttSocket mqttSocket, MqttMessageIco mqttMessageIco, Exception e);

    /**
     * 当Tcp连接因IO异常被断开时
     *
     * @param mqttSocket
     */
    void connectDisconnect(MqttSocket mqttSocket, Throwable throwable);
}
