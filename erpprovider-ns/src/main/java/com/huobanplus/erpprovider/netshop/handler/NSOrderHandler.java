package com.huobanplus.erpprovider.netshop.handler;

import com.huobanplus.erpservice.event.model.EventResult;
import com.huobanplus.erpservice.event.model.Monitor;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 订单方法处理接口
 */
public interface NSOrderHandler {
    /**
     * 获取订单信息（列表）
     *
     * @param request 请求实体
     * @return 返回订单详细信息结果
     * @throws IOException IO 异常
     */
    Monitor<EventResult> obtainOrderInfoList(HttpServletRequest request) throws IOException;

    /**
     * 获取订单信息（详情）
     *
     * @param request
     * @return
     * @throws IOException
     */
    Monitor<EventResult> obtainOrderInfo(HttpServletRequest request) throws IOException;

    /**
     * 发货通知
     *
     * @param request
     * @return
     * @throws IOException
     */
    Monitor<EventResult> deliverOrder(HttpServletRequest request) throws IOException;
}
