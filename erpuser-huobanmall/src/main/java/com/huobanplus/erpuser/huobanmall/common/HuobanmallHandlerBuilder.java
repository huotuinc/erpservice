package com.huobanplus.erpuser.huobanmall.common;

import com.huobanplus.erpservice.eventhandler.erpevent.DeliveryInfoEvent;
import com.huobanplus.erpservice.eventhandler.erpevent.ERPBaseEvent;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.huobanplus.erpservice.eventhandler.model.EventResult;
import com.huobanplus.erpservice.eventhandler.model.FailedBean;
import com.huobanplus.erpservice.eventhandler.model.Monitor;
import com.huobanplus.erpservice.eventhandler.userhandler.ERPUserHandler;
import com.huobanplus.erpservice.eventhandler.userhandler.ERPUserHandlerBuilder;
import com.huobanplus.erpuser.huobanmall.handler.HBOrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by liual on 2015-10-15.
 */
@Component
public class HuobanmallHandlerBuilder implements ERPUserHandlerBuilder {
    @Autowired
    private HBOrderHandler orderHandler;


    @Override
    public ERPUserHandler buildHandler(ERPUserInfo info) {
        if ("huobanmall".equals(info.getERPUserName())) {
            return new ERPUserHandler() {
                @Override
                public EventResult handleEvent(ERPBaseEvent erpBaseEvent, Object data) throws IOException {
                    if (erpBaseEvent instanceof DeliveryInfoEvent) {
                        return orderHandler.deliverInfo(((DeliveryInfoEvent) erpBaseEvent).getDeliveryInfo());
                    }
                    return null;
                }

                @Override
                public EventResult handleException(Class<? extends ERPBaseEvent> baseEventClass, FailedBean failedBean) {
                    return null;
                }
            };
        }
        return null;
    }
}
