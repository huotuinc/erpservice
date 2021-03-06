/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.dtw.config;

import com.alibaba.fastjson.JSON;
import com.huobanplus.erpprovider.dtw.common.DtwConstant;
import com.huobanplus.erpprovider.dtw.common.DtwEnum;
import com.huobanplus.erpprovider.dtw.common.DtwEventResult;
import com.huobanplus.erpprovider.dtw.common.DtwSysData;
import com.huobanplus.erpprovider.dtw.handler.DtwOrderHandler;
import com.huobanplus.erpprovider.dtw.handler.DtwPuchaseHandler;
import com.huobanplus.erpprovider.dtw.util.AESUtil;
import com.huobanplus.erpprovider.dtw.util.RSAUtil;
import com.huobanplus.erpservice.common.ienum.OrderSyncStatus;
import com.huobanplus.erpservice.datacenter.common.ERPTypeEnum;
import com.huobanplus.erpservice.datacenter.entity.ERPDetailConfigEntity;
import com.huobanplus.erpservice.datacenter.entity.ERPSysDataInfo;
import com.huobanplus.erpservice.datacenter.entity.logs.OrderDetailSyncLog;
import com.huobanplus.erpservice.datacenter.service.ERPDetailConfigService;
import com.huobanplus.erpservice.datacenter.service.ERPSysDataInfoService;
import com.huobanplus.erpservice.datacenter.service.logs.OrderDetailSyncLogService;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.erpevent.ERPBaseEvent;
import com.huobanplus.erpservice.eventhandler.erpevent.push.PushNewOrderEvent;
import com.huobanplus.erpservice.eventhandler.erpevent.push.PushPurchaseOrderEvent;
import com.huobanplus.erpservice.eventhandler.handler.ERPHandler;
import com.huobanplus.erpservice.eventhandler.handler.ERPHandlerBuilder;
import com.huobanplus.erpservice.eventhandler.model.ERPInfo;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/23.
 */
@Component
public class DtwHandlerBuilder implements ERPHandlerBuilder {

    private static final Log log = LogFactory.getLog(DtwHandlerBuilder.class);

    @Autowired
    private DtwOrderHandler dtwOrderHandler;
    @Autowired
    private DtwPuchaseHandler dtwPuchaseHandler;
    @Autowired
    private ERPSysDataInfoService sysDataInfoService;
    @Autowired
    private ERPDetailConfigService detailConfigService;
    @Autowired
    private OrderDetailSyncLogService orderDetailSyncLogService;

    @Override
    public ERPHandler buildHandler(ERPInfo info) {
        if (info.getErpType() == ERPTypeEnum.ProviderType.DTW) {
            return new ERPHandler() {
//            @Override
//            public boolean eventSupported(Class<? extends ERPBaseEvent> baseEventClass) {
//                if (baseEventClass == PushNewOrderEvent.class) {
//                    return true;
//                }
//                if (baseEventClass == DeliveryInfoEvent.class) {
//                    return true;
//                }
//                return false;
//            }

                @Override
                public EventResult handleEvent(ERPBaseEvent erpBaseEvent) {
                    if (erpBaseEvent instanceof PushNewOrderEvent) {
                        PushNewOrderEvent pushNewOrderEvent = (PushNewOrderEvent) erpBaseEvent;
                        return dtwOrderHandler.pushOrder(pushNewOrderEvent);
                    }
                    if (erpBaseEvent instanceof PushPurchaseOrderEvent) {
                        PushPurchaseOrderEvent pushPurchaseOrderEvent = (PushPurchaseOrderEvent) erpBaseEvent;
                        return dtwPuchaseHandler.pushPurchaseOrder(pushPurchaseOrderEvent);
                    }
                    return null;
                }

                @Override
                public EventResult handleRequest(HttpServletRequest request, ERPTypeEnum.ProviderType providerType, ERPTypeEnum.UserType erpUserType) {
                    if (request.getAttribute("backType").equals(1)) {//海关回执
                        try {
                            String content = request.getParameter("content");
                            String msgType = request.getParameter("msg_type");
                            String dataDigest = request.getParameter("data_digest");

                            //解密
                            byte[] inputContent = Base64.getDecoder().decode(content.getBytes("utf-8"));
                            byte[] aesKey = Base64.getDecoder().decode(DtwConstant.CUSTOM_DEFAULT_AES_KEY.getBytes("utf-8"));
                            String orignalContent = new String(AESUtil.decrypt(inputContent, aesKey), "utf-8");

                            // 验签
                            byte[] inputData = orignalContent.getBytes("utf-8");
                            byte[] publicKey = Base64.getDecoder().decode(DtwConstant.CUSTOM_DEFAULT_PUBLIC_KEY);
                            byte[] sign = Base64.getDecoder().decode(dataDigest);
                            Boolean signSuccess = RSAUtil.verify(inputData, publicKey, sign);
                            if (signSuccess) {

                                Document document = DocumentHelper.parseText(orignalContent);
                                Element root = document.getRootElement();
                                Element chkMark = root.element("body")
                                        .element("list")
                                        .element("jkfResult")
                                        .element("chkMark");

                                Element orderNo = root.element("body")
                                        .element("list")
                                        .element("jkfResult")
                                        .element("businessNo");

                                OrderDetailSyncLog orderDetailSyncLog = null;
                                orderDetailSyncLog = orderDetailSyncLogService.findByOrderId(orderNo.getText());

                                if (chkMark.getText().equals("2")) {// 处理失败，订单有问题
                                    orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_FAILURE);
                                    //需要重推其他所有的单子
                                    orderDetailSyncLog.setCustomOrderSyncStatus(false);
                                    orderDetailSyncLog.setOrderSyncStatus(false);
                                    orderDetailSyncLog.setPayOrderSyncStatus(false);
                                    orderDetailSyncLog.setPersonalSyncStatus(false);

                                    // 设置错误信息
                                    String xpath = "/mo/body/list/jkfResult/resultList/jkfResultDetail/resultInfo";
                                    List<Element> resultInfo = document.selectNodes(xpath);
                                    StringBuffer errorMsg = new StringBuffer();
                                    for (Element element : resultInfo) {
                                        errorMsg.append(element.getText()).append(";");
                                    }
                                    String newErrorMsg = orderDetailSyncLog.getErrorMsg() + errorMsg + "|";
                                    orderDetailSyncLog.setErrorMsg(newErrorMsg);
                                } else {
                                    orderDetailSyncLog.setCustomBackStatus(true);
                                    if (orderDetailSyncLog.isOrderSyncStatus() && orderDetailSyncLog.isPayOrderSyncStatus()
                                            && orderDetailSyncLog.isPersonalSyncStatus() && orderDetailSyncLog.isCustomOrderSyncStatus()) {
                                        orderDetailSyncLog.setDetailSyncStatus(OrderSyncStatus.DetailSyncStatus.SYNC_SUCCESS);
                                    }
                                }

                                orderDetailSyncLogService.save(orderDetailSyncLog);
                                log.info("Dtw Custom Back:" + orignalContent);
                                return EventResult.resultWith(EventResultEnum.SUCCESS,
                                        "<response><success>true</success></response>");
                            } else {
                                return EventResult.resultWith(EventResultEnum.ERROR,
                                        "<response><success>false</success>" +
                                                "<errorCode>S02</errorCode>" +
                                                "<errorMsg>非法的数字签名</errorMsg>" +
                                                "</response>");
                            }
                        } catch (Exception e) {
                            return EventResult.resultWith(EventResultEnum.ERROR, "<response><success>false</success>" +
                                    "<errorCode>S07</errorCode>" +
                                    "<errorMsg>" + e.getMessage() + "</errorMsg>" +
                                    "</response>");
                        }

                    } else {// 大田运单回执

                        try {
                            DtwEventResult dtwEventResult = new DtwEventResult();
                            String requestKey = request.getParameter("PassKey");

                            List<ERPSysDataInfo> sysDataInfos = sysDataInfoService.findByErpTypeAndErpUserTypeAndParamNameAndParamVal(providerType, erpUserType, "passKey", requestKey);
                            ERPDetailConfigEntity erpDetailConfig = detailConfigService.findBySysData(sysDataInfos, providerType, erpUserType);
                            DtwSysData dtwSysData = JSON.parseObject(erpDetailConfig.getErpSysData(), DtwSysData.class);
                            String passKey = dtwSysData.getPassKey();

                            if (!passKey.equals(requestKey)) {
                                dtwEventResult.setErrorCode(DtwEnum.ErrorCode.CHECK_ERROR.getCode());
                                dtwEventResult.setErrMsg("PassKey 不一致");
                                return EventResult.resultWith(EventResultEnum.ERROR, JSON.toJSONString(dtwEventResult));
                            }
                            ERPUserInfo erpUserInfo = new ERPUserInfo(erpUserType, erpDetailConfig.getCustomerId());

                            String msgId = request.getParameter("Msgid");
                            String wayBill = request.getParameter("wayBill");
                            String weight = request.getParameter("Weight");
                            String state = request.getParameter("State");

                            log.info("Dtw back:");
                            log.info("MsgId:" + msgId);
                            log.info("wayBill" + wayBill);
                            log.info("weight:" + weight);
                            log.info("state:" + state);

                            EventResult eventResult = dtwOrderHandler.deliverOrder(msgId, wayBill, weight, state, erpUserInfo);
                            if (eventResult == null) {
                                throw new Exception(EventResultEnum.UNSUPPORT_EVENT.getResultMsg());
                            }

                            if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {

                                dtwEventResult.setErrorCode(DtwEnum.ErrorCode.ERROR_REPUSH.getCode());
                                dtwEventResult.setErrMsg("");
                                return EventResult.resultWith(EventResultEnum.SUCCESS, JSON.toJSONString(dtwEventResult));
                            } else {
                                dtwEventResult.setErrorCode("997");
                                dtwEventResult.setErrMsg(eventResult.getResultMsg());
                                return EventResult.resultWith(EventResultEnum.ERROR, JSON.toJSONString(dtwEventResult));
                            }

                        } catch (Exception ex) {
                            DtwEventResult dtwEventResult = new DtwEventResult();
                            dtwEventResult.setErrorCode(DtwEnum.ErrorCode.ERROR_REPUSH.getCode());
                            dtwEventResult.setErrMsg(ex.getMessage());
                            return EventResult.resultWith(EventResultEnum.ERROR, JSON.toJSONString(dtwEventResult));
                        }
                    }
                }
            };
        }
        return null;
    }
}
