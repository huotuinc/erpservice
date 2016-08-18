/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.dtw.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.huobanplus.erpprovider.dtw.common.DtwConstant;
import com.huobanplus.erpprovider.dtw.common.DtwEnum;
import com.huobanplus.erpprovider.dtw.common.DtwSysData;
import com.huobanplus.erpprovider.dtw.formatdtw.*;
import com.huobanplus.erpprovider.dtw.handler.DtwOrderHandler;
import com.huobanplus.erpprovider.dtw.search.DtwStockSearch;
import com.huobanplus.erpprovider.dtw.util.AESUtil;
import com.huobanplus.erpprovider.dtw.util.DtwUtil;
import com.huobanplus.erpprovider.dtw.util.RSAUtil;
import com.huobanplus.erpservice.common.httputil.HttpClientUtil;
import com.huobanplus.erpservice.common.httputil.HttpResult;
import com.huobanplus.erpservice.common.ienum.OrderSyncStatus;
import com.huobanplus.erpservice.datacenter.entity.logs.OrderDetailSyncLog;
import com.huobanplus.erpservice.datacenter.model.Order;
import com.huobanplus.erpservice.datacenter.model.OrderItem;
import com.huobanplus.erpservice.datacenter.service.logs.OrderDetailSyncLogService;
import com.huobanplus.erpservice.eventhandler.ERPRegister;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.erpevent.DeliveryInfoEvent;
import com.huobanplus.erpservice.eventhandler.erpevent.push.PushNewOrderEvent;
import com.huobanplus.erpservice.eventhandler.model.DeliveryInfo;
import com.huobanplus.erpservice.eventhandler.model.ERPInfo;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import com.huobanplus.erpservice.eventhandler.userhandler.ERPUserHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by wuxiongliu on 2016/5/23.
 */
@Component
public class DtwOrderHandlerImpl implements DtwOrderHandler {

    private static final Log log = LogFactory.getLog(DtwOrderHandlerImpl.class);

    @Autowired
    private OrderDetailSyncLogService orderDetailSyncLogService;

    @Autowired
    private ERPRegister erpRegister;

    @Override
    public EventResult pushOrder(PushNewOrderEvent pushNewOrderEvent) {
        try {
            Order order = JSON.parseObject(pushNewOrderEvent.getOrderInfoJson(), Order.class);
            ERPInfo erpInfo = pushNewOrderEvent.getErpInfo();
            DtwSysData dtwSysData = JSON.parseObject(erpInfo.getSysDataJson(), DtwSysData.class);
            ERPUserInfo erpUserInfo = pushNewOrderEvent.getErpUserInfo();
            Date now = new Date();

            DtwThreeOrderStatus dtwThreeOrderStatus = new DtwThreeOrderStatus();
            EventResult eventResult = null;
            OrderDetailSyncLog orderDetailSyncLog = orderDetailSyncLogService.findByOrderId(order.getOrderId());
            if (orderDetailSyncLog == null) {
                orderDetailSyncLog = new OrderDetailSyncLog();
                orderDetailSyncLog.setCreateTime(now);
                orderDetailSyncLog.setCustomerId(erpUserInfo.getCustomerId());
                orderDetailSyncLog.setProviderType(erpInfo.getErpType());
                orderDetailSyncLog.setUserType(erpUserInfo.getErpUserType());
                orderDetailSyncLog.setOrderId(order.getOrderId());
                orderDetailSyncLog.setOrderInfoJson(pushNewOrderEvent.getOrderInfoJson());
                orderDetailSyncLog.setErpSysData(erpInfo.getSysDataJson());
                orderDetailSyncLog.setSyncTime(now);

                eventResult = pushThreeOrder(order, dtwSysData, dtwThreeOrderStatus);
                if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                    orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_SUCCESS);
                } else {
                    orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_FAILURE);
                }

                dtwThreeOrderStatus = (DtwThreeOrderStatus) eventResult.getData();
                orderDetailSyncLog.setOrderSyncStatus(dtwThreeOrderStatus.isOrderSyncStatus());
                orderDetailSyncLog.setPersonalSyncStatus(dtwThreeOrderStatus.isPersonalSyncStatus());
                orderDetailSyncLog.setPayOrderSyncStatus(dtwThreeOrderStatus.isPayOrderSyncStatus());

                orderDetailSyncLogService.save(orderDetailSyncLog);

            } else {

                dtwThreeOrderStatus.setPayOrderSyncStatus(orderDetailSyncLog.isPayOrderSyncStatus());
                dtwThreeOrderStatus.setPersonalSyncStatus(orderDetailSyncLog.isPersonalSyncStatus());
                dtwThreeOrderStatus.setOrderSyncStatus(orderDetailSyncLog.isOrderSyncStatus());
                eventResult = pushThreeOrder(order, dtwSysData, dtwThreeOrderStatus);
                if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                    orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_SUCCESS);
                } else {
                    orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_FAILURE);
                }
                dtwThreeOrderStatus = (DtwThreeOrderStatus) eventResult.getData();
                orderDetailSyncLog.setOrderSyncStatus(dtwThreeOrderStatus.isOrderSyncStatus());
                orderDetailSyncLog.setPersonalSyncStatus(dtwThreeOrderStatus.isPersonalSyncStatus());
                orderDetailSyncLog.setPayOrderSyncStatus(dtwThreeOrderStatus.isPayOrderSyncStatus());
                orderDetailSyncLogService.save(orderDetailSyncLog);
            }

            return eventResult;

        } catch (Exception e) {
            log.error(e.getMessage());
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }


    /**
     * 推送综合订单，个人信息申报单，支付单
     *
     * @param order
     * @param dtwSysData
     * @return
     */
    private EventResult pushThreeOrder(Order order, DtwSysData dtwSysData, DtwThreeOrderStatus dtwThreeOrderStatus) {

        StringBuilder errorMsg = new StringBuilder();
        if (!dtwThreeOrderStatus.isPayOrderSyncStatus()) {
            EventResult payOrderEvent = null;
            if (order.getPaymentName().equals("支付宝")) {// FIXME: 2016-07-27  可能空指针
                payOrderEvent = pushAliPayOrder(order, dtwSysData);
            } else if (order.getPaymentName().equals("微信")) {
                payOrderEvent = pushWeixinPayOrder(order, dtwSysData);
            }

            if (payOrderEvent.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                dtwThreeOrderStatus.setPayOrderSyncStatus(true);
            } else {
                dtwThreeOrderStatus.setPayOrderSyncStatus(false);
                errorMsg.append(payOrderEvent.getResultMsg()).append(",");
            }
        }

        if (!dtwThreeOrderStatus.isPersonalSyncStatus()) {
            EventResult personalOrderEvent = pushPersonalDeclareOrder(order, dtwSysData);
            if (personalOrderEvent.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                dtwThreeOrderStatus.setPersonalSyncStatus(true);
            } else {
                dtwThreeOrderStatus.setPersonalSyncStatus(false);
                errorMsg.append(personalOrderEvent.getResultMsg()).append(",");
            }
        }

        if (!dtwThreeOrderStatus.isOrderSyncStatus()) {
            EventResult orderEvent = orderPush(order, dtwSysData);
            if (orderEvent.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                dtwThreeOrderStatus.setOrderSyncStatus(true);
            } else {
                dtwThreeOrderStatus.setOrderSyncStatus(false);
                errorMsg.append(orderEvent.getResultMsg());
            }
        }

        if (dtwThreeOrderStatus.isOrderSyncStatus() && dtwThreeOrderStatus.isPersonalSyncStatus()
                && dtwThreeOrderStatus.isPayOrderSyncStatus()) {
            return EventResult.resultWith(EventResultEnum.SUCCESS, dtwThreeOrderStatus);
        }

        return EventResult.resultWith(EventResultEnum.ERROR, errorMsg.toString(), dtwThreeOrderStatus);
    }

    private EventResult orderPush(Order order, DtwSysData dtwSysData) {

        Map<String, Object> requestMap = new HashMap<>();
        DtwOrder dtwOrder = new DtwOrder();
        dtwOrder.setPassKey(dtwSysData.getPassKey());//(必填)
//            dtwOrder.setPreEntryNumber("");
        dtwOrder.setECommerceCode(dtwSysData.getECommerceCode());//(必填)
        dtwOrder.setECommerceName(dtwSysData.getECommerceName());//(必填)
        dtwOrder.setImportType(DtwEnum.ImportTypeEnum.BAOSHUI.getCode());//进口类型（0一般进口，1保税进口）(必填)
        dtwOrder.setOrderType(2);//订单类型（1：普通订单：与快递已经完成对接，2：综合订单：委托大田与快递公司对接）

        dtwOrder.setMsgid(order.getOrderId());//(必填)
        dtwOrder.setPayType(DtwEnum.PaytypeEnum.Other.getCode());
        dtwOrder.setPayCompanyCode("001");//(必填) 支付公司在海关的备案码 // FIXME: 2016-07-13
        dtwOrder.setPayNumber(order.getPayNumber());//(必填) 支付单号
        dtwOrder.setOrderTotalAmount(order.getFinalAmount());//(必填)
        dtwOrder.setOrderGoodsAmount(order.getFinalAmount() - order.getCostFreight());//(必填)
        dtwOrder.setOrderNo(order.getOrderId());//(必填)
        dtwOrder.setOrderTaxAmount(0);// FIXME: 2016/6/17//(必填)
        dtwOrder.setTotalCount(order.getItemNum());//(必填)
        dtwOrder.setTotalAmount(order.getFinalAmount());//(必填)
        dtwOrder.setLogisCompanyName("百世物流科技");//(必填)
        dtwOrder.setLogisCompanyCode("WL15041401");//(必填) FIXME: 2016-07-13
        dtwOrder.setPurchaserId(String.valueOf(order.getMemberId()));//(必填)
        dtwOrder.setShipper("发货人姓名");// TODO: 2016/6/16//(必填)
        dtwOrder.setShipperPro("");
        dtwOrder.setShipperCity("");
        dtwOrder.setShipperDistrict("");
        dtwOrder.setShipperAddress("浙江杭州滨江区");//TODO 发货人地址（必填）
        dtwOrder.setShipperMobile("");
        dtwOrder.setShipperTel("");
        dtwOrder.setShipperCountry(DtwEnum.CountryEnum.CHINA.getCode());
        dtwOrder.setShipperZip("");
        dtwOrder.setConsignee(order.getShipName());//(必填)
        dtwOrder.setConsigneePro(order.getProvince());//(必填)
        dtwOrder.setConsigneeCity(order.getCity());//(必填)
        dtwOrder.setConsigneeDistrict(order.getDistrict());//(必填)
        dtwOrder.setConsigneeAdd(order.getShipAddr());//(必填)
        dtwOrder.setConsigneeMobile(order.getShipMobile());//收货人手机(手机与电话二选一)
        dtwOrder.setConsigneeTel(order.getShipTel());//收货人手机(手机与电话二选一)
        dtwOrder.setConsigneeCountry(DtwEnum.CountryEnum.CHINA.getCode());
        dtwOrder.setConsigneeZip(order.getShipZip());//(必填)
        dtwOrder.setWeight(order.getWeight());// FIXME: 2016/6/16//(必填) 毛重
        dtwOrder.setLotNo("");
        dtwOrder.setNetWeight(order.getWeight());// FIXME: 2016/6/16//(必填) 净重
        dtwOrder.setIeFlag(73);//(必填)// FIXME: 2016/6/16 73:进口 79:出口

        List<DtwOrderItem> dtwOrderItemList = new ArrayList<>();
        List<OrderItem> orderItemList = order.getOrderItems();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            DtwOrderItem dtwOrderItem = new DtwOrderItem();
            dtwOrderItem.setMsgitem(i);//(必填)
            dtwOrderItem.setPartno(orderItem.getProductBn());//(必填)
            dtwOrderItem.setPartName(orderItem.getName());//(必填)
            dtwOrderItem.setSpec(orderItem.getStandard());//(必填)
            dtwOrderItem.setUnit(DtwEnum.UnitEnum.JIAN.getCode());// FIXME: 2016-07-08 计量单位，海关三字代码(必填)  件
            dtwOrderItem.setCurrency(DtwEnum.CurrencyEnum.RMB.getCode());
            dtwOrderItem.setAmount(orderItem.getAmount());//(必填)
            dtwOrderItemList.add(dtwOrderItem);
        }

        dtwOrder.setDtwOrderItems(dtwOrderItemList);//(必填)
        requestMap.put("data", JSON.toJSONString(dtwOrder));

        HttpResult httpResult = HttpClientUtil.getInstance().post(dtwSysData.getRequestUrl() + "/QBIntegratedOrder", requestMap);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            JSONObject result = JSON.parseObject(httpResult.getHttpContent());
            if (result.getString("ErrCode").equals("000")) {
                return EventResult.resultWith(EventResultEnum.SUCCESS);
            } else {
                return EventResult.resultWith(EventResultEnum.ERROR, result.getString("ErrMsg"), null);
            }
        } else {
            log.error("服务器请求失败:" + httpResult.getHttpContent());
            return EventResult.resultWith(EventResultEnum.ERROR, "服务器请求失败", null);
        }
    }

    private EventResult pushPersonalDeclareOrder(Order order, DtwSysData dtwSysData) {
        List<OrderItem> orderItems = order.getOrderItems();

        DtwPersonalDelcareInfo dtwPersonalDelcareInfo = new DtwPersonalDelcareInfo();
        dtwPersonalDelcareInfo.setPassKey(dtwSysData.getPassKey());// 必填
        dtwPersonalDelcareInfo.setMsgid(order.getOrderId());// 必填
        dtwPersonalDelcareInfo.setCompanyName(dtwSysData.getCompanyName());// 必填
        dtwPersonalDelcareInfo.setCompanyCode(dtwSysData.getCompanyCode());// 必填
//        dtwPersonalDelcareInfo.setPreEntryNumber("");
        dtwPersonalDelcareInfo.setImportType(DtwEnum.ImportTypeEnum.BAOSHUI.getCode());// FIXME: 2016/7/4 进口类型（0一般进口，1保税进口）
        dtwPersonalDelcareInfo.setOrderNo(order.getOrderId());// 必填
        dtwPersonalDelcareInfo.setWayBill("");
        dtwPersonalDelcareInfo.setPackNo(order.getItemNum());// 必填
        dtwPersonalDelcareInfo.setGrossWeight(order.getWeight());// 必填
        dtwPersonalDelcareInfo.setNetWeight(order.getWeight());// 必填
        dtwPersonalDelcareInfo.setRemark(order.getMemo());
        dtwPersonalDelcareInfo.setSenderName("");// 必填
        dtwPersonalDelcareInfo.setConsignee(order.getShipName());// 必填
        dtwPersonalDelcareInfo.setSenderCity("发件人城市");//非必填
        dtwPersonalDelcareInfo.setPaperType("");// 非必填
        dtwPersonalDelcareInfo.setPaperNumber("");// 非必填
        dtwPersonalDelcareInfo.setPurchaserTelNumber(order.getUserLoginName());// 必填
        dtwPersonalDelcareInfo.setBuyerIdType("1");// 必填
        dtwPersonalDelcareInfo.setBuyerIdNumber(order.getBuyerPid());// 必填
        dtwPersonalDelcareInfo.setBuyerName(order.getBuyerName());// 必填
        dtwPersonalDelcareInfo.setWorth(order.getFinalAmount());// 必填
        dtwPersonalDelcareInfo.setFeeAmount(order.getCostFreight());// 必填
        dtwPersonalDelcareInfo.setInsureAmount(0.0);// 必填
        dtwPersonalDelcareInfo.setCurrCode(DtwEnum.CurrencyEnum.RMB.getCode());
        if (orderItems.size() > 0) {
            dtwPersonalDelcareInfo.setMainGName(orderItems.get(0).getName());// 必填
        }
        dtwPersonalDelcareInfo.setTradeCountry(DtwEnum.CountryEnum.CHINA.getCode());// 必填
        dtwPersonalDelcareInfo.setSenderCountry(DtwEnum.CountryEnum.CHINA.getCode());// 必填
        dtwPersonalDelcareInfo.setECommerceCode(dtwSysData.getECommerceCode());// 必填
        dtwPersonalDelcareInfo.setECommerceName(dtwSysData.getECommerceName());// 必填

        List<DtwGoodsDelcareItem> dtwItems = new ArrayList<>();

        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);

            DtwGoodsDelcareItem dtwGoodsDelcareItem = new DtwGoodsDelcareItem();
            dtwGoodsDelcareItem.setGoodsOrder(i);// 必填
            dtwGoodsDelcareItem.setCodeTs(item.getGoodBn());// 必填//商品HScode:填写商品海关编码
            dtwGoodsDelcareItem.setGoodsItemNo(item.getProductBn());// 必填
            dtwGoodsDelcareItem.setGoodsName(item.getName());// 必填
            dtwGoodsDelcareItem.setGoodsModel(item.getStandard());// 必填
            dtwGoodsDelcareItem.setOriginCountry("");// 必填
            dtwGoodsDelcareItem.setTradeCurr(DtwEnum.CurrencyEnum.RMB.getCode());
            dtwGoodsDelcareItem.setTradeTotal(item.getAmount() * item.getNum());// 必填
            dtwGoodsDelcareItem.setDeclPrice(item.getAmount());// 必填
            dtwGoodsDelcareItem.setDeclTotalPrice(item.getAmount() * item.getNum());// 必填
//            dtwGoodsDelcareItem.setUseTo("");
            dtwGoodsDelcareItem.setDeclareCount(item.getNum());// 必填
            dtwGoodsDelcareItem.setGoodsUnit(DtwEnum.UnitEnum.JIAN.getCode());// 必填
//            dtwGoodsDelcareItem.setGoodsGrossWeight("");
            dtwGoodsDelcareItem.setFirstUnit(DtwEnum.UnitEnum.JIAN.getCode());// 必填
            dtwGoodsDelcareItem.setFirstCount(item.getNum());// 必填
//            dtwGoodsDelcareItem.setSecondUnit("");
//            dtwGoodsDelcareItem.setSecondCount(0.0);

            dtwItems.add(dtwGoodsDelcareItem);
        }

        dtwPersonalDelcareInfo.setItems(dtwItems);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("data", JSON.toJSONString(dtwPersonalDelcareInfo));
        HttpResult httpResult = HttpClientUtil.getInstance().post(dtwSysData.getRequestUrl() + "/QBPresonal", requestMap);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            JSONObject result = JSON.parseObject(httpResult.getHttpContent());
            if (result.getString("ErrCode").equals("000")) {
                return EventResult.resultWith(EventResultEnum.SUCCESS);
            } else {
                return EventResult.resultWith(EventResultEnum.ERROR, result.getString("ErrMsg"), null);
            }
        } else {
            log.error("服务器请求失败:" + httpResult.getHttpContent());
            return EventResult.resultWith(EventResultEnum.ERROR, "服务器请求失败", null);
        }

    }

    @Override
    public EventResult deliverOrder(String Msgid, String wayBill, String weight, String state, ERPUserInfo erpUserInfo) {
        try {
            ERPUserHandler erpUserHandler = erpRegister.getERPUserHandler(erpUserInfo);
            if (erpUserHandler == null) {
                return EventResult.resultWith(EventResultEnum.NO_DATA, "未找到数据源信息", null);
            }
            DeliveryInfoEvent deliveryInfoEvent = new DeliveryInfoEvent();
            deliveryInfoEvent.setErpUserInfo(erpUserInfo);
            DeliveryInfo deliveryInfo = new DeliveryInfo();
            deliveryInfo.setOrderId(Msgid);
//            deliveryInfo.setLogiName(logiName);
            deliveryInfo.setLogiNo(wayBill);
            deliveryInfoEvent.setDeliveryInfo(deliveryInfo);
            EventResult eventResult = erpUserHandler.handleEvent(deliveryInfoEvent);
            return eventResult;
        } catch (Exception e) {
            log.error(e.getMessage());
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult stockQuery(DtwStockSearch dtwStockSearch, DtwSysData dtwSysData) {
        try {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("data", JSON.toJSONString(dtwStockSearch));
            HttpResult httpResult = HttpClientUtil.getInstance().post(dtwSysData.getRequestUrl() + "/QBStockQey", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                JSONObject result = JSON.parseObject(httpResult.getHttpContent());
                if (result.getString("ErrCode").equals("000")) {
                    JSONArray jsonArray = result.getJSONArray("Items");
                    List<DtwStockItem> dtwStockItems = new ArrayList<>();
                    jsonArray.forEach(item -> {
                        DtwStockItem dtwStockItem = JSON.parseObject(item.toString(), DtwStockItem.class);
                        dtwStockItems.add(dtwStockItem);
                    });
                    return EventResult.resultWith(EventResultEnum.SUCCESS, dtwStockItems);
                } else {
                    return EventResult.resultWith(EventResultEnum.ERROR, result.getString("ErrMsg"), null);
                }
            } else {
                log.error("服务器请求失败:" + httpResult.getHttpContent());
                return EventResult.resultWith(EventResultEnum.ERROR, "服务器请求失败", null);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult wayBill(DtwWayBill dtwWayBill, DtwSysData dtwSysData) {
        try {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("data", JSON.toJSONString(dtwWayBill));
            HttpResult httpResult = HttpClientUtil.getInstance().post(dtwSysData.getRequestUrl() + "/QBWaybill", requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                JSONObject result = JSON.parseObject(httpResult.getHttpContent());
                if (result.getString("ErrCode").equals("000")) {
                    return EventResult.resultWith(EventResultEnum.SUCCESS);
                } else {
                    return EventResult.resultWith(EventResultEnum.ERROR, result.getString("ErrMsg"), null);
                }
            } else {
                log.error("服务器请求失败:" + httpResult.getHttpContent());
                return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult pushAliPayOrder(Order order, DtwSysData dtwSysData) {
        try {
            Map requestMap = new TreeMap<>();
            requestMap.put("service", "alipay.acquire.customs");
            requestMap.put("_input_charset", "utf-8");
            requestMap.put("sign_type", "MD5");

            requestMap.put("partner", dtwSysData.getAliPartner());
            requestMap.put("out_request_no", order.getOrderId());
            requestMap.put("trade_no", order.getPayNumber());
            requestMap.put("merchant_customs_code", dtwSysData.getECommerceCode());// FIXME: 2016-07-28 
            requestMap.put("amount", order.getFinalAmount());
            requestMap.put("customs_place", DtwEnum.CustomerEnum.HANGZHOU);
            requestMap.put("merchant_customs_name", dtwSysData.getECommerceName());// FIXME: 2016-07-27
//            requestMap.put("is_split", "n");
//            requestMap.put("sub_out_biz_no", "2015080811223212345453");
            String sign = DtwUtil.aliBuildSign(requestMap);
            requestMap.put("sign", sign);

            HttpResult httpResult = HttpClientUtil.getInstance().get(DtwConstant.ALI_PAY_URL, requestMap);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                String xmlResp = httpResult.getHttpContent();
                Document document = DocumentHelper.parseText(xmlResp);
                Element root = document.getRootElement();

                Element isSuccessElem = root.element("is_success");
                if (isSuccessElem.getText().equals("T")) {
                    Element resultCodeElem = root.element("result_code");
                    if (resultCodeElem.equals("SUCCESS")) {
                        log.info("大田推送支付宝支付单到海关");
                        return EventResult.resultWith(EventResultEnum.SUCCESS);
                    } else {
                        Element errorElem = root.element("detail_error_des");
                        log.error("推送支付宝支付单到海关失败:" + errorElem.getText());
                        return EventResult.resultWith(EventResultEnum.ERROR, errorElem.getText(), null);
                    }
                } else {
                    Element errorElem = root.element("error");
                    log.error("推送支付宝支付单到海关失败:" + errorElem.getText());
                    return EventResult.resultWith(EventResultEnum.ERROR, errorElem.getText(), null);
                }
            } else {
                log.error("服务器请求失败:" + httpResult.getHttpContent());
                return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
            }


        } catch (Exception e) {
            log.error(e.getMessage());
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult pushWeixinPayOrder(Order order, DtwSysData dtwSysData) {
        try {
            WeixinCustomer weixinCustomer = new WeixinCustomer();
            weixinCustomer.setAppid(dtwSysData.getWeiXinAppId());
            weixinCustomer.setMchId(dtwSysData.getWeixinMchId());
            weixinCustomer.setOutTradeNo(order.getOrderId());
            weixinCustomer.setTransactionId(order.getPayNumber());
            weixinCustomer.setCustoms(DtwEnum.CustomerEnum.HANGZHOU.name());
            weixinCustomer.setMchCustomsNo(dtwSysData.getECommerceCode());// FIXME: 2016-07-28
            weixinCustomer.setSubOrderNo("");
            weixinCustomer.setFeeType("CNY");
            weixinCustomer.setOrderFee((int) (order.getFinalAmount() * 100));
            weixinCustomer.setTransportFee((int) (order.getCostFreight() * 100));
//            weixinCustomer.setProductFee(order.get);// FIXME: 2016-07-27
            weixinCustomer.setDuty(0);


            Map<String, Object> requestMap = new TreeMap<>();
            requestMap.put("appid", weixinCustomer.getAppid());
            requestMap.put("mch_id", weixinCustomer.getMchId());
            requestMap.put("out_trade_no", weixinCustomer.getOutTradeNo());
            requestMap.put("transaction_id", weixinCustomer.getTransactionId());
            requestMap.put("customs", weixinCustomer.getCustoms());
            requestMap.put("mch_customs_no", weixinCustomer.getMchCustomsNo());
            requestMap.put("sub_order_no", weixinCustomer.getSubOrderNo());
            requestMap.put("fee_type", weixinCustomer.getFeeType());
            requestMap.put("order_fee", weixinCustomer.getOrderFee());
            requestMap.put("transport_fee", weixinCustomer.getTransportFee());
            requestMap.put("product_fee", weixinCustomer.getProductFee());
            requestMap.put("duty", weixinCustomer.getDuty());
            requestMap.put("cert_type", weixinCustomer.getCertType());
            requestMap.put("cert_id", weixinCustomer.getCertId());
            requestMap.put("name", weixinCustomer.getName());

            String sign = DtwUtil.weixinBuildSign(requestMap, dtwSysData.getWeixinKey());
            requestMap.put("sign", sign);
            weixinCustomer.setSign(sign);

            String requestData = new XmlMapper().writeValueAsString(weixinCustomer);

            HttpResult httpResult = HttpClientUtil.getInstance().post(DtwConstant.WEIXIN_PAY_URL, requestData);
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                String xmlResp = httpResult.getHttpContent();
                Document document = DocumentHelper.parseText(xmlResp);
                Element root = document.getRootElement();
                Element returnCodeElem = root.element("return_code");
                if (returnCodeElem.getText().equals("SUCCESS")) {
                    Element resultCode = root.element("result_code");
                    if (resultCode.getText().equals("SUCCESS")) {
                        return EventResult.resultWith(EventResultEnum.SUCCESS);
                    } else {
                        Element errorMsgElem = root.element("err_code_des");
                        log.error("推送微信支付单到海关失败:" + errorMsgElem.getText());
                        return EventResult.resultWith(EventResultEnum.ERROR, errorMsgElem.getText(), null);
                    }
                } else {
                    Element returnMsg = root.element("return_msg");
                    log.error("推送微信支付单到海关失败:" + returnMsg.getText());
                    return EventResult.resultWith(EventResultEnum.ERROR, returnMsg.getText(), null);
                }
            } else {
                log.error("服务器请求失败:" + httpResult.getHttpContent());
                return EventResult.resultWith(EventResultEnum.ERROR, httpResult.getHttpContent(), null);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return EventResult.resultWith(EventResultEnum.ERROR, e.getMessage(), null);
        }
    }

    @Override
    public EventResult pushCustomOrder(Order order, DtwSysData dtwSysData) {
        try {

            CustomOrder customOrder = convertToCustomOrder(order, dtwSysData);
            String requestXml = new XmlMapper().writeValueAsString(customOrder);
            requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + requestXml;
            // rsa 加密
            String privateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAlFs+R0NKXS6SZs5EwRDHRdi41lN/KrWBpU3ZPStg7+OzCch5guHDurzgcvPtcj0adkobpt1BUgA5lRWNNwM8MwIDAQABAkAhaPb6h33sxDs2Kce0DvpBkY/2vHEMBjo/JuwAaY94FmA87NhW2wctbosAbQUNlFPRBeFz1ez4EDZdAZGud2RBAiEA06xzkallIIzfbXzr/n32tmX53phWhc1GyHxru2Vd+eECIQCzbHAiHiaWKYQlzzqklZSB6LOwQIsxkjLVoE4ewyTAkwIgKimJg5QYPpmQz4A4iaKRh9dcJAh4A4LV/I078EHKrEECIC3H02zBKLNT8IX9NEdea0Aicgbc0Sda2GGtv4EV5cDFAiB8T0QOg11mVTnMcxVDLQLE7YAlyoGBqGRsbHCkMwlpOQ==";
            String aesKey = "eHQzzQ/pmyddSkhFFwX4Ig==";
            byte[] data = requestXml.getBytes("utf-8");
            byte[] privateKeyCode = Base64.getDecoder().decode(privateKey.getBytes("utf-8"));
            byte[] aesKeyCode = Base64.getDecoder().decode(aesKey.getBytes("utf-8"));

            String encData = new String(Base64.getEncoder().encode(AESUtil.encrypt(data, aesKeyCode)), "utf-8");
            String sign = new String(Base64.getEncoder().encode(RSAUtil.sign(data, privateKeyCode)), "utf-8");
            String requestData = buildSoapXml(encData, "IMPORTORDER", sign, dtwSysData.getCompanyCode());

            String result = DtwUtil.requestCustomWebService(requestData, "IMPORTORDER", sign, dtwSysData.getCompanyCode());

            System.out.println("\n**************Data pane begin*******************");
            System.out.println("请求数据：" + requestXml);
            System.out.println("加密后的数据：" + encData);
            System.out.println("AES秘钥：" + aesKey);
            System.out.println("RSA秘钥：" + privateKey);
            System.out.println("请求结果：" + result);
            System.out.println("\n**************Data pane end*******************");


//            if(httpResult.getHttpStatus() == HttpStatus.SC_OK){
//                String xmlResp = httpResult.getHttpContent();
//                Document document = DocumentHelper.parseText(xmlResp);
//                Element root = document.getRootElement();
//                // 如何判断请求处理结果是成功了还是失败了？
//
//
//            } else{
//
//
//                return EventResult.resultWith(EventResultEnum.ERROR,httpResult.getHttpContent(),null);
//            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private String buildSoapXml(String content, String msgType, String dataDigest, String sendCode) {
        StringBuffer sb = new StringBuffer();
        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.newyork.zjport.gov.cn/\">")
                .append("<soapenv:Header/>")
                .append("<soapenv:Body>")
                .append("<ws:receive>")
                .append("<content>").append(content).append("</content>")
                .append("<msgType>").append(msgType).append("</msgType>")
                .append("<dataDigest>").append(dataDigest).append("</dataDigest>")
                .append("<sendCode>").append(sendCode).append("</sendCode>")
                .append("</ws:receive>")
                .append("</soapenv:Body>")
                .append("</soapenv:Envelope>");
        return sb.toString();
    }

    private CustomOrder convertToCustomOrder(Order order, DtwSysData dtwSysData) {
        CustomOrder customOrder = new CustomOrder();
        CustomHead customHead = new CustomHead();
        customHead.setBusinessType("IMPORTORDER");

        CustomBody customBody = new CustomBody();
        CustomOrderInfoList customOrderInfoList = new CustomOrderInfoList();
        CustomOrderInfo customOrderInfo = new CustomOrderInfo();

        CustomSign customSign = new CustomSign();
        customSign.setCompanyCode(dtwSysData.getCompanyCode());
        customSign.setBusinessNo(order.getOrderId());
        customSign.setBusinessType("IMPORTORDER");
        customSign.setDeclareType("1");// 固定填写1
        customSign.setNote("");

        CustomOrderHead customOrderHead = new CustomOrderHead();
        customOrderHead.setECommerceCode(dtwSysData.getECommerceCode());
        customOrderHead.setECommerceName(dtwSysData.getECommerceName());
        customOrderHead.setIeFlag("I");
        customOrderHead.setPayType("03");
        customOrderHead.setPayCompanyCode("");// FIXME: 2016-08-18
        customOrderHead.setPayNumber(order.getPayNumber());
        customOrderHead.setOrderTotalAmount(order.getFinalAmount());
        customOrderHead.setOrderNo(order.getOrderId());
        customOrderHead.setOrderTaxAmount(0.0);// FIXME: 2016-08-18
        customOrderHead.setOrderGoodsAmount(order.getFinalAmount());
        customOrderHead.setFeeAmount(order.getCostFreight());
        customOrderHead.setInsureAmount(0.0);
        customOrderHead.setCompanyCode(dtwSysData.getCompanyCode());
        customOrderHead.setCompanyName(dtwSysData.getCompanyName());
        customOrderHead.setTradeTime(order.getPayTime());
        customOrderHead.setCurrCode(DtwEnum.CurrencyEnum.RMB.getCode());
        customOrderHead.setTotalAmount(order.getFinalAmount());
        customOrderHead.setConsigneeEmail(order.getShipEmail());
        customOrderHead.setConsigneeTel(order.getShipMobile());
        customOrderHead.setConsignee(order.getShipName());
        customOrderHead.setConsigneeAddress(order.getShipAddr());
        customOrderHead.setTotalCount(order.getItemNum());
//        customOrderHead.setPostMode();
        customOrderHead.setSenderCountry(DtwEnum.CountryEnum.CHINA.getCode());
        customOrderHead.setSenderName("发件人姓名");// FIXME: 2016-08-18
        customOrderHead.setPurchaserId(String.valueOf(order.getMemberId()));
        customOrderHead.setLogisCompanyCode("WL15041401");
        customOrderHead.setLogisCompanyName("百世物流科技");
        customOrderHead.setZipCode(order.getShipZip());
        customOrderHead.setNote(order.getMemo());
//        customOrderHead.setWayBills("");
        customOrderHead.setRate("1");//人民币，统一填写1
        customOrderHead.setUserProcotol("本人承诺所购买商品系个人合理自用，" +
                "现委托商家代理申报、代缴税款等通关事宜，" +
                "本人保证遵守《海关法》和国家相关法律法规，保证所提供的身份信息和收货信息真实完整，" +
                "无侵犯他人权益的行为，以上委托关系系如实填写，本人愿意接受海关、" +
                "检验检疫机构及其他监管部门的监管，并承担相应法律责任.");

        List<CustomOrderDetail> customOrderDetails = new ArrayList<>();
        List<OrderItem> orderItems = order.getOrderItems();
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem orderItem = orderItems.get(i);
            CustomOrderDetail customOrderDetail = new CustomOrderDetail();
            customOrderDetail.setGoodsOrder(i);
            customOrderDetail.setGoodsName(orderItem.getName());
            customOrderDetail.setGoodsModel(orderItem.getStandard());
            customOrderDetail.setCodeTs(orderItem.getProductBn());
            customOrderDetail.setOriginCountry(DtwEnum.CountryEnum.CHINA.getCode());
            customOrderDetail.setUnitPrice(orderItem.getPrice());
            customOrderDetail.setGoodsCount(orderItem.getNum());
            customOrderDetail.setGoodsUnit(DtwEnum.UnitEnum.JIAN.getCode());
//            customOrderDetail.setGrossWeight(0.0);//商品毛重 非必填
            customOrderDetails.add(customOrderDetail);
        }

        CustomGoodsPurchaser customGoodsPurchaser = new CustomGoodsPurchaser();
        customGoodsPurchaser.setId(String.valueOf(order.getMemberId()));
        customGoodsPurchaser.setName(order.getBuyerName());
        customGoodsPurchaser.setEmail("");//注册邮箱 非必填
        customGoodsPurchaser.setTelNumber(order.getUserLoginName());
        customGoodsPurchaser.setPaperType("01");
        customGoodsPurchaser.setPaperNumber(order.getBuyerPid());
//        customGoodsPurchaser.setAddress("");//地址 非必填


        customOrderInfo.setCustomSign(customSign);
        customOrderInfo.setCustomOrderHead(customOrderHead);
        customOrderInfo.setCustomOrderDetails(customOrderDetails);
        customOrderInfo.setCustomGoodsPurchaser(customGoodsPurchaser);

        customOrderInfoList.setCustomOrderInfo(customOrderInfo);
        customBody.setOrerInfoList(customOrderInfoList);

        customOrder.setBody(customBody);
        customOrder.setHead(customHead);

        return customOrder;
    }
}
