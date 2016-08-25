/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huobanplus.erpprovider.dtw.common;

import lombok.Data;

/**
 * Created by wuxiongliu on 2016/5/23.
 */
@Data
public class DtwSysData {

    /**
     * 请求地址
     */
    private String requestUrl;

    /**
     * passkey
     */
    private String passKey;

    /**
     * 电商企业编码(电商企业在跨境平台备案编码)
     */
    private String eCommerceCode;

    /**
     * 电商企业名称
     */
    private String eCommerceName;

    /**
     * 电商平台名称（电商平台在跨境电商通关服务平台的备案名称）
     */
    private String companyName;

    /**
     * 电商平台代码（电商平台在跨境电商通关服务的备案编号）
     */
    private String companyCode;

    /**
     * 微信公众账号ID
     */
    private String weiXinAppId;

    /**
     * 微信 商户号
     */
    private String weixinMchId;

    /**
     * 微信秘钥
     */
    private String weixinKey;

    /**
     * 支付宝商户号
     */
    private String aliPartner;

    /**
     * 发货人姓名
     */
    private String senderName;

    /**
     * 发货人地址
     */
    private String senderAddr;

    /**
     * 商品税率
     */
    private double taxRate;


}