/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpservice.proxy.controller.impl;

import com.huobanplus.erpservice.eventhandler.ERPRegister;
import com.huobanplus.erpservice.proxy.common.HotBaseController;
import com.huobanplus.erpservice.proxy.controller.HotOrderController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>类描述：</p>
 * API对接伙伴商城订单操作实现类
 */
@Controller
@RequestMapping("/hotClientOrderApi")
public class HotOrderControllerImpl extends HotBaseController implements HotOrderController {
    @Autowired
    private ERPRegister erpRegister;

//    @Override
//    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult createOrder(String orderInfoJson, ERPInfo erpInfo, String sign) {
//        try {
//            ERPInfo info = erpInfo;
//
//            ERPHandler erpHandler = erpRegister.getERPHandler(info);
//            if (erpHandler == null) {
//                return ApiResult.resultWith(ResultCode.NO_SUCH_ERPHANDLER);
//            }
//            if (erpHandler.eventSupported(CreateOrderEvent.class)) {
//                CreateOrderEvent createOrderEvent = new CreateOrderEvent();
//                createOrderEvent.setErpInfo(info);
//                MallOrderBean orderInfo = new ObjectMapper().readValue(orderInfoJson, MallOrderBean.class);
////                orderInfo.setRotaryStatus(1);
//                orderInfo.setErpInfo(new ObjectMapper().writeValueAsString(info));
//                EventResult eventResult = erpHandler.handleEvent(createOrderEvent);
//                if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
////                    orderService.save(orderInfo);
//                    return ApiResult.resultWith(ResultCode.SUCCESS, eventResult.getData());
//                } else {
//                    return ApiResult.resultWith(ResultCode.ERP_BAD_REQUEST);
//                }
//            } else {
//                return ApiResult.resultWith(ResultCode.EVENT_NOT_SUPPORT);
//            }
//        } catch (Exception e) {
//            return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST);
//        }
//    }
//
//    @Override
//    @RequestMapping(value = "/orderDeliver", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult orderDeliver(String orderInfoJson, ERPInfo erpInfo, String sign) {
//        try {
//            ERPInfo info = erpInfo;
//            ERPHandler erpHandler = erpRegister.getERPHandler(info);
//            if (erpHandler == null) {
//                return ApiResult.resultWith(ResultCode.NO_SUCH_ERPHANDLER);
//            }
//            if (erpHandler.eventSupported(OrderDeliverEvent.class)) {
//                OrderDeliverEvent orderDeliverEvent = new OrderDeliverEvent();
//                orderDeliverEvent.setErpInfo(info);
//                MallOrderBean orderInfo = new ObjectMapper().readValue(orderInfoJson, MallOrderBean.class);
//
//                EventResult eventResult = erpHandler.handleEvent(orderDeliverEvent);
//                if (eventResult.getResultCode() == ResultCode.SUCCESS.getResultCode()) {
//                    //本地数据更新
////                    MallOrderBean preBean = orderService.findByOrderId(orderInfo.getOrderId());
////                    preBean.setDeliveryTime(orderInfo.getDeliveryTime());
////                    preBean.setExpress(orderInfo.getExpress());
////                    preBean.setLogiCode(orderInfo.getLogiCode());
////                    preBean.setTidNetWeight(orderInfo.getTidNetWeight());
////                    orderService.save(preBean);
//
//                    return ApiResult.resultWith(ResultCode.SUCCESS, eventResult.getData());
//                } else {
//                    return ApiResult.resultWith(ResultCode.ERP_BAD_REQUEST, eventResult.getData());
//                }
//            } else {
//                return ApiResult.resultWith(ResultCode.EVENT_NOT_SUPPORT);
//            }
//        } catch (Exception e) {
//            return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @Override
//    @RequestMapping(value = "/orderUpdate", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult orderUpdate(String orderInfoJson, ERPInfo erpInfo, String sign) {
//        try {
//            ERPInfo info = erpInfo;
//            ERPHandler erpHandler = erpRegister.getERPHandler(info);
//            if (erpHandler == null) {
//                return ApiResult.resultWith(ResultCode.NO_SUCH_ERPHANDLER);
//            }
//            if (erpHandler.eventSupported(OrderUpdateEvent.class)) {
//                OrderUpdateEvent orderUpdateEvent = new OrderUpdateEvent();
//                orderUpdateEvent.setErpInfo(info);
//                MallOrderBean orderInfo = new ObjectMapper().readValue(orderInfoJson, MallOrderBean.class);
//
//                EventResult eventResult = erpHandler.handleEvent(orderUpdateEvent);
//                if (eventResult.getResultCode() == ResultCode.SUCCESS.getResultCode()) {
//
//                    //本地数据更新
////                    MallOrderBean preOrder = orderService.findByOrderId(orderInfo.getOrderId());
////                    preOrder.setExpress(orderInfo.getExpress());
////                    preOrder.setExpressNo(orderInfo.getExpressNo());
////                    preOrder.setExpressCoding(orderInfo.getExpressCoding());
////                    preOrder.setPrinter(orderInfo.getPrinter());
////                    preOrder.setDistributer(orderInfo.getDistributer());
////                    preOrder.setDistributTime(orderInfo.getDistributTime());
////                    preOrder.setPrintTime(orderInfo.getPrintTime());
////                    preOrder.setInspecter(orderInfo.getInspecter());
////                    preOrder.setInspectTime(orderInfo.getInspectTime());
////                    preOrder.setDeliveryOperator(orderInfo.getDeliveryOperator());
////                    preOrder.setDeliveryTime(orderInfo.getDeliveryTime());
////                    preOrder.setGrossWeight(orderInfo.getGrossWeight());
////                    preOrder.setInnerLable(orderInfo.getInnerLable());
////                    for (MallOrderItemBean orderItem : orderInfo.getOrderItems()) {
////                        preOrder.getOrderItems().stream().filter(preOrderItem -> orderItem.getId() == preOrderItem.getId()).forEach(preOrderItem -> {
////                            preOrderItem.setInspectionNum(orderItem.getInspectionNum());
////                        });
////                    }
////                    orderService.save(preOrder);
//
//                    return ApiResult.resultWith(ResultCode.SUCCESS, eventResult.getData());
//                } else {
//                    return ApiResult.resultWith(ResultCode.ERP_BAD_REQUEST);
//                }
//            } else {
//                return ApiResult.resultWith(ResultCode.EVENT_NOT_SUPPORT);
//            }
//        } catch (Exception e) {
//            return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @Override
//    @RequestMapping(value = "/orderStatusUpdate", method = RequestMethod.POST)
//    @ResponseBody
//    public ApiResult orderStatusUpdate(String orderInfoJson, ERPInfo erpInfo, String sign) {
//        try {
//            ERPInfo info = erpInfo;
//            ERPHandler erpHandler = erpRegister.getERPHandler(info);
//            if (erpHandler == null) {
//                return ApiResult.resultWith(ResultCode.NO_SUCH_ERPHANDLER);
//            }
//            if (erpHandler.eventSupported(OrderStatusUpdateEvent.class)) {
//                OrderStatusUpdateEvent orderStatusUpdateEvent = new OrderStatusUpdateEvent();
//                orderStatusUpdateEvent.setErpInfo(erpInfo);
//                MallOrderBean orderInfo = new ObjectMapper().readValue(orderInfoJson, MallOrderBean.class);
//                EventResult eventResult = erpHandler.handleEvent(orderStatusUpdateEvent);
//                if (eventResult.getResultCode() == ResultCode.SUCCESS.getResultCode()) {
//                    return ApiResult.resultWith(ResultCode.SUCCESS, eventResult.getData());
//                } else {
//                    return ApiResult.resultWith(ResultCode.ERP_BAD_REQUEST, eventResult.getData());
//                }
//            } else {
//                return ApiResult.resultWith(ResultCode.EVENT_NOT_SUPPORT);
//            }
//        } catch (Exception e) {
//            return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST, e.getMessage());
//        }
//    }
}
