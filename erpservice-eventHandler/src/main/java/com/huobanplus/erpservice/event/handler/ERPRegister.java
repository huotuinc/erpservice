package com.huobanplus.erpservice.event.handler;

import org.springframework.stereotype.Component;
import com.huobanplus.erpservice.event.model.ERPInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * erp注册器，提供给erp-provider注册
 * <p>平台将根据事件中携带ERPInfo来分配到某一个erp-provider处理</p>
 * Created by allan on 2015/7/13.
 */
@Component
public class ERPRegister {
    List<ERPHandlerBuilder> handlerBuilders = new ArrayList<>();

    public List<ERPHandlerBuilder> getHandlerBuilders() {
        return handlerBuilders;
    }

    public void setHandlerBuilders(List<ERPHandlerBuilder> handlerBuilders) {
        this.handlerBuilders = handlerBuilders;
    }

    /**
     * 添加(注册)一个erp com.huobanplus.erpservice.event.handler builder
     *
     * @param handlerBuilder
     */
    public void addBuilders(ERPHandlerBuilder handlerBuilder) {
        this.handlerBuilders.add(handlerBuilder);
    }

    /**
     * 根据erpinfo得到指定的erp事件处理器
     *
     * @param erpInfo erp信息
     * @return
     */
    public ERPHandler getERPHandler(ERPInfo erpInfo) {
        ERPHandler erpHandler;
        for (ERPHandlerBuilder handlerBuilder : handlerBuilders) {
            erpHandler = handlerBuilder.buildHandler(erpInfo);
            if (erpHandler != null) {
                return erpHandler;
            }
        }
        return null;
    }
}