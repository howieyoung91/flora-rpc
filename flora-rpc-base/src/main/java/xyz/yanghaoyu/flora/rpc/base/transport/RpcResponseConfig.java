/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.transport;

public class RpcResponseConfig {
    private String serializer;  // response 序列化
    private String compressor;  // response 压缩类型
    private Object data;        // service  处理处理结果

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public Object getData() {
        return data;
    }

    public String getCompressor() {
        return compressor;
    }

    public void setCompressor(String compressor) {
        this.compressor = compressor;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
