/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.sap.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

/**
 * Created by allan on 12/24/15.
 */
@Service
public class ScheduledService {
    private static final Log log = LogFactory.getLog(ScheduledService.class);

   /* @Autowired
    private ERPDetailConfigService detailConfigService;
    @Autowired
    private OrderScheduledLogService scheduledLogService;
    @Autowired
    private ERPRegister erpRegister;
    @Autowired
    private EDBOrderHandler edbOrderHandler;

    *//**
     * 同步订单发货状态轮训服务
     *//*
    @Scheduled(cron = "0 0 *//*2 * * ?")
    public void syncOrderShip() {
        Date now = new Date();
        log.info("E店宝获取订单开始:" + StringUtil.DateFormat(now, StringUtil.TIME_PATTERN));
        //得到所有配置过edb信息的商家,准备获取数据
        List<ERPDetailConfigEntity> detailConfigs = detailConfigService.findByErpTypeAndDefault(ERPTypeEnum.ProviderType.EDB);
        for (ERPDetailConfigEntity detailConfig : detailConfigs) {
            log.info(detailConfig.getErpUserType().getName() + detailConfig.getCustomerId() + "开始获取订单数据进行同步");
            int currentPageIndex = 1;
            EDBSysData sysData = JSON.parseObject(detailConfig.getErpSysData(), EDBSysData.class);

            EDBOrderSearch edbOrderSearch = new EDBOrderSearch();
            edbOrderSearch.setBeginTime(sysData.getBeginTime());
            edbOrderSearch.setEndTime(now);
            edbOrderSearch.setShipStatus(EDBEnum.ShipStatusEnum.ALL_DELIVER);
            edbOrderSearch.setPlatformStatus(EDBEnum.PlatformStatus.PAYED);
            edbOrderSearch.setProceStatus(EDBEnum.OrderStatusEnum.ACTIVE);

            //第一次获取订单
            EventResult eventResult = edbOrderHandler.obtainOrderList(currentPageIndex, sysData, edbOrderSearch);

            if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                JSONObject jsonObject = (JSONObject) eventResult.getData();
                JSONArray resultOrders = jsonObject.getJSONObject("items").getJSONArray("item");
                log.info("本次获取" + resultOrders.size() + "条订单数据");
                if (resultOrders.size() > 0) {
                    int totalResult = resultOrders.getJSONObject(0).getIntValue("总数量");//本次获取的总数据量
                    int successCount = 0;//成功走完流程的数量

                    //推送给相应的erp使用商户
                    List<Order> orders = getLogiInfo(resultOrders);
                    ERPUserInfo erpUserInfo = new ERPUserInfo(detailConfig.getErpUserType(), detailConfig.getCustomerId());
                    //得到相应使用者处理器
                    ERPUserHandler erpUserHandler = erpRegister.getERPUserHandler(erpUserInfo);

                    PushOrderListInfoEvent pushOrderListInfoEvent = new PushOrderListInfoEvent(JSON.toJSONString(orders));
                    //处理事件,此处为推送订单列表信息到使用者
                    EventResult firstPushResult = erpUserHandler.handleEvent(pushOrderListInfoEvent);

                    if (firstPushResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                        List<Order> successList = JSON.parseArray(String.valueOf(firstPushResult.getData()), Order.class);

                        int totalPage = totalResult / EDBConstant.PAGE_SIZE;
                        if (totalResult % EDBConstant.PAGE_SIZE != 0) {
                            totalPage++;
                        }
                        if (totalPage > 1) {
                            currentPageIndex++;
                            //取下一笔数据
                            for (int i = currentPageIndex; i <= totalPage; i++) {
                                EventResult nextResult = edbOrderHandler.obtainOrderList(currentPageIndex, sysData, edbOrderSearch);
                                if (nextResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                                    JSONObject nextJsonObject = (JSONObject) nextResult.getData();
                                    JSONArray nextJsonArray = nextJsonObject.getJSONObject("items").getJSONArray("item");
                                    //推送给erp使用商户
                                    List<Order> nextOrders = getLogiInfo(nextJsonArray);
                                    pushOrderListInfoEvent = new PushOrderListInfoEvent(JSON.toJSONString(nextOrders));
                                    EventResult nextPushResult = erpUserHandler.handleEvent(pushOrderListInfoEvent);
                                    if (nextPushResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                                        successList.addAll(JSON.parseArray(String.valueOf(nextPushResult.getData()), Order.class));
                                    }
                                }
                            }
                        }
                        //回写EDB,修改EDB的外部平台订单状态
                        for (Order successOrder : successList) {
                            successOrder.setPayStatus(EDBEnum.PayStatusEnum.ALL_DELIVER.getCode());
                            EventResult rewriteResult = edbOrderHandler.pushOrder(successOrder, sysData);
                            if (rewriteResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
                                successCount++;
                            }
                        }
                    }
                    //存入轮训记录表
                    OrderScheduledLog orderScheduledLog = new OrderScheduledLog();
                    orderScheduledLog.setCustomerId(detailConfig.getCustomerId());
                    orderScheduledLog.setNum(totalResult);
                    orderScheduledLog.setSuccessNum(successCount);
                    orderScheduledLog.setCreateTime(now);
                    scheduledLogService.save(orderScheduledLog);
                }
            }
        }

    }

    *//**
     * 用于发货
     *
     * @param resultOrders
     * @return
     *//*
    private List<Order> getLogiInfo(JSONArray resultOrders) {
        //推送给erp使用商户
        List<Order> orders = new ArrayList<>();
        for (Object object : resultOrders) {
            JSONObject jo = (JSONObject) object;
            Order order = new Order();
            order.setOrderId(jo.getString("out_tid"));
            order.setLogiName(jo.getString("express"));
            order.setLogiNo(jo.getString("express_no"));
            order.setLogiCode(jo.getString("express_coding"));

            List<OrderItem> orderItems = new ArrayList<>();
            JSONArray jsonOrderItem = jo.getJSONArray("tid_item");
            for (Object ob : jsonOrderItem) {
                JSONObject joItem = (JSONObject) ob;
                OrderItem orderItem = new OrderItem();
                orderItem.setProductBn(joItem.getString("barcode"));
                String sendNum = joItem.getString("send_num");
                if (!StringUtils.isEmpty(sendNum)) {
                    orderItem.setSendNum(Integer.parseInt(sendNum));
                }
                orderItem.setSendNum(1);
                String refundNum = joItem.getString("refund_num");
                if (!StringUtils.isEmpty(refundNum)) {
                    orderItem.setRefundNum(Integer.parseInt(refundNum));
                }
                orderItems.add(orderItem);
            }
            order.setOrderItems(orderItems);
            orders.add(order);
        }
        return orders;
    }*/
}