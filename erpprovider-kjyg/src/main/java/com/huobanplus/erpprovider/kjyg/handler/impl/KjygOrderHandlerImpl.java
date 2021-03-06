/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.kjyg.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huobanplus.erpprovider.kjyg.common.KjygSysData;
import com.huobanplus.erpprovider.kjyg.formatkjyg.KjygCreateOrderInfo;
import com.huobanplus.erpprovider.kjyg.formatkjyg.KjygOrderItem;
import com.huobanplus.erpprovider.kjyg.handler.KjygOrderHandler;
import com.huobanplus.erpprovider.kjyg.search.KjygOrderSearch;
import com.huobanplus.erpservice.common.httputil.HttpClientUtil;
import com.huobanplus.erpservice.common.httputil.HttpResult;
import com.huobanplus.erpservice.common.ienum.OrderSyncStatus;
import com.huobanplus.erpservice.common.util.StringUtil;
import com.huobanplus.erpservice.datacenter.entity.logs.OrderDetailSyncLog;
import com.huobanplus.erpservice.datacenter.model.Order;
import com.huobanplus.erpservice.datacenter.model.OrderDeliveryInfo;
import com.huobanplus.erpservice.datacenter.model.OrderItem;
import com.huobanplus.erpservice.datacenter.service.logs.OrderDetailSyncLogService;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.erpevent.push.PushNewOrderEvent;
import com.huobanplus.erpservice.eventhandler.model.ERPInfo;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by wuxiongliu on 2016/5/23.
 */
@Component
public class KjygOrderHandlerImpl implements KjygOrderHandler {
    private static final Log log = LogFactory.getLog(KjygOrderHandlerImpl.class);

    @Autowired
    private OrderDetailSyncLogService orderDetailSyncLogService;

    @Override
    public EventResult pushOrder(PushNewOrderEvent pushNewOrderEvent) {
        Order orderInfo = JSON.parseObject(pushNewOrderEvent.getOrderInfoJson(), Order.class);
        String kjOrderId = orderInfo.getOrderId().substring(3);//因为跨境易购订单限制为14位

        ERPInfo erpInfo = pushNewOrderEvent.getErpInfo();
        KjygSysData kjygSysData = JSON.parseObject(erpInfo.getSysDataJson(), KjygSysData.class);
        ERPUserInfo erpUserInfo = pushNewOrderEvent.getErpUserInfo();

        List<OrderItem> orderItems = orderInfo.getOrderItems();
        List<KjygOrderItem> kjygOrderItems = new ArrayList<>();
        orderItems.forEach(orderItem -> {
            KjygOrderItem kjygOrderItem = new KjygOrderItem();
            kjygOrderItem.setSpe(orderItem.getProductBn());
            kjygOrderItem.setAmount(String.valueOf(orderItem.getNum()));
            kjygOrderItem.setPrice(String.valueOf(orderItem.getAmount()));// FIXME: 2016/5/30
            kjygOrderItems.add(kjygOrderItem);
        });

        KjygCreateOrderInfo createOrderInfo = new KjygCreateOrderInfo();

        createOrderInfo.setClientcode(kjygSysData.getClientCode());
        createOrderInfo.setShipName(orderInfo.getShipName());
        createOrderInfo.setShipMobile(orderInfo.getShipMobile());
        createOrderInfo.setWebAccountNo(orderInfo.getUserLoginName());
        createOrderInfo.setWebTradeNo(kjOrderId);
        createOrderInfo.setWebPayNo(kjOrderId);

        String payWay = "02";
//        if(orderPayWay == OrderEnum.PaymentOptions.ALIPAY_MOBILE.getCode() || orderPayWay==OrderEnum.PaymentOptions.ALIPAY_PC.getCode()){
//            payWay = "02";
//        } else if(orderPayWay == OrderEnum.PaymentOptions.WEIXINPAY.getCode() || orderPayWay== OrderEnum.PaymentOptions.WEIXINPAY_V3.getCode()
//                || orderPayWay== OrderEnum.PaymentOptions.WEIXINPAY_APP.getCode()){
//            payWay = "16";
//        } else if(orderPayWay == OrderEnum.PaymentOptions.REDEMPTION.getCode()){
//
//        } else if(orderPayWay == OrderEnum.PaymentOptions.UNIONPAY.getCode()){
//            payWay = "01";
//        } else if(orderPayWay == OrderEnum.PaymentOptions.BAIDUPAY.getCode()){// 百度钱包
//            payWay = "02";
//        } else if(orderPayWay == OrderEnum.PaymentOptions.WEIFUTONG.getCode()){// 威富通
//            payWay = "02";
//        }

        createOrderInfo.setPayWay(payWay);
        createOrderInfo.setBuyerPid(orderInfo.getBuyerPid());
        createOrderInfo.setBuyerName(orderInfo.getBuyerName());
        createOrderInfo.setBuyerTel(orderInfo.getUserLoginName());
        createOrderInfo.setPayment(String.valueOf(orderInfo.getOnlinePayAmount()));
        createOrderInfo.setWebsite(kjygSysData.getWebsite());
        createOrderInfo.setProvince(orderInfo.getProvince());
        createOrderInfo.setCity(orderInfo.getCity());
        createOrderInfo.setArea(orderInfo.getShipArea());
        createOrderInfo.setPostCode(orderInfo.getShipZip());
        createOrderInfo.setShipAddr(orderInfo.getShipAddr());
        createOrderInfo.setRemark(orderInfo.getRemark());
        createOrderInfo.setFharea("德国汉堡");
        createOrderInfo.setOrderNo(kjOrderId);
        createOrderInfo.setOrderItems(kjygOrderItems);

        EventResult eventResult = orderPush(kjygSysData, createOrderInfo, orderInfo.getOrderId());
        if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
            saveLog(orderInfo, erpUserInfo, erpInfo, pushNewOrderEvent, true, null);
        } else {
            saveLog(orderInfo, erpUserInfo, erpInfo, pushNewOrderEvent, false, eventResult.getResultMsg());
        }

        return eventResult;
    }

    private EventResult orderPush(KjygSysData kjygSysData, KjygCreateOrderInfo createOrderInfo, String originOrderId) {
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(createOrderInfo);
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("clientkey", kjygSysData.getClientKey());
            requestData.put("mtype", "trade");
            requestData.put("tradelist", jsonArray);

            HttpResult httpResult = HttpClientUtil.getInstance().post(kjygSysData.getRequestUrl(), requestData);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                JSONObject result = JSON.parseObject(httpResult.getHttpContent());
                if (result.getString("sts").equals("Y")) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS);
                } else {
                    return EventResult.resultWith(EventResultEnum.ERROR, result.getString("res"), null);
                }
            } else {
                return EventResult.resultWith(EventResultEnum.SYSTEM_BAD_REQUEST, httpResult.getHttpContent(), null);
            }
        } catch (Exception e) {
            log.info("跨境易购推送订单异常（" + originOrderId + "）-----" + e.getMessage());
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    /**
     * 记录日志
     *
     * @param orderInfo
     * @param erpUserInfo
     * @param erpInfo
     * @param pushNewOrderEvent
     * @param isSuccess
     */
    private void saveLog(Order orderInfo, ERPUserInfo erpUserInfo, ERPInfo erpInfo, PushNewOrderEvent pushNewOrderEvent, boolean isSuccess, String errorMsg) {
        OrderDetailSyncLog orderDetailSyncLog = orderDetailSyncLogService.findByOrderId(orderInfo.getOrderId());
        Date now = new Date();
        if (orderDetailSyncLog == null) {
            orderDetailSyncLog = new OrderDetailSyncLog();
            orderDetailSyncLog.setCreateTime(now);
        }
        orderDetailSyncLog.setCustomerId(erpUserInfo.getCustomerId());
        orderDetailSyncLog.setProviderType(erpInfo.getErpType());
        orderDetailSyncLog.setUserType(erpUserInfo.getErpUserType());
        orderDetailSyncLog.setOrderId(orderInfo.getOrderId());
        orderDetailSyncLog.setOrderInfoJson(pushNewOrderEvent.getOrderInfoJson());
        orderDetailSyncLog.setErpSysData(erpInfo.getSysDataJson());
        orderDetailSyncLog.setSyncTime(now);
        if (!StringUtils.isEmpty(errorMsg)) {
            orderDetailSyncLog.setErrorMsg(errorMsg);
        }

        if (isSuccess) {
            orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_SUCCESS);
        } else {
            orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_FAILURE);
        }

        orderDetailSyncLogService.save(orderDetailSyncLog);
    }

    @Override
    public EventResult queryOrderTradNo(List<Order> orderList, KjygSysData kjygSysData) {

        List<OrderDeliveryInfo> orderDeliveryInfoList = new ArrayList<>();
        for (Order order : orderList) {

            OrderDeliveryInfo orderDeliveryInfo = new OrderDeliveryInfo();
            orderDeliveryInfo.setOrderId(order.getOrderId());

            KjygOrderSearch kjygOrderSearch = new KjygOrderSearch();
            kjygOrderSearch.setOrderNo(order.getOrderId().substring(3));
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(kjygOrderSearch);
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("clientkey", kjygSysData.getClientKey());
            requestData.put("mtype", "awb");
            requestData.put("clientcode", kjygSysData.getClientCode());
            requestData.put("ordernos", jsonArray.toJSONString());

            HttpResult httpResult = HttpClientUtil.getInstance().post(kjygSysData.getRequestUrl(), requestData);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                JSONObject result = JSON.parseObject(httpResult.getHttpContent());
                if (result.getString("sts").equals("Y")) {
                    JSONArray resultArray = result.getJSONArray("res");
                    if (resultArray.size() > 0) {
                        JSONObject jsonObject = JSON.parseObject(resultArray.get(0).toString());
                        String trackNo = jsonObject.getString("trackno");
                        String awb = jsonObject.getString("awb");//航班

                        if (StringUtil.isNotEmpty(trackNo) && StringUtil.isNotEmpty(awb)) {
                            orderDeliveryInfo.setLogiNo(trackNo);
                            orderDeliveryInfo.setLogiName(awb);
                            orderDeliveryInfoList.add(orderDeliveryInfo);
                        }
                    }
                }
            }
        }
        return EventResult.resultWith(EventResultEnum.SUCCESS, orderDeliveryInfoList);

    }

    @Override
    public EventResult queryOrderStat(String orderNo, KjygSysData kjygSysData) {

        KjygOrderSearch kjygOrderSearch = new KjygOrderSearch();
        kjygOrderSearch.setOrderNo(orderNo);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(kjygOrderSearch);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("clientkey", kjygSysData.getClientKey());
        requestData.put("mtype", "orderstat");
        requestData.put("clientcode", kjygSysData.getClientCode());
        requestData.put("ordernos", jsonArray.toJSONString());

        HttpResult httpResult = HttpClientUtil.getInstance().post(kjygSysData.getRequestUrl(), requestData);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            JSONObject result = JSON.parseObject(httpResult.getHttpContent());
            if (result.getString("sts").equals("Y")) {
                JSONArray resultArray = result.getJSONArray("res");
                if (resultArray.size() == 0) {
                    return EventResult.resultWith(EventResultEnum.ERROR, "无订单数据", null);
                } else {
                    JSONObject jsonObject = JSON.parseObject(resultArray.get(0).toString());
                    System.out.println(jsonObject);
                    return EventResult.resultWith(EventResultEnum.SUCCESS, jsonObject);
                }

            } else {
                return EventResult.resultWith(EventResultEnum.ERROR, result.getString("res"), null);
            }
        } else {
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
        }
    }

}
