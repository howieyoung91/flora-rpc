/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.annotation;

public class RpcRequestAttribute {
    private String  serializerName;
    private String  compressorName;
    private boolean alwaysRemote;

    public String getSerializerName() {
        return serializerName;
    }

    public void setSerializerName(String serializerName) {
        this.serializerName = serializerName;
    }

    public String getCompressorName() {
        return compressorName;
    }

    public void setCompressorName(String compressorName) {
        this.compressorName = compressorName;
    }

    public boolean isAlwaysRemote() {
        return alwaysRemote;
    }

    public void setAlwaysRemote(boolean remote) {
        this.alwaysRemote = remote;
    }
}
