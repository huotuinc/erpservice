/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huobanplus.erpservice.eventhandler;

import com.huobanplus.erpservice.eventhandler.handler.ERPHandler;
import com.huobanplus.erpservice.eventhandler.handler.ERPHandlerBuilder;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.userhandler.ERPUserHandler;
import com.huobanplus.erpservice.eventhandler.userhandler.ERPUserHandlerBuilder;
import lombok.Data;
import org.springframework.stereotype.Component;
import com.huobanplus.erpservice.eventhandler.model.ERPInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * erp注册器，提供给erp-provider注册
 * <p>平台将根据事件中携带ERPInfo来分配到某一个erp-provider处理</p>
 * Created by allan on 2015/7/13.
 */
@Component
@Data
public class ERPRegister {
    List<ERPHandlerBuilder> handlerBuilders = new ArrayList<>();
    List<ERPUserHandlerBuilder> userHandlerBuilders = new ArrayList<>();

    /**
     * 添加(注册)一个erp com.huobanplus.erpservice.eventhandler.handler builder
     *
     * @param handlerBuilder
     */
    public void addBuilders(ERPHandlerBuilder handlerBuilder) {
        this.handlerBuilders.add(handlerBuilder);
    }

    /**
     * 添加（注册）一个erp-user
     *
     * @param userHandlerBuilder
     */
    public void addUserBuilders(ERPUserHandlerBuilder userHandlerBuilder) {
        this.userHandlerBuilders.add(userHandlerBuilder);
    }

    /**
     * 根据erpinfo得到指定的erp事件处理器
     *
     * @param erpInfo erp信息
     * @return
     */
    public ERPHandler getERPHandler(ERPInfo erpInfo) {
        ERPHandler erpHandler = null;
        for (ERPHandlerBuilder handlerBuilder : handlerBuilders) {
            erpHandler = handlerBuilder.buildHandler(erpInfo);
            if (erpHandler != null) {
                return erpHandler;
            }
        }
        return erpHandler;
    }

    /**
     * 根据erpuser-info得到指定的erp事件处理器
     *
     * @param erpUserInfo
     * @return
     */
    public ERPUserHandler getERPUserHandler(ERPUserInfo erpUserInfo) {
        ERPUserHandler erpUserHandler = null;
        for (ERPUserHandlerBuilder userHandlerBuilder : userHandlerBuilders) {
            erpUserHandler = userHandlerBuilder.buildHandler(erpUserInfo);
            if (erpUserHandler != null) {
                return erpUserHandler;
            }
        }
        return erpUserHandler;
    }
}
