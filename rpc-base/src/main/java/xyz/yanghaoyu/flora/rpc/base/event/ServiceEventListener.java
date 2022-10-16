package xyz.yanghaoyu.flora.rpc.base.event;

import xyz.yanghaoyu.flora.framework.core.context.ApplicationListener;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/15 15:01]
 */
public interface ServiceEventListener<E extends ServiceEvent> extends ApplicationListener<E> {
}
