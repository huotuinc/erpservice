package com.huobanplus.erpprovider.gy.handler;

import com.huobanplus.erpservice.eventhandler.erpevent.push.PushNewOrderEvent;
import com.huobanplus.erpservice.eventhandler.model.EventResult;

/**
 * Created by admin on 2016/5/9.
 */
public interface GYOrderHandler {

    public EventResult pushOrder(PushNewOrderEvent pushNewOrderEvent);
}
