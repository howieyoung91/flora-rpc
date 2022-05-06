/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.transport.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import xyz.yanghaoyu.flora.rpc.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.serialize.support.KryoSerializer;
import xyz.yanghaoyu.flora.rpc.transport.dto.Message;

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
public class MessageEncoder extends MessageToByteEncoder<Message> {
    AtomicInteger   ID_GENERATOR = new AtomicInteger(0);
    SmartSerializer serializer   = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext context, Message message, ByteBuf byteBuf) {
        byteBuf.writeBytes(Message.MAGIC_NUMBER);
        byteBuf.writeByte(Message.VERSION);
        byte messageType = message.getMessageType();
        byteBuf.writeByte(messageType);
        byteBuf.writeByte(message.getCodec());
        byteBuf.writeByte(message.getCompress());
        byteBuf.writeInt(ID_GENERATOR.getAndIncrement());

        byteBuf.markWriterIndex();
        // skip length field
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        int length = Message.HEADER_LENGTH;
        if (messageType != Message.HEARTBEAT_REQUEST_MESSAGE_TYPE
            && messageType != Message.HEARTBEAT_RESPONSE_MESSAGE_TYPE
        ) {
            byte[] body = serializer.serialize(message.getData());
            length += body.length;
            byteBuf.writeBytes(body);
        }

        byteBuf.resetWriterIndex();
        byteBuf.writeInt(length);
        // writeLength(byteBuf, length);
    }

    // private void writeLength(ByteBuf byteBuf, int length) {
    //     int index = byteBuf.writerIndex();
    //
    //     byteBuf.writerIndex(5);
    //     byteBuf.writeInt(length);
    //
    //     byteBuf.writerIndex(index);
    // }
}
