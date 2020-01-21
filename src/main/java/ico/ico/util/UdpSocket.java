package ico.ico.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

/**
 * @包名 sunrun.szogemray.util
 * @作者 ico
 * @创建日期 2015/1/20 0020
 * @版本 V 1.0
 */
public class UdpSocket extends IcoThread {
    /**
     * 实体对象
     */
    //Udp连接对象
    private DatagramSocket mSocket;
    //UDP发送的端口号
    private int mPort;
    //TCP连接次数上限，超过该次数将调用回调函数createFail
    private int mMaxConnectTimes = 10;
    //TCP连接数据接收超时时间
    private Long mSotimeout = -1L;
    //Udp回调函数
    private UdpCallback mUdpCallback;
    //标记Udp是否已连接
    private boolean isConnected = false;


    /**
     * 创建一个UDP连接对象
     *
     * @param port            目标IP端口号
     * @param sotimeout       数据读取超时，默认不设置，-1缺省值
     * @param maxConnectTimes 最大失败连接次数，指定超过多少次连接失败后调用回调函数的connectFail
     * @param udpCallback     回调函数
     */
    public UdpSocket(int port, Long sotimeout, int maxConnectTimes, UdpCallback udpCallback) {
        this.mPort = port;
        this.mSotimeout = sotimeout;
        this.mUdpCallback = udpCallback;
        this.mMaxConnectTimes = maxConnectTimes;
    }

    /**
     * 单次通信（发，收，关）
     *
     * @param sotimeout 设置数据接收超时时间
     * @param data      要发送的数据
     * @param ip        要发送的目标的IP地址，如果是全网可以发255.255.255.255
     * @param port      要发送的目标的端口号，本机UDP端口号不能和目标端口号相同
     * @return
     */
    public synchronized static String[] sendMessage(Long sotimeout, String data, String ip, int port) throws IOException {
        return UdpSocket.sendMessage(sotimeout, data.getBytes(Charset.defaultCharset()), ip, port);
    }

    /**
     * 单次通信（发，收，关）
     *
     * @param sotimeout 设置数据接收超时时间
     * @param data      要发送的数据,byte数组
     * @param ip        要发送的目标的IP地址，如果是全网可以发255.255.255.255
     * @param port      要发送的目标的端口号，本机UDP端口号不能和目标端口号相同
     * @return
     */
    public synchronized static String[] sendMessage(Long sotimeout, byte[] data, String ip, int port) throws IOException {
        String[] result = new String[2];
        DatagramSocket udpSocket = null;

        try {
            // 创建Udp连接
            udpSocket = UdpSocket.createSocket(port);
            // 设置数据接收超时
            udpSocket.setSoTimeout(sotimeout.intValue());
            // 创建发送包
            DatagramPacket _data = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
            // 发送
            udpSocket.send(_data);
            // 创建接收包，接收
            DatagramPacket receive = new DatagramPacket(new byte[9999], 0, 9999);
            udpSocket.receive(receive);
            if (receive.getLength() == -1) {
                throw new IOException();
            }
            // 获取数据，进行处理
            result[0] = new String(receive.getData(), 0, receive.getLength(), "UTF-8");
            result[1] = receive.getAddress().toString().substring(1);
        } catch (Exception e) {
            throw e;
        } finally {
            if (udpSocket != null) {
                udpSocket.close();
            }
        }

        return result;
    }

    /**
     * 发送一次广播，等待时间内无限收响应
     *
     * @param port 要发送的目标的端口号，本机UDP端口号不能和目标端口号相同
     * @return {@link DatagramSocket}UDP的连接对象
     * @throws SocketException 创建失败会抛出该异常
     */
    public static DatagramSocket createSocket(int port) throws SocketException {
        DatagramSocket udpSocket = null;

        // 计算随机的端口地址
        int aport = -1;
        while (true) {
            aport = Common.random(65535);
            if ((aport != -1) && (aport != port)) {
                break;
            }
        }
        // 创建Udp连接
        udpSocket = new DatagramSocket(aport);
        return udpSocket;
    }

    @Override
    public void run() {
        /* 建立udp对象，失败会尝试多次，失败次数超过设置的次数上限后将会结束线程并回调 */
        for (int i = 0; (!isClosed()); i++) {
            try {
                log.d(String.format("UDP正在建立连接%d,port:%d", i, mPort) + i, UdpSocket.class.getSimpleName());
                DatagramSocket _socket = UdpSocket.createSocket(mPort);
                mSocket = _socket;
                //设置数据接收超时时间
                if (mSotimeout != -1) {
                    mSocket.setSoTimeout(mSotimeout.intValue());
                }
                //设置连接状态
                setConnected(true);
                break;
            } catch (Exception e) {
                log.ew(String.format("UDP建立连接失败%d,port:%d,Exception:" + e.toString(), i, mPort) + i, UdpSocket.class.getSimpleName());
                //失败次数超过设置上限，回调，关闭，结束
                if (i == mMaxConnectTimes - 1) {
                    mUdpCallback.onCreatefail(this);
                    close();
                    return;
                }
                //休眠500ms后继续
                try {
                    sleep(500L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        /* 循环接收数据 */
        while (!isClosed()) {
            DatagramPacket receive = null;
            try {
                // 创建接收包，接收
                receive = new DatagramPacket(new byte[1024], 0, 1024);
                log.d(String.format("UDP正在接收数据，port:%d", mPort), UdpSocket.class.getSimpleName());
                mSocket.receive(receive);

                log.d(String.format("UDP接收数据成功，port:%d，ip：%s，数据长度：%d；数据：%s"
                        , mPort, receive.getAddress().toString(), receive.getLength(), Common.bytes2Int16(" ", receive.getData()))
                        , UdpSocket.class.getSimpleName());
                mUdpCallback.onReceive(receive);
            } catch (IOException e) {
                //e.printStackTrace();
                log.ew("UDP连接IO流异常，Exception:" + e.toString(), UdpSocket.class.getSimpleName());
                close();
                return;
            } catch (Exception e) {
                //e.printStackTrace();
                log.ew("UDP异常，Exception:" + e.toString(), UdpSocket.class.getSimpleName());
            }
        }
    }

    /**
     * 关闭该UDP连接，关闭后必须重新创建
     */
    @Override
    public void close() {
        super.close();
        setConnected(false);
        if (mSocket != null) {
            mSocket.close();
        }
    }

    /**
     * 发送UDP广播
     *
     * @param buffer 要发送的数据
     * @param ip     要发送的目标IP，如果是全网可以发255.255.255.255
     * @param port   要发送的目标端口号
     * @throws {@link IOException}
     */
    public void send(byte[] buffer, String ip, int port) throws IOException {
        // 创建发送包
        DatagramPacket send = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
        log.d(String.format("UDP发送数据，ip：%s，port：%d，数据长度：%d", ip, port, buffer.length), UdpSocket.class.getSimpleName(), "send");
        // 发送
        mSocket.send(send);
    }

    //region GETSET

    /**
     * 判断Udp是否连接
     *
     * @return boolean
     */
    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
        if (isConnected) {
            mUdpCallback.onCreateSuccess(this);
        }
    }
    //endregion
}
