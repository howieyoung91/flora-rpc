/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.support.KryoSerializer;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequest;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponse;

import java.util.Arrays;

/**
 * [0,3]   magic number
 * 4       version
 * 5       message type
 * 6       codec
 * 7       compress
 * [8,11]  request id
 * [12,15] length
 * ...     body
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

    private SmartSerializer serializer = new KryoSerializer();

    public MessageDecoder() {
        super(RpcMessage.MAX_FRAME_LENGTH,
                12, 4,
                -16, 0
        );
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        logger.info("receive message {}", ctx.channel());
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcMessage.HEADER_LENGTH) {
                try {
                    return decodeFrame(frame);
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    /**
     * [0,3]   magic number
     * 4       version
     * 5       message type
     * 6       codec
     * 7       compress
     * [8,11]  request id
     * [12,15] length
     * ...     body
     */
    private Object decodeFrame(ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        byte messageType  = in.readByte();
        byte codecType    = in.readByte();
        byte compressType = in.readByte();
        int  id           = in.readInt();

        int length = in.readInt();

        RpcMessage message = new RpcMessage();
        message.setMessageType(messageType);
        message.setCodec(codecType);
        message.setCompress(compressType);
        message.setId(id);
        switch (messageType) {
            case RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE: {
                message.setData("ping");
                return message;
            }
            case RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE: {
                message.setData("pong");
                return message;
            }
        }

        int bodyLength = length - RpcMessage.HEADER_LENGTH;

        if (bodyLength > 0) {
            byte[] data = new byte[bodyLength];
            in.readBytes(data);
            // todo decompress

            deserialize(messageType, message, data);
        }
        return message;
    }

    private void deserialize(byte messageType, RpcMessage message, byte[] bs) {
        if (messageType == RpcMessage.REQUEST_MESSAGE_TYPE) {
            RpcRequest tmpValue = serializer.deserialize(bs, RpcRequest.class);
            message.setData(tmpValue);
        } else {
            RpcResponse tmpValue = serializer.deserialize(bs, RpcResponse.class);
            message.setData(tmpValue);
        }
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcMessage.VERSION) {
            throw new RuntimeException("version isn't compatible" + version);
        }
    }

    private void checkMagicNumber(ByteBuf in) {
        int    len  = RpcMessage.MAGIC_NUMBER.length;
        byte[] temp = new byte[len];

        in.readBytes(temp);
        for (int i = 0; i < len; i++) {
            if (temp[i] != RpcMessage.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("unknown magic number: " + Arrays.toString(temp));
            }
        }
    }
}
