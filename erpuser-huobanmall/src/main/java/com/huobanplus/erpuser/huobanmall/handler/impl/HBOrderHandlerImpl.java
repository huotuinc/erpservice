/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huobanplus.erpuser.huobanmall.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huobanplus.erpservice.common.httputil.HttpClientUtil;
import com.huobanplus.erpservice.common.httputil.HttpResult;
import com.huobanplus.erpservice.common.util.SignBuilder;
import com.huobanplus.erpservice.datacenter.model.*;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import com.huobanplus.erpuser.huobanmall.common.ApiResult;
import com.huobanplus.erpuser.huobanmall.common.HBConstant;
import com.huobanplus.erpuser.huobanmall.handler.HBOrderHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by liual on 2015-10-19.
 */
@Service
public class HBOrderHandlerImpl implements HBOrderHandler {
    private static final Log log = LogFactory.getLog(HBOrderHandlerImpl.class);

    private final Gson gson = new Gson();

    @Override
    @SuppressWarnings("Duplicates")
    public EventResult deliverInfo(OrderDeliveryInfo deliveryInfo, ERPUserInfo erpUserInfo) {
        Map<String, Object> signMap = HBConstant.buildSignMap(deliveryInfo);
//        signMap.put("orderId", deliveryInfo.getOrderId());
//        signMap.put("logiName", deliveryInfo.getLogiName());
//        signMap.put("logiNo", deliveryInfo.getLogiNo());
//        signMap.put("remark", deliveryInfo.getRemark());
//        signMap.put("freight", String.valueOf(deliveryInfo.getFreight()));
//        signMap.put("deliverItemsStr", deliveryInfo.getDeliverItemsStr());
        signMap.put("timestamp", String.valueOf(new Date().getTime()));
        try {
            String sign = SignBuilder.buildSignIgnoreEmpty(signMap, null, HBConstant.SECRET_KEY);
            Map<String, Object> requestMap = new HashMap<>(signMap);

            requestMap.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/ErpOrderApi/Deliver", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                ApiResult apiResult = JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS);
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
        } catch (IOException e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    public EventResult returnInfo(ReturnInfo returnInfo, ERPUserInfo erpUserInfo) {
        Map<String, Object> signMap = HBConstant.buildSignMap(returnInfo);
        signMap.put("timestamp", String.valueOf(new Date().getTime()));
        try {
            String sign = SignBuilder.buildSignIgnoreEmpty(signMap, null, HBConstant.SECRET_KEY);
            Map<String, Object> requestMap = new HashMap<>(signMap);
            requestMap.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/ErpOrderApi/ReturnProduct", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                ApiResult apiResult = JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS);
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
        } catch (IOException e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult obtainOrderList(OrderSearchInfo orderSearchInfo, ERPUserInfo erpUserInfo) {
        //获取伙伴商城接口数据
        //签名
        Map<String, Object> signMap = HBConstant.buildSignMap(orderSearchInfo);
//        signMap.put("orderStatus", orderSearchInfo.getOrderStatus());
//        signMap.put("pageIndex", orderSearchInfo.getPageIndex());
//        signMap.put("pageSize", orderSearchInfo.getPageSize());
//        signMap.put("shipStatus", orderSearchInfo.getShipStatus());
//        signMap.put("payStatus", orderSearchInfo.getPayStatus());
//        signMap.put("customerId", erpUserInfo.getCustomerId());
//        signMap.put("beginTime", orderSearchInfo.getBeginTime());
//        signMap.put("endTime", orderSearchInfo.getEndTime());
        signMap.put("customerId", erpUserInfo.getCustomerId());
        signMap.put("erpUserType", erpUserInfo.getErpUserType().getCode());
        signMap.put("timestamp", new Date().getTime());
        try {
            String sign = SignBuilder.buildSignIgnoreEmpty(signMap, null, HBConstant.SECRET_KEY);
            Map<String, Object> requestMap = new HashMap<>(signMap);
            requestMap.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/ErpOrderApi/OrderList", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
//                ApiResult<OrderListInfo> apiResult = JSON.parseObject(httpResult.getHttpContent(), new TypeReference<ApiResult<OrderListInfo>>() {
//                });
                ApiResult<OrderListInfo> apiResult = gson.fromJson(httpResult.getHttpContent(), new TypeToken<ApiResult<OrderListInfo>>() {
                }.getType());

                log.info("json cast successfully");

                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS, apiResult.getData());
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }

            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
        } catch (IOException e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult obtainOrderDetail(String orderId, ERPUserInfo erpUserInfo) {
        if (StringUtils.isEmpty(orderId)) {
            return EventResult.resultWith(EventResultEnum.BAD_REQUEST_PARAM, "orderId未传", null);
        }
        Map<String, Object> signMap = new TreeMap<>();
        signMap.put("orderId", orderId);
        signMap.put("timestamp", new Date().getTime());
        try {
            String sign = SignBuilder.buildSignIgnoreEmpty(signMap, null, HBConstant.SECRET_KEY);
            Map<String, Object> requestMap = new HashMap<>(signMap);
            requestMap.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/ErpOrderApi/OrderDetail", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
//                ApiResult<Order> apiResult = JSON.parseObject(httpResult.getHttpContent(), new TypeReference<ApiResult<Order>>() {
//                });
                ApiResult<Order> apiResult = gson.fromJson(httpResult.getHttpContent(), new TypeToken<ApiResult<Order>>() {
                }.getType());

                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS, apiResult.getData());
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }
            return EventResult.resultWith(EventResultEnum.SYSTEM_BAD_REQUEST, httpResult.getHttpContent(), null);
        } catch (IOException e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult pushOrderDetailList(String orderListJson) {
        if (StringUtils.isEmpty(orderListJson)) {
            return EventResult.resultWith(EventResultEnum.BAD_REQUEST_PARAM, "没有可以推送的订单数据", null);
        }
        Map<String, Object> signMap = new TreeMap<>();
        signMap.put("orderListJson", orderListJson);
        signMap.put("timestamp", new Date().getTime());
        try {
            String sign = SignBuilder.buildSignIgnoreEmpty(signMap, null, HBConstant.SECRET_KEY);
            Map<String, Object> requestMap = new HashMap<>(signMap);
            requestMap.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/ErpOrderApi/OrderUpdate", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                ApiResult apiResult = JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS, apiResult.getData());
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
        } catch (IOException e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult batchDeliver(List<OrderDeliveryInfo> orderDeliveryInfoList, ERPUserInfo erpUserInfo) {
        Map<String, Object> requestMap = new TreeMap<>();
        requestMap.put("lstDeliveryInfoJson", gson.toJson(orderDeliveryInfoList));
        requestMap.put("customerId", erpUserInfo.getCustomerId());
        try {
            String sign = SignBuilder.buildSignIgnoreEmpty(requestMap, null, HBConstant.SECRET_KEY);
            requestMap.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/ErpOrderApi/BatchDeliver", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
//                ApiResult<BatchDeliverResult> apiResult = JSON.parseObject(httpResult.getHttpContent(), new TypeReference<ApiResult<BatchDeliverResult>>() {
//                });

                ApiResult<BatchDeliverResult> apiResult = gson.fromJson(httpResult.getHttpContent(), new TypeToken<ApiResult<BatchDeliverResult>>() {
                }.getType());

                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS, apiResult.getData());
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
        } catch (Exception e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult syncChannelOrderList(List<Order> orderList, ERPUserInfo erpUserInfo) {
        Map<String, Object> requestMap = new TreeMap<>();

        try {
            String orderListJson = JSON.toJSONString(orderList);
            requestMap.put("orderListJson", URLEncoder.encode(orderListJson, "utf-8"));
            requestMap.put("customerId", erpUserInfo.getCustomerId());
            requestMap.put("timestamp", new Date().getTime());

            String sign = SignBuilder.buildSignIgnoreEmpty(requestMap, null, HBConstant.SECRET_KEY);
            requestMap.put("sign", sign);
            requestMap.put("orderListJson", URLEncoder.encode(URLEncoder.encode(orderListJson, "utf-8"), "utf-8"));

            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/order/PushErpOrder", requestMap);

            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                ApiResult<BatchPushOrderResult> apiResult = JSON.parseObject(httpResult.getHttpContent(), new TypeReference<ApiResult<BatchPushOrderResult>>() {
                });
                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS, apiResult.getData());
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);

        } catch (Exception e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult pushAuditedOrderList(List<String> orderIds, ERPUserInfo erpUserInfo) {
        Map<String, Object> requestMap = new TreeMap<>();
        try {
            requestMap.put("orderListJson", JSON.toJSONString(orderIds));
            requestMap.put("customerId", erpUserInfo.getCustomerId());
            requestMap.put("timestamp", new Date().getTime());

            String sign = SignBuilder.buildSignIgnoreEmpty(requestMap, null, HBConstant.SECRET_KEY);
            requestMap.put("sign", sign);

            HttpResult httpResult = HttpClientUtil.getInstance().post(HBConstant.REQUEST_URL + "/ErpOrderApi/OrderChecked", requestMap);

            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                ApiResult<BatchAuditedOrderResult> apiResult = JSON.parseObject(httpResult.getHttpContent(), new TypeReference<ApiResult<BatchAuditedOrderResult>>() {
                });
                if (apiResult.getCode() == 200) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS, apiResult.getData());
                }
                return EventResult.resultWith(EventResultEnum.ERROR, apiResult.getMsg(), null);
            }
            return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);

        } catch (Exception e) {
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult orderRemarkUpdate(OrderRemarkUpdateInfo orderRemarkUpdateInfo, ERPUserInfo erpUserInfo) {
        Map<String,Object> signMap = HBConstant.buildSignMap(orderRemarkUpdateInfo);
        signMap.put("customerId",erpUserInfo.getCustomerId());
        signMap.put("erpUserType", erpUserInfo.getErpUserType().getCode());
        signMap.put("timestamp",new Date().getTime());
        try {
            String sign = SignBuilder.buildSignIgnoreEmpty(signMap, null, HBConstant.SECRET_KEY);
            Map<String,Object> requestMap = new HashMap<>(signMap);
            requestMap.put("sign",sign);

        }catch (Exception e){
            //TODO 修改订单备注
        }
        return null;
    }
}
