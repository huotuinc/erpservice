package com.huobanplus.erpprovider.netshop.config;

import com.huobanplus.erpservice.eventhandler.ERPRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 类描述：网店管家配置服务
 */
@Configuration
@ComponentScan({"com.huobanplus.erpprovider.netshop", "com.huobanplus.erpservice.eventhandler"})
public class NetShopConfig {
    @Autowired
    private ERPRegister register;

    @Bean
    public NetShopHandlerBuilder netShopHandlerBuilder() {
        return new NetShopHandlerBuilder();
    }

    @Autowired
    private NetShopHandlerBuilder netShopHandlerBuilder;

    @PostConstruct
    public void init() {
        register.addBuilders(netShopHandlerBuilder);
    }
}
