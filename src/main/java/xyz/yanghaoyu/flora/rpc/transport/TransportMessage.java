package xyz.yanghaoyu.flora.rpc.transport;

/**
 * 协议报文
 */
public class TransportMessage {
    public static final byte[] MAGIC_NUMBER                    = {'R', 'O', 'S', 'E'};
    public static final byte   REQUEST_MESSAGE_TYPE            = 1;
    public static final byte   RESPONSE_MESSAGE_TYPE           = 2;
    public static final byte   HEARTBEAT_REQUEST_MESSAGE_TYPE  = -1; // ping
    public static final byte   HEARTBEAT_RESPONSE_MESSAGE_TYPE = -2; // pong
    public static final byte   HEADER_LENGTH                   = 16;

    // 消息类型
    private byte   messageType;
    // 序列化类型
    private byte   codec;
    // 压缩类型
    private byte   compress;
    // 请求 id
    private int    requestId;
    // 具体数据
    private Object data;
    private byte   version;

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte getCodec() {
        return codec;
    }

    public void setCodec(byte codec) {
        this.codec = codec;
    }

    public byte getCompress() {
        return compress;
    }

    public void setCompress(byte compress) {
        this.compress = compress;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }
}
