package ico.ico.util;

import java.net.DatagramPacket;

/**
 * Created by admin on 2015/5/8 0008.
 */
public interface UdpCallback {
    /**
     * 当接收到数据时触发
     *
     * @param packet udp的数据包对象，{@link DatagramPacket}
     */
    void onReceive(DatagramPacket packet);

    /**
     * 当udp创建失败次数超过设置时触发
     */
    void onCreatefail(UdpSocket _udpSocket);

    /**
     * 当udp创建成功时触发
     */
    void onCreateSuccess(UdpSocket _udpSocket);

    /**
     * 当udp创建成功时触发
     */
    void onClosed(UdpSocket _udpSocket);
}
