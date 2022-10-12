/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.util;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.*;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.factory.RpcMessageBuilder;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public abstract class ServiceUtil {
    public static String buildNamespacedServiceNodePath(String namespace, String serviceName) {
        return namespace + "/" + serviceName;
    }

    public static String buildServicePathWithAddress(String namespace, String serviceName, InetSocketAddress address) {
        return buildServicePathWithAddress(buildNamespacedServiceNodePath(namespace, serviceName), address);
    }

    public static String buildServicePathWithAddress(String serviceNodePath, InetSocketAddress address) {
        return serviceNodePath + address.toString();
    }

    public static InetSocketAddress buildAddress(String addressString) {
        return buildAddress(addressString.split(":"));
    }

    public static InetSocketAddress buildAddress(String[] socketAddressArray) {
        String host = socketAddressArray[0];
        int    port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }

    public static RpcMessage<RpcRequestBody> buildRpcRequestMessage(RpcRequestConfig requestConfig) {
        RpcRequestBody    requestBody = buildRpcRequestBody(requestConfig);
        RpcMessageBuilder builder     = RpcMessageBuilder.<RpcRequestBody>aRpcMessage();
        builder.body(requestBody)
                .compressor(requestConfig.compressorName())
                .serializer(requestConfig.serializerName())
                .type(RpcMessage.REQUEST_MESSAGE_TYPE);
        return builder.build();
    }

    public static RpcRequestBody buildRpcRequestBody(RpcRequestConfig requestConfig) {
        RpcRequestBody requestBody = new RpcRequestBody();
        requestBody.setId(requestConfig.id());
        requestBody.setMethodName(requestConfig.methodName());
        requestBody.setParamTypes(requestConfig.paramTypes());
        requestBody.setServiceName(requestConfig.serviceReferenceAttribute().getServiceName());
        requestBody.setArgs(requestConfig.args());
        return requestBody;
    }

    public static InetSocketAddress getLocalAddress(int port) {
        try {
            return new InetSocketAddress(Inet4Address.getLocalHost().getHostAddress(), port);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("fail to get local address. port: [" + port + "]");
    }

    public static RpcResponseBody buildResponseBody(String requestId, RpcResponseConfig responseConfig) {
        RpcResponseBody responseBody = new RpcResponseBody();
        responseBody.setMessage("ok");
        responseBody.setCode(200);
        responseBody.setRequestId(requestId);
        responseBody.setData(responseConfig.getData());
        return responseBody;
    }
}

