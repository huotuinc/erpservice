package com.huobanplus.erpservice.event.handler;

import com.huobanplus.erpservice.event.erpevent.ERPBaseEvent;
import com.huobanplus.erpservice.event.model.EventResult;
import com.huobanplus.erpservice.event.model.FailedBean;
import com.huobanplus.erpservice.event.model.Monitor;
import org.dom4j.DocumentException;
import org.springframework.dao.DataAccessException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 事件处理器
 * <p>由epr-provider具体实现</p>
 * Created by allan on 2015/7/13.
 */
public interface ERPHandler {
    /**
     * 是否支持该事件
     *
     * @param baseEventClass 某事件
     * @return true表示支持
     */
    boolean eventSupported(Class<? extends ERPBaseEvent> baseEventClass);

    /**
     * 处理事件
     * <p>ERP-Provider会具体执行一个事件的处理，比如访问第三方API或者保存到数据。</p>
     * <p>所以结果可能不是立刻可以获取的，但应该可以检测它的运行情况。</p>
     *
     * @param baseEventClass 处理该事件
     * @return 事件处理结果
     * @throws IOException            网络操作异常
     * @throws IllegalAccessException 接口访问不合法异常
     * @throws DataAccessException    操作数据库时异常
     */
    Monitor handleEvent(Class<? extends ERPBaseEvent> baseEventClass, Object data) throws IOException, IllegalAccessException, DataAccessException, IllegalArgumentException;

    /**
     * 处理异常信息
     *
     * @param baseEventClass 事件类
     * @param failedBean     异常信息实体
     * @return
     */
    Monitor handleException(Class<? extends ERPBaseEvent> baseEventClass, FailedBean failedBean);
}
