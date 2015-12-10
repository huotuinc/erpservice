/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huobanplus.erpuser.hotsupplier.handler;

import com.huobanplus.erpservice.eventhandler.model.*;

/**
 * Created by liual on 2015-12-08.
 */
public interface SupOrderHandler {
    /**
     * 发货通知处理
     *
     * @param deliveryInfo
     * @return
     */
    EventResult deliverInfo(DeliveryInfo deliveryInfo, ERPUserInfo erpUserInfo);

    /**
     * 退货通知
     *
     * @param returnInfo
     * @return
     */
    EventResult returnInfo(ReturnInfo returnInfo, ERPUserInfo erpUserInfo);

    /**
     * 获取订单列表
     *
     * @param orderSearchInfo
     * @return
     */
    EventResult obtainOrderList(OrderSearchInfo orderSearchInfo, ERPUserInfo erpUserInfo);

    /**
     * 获得订单详情
     *
     * @param orderId
     * @return
     */
    EventResult obtainOrderDetail(String orderId, ERPUserInfo erpUserInfo);
}