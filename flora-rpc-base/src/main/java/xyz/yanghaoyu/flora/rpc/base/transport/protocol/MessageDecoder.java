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
import xyz.yanghaoyu.flora.rpc.base.serialize.Deserializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializerFactory;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestBody;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;

import java.util.Arrays;

/*
  自定义一个 rpc 协议

  0       4         5              6                7               8    11      15
  +-------+---------+--------------+----------------+---------------+----+--------+
  | magic | version | message type | serialize type | compress type | id | length |
  +-------------------------------------------------------------------------------+
  |                                     body                                      |
  +-------------------------------------------------------------------------------+

  1. magic              魔数            用于快速判断是否是无效包
  2. version            版本号          用于协议升级
  3. message type       消息类型        可能是请求包，响应包，心跳检测请求包，心跳检测响应包
  4. serialize type     序列化类型
  5. compress type      body 压缩类型
  6. id                 报文 id
  7. length             报文长度
  8. body               报文数据
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDecoder.class);

    private SerializerFactory serializerFactory;

    public MessageDecoder(SerializerFactory serializerFactory) {
        super(RpcMessage.MAX_FRAME_LENGTH,
                12, 4,
                -16, 0
        );
        this.serializerFactory = serializerFactory;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcMessage.HEADER_LENGTH) {
                try {
                    return decodeFrame(ctx, frame);
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ChannelHandlerContext ctx, ByteBuf in) {
        checkMagicNumber(in);
        checkVersion(in);
        byte         messageType  = in.readByte();
        Deserializer deserializer = getDeserializer(in.readByte());
        byte         compressType = in.readByte();
        int          id           = in.readInt();

        if (messageType == RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE) {
            // server
            LOGGER.info("{} ping", ctx.channel().remoteAddress());

            RpcMessage<Object> pong =
                    RpcMessage.of(RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE, null);
            pong.setId(id);
            pong.setSerializer("KRYO");
            pong.setCompress((byte) 0);
            ctx.writeAndFlush(pong);
            return null;
        } else if (messageType == RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE) {
            // client
            LOGGER.info("{} pong", ctx.channel().remoteAddress());
            return null;
        }

        int length = in.readInt();
        // handle rpc
        int bodyLength = length - RpcMessage.HEADER_LENGTH;
        if (bodyLength == 0) {
            return null;
        }

        byte[] body = new byte[bodyLength];
        in.readBytes(body);


        try {
            // request
            if (messageType == RpcMessage.REQUEST_MESSAGE_TYPE) {
                return deserializer.deserialize(body, RpcRequestBody.class);
            }
            // response
            else {
                return deserializer.deserialize(body, RpcResponseBody.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("fail to decode");
        }
    }

    private Deserializer getDeserializer(byte codecType) {
        Deserializer deserializer = serializerFactory.getDeserializer(codecType);
        if (deserializer == null) {
            LOGGER.warn("unknown deserializer marked by code [{}]", codecType);
            deserializer = serializerFactory.getDeserializer((byte) 0);
        }
        return deserializer;
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
