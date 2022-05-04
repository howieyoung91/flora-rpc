package xyz.yanghaoyu.flora.rpc.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import xyz.yanghaoyu.flora.rpc.serialize.SmartSerializer;
import xyz.yanghaoyu.flora.rpc.serialize.support.KryoSerializer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * [0,3]   magic number
 * [4,7]   length
 * 8       message type
 * 9       codec
 * 10      compress
 * [11,14] request id
 * 15      version
 * ...     body
 */
public class TransportMessageEncoder extends MessageToByteEncoder<TransportMessage> {
    AtomicInteger   i          = new AtomicInteger(0);
    SmartSerializer serializer = new KryoSerializer();

    @Override
    protected void encode(ChannelHandlerContext context, TransportMessage message, ByteBuf byteBuf) {
        byteBuf.writeBytes(TransportMessage.MAGIC_NUMBER);
        // skip length
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        byte messageType = message.getMessageType();
        byteBuf.writeByte(messageType);
        byteBuf.writeByte(message.getCodec());
        byteBuf.writeByte(message.getCompress());
        byteBuf.writeInt(i.getAndIncrement());
        byteBuf.writeByte(message.getVersion());

        int length = TransportMessage.HEADER_LENGTH;
        if (messageType != TransportMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE
            && messageType != TransportMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE) {
            byte[] body = serializer.serialize(message.getData());
            length += body.length;
        }

        writeLength(byteBuf, length);
    }

    private void writeLength(ByteBuf byteBuf, int length) {
        int index = byteBuf.writerIndex();

        byteBuf.writerIndex(4);
        byteBuf.writeInt(length);

        byteBuf.writerIndex(index);
    }
}
