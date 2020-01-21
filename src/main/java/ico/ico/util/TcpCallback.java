package ico.ico.util;

/**
 * Created by admin on 2015/5/8 0008.
 */
public interface TcpCallback {
    /**
     * 当接收到数据时触发
     *
     * @param buffer 接收到的数据
     */
    void onReceive(byte[] buffer);

    /**
     * 当TCP连接失败次数超过设置时触发
     */
    void onConnectFail(TcpSocket tcpSocket);

    /**
     * 当TCP连接成功时触发
     */
    void onConnectSuccess(TcpSocket tcpSocket);

    /**
     * 数据发送成功时触发
     *
     * @param buffer 发送成功的数据
     */
    void onSend(TcpSocket tcpSocket, byte[] buffer, boolean success);

    /**
     * 当Tcp连接因IO异常被断开时触发
     */
    void connectDisconnect(TcpSocket tcpSocket);
}
