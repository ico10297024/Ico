package ico.ico.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @包名 sunrun.szogemray.util
 * @作者 ico
 * @创建日期 2015/1/20 0020
 * @版本 V 1.0
 */

public class TcpSocket extends IcoThread {
    /**
     * 实体参数
     */
    //TCP连接对象和IO流
    private Socket mSocket = null;
    private BufferedInputStream mInput = null;
    private BufferedOutputStream mOutput = null;
    //TCP连接对象的IP地址和端口
    private String mIp;
    private int mPort;
    //TCP连接超时时间
    private Long mConnectTimeout = 20000L;
    //TCP连接次数上限，超过该次数将调用回调函数connectFail
    private int mMaxConnectTimes = 10;
    //TCP连接数据接收超时时间
    private Long mReceiveSotimeout = -1L;
    //TCP回调函数
    private TcpCallback mTcpCallback;
    //要发送的数据
    private List<byte[]> mDataBuffer = new ArrayList<>();
    //心跳包相关
    /** 是否开启心跳 */
    private boolean isHeart = false;
    /** 心跳数据 */
    private byte[] mHeartData;
    /** 心跳包发送的间隔时间 */
    private Long mHeartInterval = 0L;
    //发送线程
    private SendThread mSendThread;
    //标记Udp是否已连接
    private boolean isConnected = false;
    // 最后一次发送命令的时间
    private long mLastSendTime;
    // 每次命令需间隔时间
    private Long mInterval = 300L;

    /**
     * 创建一个TCP连接对象，不使用心跳机制
     *
     * @param ip          目标IP地址
     * @param port        目标IP端口号
     * @param timeout     连接超时设置，默认20S,-1位缺省值
     * @param sotimeout   数据读取超时，默认不设置，-1缺省值
     * @param times       最大失败连接次数，指定超过多少次连接失败后调用回调函数的connectFail
     * @param tcpCallback 回调函数
     */
    public TcpSocket(String ip, int port, Long timeout, Long sotimeout, int times, TcpCallback tcpCallback) {
        this.mIp = ip;
        this.mPort = port;
        if (timeout > 0) {
            this.mConnectTimeout = timeout;
        }
        this.mReceiveSotimeout = sotimeout;
        this.mTcpCallback = tcpCallback;
        this.mMaxConnectTimes = times;
    }

    /**
     * 创建一个TCP连接对象，不使用心跳机制
     *
     * @param ip            目标IP地址
     * @param port          目标IP端口号
     * @param timeout       连接超时设置，默认20S,-1位缺省值
     * @param sotimeout     数据读取超时，默认不设置，-1缺省值
     * @param times         最大失败连接次数，指定超过多少次连接失败后调用回调函数的connectFail
     * @param tcpCallback   回调函数
     * @param heartData     心跳包数据
     * @param heartInterval 心跳包发送间隔
     */
    public TcpSocket(String ip, int port, Long timeout, Long sotimeout, int times, TcpCallback tcpCallback, byte[] heartData, Long heartInterval) {
        this(ip, port, timeout, sotimeout, times, tcpCallback);
        isHeart = true;
        this.mHeartInterval = heartInterval;
        this.mHeartData = heartData;
    }

    /**
     * 创建一个Tcp连接
     *
     * @param ip      TCP服务器的IP地址
     * @param port    TCP服务器的端口号
     * @param timeout TCP服务器连接超时时间
     * @return
     * @throws {@link IOException}
     */
    public static Socket createSocket(String ip, int port, Long timeout) throws IOException {
        Socket socket = null;
        // 创建socket
        socket = new Socket();
        // 创建Udp连接
        socket.connect(new InetSocketAddress(ip, port), timeout.intValue());
        return socket;
    }

    @Override
    public void run() {
        /* 创建和进行TCP连接 */
        for (int i = 0; (!isClosed()); i++) {
            try {
                log.d(String.format("TCP正在建立连接%d,ip:%s,port:%d", i, mIp, mPort), TcpSocket.class.getSimpleName());
                Socket _socket = TcpSocket.createSocket(mIp, mPort, mConnectTimeout);
                log.d(String.format("TCP建立连接成功%d,ip:%s,port:%d", i, mIp, mPort), TcpSocket.class.getSimpleName());
                //设置超时
                if (mReceiveSotimeout != -1) {
                    _socket.setSoTimeout(mReceiveSotimeout.intValue());
                }
                // 获取输入输出
                BufferedOutputStream _output = new BufferedOutputStream(_socket.getOutputStream());
                BufferedInputStream _input = new BufferedInputStream(_socket.getInputStream());
                mSocket = _socket;
                mOutput = _output;
                mInput = _input;
                setConnected(true);
                break;
            } catch (Exception e) {
                log.ew(String.format("TCP建立连接失败%d,ip:%s,port:%d,Exception:" + e.toString(), i, mIp, mPort) + i, TcpSocket.class.getSimpleName());
                if (i == mMaxConnectTimes - 1) {
                    mTcpCallback.onConnectFail(this);
                    close();
                    return;
                }
                try {
                    sleep(500L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        /* 读取数据 */
        while (!isClosed()) {
            byte[] buffer = null;
            try {
                buffer = new byte[1024];
                log.d(String.format("TCP正在接收数据，ip：%s，port：%d", mIp, mPort), TcpSocket.class.getSimpleName());
                int len = mInput.read(buffer);

                if (len == -1) {
                    throw new IOException("len==-1");
                }
                mTcpCallback.onReceive(buffer);
                log.d(String.format("TCP接收数据成功，ip：%s，port：%d，数据长度：%d；数据：%s", mIp, mPort, len, Common.bytes2Int16(" ", buffer)), TcpSocket.class.getSimpleName());
            } catch (IOException e) {
//                e.printStackTrace();
                log.ew(String.format("TCP连接IO流异常，ip:%s,port:%d，Exception:" + e.toString(), mIp, mPort), TcpSocket.class.getSimpleName());
                close();
                mTcpCallback.connectDisconnect(this);
            } catch (Exception e) {
//                e.printStackTrace();
                log.ew(String.format("TCP数据接收异常，ip:%s,port:%d，Exception:" + e.toString(), mIp, mPort), TcpSocket.class.getSimpleName());
            }
        }
    }

    /**
     * 关闭TCP连接，不能再次使用
     */
    @Override
    public void close() {
        super.close();
        setConnected(false);
        if (mSendThread != null) {
            mSendThread.close();
        }
        if (mInput != null) {
            try {
                mInput.close();
                mInput = null;
            } catch (Exception e) {
                log.ew(String.format("TCP输入流关闭异常，ip:%s,port:%d，Exception:" + e.toString(), mIp, mPort), TcpSocket.class.getSimpleName());
            }
        }
        if (mOutput != null) {
            try {
                mOutput.close();
                mOutput = null;
            } catch (Exception e) {
                log.ew(String.format("TCP输出流关闭异常，ip:%s,port:%d，Exception:" + e.toString(), mIp, mPort), TcpSocket.class.getSimpleName());
            }
        }
        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (Exception e) {
                log.ew(String.format("TCP关闭异常，ip:%s,port:%d，Exception:" + e.toString(), mIp, mPort), TcpSocket.class.getSimpleName());
            }
        }
        log.d(String.format("TCP连接断开,ip:%s,port:%d", mIp, mPort), TcpSocket.class.getSimpleName());
    }

    /**
     * 将数据添加到数据队列中并立即发送
     *
     * @param buffer
     */
    public void send(byte[] buffer) {
        mDataBuffer.add(buffer);
        send();
    }

    /**
     * 将条数据添加至数据队列，不发送
     *
     * @param buffer
     */
    public void addData(byte[] buffer) {
        synchronized (mDataBuffer) {
            mDataBuffer.add(buffer);
        }
    }

    /**
     * 将一组数据添加至数据队列中，不发送
     *
     * @param list
     */
    public void addDatas(List<byte[]> list) {
        synchronized (mDataBuffer) {
            mDataBuffer.addAll(list);
        }
    }

    /**
     * 立即发送数据队列中的数据
     */
    public void send() {
        if (mSendThread == null || mSendThread.isClosed()) {
            mSendThread = new SendThread();
            mSendThread.start();
        } else {
            mSendThread.notifyContinue();
        }
    }

    //region ***************************************************************************************开启和关闭心跳机制

    /**
     * 开启心跳包
     *
     * @param buffer        心跳包的数据
     * @param heartInterval 心跳包间隔时间
     */
    public void startHeart(byte[] buffer, Long heartInterval) {
        this.mHeartInterval = heartInterval;
        mHeartData = buffer;
        if (!isHeart) {
            isHeart = true;
            //不为null&&已关闭则重新开启
            if (mSendThread == null || mSendThread.isClosed()) {
                mSendThread = new SendThread();
                mSendThread.start();
            }
        }
    }

    /**
     * 关闭心跳包
     */
    public void stopHeart() {
        isHeart = false;
    }
    //endregion

    //region ***************************************************************************************GETSET
    public Socket getSocket() {
        return mSocket;
    }

    public void setSocket(Socket socket) {
        this.mSocket = socket;
    }

    public TcpCallback getTcpCallback() {
        return mTcpCallback;
    }

    public void setmTcpCallback(TcpCallback tcpCallback) {
        this.mTcpCallback = tcpCallback;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        this.mIp = ip;
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 设置当前连接状态，若设置true则会调用回调函数{@link ico.ico.util.TcpCallback} onConnectSuccess
     *
     * @param isConnected
     */
    public void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
        if (isConnected) {
            mTcpCallback.onConnectSuccess(this);
        }
    }
    //endregion

    /**
     * 数据发送线程
     * 数据队列中没有数据并且没有开启心跳时，该线程不开启
     * 数据队列中有数据时创建该线程并启动，当全部数据发送完成后，若不需要心跳则线程关闭，反之则根据设置进行心跳包的发送
     * 心跳睡眠中，若需要发送数据时，将唤醒线程；
     * 心跳睡眠被唤醒时会判断数据队列是否有数据，若有数据则直接发送数据，若没有数据则发送心跳包
     */
    public class SendThread extends IcoThread {
        @Override
        public void run() {
            while (!SendThread.this.isClosed()) {
                /* 用于临时保存数据的集合 */
                List<byte[]> _data = new ArrayList<>();
                /* 在同步块内检查数据，没有数据则执行心跳处理，有数据则将数据存储到_data,然后在第二大块将_data依次发送 */
                synchronized (mDataBuffer) {
                    //检查有没有数据
                    if (mDataBuffer.size() == 0) {
                        //检查心跳是否开启
                        if (isHeart) {
                            //发送心跳
                            sendHeart();
                        } else {
                            //没有待发数据，未开启心跳包，则关闭线程
                            SendThread.this.close();
                        }
                        continue;
                    } else {
                        //将data数据转存到_data中
                        _data.addAll(mDataBuffer);
                        mDataBuffer.clear();
                    }
                }

                /** 将数据队列的数据进行发送 */
                for (int i = 0; i < _data.size() && (!isClosed()); i++) {
                    /** 保持数据发送间隔 */
                    try {
                        keepInterval(mLastSendTime, mInterval);
                    } catch (InterruptedException e) {
//                            e.printStackTrace();
                    }

                    if (isClosed()) {
                        return;
                    }

                    /** 发送数据 */
                    sendData(mHeartData);
                }
            }
            SendThread.this.close();
        }

        /** 发送心跳 */
        private void sendHeart() {
            /* 保持发送间隔 */
            try {
                keepInterval(mLastSendTime, mHeartInterval);
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }

            if (mDataBuffer.size() != 0 || isClosed()) {
                return;
            }

            /* 数据发送 */
            sendData(mHeartData);
            /* 等待心跳包间隔 */
            try {
                wait(mHeartInterval);
            } catch (InterruptedException e) {
                log.ew(String.format("TCP发送心跳包等待时被唤醒，ip：%s，port：%d，Exception：" + e.toString(), mIp, mPort), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName());
            }
        }

        /** 发送数据 */
        private void sendData(byte[] data) {
            String _log = "";
            boolean _isHeart = data == mHeartData;
            if (_isHeart) {
                _log = "心跳包";
            } else {
                _log = Common.bytes2Int16(" ", data);
            }

            try {
                //发送
                log.d(String.format("TCP正在发送数据，ip：%s，port：%d，数据长度：%d；数据：%s", mIp, mPort, data.length, _log), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName());
                mOutput.write(mHeartData);
                mOutput.flush();
                log.d(String.format("TCP发送数据成功，ip：%s，port：%d，数据长度：%d；数据：%s", mIp, mPort, data.length, _log), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName());
                //回调
                mTcpCallback.onSend(TcpSocket.this, data, true);
                //记录
                mLastSendTime = System.currentTimeMillis();
            } catch (Exception e) {
                //e.printStackTrace();
                log.ew(String.format("TCP发送数据异常，ip：%s，port：%d，数据长度：%d；数据：%s；Exception：%s", mIp, mPort, data.length, _log, e.toString()), TcpSocket.class.getSimpleName(), SendThread.class.getSimpleName());
                mTcpCallback.onSend(TcpSocket.this, data, false);
            }
        }

        /**
         * 计算最后一次发送的时间,若间隔过小则睡眠相差的毫秒数
         * 保持两次发送数据的间隔，如果太快则进行睡眠
         *
         * @throws InterruptedException 线程睡眠时被唤醒
         */
        private void keepInterval(long lastSendTime, long interval)
                throws InterruptedException {
            if (lastSendTime == 0L || interval == 0L) {
                return;
            }
            Long _intervel = System.currentTimeMillis() - lastSendTime;
            if (_intervel < interval) {
                wait(interval - _intervel);
            }
        }
    }
}
