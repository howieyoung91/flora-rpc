/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
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
    private       AtomicInteger           ID_GENERATOR = new AtomicInteger(0);
    private final Map<String, Serializer> serializers;

    public MessageEncoder(Map<String, Serializer> serializers) {
        this.serializers = serializers;
    }

    @Override
    protected void encode(ChannelHandlerContext context, RpcMessage message, ByteBuf byteBuf) {
        byteBuf.writeBytes(RpcMessage.MAGIC_NUMBER);
        byteBuf.writeByte(RpcMessage.VERSION);

        byte messageType = message.getMessageType();
        byteBuf.writeByte(message.getMessageType());

        Serializer serializer = getSerializer(message);
        byteBuf.writeByte(serializer.code());

        byteBuf.writeByte(message.getCompress());
        byteBuf.writeInt(ID_GENERATOR.getAndIncrement());

        byteBuf.markWriterIndex();
        // skip length field
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        int length = RpcMessage.HEADER_LENGTH;
        if (isHeartbeat(messageType)) {
            byte[] body = serializer.serialize(message.getData());

            length += body.length;

            byteBuf.writeBytes(body);
        }

        int end = byteBuf.writerIndex();

        byteBuf.resetWriterIndex();
        byteBuf.writeInt(length);

        byteBuf.writerIndex(end);
    }

    private boolean isHeartbeat(byte messageType) {
        return messageType != RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE && messageType != RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE;
    }

    private Serializer getSerializer(RpcMessage message) {
        Serializer serializer = serializers.get(message.getSerializer());
        if (serializer == null) {
            serializer = serializers.get("KRYO");
        }
        return serializer;
    }
}
