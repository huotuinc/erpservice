/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huobanplus.erpservice.hotapi.controller.impl;

import com.huobanplus.erpservice.commons.annotation.RequestAttribute;
import com.huobanplus.erpservice.commons.bean.ApiResult;
import com.huobanplus.erpservice.commons.bean.ResultCode;
import com.huobanplus.erpservice.datacenter.common.ERPTypeEnum;
import com.huobanplus.erpservice.eventhandler.ERPRegister;
import com.huobanplus.erpservice.eventhandler.common.EventResultEnum;
import com.huobanplus.erpservice.eventhandler.erpevent.InventoryEvent;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import com.huobanplus.erpservice.eventhandler.model.InventoryInfo;
import com.huobanplus.erpservice.eventhandler.userhandler.ERPUserHandler;
import com.huobanplus.erpservice.hotapi.controller.ProductApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * Created by liual on 2015-10-21.
 */
@Controller
@RequestMapping(value = "/hotApi/rest", method = RequestMethod.POST)
public class ProductControllerImpl implements ProductApiController {
    @Autowired
    private ERPRegister erpRegister;

    @Override
    @RequestMapping(value = "/product/index", method = RequestMethod.POST)
    public ApiResult syncInventory(
            String goodBn,
            String proBn,
            int stock,
            @RequestAttribute int customerId,
            @RequestAttribute ERPTypeEnum.UserType erpUserType) throws IOException {
        ERPUserInfo erpUserInfo = new ERPUserInfo();
        erpUserInfo.setErpUserType(erpUserType);
        erpUserInfo.setCustomerId(customerId);
        ERPUserHandler erpUserHandler = erpRegister.getERPUserHandler(erpUserInfo);
        if (erpUserHandler == null) {
            return ApiResult.resultWith(ResultCode.NO_SUCH_ERPHANDLER);
        }
        InventoryEvent inventoryEvent = new InventoryEvent();
        InventoryInfo inventoryInfo = new InventoryInfo();
        inventoryInfo.setGoodBn(goodBn);
        inventoryInfo.setProBn(proBn);
        inventoryInfo.setStock(stock);

        EventResult eventResult = erpUserHandler.handleEvent(inventoryEvent);
        if (eventResult.getResultCode() == EventResultEnum.SUCCESS.getResultCode()) {
            return ApiResult.resultWith(ResultCode.SUCCESS, eventResult.getData());
        } else {
            return ApiResult.resultWith(ResultCode.SYSTEM_BAD_REQUEST, eventResult.getResultMsg(), null);
        }
    }
}
