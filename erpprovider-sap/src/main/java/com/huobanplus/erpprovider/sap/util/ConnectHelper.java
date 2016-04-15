package com.huobanplus.erpprovider.sap.util;

import com.huobanplus.erpprovider.sap.common.SAPSysData;
import com.huobanplus.erpservice.eventhandler.model.ERPUserInfo;
import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by elvis on 2016/4/15.
 */
public class ConnectHelper {

    private static final String ABAP_AS_POOLED = "ABAP_AS_WITH_POOL";


    public static JCoFunction getRfcFunction(SAPSysData sysData, ERPUserInfo erpUserInfo,String rfcName,Map<String, Object> datamap) throws Exception {
        JCoDestination destination = connect(sysData, erpUserInfo);
        //如果有数据则进行填充，没有直接返回JCoFunction
        if(datamap!=null && datamap.size()>0){
            JCoFunction function=fillData(destination.getRepository().getFunction(rfcName),datamap);
            return function;
        }
        return destination.getRepository().getFunction(rfcName);
    }
    /**
     * 填充数据
     */
    public static JCoFunction fillData(JCoFunction jcoFunc,Map<String, Object> datamap){
        JCoTable jCoTable = null;
        jCoTable = jcoFunc.getTableParameterList().getTable("ZTABLE");

        if(jCoTable==null){
            return jcoFunc;
        }
//      填充传入值
        for(String key:datamap.keySet()){
            Object value=datamap.get(key);
            jCoTable.setValue(key, value);
        }
        return jcoFunc;
    }

    public static JCoDestination connect(SAPSysData sysData, ERPUserInfo erpUserInfo) {
        JCoDestination destination = null;
        String fileBaseName = ABAP_AS_POOLED+erpUserInfo.getCustomerId();
        createDataFile(fileBaseName, "jcoDestination", sysData);
        try {
            destination = JCoDestinationManager.getDestination(fileBaseName);
        } catch (JCoException e) {
//            log.error("Connect SAP fault, error msg: " + e.toString());
        }
        return destination;
    }

    /**
     * @param name 创建连接属性文件
     * @param suffix
     * @param sysData
     */
    private static void createDataFile(String name, String suffix, SAPSysData sysData) {
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, sysData.getHost());//服务器
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sysData.getSysNo());        //系统编号
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, sysData.getClient());       //SAP集团
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, sysData.getJcoUser());  //SAP用户名
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sysData.getJcoPass());     //密码
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "zh");        //登录语言
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");  //最大连接数
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");     //最大连接线程
        connectProperties.setProperty(DestinationDataProvider.JCO_SAPROUTER, sysData.getSapRouter()); //路由
        File cfg = new File(name + "." + suffix);
        if (cfg.exists()) {
            cfg.deleteOnExit();
        }
        try {
            FileOutputStream fos = new FileOutputStream(cfg, false);
            connectProperties.store(fos, "for sap !");
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create the destination file " + cfg.getName(), e);
        }
    }

}
