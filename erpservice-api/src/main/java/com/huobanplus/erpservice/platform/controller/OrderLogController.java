/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2017. All rights reserved.
 */

package com.huobanplus.erpservice.platform.controller;

import com.alibaba.fastjson.JSON;
import com.huobanplus.erpservice.common.SysConstant;
import com.huobanplus.erpservice.common.ienum.OrderSyncStatus;
import com.huobanplus.erpservice.commons.annotation.RequestAttribute;
import com.huobanplus.erpservice.commons.bean.ApiResult;
import com.huobanplus.erpservice.commons.bean.ResultCode;
import com.huobanplus.erpservice.datacenter.entity.logs.*;
import com.huobanplus.erpservice.datacenter.model.BatchPushOrderResult;
import com.huobanplus.erpservice.datacenter.model.Order;
import com.huobanplus.erpservice.datacenter.model.OrderDeliveryInfo;
import com.huobanplus.erpservice.datacenter.searchbean.OrderDetailSyncSearch;
import com.huobanplus.erpservice.datacenter.service.logs.*;
import com.huobanplus.erpservice.eventhandler.ERPRegister;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.erpevent.pull.GetOrderDetailEvent;
import com.huobanplus.erpservice.eventhandler.erpevent.push.PushDeliveryInfoEvent;
import com.huobanplus.erpservice.eventhandler.erpevent.push.PushNewOrderEvent;
import com.huobanplus.erpservice.eventhandler.erpevent.sync.SyncChannelOrderEvent;
import com.huobanplus.erpservice.eventhandler.model.ERPInfo;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import com.huobanplus.erpservice.eventhandler.userhandler.ERPUserHandler;
import com.huobanplus.erpservice.proxy.utils.OrderProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by allan on 4/13/16.
 */
@Controller
@RequestMapping("/erpService/platform")
public class OrderLogController {
    @Autowired
    private OrderDetailSyncLogService orderDetailSyncLogService;
    @Autowired
    private OrderDetailSyncLogDetailService orderDetailSyncLogDetailService;
    @Autowired
    private OrderProxyService orderProxyService;
    @Autowired
    private OrderShipSyncLogService orderShipSyncLogService;
    @Autowired
    private ShipSyncDeliverInfoService shipSyncDeliverInfoService;
    @Autowired
    private ERPRegister erpRegister;
    @Autowired
    private ChannelOrderSyncLogService channelOrderSyncLogService;
    @Autowired
    private ChannelOrderSyncInfoService channelOrderSyncInfoService;


    @RequestMapping(value = "/orderDetailSyncs", method = RequestMethod.GET)
    private String orderDetailSyncs(
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            OrderDetailSyncSearch orderDetailSyncSearch,
            @RequestAttribute int customerId,
            int erpUserType,
            @RequestParam(required = false, defaultValue = "-1") int syncStatus,
            Model model
    ) {
        orderDetailSyncSearch.setSyncStatus(syncStatus);
        Page<OrderDetailSyncLog> orderDetailSyncLogs = orderDetailSyncLogService.findAll(pageIndex, SysConstant.DEFALUT_PAGE_SIZE, customerId, orderDetailSyncSearch);
        model.addAttribute("orderDetailSyncLogs", orderDetailSyncLogs);
        model.addAttribute("orderDetailSyncSearch", orderDetailSyncSearch);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", SysConstant.DEFALUT_PAGE_SIZE);
        model.addAttribute("erpUserType", erpUserType);
        return "logs/order_detail_sync_list";
    }

    @RequestMapping(value = "/orderDetailSyncDetail", method = RequestMethod.GET)
    private String orderDetailSyncDetail(
            @RequestAttribute int customerId,
            @ModelAttribute(value = "erpUserType") int erpUserType,
            long syncLogId,
            Model model
    ) {
        List<OrderDetailSyncLogDetail> orderDetailSyncLogDetails = orderDetailSyncLogDetailService.findBySyncLogId(syncLogId);
        // TODO: 6/30/16
        return null;
    }

    @RequestMapping(value = "/orderShipSyncs", method = RequestMethod.GET)
    private String orderShipSyncs(
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            String beginTime, String endTime,
            @RequestAttribute int customerId,
            int erpUserType,
            Model model
    ) {
        Page<OrderShipSyncLog> orderShipSyncLogs = orderShipSyncLogService.findAll(pageIndex, SysConstant.DEFALUT_PAGE_SIZE, beginTime, endTime, customerId);
        model.addAttribute("orderShipSyncLogs", orderShipSyncLogs);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", SysConstant.DEFALUT_PAGE_SIZE);
        model.addAttribute("erpUserType", erpUserType);
        model.addAttribute("beginTime", beginTime);
        model.addAttribute("endTime", endTime);

        return "logs/order_ship_sync_list";
    }

    @RequestMapping(value = "/shipSyncInfoList", method = RequestMethod.GET)
    private String shipSyncFailureOrders(
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            String orderId, long shipSyncId,
            int erpUserType,
            Model model
    ) {
        Page<ShipSyncDeliverInfo> shipSyncDeliverInfoList = shipSyncDeliverInfoService.findAll(pageIndex, SysConstant.DEFALUT_PAGE_SIZE, shipSyncId, orderId);
        model.addAttribute("shipSyncDeliverInfoList", shipSyncDeliverInfoList);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", SysConstant.DEFALUT_PAGE_SIZE);
        model.addAttribute("erpUserType", erpUserType);
        model.addAttribute("orderId", orderId);
        model.addAttribute("shipSyncId", shipSyncId);

        return "logs/ship_sync_info_list";
    }

    @RequestMapping(value = "/rePushOrder", method = RequestMethod.POST)
    @ResponseBody
    private ApiResult rePushOrder(long id) {
        OrderDetailSyncLog orderDetailSyncLog = orderDetailSyncLogService.findById(id);
        ERPInfo erpInfo = new ERPInfo(orderDetailSyncLog.getProviderType(), orderDetailSyncLog.getErpSysData());
        ERPUserInfo erpUserInfo = new ERPUserInfo(orderDetailSyncLog.getUserType(), orderDetailSyncLog.getCustomerId());
        PushNewOrderEvent pushNewOrderEvent = new PushNewOrderEvent();
        pushNewOrderEvent.setErpInfo(erpInfo);
        pushNewOrderEvent.setErpUserInfo(erpUserInfo);

        ERPUserHandler userHandler = erpRegister.getERPUserHandler(erpUserInfo);
        if (userHandler == null) {
            return ApiResult.resultWith(ResultCode.NO_SUCH_ERPHANDLER);
        }

        GetOrderDetailEvent getOrderDetailEvent = new GetOrderDetailEvent();
        getOrderDetailEvent.setErpUserInfo(erpUserInfo);
        getOrderDetailEvent.setErpInfo(erpInfo);
        getOrderDetailEvent.setOrderId(orderDetailSyncLog.getOrderId());
        EventResult eventResult = userHandler.handleEvent(getOrderDetailEvent);
        if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
            pushNewOrderEvent.setOrderInfoJson(JSON.toJSONString(eventResult.getData()));
            return orderProxyService.handleEvent(pushNewOrderEvent);
        }
        return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST, eventResult.getResultMsg(), null);
    }

    @RequestMapping(value = "/reSyncOrderShip", method = RequestMethod.POST)
    @ResponseBody
    private ApiResult syncOrderShip(long id) {
        ShipSyncDeliverInfo shipSyncDeliverInfo = shipSyncDeliverInfoService.findById(id);
        ERPUserInfo erpUserInfo = new ERPUserInfo(shipSyncDeliverInfo.getOrderShipSyncLog().getUserType(), shipSyncDeliverInfo.getOrderShipSyncLog().getCustomerId());

        ERPUserHandler erpUserHandler = erpRegister.getERPUserHandler(erpUserInfo);

        PushDeliveryInfoEvent pushDeliveryInfoEvent = new PushDeliveryInfoEvent();
        pushDeliveryInfoEvent.setErpUserInfo(erpUserInfo);
        OrderDeliveryInfo orderDeliveryInfo = shipSyncDeliverInfo.getOrderDeliveryInfo();
        pushDeliveryInfoEvent.setDeliveryInfo(orderDeliveryInfo);

        EventResult eventResult = erpUserHandler.handleEvent(pushDeliveryInfoEvent);

        if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
            shipSyncDeliverInfo.setShipSyncStatus(OrderSyncStatus.ShipSyncStatus.SYNC_SUCCESS);
            shipSyncDeliverInfoService.save(shipSyncDeliverInfo);
            return ApiResult.resultWith(ResultCode.SUCCESS);
        } else {
            shipSyncDeliverInfo.getOrderDeliveryInfo().setRemark(eventResult.getResultMsg());
            shipSyncDeliverInfoService.save(shipSyncDeliverInfo);
            return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST, eventResult.getResultMsg(), null);
        }
    }

    @RequestMapping(value = "/reSyncChannelOrder", method = RequestMethod.POST)
    @ResponseBody
    private ApiResult syncChannelOrder(long id) throws UnsupportedEncodingException {
        ChannelOrderSyncInfo channelOrderSyncInfo = channelOrderSyncInfoService.findById(id);
        ERPUserInfo erpUserInfo = new ERPUserInfo(channelOrderSyncInfo.getChannelOrderSyncLog().getUserType(), channelOrderSyncInfo.getChannelOrderSyncLog().getCustomerId());
        ERPUserHandler erpUserHandler = erpRegister.getERPUserHandler(erpUserInfo);

        SyncChannelOrderEvent syncChannelOrderEvent = new SyncChannelOrderEvent();
        List<Order> orders = new ArrayList<>();
        Order order = JSON.parseObject(URLDecoder.decode(channelOrderSyncInfo.getOrderJson(), "utf-8"), Order.class);
        orders.add(order);

        syncChannelOrderEvent.setOrderList(orders);
        syncChannelOrderEvent.setErpUserInfo(erpUserInfo);
        EventResult eventResult = erpUserHandler.handleEvent(syncChannelOrderEvent);
        if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
            BatchPushOrderResult batchPushOrderResult = (BatchPushOrderResult) eventResult.getData();
            List<Order> failedOrder = batchPushOrderResult.getFailedOrders();
            if (failedOrder.size() == 0) {
                channelOrderSyncInfo.setChannelOrderSyncStatus(OrderSyncStatus.ChannelOrderSyncStatus.SYNC_SUCCESS);
                channelOrderSyncInfo.setRemark("");
                channelOrderSyncInfoService.save(channelOrderSyncInfo);
                return ApiResult.resultWith(ResultCode.SUCCESS);
            } else {
                channelOrderSyncInfo.setChannelOrderSyncStatus(OrderSyncStatus.ChannelOrderSyncStatus.SYNC_FAILURE);
                channelOrderSyncInfoService.save(channelOrderSyncInfo);
                return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST, failedOrder.get(0).getErrorMessage(), null);
            }
        } else {
            channelOrderSyncInfo.setChannelOrderSyncStatus(OrderSyncStatus.ChannelOrderSyncStatus.SYNC_FAILURE);
            channelOrderSyncInfoService.save(channelOrderSyncInfo);
            return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/resyncAllChannelOrder", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult syncAllChannelOrder(long logSyncId) throws UnsupportedEncodingException {

        List<ChannelOrderSyncInfo> channelOrderSyncInfos = channelOrderSyncInfoService.findByChannelOrderStatusAndChannelOrderSyncLogId(OrderSyncStatus.ChannelOrderSyncStatus.SYNC_FAILURE, logSyncId);

        if (channelOrderSyncInfos.size() > 0) {
            ChannelOrderSyncInfo channelOrderSyncInfo = channelOrderSyncInfos.get(0);
            ERPUserInfo erpUserInfo = new ERPUserInfo(channelOrderSyncInfo.getChannelOrderSyncLog().getUserType(), channelOrderSyncInfo.getChannelOrderSyncLog().getCustomerId());
            ERPUserHandler erpUserHandler = erpRegister.getERPUserHandler(erpUserInfo);

            List<Order> orderList = new ArrayList<>();
            List<String> orderIds = new ArrayList<>();
            for (ChannelOrderSyncInfo item : channelOrderSyncInfos) {
                Order order = JSON.parseObject(URLDecoder.decode(item.getOrderJson(), "utf-8"), Order.class);
                orderList.add(order);
                orderIds.add(order.getOrderId());
            }

            SyncChannelOrderEvent syncChannelOrderEvent = new SyncChannelOrderEvent();
            syncChannelOrderEvent.setOrderList(orderList);
            syncChannelOrderEvent.setErpUserInfo(erpUserInfo);


            List<String> failedOrderIds = new ArrayList<>();


            EventResult eventResult = erpUserHandler.handleEvent(syncChannelOrderEvent);
            if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                BatchPushOrderResult firstBatchPushOrderResult = (BatchPushOrderResult) eventResult.getData();
                List<Order> failedOrders = firstBatchPushOrderResult.getFailedOrders();

                for (Order item : failedOrders) {
                    failedOrderIds.add(item.getOrderId());
//                    List<ChannelOrderSyncInfo> syncInfos = channelOrderSyncInfoService.findByOrderIdAndLogId(item.getOrderId(), logSyncId);
//                    syncInfos.forEach(info -> {
//                        info.setChannelOrderSyncStatus(OrderSyncStatus.ChannelOrderSyncStatus.SYNC_FAILURE);
//                        channelOrderSyncInfoService.save(info);
//                    });
                }

                orderIds.removeAll(failedOrderIds);

                orderIds.forEach(orderId -> {
                    List<ChannelOrderSyncInfo> syncInfos = channelOrderSyncInfoService.findByOrderIdAndLogId(orderId, logSyncId);
                    syncInfos.forEach(info -> {
                        info.setChannelOrderSyncStatus(OrderSyncStatus.ChannelOrderSyncStatus.SYNC_SUCCESS);
                        channelOrderSyncInfoService.save(info);
                    });
                });

                String msg = "一键同步成功" + orderIds.size() + "条;" + "失败" + failedOrderIds.size() + "条";
                return ApiResult.resultWith(ResultCode.SUCCESS, msg, null);
            }
        }
        return ApiResult.resultWith(ResultCode.ERPUSER_BAD_REQUEST);

    }

    @RequestMapping(value = "/channelOrderSyncs", method = RequestMethod.GET)
    private String channelOrderSyncs(@RequestParam(required = false, defaultValue = "1") int pageIndex,
                                     String beginTime, String endTime,
                                     @RequestAttribute int customerId,
                                     int erpUserType,
                                     Model model) {

        Page<ChannelOrderSyncLog> channelOrderSyncLogs = channelOrderSyncLogService.findAll(pageIndex, SysConstant.DEFALUT_PAGE_SIZE, beginTime, endTime, customerId);
        model.addAttribute("channelOrderSyncLogs", channelOrderSyncLogs);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", SysConstant.DEFALUT_PAGE_SIZE);
        model.addAttribute("erpUserType", erpUserType);
        model.addAttribute("beginTime", beginTime);
        model.addAttribute("endTime", endTime);
        return "logs/channel_order_sync_list";
    }


    @RequestMapping(value = "/channelOrderSyncList", method = RequestMethod.GET)
    private String channelOrderSyncsFailedList(@RequestParam(required = false, defaultValue = "1") int pageIndex,
                                               String orderId, long logSyncId,
                                               int erpUserType,
                                               Model model) {

        Page<ChannelOrderSyncInfo> channelOrderSyncInfoList = channelOrderSyncInfoService.findAll(pageIndex, SysConstant.DEFALUT_PAGE_SIZE, logSyncId, orderId);
        model.addAttribute("channelOrderSyncInfoList", channelOrderSyncInfoList);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("pageSize", SysConstant.DEFALUT_PAGE_SIZE);
        model.addAttribute("erpUserType", erpUserType);
        model.addAttribute("orderId", orderId);
        model.addAttribute("logSyncId", logSyncId);

        return "logs/channel_order_sync_info_list";
    }
}
