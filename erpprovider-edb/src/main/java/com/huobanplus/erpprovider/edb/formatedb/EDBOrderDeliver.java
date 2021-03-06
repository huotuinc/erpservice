/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.edb.formatedb;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

/**
 * 订单出货
 * Created by allan on 2015/8/11.
 */
@Data
@JacksonXmlRootElement(localName = "orderInfo")
public class EDBOrderDeliver {
    /**
     * 订单编号
     */
    @JacksonXmlProperty(localName = "OrderCode")
    private String orderId;
    /**
     * 验货时间
     */
    @JacksonXmlProperty(localName = "delivery_time")
    private String deliveryTime;
    /**
     * 快递单号
     */
    @JacksonXmlProperty(localName = "express_no")
    private String expressNo;
    /**
     * 快递公司名：需要在EDB中存在
     */
    @JacksonXmlProperty(localName = "express")
    private String express;
    /**
     * 订单净重
     */
    private String weight;
}
