/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.annotation;

public class ServiceReferenceContext {
    private Object bean;
    private String beanName;

    public ServiceReferenceContext(Object bean, String beanName) {
        this.bean = bean;
        this.beanName = beanName;
    }

    public Object getBean() {
        return bean;
    }

    public String getBeanName() {
        return beanName;
    }
}
