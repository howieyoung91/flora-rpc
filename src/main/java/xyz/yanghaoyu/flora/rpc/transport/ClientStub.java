package xyz.yanghaoyu.flora.rpc.transport;

import xyz.yanghaoyu.flora.rpc.transport.dto.Request;
import xyz.yanghaoyu.flora.rpc.transport.dto.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ClientStub implements InvocationHandler {
    private String host;
    private int    port;
    private Client client;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request     request  = getRequest(method, args);
        Response<?> response = client.send(request, host, port);
        // todo 发送消息到服务端

        return response.getData();
    }

    private Request getRequest(Method method, Object[] args) {
        Request request = new Request();
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(args);
        return request;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }


}
