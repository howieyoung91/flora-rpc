/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport.dto;

public class RpcResponseConfig {
    // private String  requestId;
    // private Integer code;
    // private String  message;
    private String serializer;
    private Object data;


    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public Object getData() {
        return data;
    }

    public void setBody(Object data) {
        this.data = data;
    }
}
