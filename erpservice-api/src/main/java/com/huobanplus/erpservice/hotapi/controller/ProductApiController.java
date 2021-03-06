/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huobanplus.erpservice.hotapi.controller;

import com.huobanplus.erpservice.commons.annotation.RequestAttribute;
import com.huobanplus.erpservice.commons.bean.ApiResult;
import com.huobanplus.erpservice.datacenter.common.ERPTypeEnum;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by liual on 2015-10-21.
 */
@RequestMapping("/hotApi/product")
public interface ProductApiController {

    /**
     * 同步库存
     *
     * @param goodBn
     * @param proBn
     * @param stock
     * @param customerId
     * @param erpUserType
     * @return
     * @throws IOException
     */
    @RequestMapping("/syncInventory")
    @ResponseBody
    ApiResult syncInventory(
            String goodBn,
            String proBn,
            int stock,
            int customerId,
            @RequestAttribute ERPTypeEnum.UserType erpUserType
    ) throws IOException;
}
