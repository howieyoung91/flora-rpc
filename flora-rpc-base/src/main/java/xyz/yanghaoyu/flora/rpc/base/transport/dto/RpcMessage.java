/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.dto;

/**
 * 协议报文
 */
public class RpcMessage<T> {
    public static final byte[] MAGIC_NUMBER                    = {'R', 'O', 'S', 'E'};
    public static final byte   REQUEST_MESSAGE_TYPE            = 1;
    public static final byte   RESPONSE_MESSAGE_TYPE           = 2;
    public static final byte   HEARTBEAT_REQUEST_MESSAGE_TYPE  = -1; // ping
    public static final byte   HEARTBEAT_RESPONSE_MESSAGE_TYPE = -2; // pong
    public static final byte   HEADER_LENGTH                   = 16;
    public static final int    MAX_FRAME_LENGTH                = 8 * 1024 * 1024;
    public static final byte   VERSION                         = 1;

    public static <T> RpcMessage<T> of(byte type, T body) {
        RpcMessage<T> message = new RpcMessage<>();
        message.setType(type);
        message.setBody(body);
        return message;
    }

    private byte   type;        // 消息类型
    private String serializer;  // 序列化类型
    private String compressor;  // 压缩类型
    private T      body;        // 请求体或响应体

    public byte getType() {
        return type;
    }

    public void setType(byte messageType) {
        this.type = messageType;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getCompressor() {
        return compressor;
    }

    public void setCompressor(String compressor) {
        this.compressor = compressor;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(T data) {
        this.body = data;
    }
}
