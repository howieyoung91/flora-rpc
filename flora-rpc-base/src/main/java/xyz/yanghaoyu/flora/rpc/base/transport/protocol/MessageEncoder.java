/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.yanghaoyu.flora.rpc.base.compress.Compressor;
import xyz.yanghaoyu.flora.rpc.base.compress.CompressorFactory;
import xyz.yanghaoyu.flora.rpc.base.serialize.Serializer;
import xyz.yanghaoyu.flora.rpc.base.serialize.SerializerFactory;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcMessage;

/*
  自定义一个 rpc 协议

  0       4         5              6                7               8    11      15
  +-------+---------+--------------+----------------+---------------+----+--------+
  | magic | version | message type | serialize type | compress type |  ? | length |
  +-------------------------------------------------------------------------------+
  |                                     body                                      |
  +-------------------------------------------------------------------------------+

  1. magic              魔数            用于快速判断是否是无效包
  2. version            版本号          用于协议升级
  3. message type       消息类型        可能是请求包，响应包，心跳检测请求包，心跳检测响应包
  4. serialize type     序列化类型
  5. compress type      body 压缩类型
  6. ?                  未使用
  7. length             报文长度
  8. body               报文数据
 */
@ChannelHandler.Sharable
public class MessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final Logger LOGGER                        = LoggerFactory.getLogger(MessageEncoder.class);
    private static final String FLORA_DEFAULT_SERIALIZER_NAME = "KRYO";
    private static final String FLORA_DEFAULT_COMPRESSOR_NAME = "NOCOMPRESS";

    private SerializerFactory serializerFactory;
    private Serializer        defaultSerializer;
    private CompressorFactory compressorFactory;
    private Compressor        defaultCompressor;

    public MessageEncoder(
            SerializerFactory serializerFactory, String defaultSerializerName,
            CompressorFactory compressorFactory, String defaultCompressorName) {
        this.serializerFactory = serializerFactory;
        this.compressorFactory = compressorFactory;

        if (defaultSerializerName == null) {
            this.defaultSerializer = serializerFactory.getSerializer(FLORA_DEFAULT_SERIALIZER_NAME);
        }
        else {
            this.defaultSerializer = serializerFactory.getSerializer(defaultSerializerName);
            if (this.defaultSerializer == null) {
                LOGGER.warn("unknown default serializer [{}]", defaultSerializerName);
                this.defaultSerializer = serializerFactory.getSerializer(FLORA_DEFAULT_SERIALIZER_NAME);
            }
        }

        if (defaultCompressorName == null) {
            this.defaultCompressor = compressorFactory.getCompressor(FLORA_DEFAULT_COMPRESSOR_NAME);
        }
        else {
            this.defaultCompressor = compressorFactory.getCompressor(defaultCompressorName);
            if (this.defaultCompressor == null) {
                LOGGER.warn("unknown default compressor [{}]", defaultCompressorName);
                this.defaultCompressor = compressorFactory.getCompressor(FLORA_DEFAULT_COMPRESSOR_NAME);
            }
        }
    }


    @Override
    protected void encode(ChannelHandlerContext context, RpcMessage message, ByteBuf byteBuf) {
        byteBuf.writeBytes(RpcMessage.MAGIC_NUMBER);
        byteBuf.writeByte(RpcMessage.VERSION);

        byteBuf.writeByte(message.getType());

        final Serializer serializer = getSerializer(message.getSerializer());
        byteBuf.writeByte(serializer.code());

        final Compressor compressor = getCompressor(message.getCompressor());
        byteBuf.writeByte(compressor.code());

        // 跳过 4 个未使用的字节
        byteBuf.writeInt(0);

        // mark length field index
        byteBuf.markWriterIndex();
        // skip length field
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);

        byte[] bodyData = doSerialize(serializer, message, context);
        int    length   = writeBody(byteBuf, bodyData, compressor);

        final int end = byteBuf.writerIndex();

        byteBuf.resetWriterIndex();
        byteBuf.writeInt(length);

        byteBuf.writerIndex(end);
    }

    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------

    private byte[] doSerialize(Serializer serializer, RpcMessage message, ChannelHandlerContext context) {
        switch (message.getType()) {
            case RpcMessage.REQUEST_MESSAGE_TYPE:
            case RpcMessage.RESPONSE_MESSAGE_TYPE: {
                return serializer.serialize(message.getBody());
            }
            case RpcMessage.HEARTBEAT_REQUEST_MESSAGE_TYPE: {
                LOGGER.debug("ping {}", context.channel().remoteAddress());
                break;
            }
            case RpcMessage.HEARTBEAT_RESPONSE_MESSAGE_TYPE: {
                LOGGER.debug("pong {}", context.channel().remoteAddress());
                break;
            }
        }
        return null;
    }

    private int writeBody(ByteBuf byteBuf, byte[] bodyData, Compressor compressor) {
        int length = RpcMessage.HEADER_LENGTH;
        // 写入 body
        if (bodyData != null) {
            // compress
            bodyData = compressor.compress(bodyData);

            length += bodyData.length;
            byteBuf.writeBytes(bodyData);
        }
        return length;
    }

    private Serializer getSerializer(String serializerName) {
        if (serializerName == null) {
            return defaultSerializer;
        }
        Serializer serializer = serializerFactory.getSerializer(serializerName);
        if (serializer == null) {
            LOGGER.warn("unknown serializer [{}]", serializerName);
            return defaultSerializer;
        }
        return serializer;
    }


    private Compressor getCompressor(String compressorName) {
        if (compressorName == null) {
            return defaultCompressor;
        }
        Compressor compressor = compressorFactory.getCompressor(compressorName);
        if (compressor == null) {
            LOGGER.warn("unknown compressor [{}]", defaultCompressor);
            return defaultCompressor;
        }
        return compressor;
    }

    // -----------------------------------------------------------------------------------------------------
    // --------------------------------------    private methods    ----------------------------------------
    // -----------------------------------------------------------------------------------------------------
}
