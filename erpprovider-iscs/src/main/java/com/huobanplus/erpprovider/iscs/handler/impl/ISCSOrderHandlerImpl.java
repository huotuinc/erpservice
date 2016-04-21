/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.iscs.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huobanplus.erpprovider.iscs.common.ISCSSysData;
import com.huobanplus.erpprovider.iscs.formatiscs.ISCSCreateOrder;
import com.huobanplus.erpprovider.iscs.formatiscs.ISCSCreateOrderInfo;
import com.huobanplus.erpprovider.iscs.formatiscs.ISCSCreateOrderItem;
import com.huobanplus.erpprovider.iscs.handler.ISCSBaseHandler;
import com.huobanplus.erpprovider.iscs.handler.ISCSOrderHandler;
import com.huobanplus.erpprovider.iscs.search.ISCSOrderSearch;
import com.huobanplus.erpservice.common.httputil.HttpClientUtil;
import com.huobanplus.erpservice.common.httputil.HttpResult;
import com.huobanplus.erpservice.common.ienum.EnumHelper;
import com.huobanplus.erpservice.common.ienum.OrderEnum;
import com.huobanplus.erpservice.common.util.StringUtil;
import com.huobanplus.erpservice.datacenter.common.ERPTypeEnum;
import com.huobanplus.erpservice.datacenter.entity.OrderOperatorLog;
import com.huobanplus.erpservice.datacenter.entity.OrderSync;
import com.huobanplus.erpservice.datacenter.jsonmodel.Order;
import com.huobanplus.erpservice.datacenter.service.OrderOperatorService;
import com.huobanplus.erpservice.datacenter.service.OrderSyncService;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.erpevent.push.PushNewOrderEvent;
import com.huobanplus.erpservice.eventhandler.model.ERPInfo;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by allan on 4/19/16.
 */
@Component
public class ISCSOrderHandlerImpl extends ISCSBaseHandler implements ISCSOrderHandler {
    @Autowired
    private OrderSyncService orderSyncService;
    @Autowired
    private OrderOperatorService orderOperatorService;

    @Override
    public EventResult pushOrder(PushNewOrderEvent pushNewOrderEvent) {
        Order order = JSON.parseObject(pushNewOrderEvent.getOrderInfoJson(), Order.class);
        ERPInfo erpInfo = pushNewOrderEvent.getErpInfo();
        ISCSSysData sysData = JSON.parseObject(erpInfo.getSysDataJson(), ISCSSysData.class);
        ERPUserInfo erpUserInfo = pushNewOrderEvent.getErpUserInfo();
        try {
            ISCSCreateOrderInfo createOrderInfo = new ISCSCreateOrderInfo();
            createOrderInfo.setOrderNo(order.getOrderId());
            createOrderInfo.setStockId(sysData.getStockId());
            createOrderInfo.setTransporterFlag(0);
            createOrderInfo.setShopId(sysData.getShopId());
            createOrderInfo.setCreateTime(order.getCreateTime());
            createOrderInfo.setPayTime(order.getPayTime());
//            createOrderInfo.setBuyNick();
//            createOrderInfo.setCountry();
            String shipArea = order.getShipArea();
            if (!StringUtils.isEmpty(shipArea)) {
                String[] shipAreaArray = shipArea.split("/");
                createOrderInfo.setProvince(shipAreaArray[0]);
                if (shipAreaArray.length > 1) {
                    createOrderInfo.setCity(shipAreaArray[1]);
                    if (shipAreaArray.length > 2) {
                        createOrderInfo.setCounty(shipAreaArray[2]);
                    }
                }
            }
            createOrderInfo.setAddress(order.getShipAddr());
            createOrderInfo.setZip(order.getShipZip());
            createOrderInfo.setName(order.getShipName());
            createOrderInfo.setMobile(order.getShipMobile());
            createOrderInfo.setTel(order.getShipTel());
//            createOrderInfo.setRequestShipDate();
//            createOrderInfo.setNeedInvoice();
//            createOrderInfo.setInvoiceName();
//            createOrderInfo.setInvoiceContent();
            createOrderInfo.setPayment(order.getFinalAmount());
            createOrderInfo.setTotalFee(order.getFinalAmount());
            createOrderInfo.setDiscountFee(order.getPmtAmount());
            createOrderInfo.setPostFee(order.getCostFreight());
            createOrderInfo.setNeedCard(0);
            List<ISCSCreateOrderItem> createOrderItems = new ArrayList<>();
            order.getOrderItems().forEach(item -> {
                ISCSCreateOrderItem createOrderItem = new ISCSCreateOrderItem();
                createOrderItem.setProductCode(item.getProductBn());
                createOrderItem.setNum(item.getNum());
                createOrderItem.setPrice(item.getPrice());
//                createOrderItem.setDiscountFee();
                createOrderItem.setShopId(sysData.getShopId());
                createOrderItem.setOwnerId(sysData.getOwnerId());
                createOrderItem.setSellPrice(item.getCost());
                createOrderItems.add(createOrderItem);
            });
            ISCSCreateOrder createOrder = new ISCSCreateOrder(1, Arrays.asList(createOrderInfo));
            Date now = new Date();
            String nowStr = StringUtil.DateFormat(now, StringUtil.TIME_PATTERN);
            Map<String, Object> requestData = getRequestData(sysData, nowStr, "pushTrades", JSON.toJSONString(createOrder));

            //订单同步操作日志
            OrderOperatorLog orderOperatorLog = new OrderOperatorLog();
            orderOperatorLog.setProviderType(erpInfo.getErpType());
            orderOperatorLog.setUserType(erpUserInfo.getErpUserType());
            orderOperatorLog.setCustomerId(erpUserInfo.getCustomerId());
            orderOperatorLog.setCreateTime(now);
            orderOperatorLog.setOrderId(order.getOrderId());
//            orderOperatorLog.setOrderJsonData(pushNewOrderEvent.getOrderInfoJson());
            orderOperatorLog.setEventInfo(JSON.toJSONString(pushNewOrderEvent));

            //订单同步记录
            OrderSync orderSync = orderSyncService.getOrderSync(order.getOrderId(), erpUserInfo.getCustomerId());
            orderSync.setOrderStatus(EnumHelper.getEnumType(OrderEnum.OrderStatus.class, order.getOrderStatus()));
            orderSync.setPayStatus(EnumHelper.getEnumType(OrderEnum.PayStatus.class, order.getPayStatus()));
            orderSync.setShipStatus(EnumHelper.getEnumType(OrderEnum.ShipStatus.class, order.getShipStatus()));
            orderSync.setProviderType(ERPTypeEnum.ProviderType.ISCS);
            orderSync.setUserType(erpUserInfo.getErpUserType());
            orderSync.setRemark(orderOperatorLog.getRemark());

            // TODO: 4/20/16
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public EventResult getOrderDeliveryInfo(ISCSSysData sysData, ISCSOrderSearch orderSearch) {
        try {
            Date now = new Date();
            String nowStr = StringUtil.DateFormat(now, StringUtil.TIME_PATTERN);
            Map<String, Object> requestData = getRequestData(sysData, nowStr, "tradeDeliverQuery", JSON.toJSONString(orderSearch));

            HttpResult httpResult = HttpClientUtil.getInstance().post(sysData.getHost(), requestData);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                JSONObject result = JSON.parseObject(httpResult.getHttpContent());
                //如果有errorCode,说明失败了,返回失败信息
                if (result.getJSONObject("errorCode") != null) {
                    return EventResult.resultWith(EventResultEnum.ERROR, result.getString("subMessage"), null);
                }
                return EventResult.resultWith(EventResultEnum.SUCCESS, result);
            }
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
        } catch (Exception e) {
            return EventResult.resultWith(EventResultEnum.ERROR);
        }
    }
}