package ico.ico.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ico.ico.util.Common;
import ico.ico.util.log;

/**
 * @包名 sunrun.szogemray.util
 * @作者 ico
 * @创建日期 2015/1/20 0020
 * @版本 V 1.0
 */
public class MqttSocket {
    //常量
    public final static int MQTTSOCKET_CONNECT_SUCCESS = 100;
    public final static int MQTTSOCKET_CONNECT_FAIL = 101;
    public final static int MQTTSOCKET_RECEIVE = 102;
    public final static int MQTTSOCKET_TIMEOUT = 103;


    //Mqtt连接设置
    MqttConnectOptions mqttConnectOptions;
    //Mqtt连接对象
    private MqttClient mqttClient;
    //MQTT的服务器
    private String serverURI;
    //MQTT的客户端ID
    private String clientId;
    //MQTT回调函数
    private MqttCallback mqttCallback;
    private MqttCallbackIco mqttCallbackIco = new MqttCallbackIco();
    //数据缓冲区
    private List<MqttMessageIco> data = new ArrayList<>();
    //连接状态
    private ConnectState connectState = ConnectState.UNCONNECT;
    //是否处于关闭状态
    private boolean isClosed = false;

    public MqttSocket(String serverURI, String clientId, MqttCallback mqttCallback) {
        this(serverURI, clientId, mqttCallback, getMqttConnectOptions());
    }

    public MqttSocket(String serverURI, String clientId, MqttCallback mqttCallback, MqttConnectOptions mqttConnectOptions) {
        this.serverURI = serverURI;
        this.clientId = clientId;
        this.mqttConnectOptions = mqttConnectOptions;
        this.mqttCallback = mqttCallback;
    }

    /**
     * 获取默认的mqtt配置,根据新然规范,用户名密码为"android",连接超时10S,心跳间隔30S
     *
     * @return
     */
    public static MqttConnectOptions getMqttConnectOptions() {
        // MQTT的连接设置
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
        options.setUserName("android");
        // 设置连接的密码
        options.setPassword("android".toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(30);
        return options;
    }

    /**
     * 根据传入的参数获取mqtt配置对象
     *
     * @param username
     * @param password
     * @param connectTimeout
     * @param heartInterval
     * @return
     */
    public static MqttConnectOptions getMqttConnectOptions(String username, String password, int connectTimeout, int heartInterval) {
        // MQTT的连接设置
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(true);
        // 设置连接的用户名
        options.setUserName(username);
        // 设置连接的密码
        options.setPassword(password.toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(connectTimeout);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(heartInterval);
        return options;
    }

    public void connect() {
        if (getConnectState() == ConnectState.UNCONNECT) {
            setConnectState(ConnectState.CONNECTING);
            new Thread() {
                @Override
                public void run() {
                    try {
                        log.w(String.format("MQTT正在建立连接,serverURI:%s,clientId:%s", serverURI, clientId), MqttSocket.class.getSimpleName());
                        //创建MQTT连接对象
                        mqttClient = new MqttClient(serverURI, clientId, new MemoryPersistence());
                        mqttClient.setCallback(mqttCallbackIco);
                        //设置MQTT登录用户名密码
                        mqttClient.connect(mqttConnectOptions);
                    } catch (MqttException e) {
                        e.printStackTrace();
                        setConnectState(ConnectState.UNCONNECT);
                        log.e(String.format("MQTT建立连接过程出现异常错误,serverURI:%s,clientId:%s,Exception:" + e.toString(), serverURI, clientId), MqttSocket.class.getSimpleName());
                        if (mqttCallback != null) {
                            mqttCallback.connectFail(MqttSocket.this, e);
                        }
                    }
                }
            }.start();
        }
    }

    //region close

    /**
     * 关闭MQTT连接，不能再次使用
     */
    public void close() {
        isClosed = true;
        setConnectState(ConnectState.UNCONNECT);
        try {
            if (mqttClient != null) {
                if (mqttClient.isConnected()) {
                    try {
                        mqttClient.disconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                        mqttClient.close();
                    }
                }
                mqttClient.close();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        } finally {
            mqttClient = null;
        }
        log.w(String.format("MQTT连接主动断开,serverURI:%s,clientId:%s", serverURI, clientId), MqttSocket.class.getSimpleName());
    }


    public boolean isClosed() {
        return isClosed;
    }

    /**
     * 重置所有数据和状态,但保留构造函数中需要的参数
     * 注:调用前请先调用close函数将上一个连接关闭
     */
    public void reset() {
        isClosed = false;
        data.clear();
        connectState = ConnectState.UNCONNECT;
        mqttClient = null;
    }

    //endregion

    //region data
    public List<MqttMessageIco> getData() {
        return data;
    }

    public MqttMessageIco addData(MqttMessageIco mqttMessageIco) {
        data.add(mqttMessageIco);
        return mqttMessageIco;
    }

    public MqttMessageIco removeData(MqttMessageIco mqttMessageIco) {
        data.remove(mqttMessageIco);
        return mqttMessageIco;
    }

    public List<MqttMessageIco> addDatas(List<MqttMessageIco> mqttMessageIcos) {
        data.addAll(mqttMessageIcos);
        return mqttMessageIcos;
    }

    public List<MqttMessageIco> removeDatas(List<MqttMessageIco> mqttMessageIcos) {
        data.removeAll(mqttMessageIcos);
        return mqttMessageIcos;
    }

    //endregion

    //region send
    public MqttMessageIco send(MqttMessageIco mqttMessageIco) {
        addData(mqttMessageIco);
        send();
        return mqttMessageIco;
    }

    /**
     * 立即发送数据队列中的数据
     */
    public synchronized void send() {
        synchronized (data) {
            switch (connectState) {
                case CONNECTED://已连接,直接发送
                    if (data.size() != 0) {
                        for (int i = 0; i < data.size(); i++) {
                            try {
                                mqttClient.publish(data.get(0).getTopic(), data.get(0).getMqttMessage());
                                log.w(String.format("MQTT数据发送,serverURI:%s,clientId:%s,topic:%s,数据:%s"
                                        , serverURI, clientId, data.get(0).getTopic(), Common.bytes2Int16(" ", data.get(0).getMqttMessage().getPayload()))
                                        , MqttSocket.class.getSimpleName());
                            } catch (MqttException e) {
                                e.printStackTrace();
                                log.e(String.format("MQTT数据发送出现异常错误,serverURI:%s,clientId:%s,topic:%s,Exception:%s,数据:%s" + e.toString()
                                        , serverURI, clientId, data.get(0).getTopic(), e.toString(), Common.bytes2Int16(" ", data.get(0).getMqttMessage().getPayload()))
                                        , MqttSocket.class.getSimpleName());
                                if (mqttCallback != null) {
                                    mqttCallback.sendFail(MqttSocket.this, data.get(0), e);
                                }
                            }
                        }
                        data.clear();
                    }
                case CONNECTING://连接中,等待连接成功后再发送
                    break;
                case UNCONNECT://未连接,执行连接操作
                    connect();
                    break;
            }
        }
    }
    //endregion

    //region GETSET
    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public MqttSocket setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
        return this;
    }

    public MqttCallback getMqttCallback() {
        return mqttCallback;
    }

    public MqttSocket setMqttCallback(MqttCallback mqttCallback) {
        this.mqttCallback = mqttCallback;
        return this;
    }

    public ConnectState getConnectState() {
        switch (connectState) {
            case CONNECTED://已连接
                if (mqttClient == null || !mqttClient.isConnected()) {
                    setConnectState(ConnectState.UNCONNECT);
                }
                return connectState;
            case CONNECTING://连接中
                if (mqttClient.isConnected()) {
                    setConnectState(ConnectState.CONNECTED);
                }
                return connectState;
            case UNCONNECT://未连接
                return connectState;
        }
        return connectState;
    }

    public MqttSocket setConnectState(ConnectState connectState) {
        this.connectState = connectState;
        return this;
    }
    //endregion

    /**
     * 用于表示当前的MQTT连接状态
     */
    public enum ConnectState {
        /** 标识未连接 */
        UNCONNECT,
        /** 连接中 */
        CONNECTING,
        /** 连接成功 */
        CONNECTED
    }

    class MqttCallbackIco implements MqttCallbackExtended {

        @Override
        public void connectionLost(Throwable throwable) {
            setConnectState(ConnectState.UNCONNECT);
            log.w(String.format("MQTT连接断开,serverURI:%s,clientId:%s", serverURI, clientId), MqttSocket.class.getSimpleName());
            if (mqttCallback != null) {
                mqttCallback.connectDisconnect(MqttSocket.this, throwable);
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
            log.w(String.format("MQTT数据接收成功,serverURI:%s,clientId:%s,topic:%s,数据:%s", serverURI, clientId, topic, Common.bytes2Int16(" ", mqttMessage.getPayload())), MqttSocket.class.getSimpleName());
            if (mqttCallback != null) {
                mqttCallback.receive(MqttSocket.this, topic, mqttMessage);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            try {
                log.w(String.format("MQTT数据发送成功,serverURI:%s,clientId:%s,topic:%s,数据:%s"
                        , serverURI, clientId, iMqttDeliveryToken.getTopics().toString(), Common.bytes2Int16(" ", iMqttDeliveryToken.getMessage().getPayload()))
                        , MqttSocket.class.getSimpleName());
            } catch (Exception e) {
//                e.printStackTrace();
                log.e(String.format("MQTT数据发送成功日志打印出错,serverURI:%s,clientId:%s,topic:%s"
                        , serverURI, clientId, Arrays.toString(iMqttDeliveryToken.getTopics()))
                        , MqttSocket.class.getSimpleName());
            }
            if (mqttCallback != null) {
                mqttCallback.sendSuccess(MqttSocket.this, iMqttDeliveryToken);
            }
        }

        @Override
        public void connectComplete(boolean b, String s) {
            setConnectState(ConnectState.CONNECTED);
            log.w(String.format("MQTT建立连接成功,serverURI:%s,clientId:%s", serverURI, clientId), MqttSocket.class.getSimpleName());
            if (mqttCallback != null) {
                mqttCallback.connectSuccess(MqttSocket.this, b, s);
            }
            send();
        }
    }
}
