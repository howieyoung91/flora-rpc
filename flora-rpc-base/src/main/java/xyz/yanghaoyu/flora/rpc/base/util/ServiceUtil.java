/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.util;

import xyz.yanghaoyu.flora.rpc.base.transport.dto.*;

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

    public static RpcMessage buildMessage(RpcRequestConfig requestConfig) {
        RpcRequestBody requestBody = buildRpcRequestBody(requestConfig);
        RpcMessage     message     = new RpcMessage();
        message.setBody(requestBody);
        message.setSerializer(requestConfig.getSerializer());
        message.setCompressor(requestConfig.getCompressor());
        message.setType(RpcMessage.REQUEST_MESSAGE_TYPE);
        return message;
    }

    public static RpcRequestBody buildRpcRequestBody(RpcRequestConfig requestConfig) {
        RpcRequestBody requestBody = new RpcRequestBody();
        requestBody.setMethodName(requestConfig.getMethodName());
        requestBody.setId(requestConfig.getId());
        requestBody.setParamTypes(requestConfig.getParamTypes());
        requestBody.setServiceName(requestConfig.getServiceReferenceAttribute().getServiceName());
        requestBody.setParams(requestConfig.getParams());
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

