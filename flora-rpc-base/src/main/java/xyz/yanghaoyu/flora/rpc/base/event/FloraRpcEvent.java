/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.event;

import xyz.yanghaoyu.flora.core.context.event.ApplicationEvent;

// todo
public abstract class FloraRpcEvent extends ApplicationEvent {
    public FloraRpcEvent(Object source) {
        super(source);
    }
}
