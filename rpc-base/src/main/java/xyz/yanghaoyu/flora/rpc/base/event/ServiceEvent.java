/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.event;

import xyz.yanghaoyu.flora.framework.core.context.event.ApplicationEvent;

public abstract class ServiceEvent extends ApplicationEvent {
    public ServiceEvent(Object source) {
        super(source);
    }

    public final RemoteService getService() {
        return ((RemoteService) getSource());
    }
}
