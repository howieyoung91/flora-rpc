/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.transport.dto;

/**
 * 协议报文
 */
public class Message {
    public static final byte[] MAGIC_NUMBER                    = {'R', 'O', 'S', 'E'};
    public static final byte   REQUEST_MESSAGE_TYPE            = 1;
    public static final byte   RESPONSE_MESSAGE_TYPE           = 2;
    public static final byte   HEARTBEAT_REQUEST_MESSAGE_TYPE  = -1; // ping
    public static final byte   HEARTBEAT_RESPONSE_MESSAGE_TYPE = -2; // pong
    public static final byte   HEADER_LENGTH                   = 16;
    public static final int    MAX_FRAME_LENGTH                = 8 * 1024 * 1024;
    public static final byte   VERSION                         = 1;

    // 消息类型
    private byte   messageType;
    // 序列化类型
    private byte   codec;
    // 压缩类型
    private byte   compress;
    // 请求 id
    private int    id;
    // 具体数据
    private Object data;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
