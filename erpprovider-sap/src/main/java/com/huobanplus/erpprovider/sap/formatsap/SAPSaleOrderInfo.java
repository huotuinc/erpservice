/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.sap.formatsap;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by liuzheng on 2016/4/14.
 */
@Setter
@Getter
public class SAPSaleOrderInfo {

    /**
     * 销售组织
     */
    private final String sellOrg = "1000";
    /**
     * 分销渠道
     */
    private final String distributWay = "15";
    /**
     * 库存地点
     */
    private final String goodsAddr = "1017";
    /**
     * 订单类型
     */
    private String orderType;
    /**
     * 售达方
     */
    private String orderSaleFrom;
    /**
     * 订单号  来自微商城
     */
    private String numId;
    /**
     * 订单行项目号
     */
    private String posNr;
    /**
     * 顾客名称 来自微商城
     */
    private String customName;
    /**
     * 顾客联系方式 来自微商城
     */
    private String customTel;
    /**
     * 城市 来自微商城
     */
    private String city;
    /**
     * 邮政编码 来自微商城
     */
    private String shipZip;
    /**
     * 送货地址 来自微商城
     */
    private String shipAddr;
    /**
     * 产品组
     */
    private String goodsInfo;
    /**
     * 物料编码
     */
    private String materialCode;
    /**
     * 订单数量
     */
    private int orderNum;
    /**
     * 单位
     */
    private String organization;
    /**
     * 交货工厂 正常订单固定为：8000 退货订单固定为：1000
     */
    private String provederFactory;
    /**
     * 折扣金额  条件类型固定为：ZWTZ金额*-1
     */
    private String discount;

    /**
     * 是否需要发票  微商城需要按订单记录顾客是否需要发票，并在订单接口传输到SAP
     */
    private boolean invoiceIsopen;

    /**
     * 发票抬头  个人或公司
     */
    private String invoiceTitle;

    /**
     * 发票内容
     */
    private String invoiceMsg;

    /**
     * SAP销售订单号 微商城需要按单记录对应SAP销售订单号
     */
    private String sapSallId;

    /**
     * 物流单号 微商城需要按单记录对应物流单号
     */
    private String logiNo;

    /**
     * 产品组 能录入微商城
     */
    private String goodsOrg;
    /**
     * 优惠金额
     */
    private double pmtAmount;
    /**
     * 产品总金额
     */
    private double costItem;
    /**
     * 联合订单号
     */
    private String unionOrderId;
    /**
     * 邮费
     */
    private double freight;

    private List<SAPOrderItem> sapOrderItems;


}
