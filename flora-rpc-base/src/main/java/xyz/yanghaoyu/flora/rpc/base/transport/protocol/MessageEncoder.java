/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.Serializer;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
public class MessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEncoder.class);

    private       AtomicInteger           ID_GENERATOR = new AtomicInteger(0);
    private final Map<String, Serializer> serializers;
    private final String                  defaultSerializer;

    public MessageEncoder(Map<String, Serializer> serializers, String defaultSerializer) {
        this.serializers = serializers;
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    protected void encode(ChannelHandlerContext context, RpcMessage message, ByteBuf byteBuf) {
        byteBuf.writeBytes(RpcMessage.MAGIC_NUMBER);
        byteBuf.writeByte(RpcMessage.VERSION);

        byte messageType = message.getMessageType();
        byteBuf.writeByte(message.getMessageType());


        Serializer serializer = getSerializer(message.getSerializer());
        byteBuf.writeByte(serializer.code());

        byteBuf.writeByte(message.getCompress());
        byteBuf.writeInt(ID_GENERATOR.getAndIncrement());

        byteBuf.markWriterIndex();
        // skip length field
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        int    length   = RpcMessage.HEADER_LENGTH;
        byte[] bodyData = null;
        if (messageType == RpcMessage.REQUEST_MESSAGE_TYPE || messageType == RpcMessage.RESPONSE_MESSAGE_TYPE) {
            Object body = message.getBody();
            bodyData = serializer.serialize(body);
        } else if (messageType == RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE) {
            // client
            LOGGER.info("ping {}", context.channel().remoteAddress());
        } else {
            // server RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE
            LOGGER.info("pong {}", context.channel().remoteAddress());
        }

        if (bodyData != null) {
            length += bodyData.length;
            byteBuf.writeBytes(bodyData);
        }


        int end = byteBuf.writerIndex();

        byteBuf.resetWriterIndex();
        byteBuf.writeInt(length);

        byteBuf.writerIndex(end);
    }

    private boolean isCommonMessage(byte messageType) {
        return messageType != RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE
               && messageType != RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE;
    }


    private Serializer getSerializer(String serializerName) {
        if (serializerName == null) {
            return getDefaultSerializer();
        }

        Serializer serializer = serializers.get(serializerName);

        if (serializer == null) {
            LOGGER.warn("unknown serializer [{}]", serializerName);
            return getDefaultSerializer();
        }
        return serializer;
    }

    private Serializer getDefaultSerializer() {
        Serializer serializer = serializers.get(defaultSerializer);
        if (serializer == null) {
            LOGGER.warn("unknown serializer [{}]", defaultSerializer);
        }
        return serializers.get("KRYO");
    }
}
