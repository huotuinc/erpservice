package com.huobanplus.erpprovider.huobanmall.util;

/**
 *
 * 类描述：一些静态常量
 * @author aaron
 * @since 2015年7月25日 下午4:30:43
 * @version V1.0
 */
public class Constant {

    /**
     * 签名的方式：默认为MD5
     */
    public static final String SIGN_TYPE = "MD5";

    /**
     * 平台秘钥
     */
    public static final String SIGN_SECRET = "secret";

    /**
     * 平台接入码
     */
    public static final String SIGN_U_CODE = "uCode";

    /**
     * 请求方法名
     */
    public static final String SIGN_M_TYPE = "mType";

    /**
     * 请求时时间戳
     */
    public static final String SIGN_TIME_STAMP = "timeStamp";

    /**
     * 请求成功
     */
    public final static String REQUEST_SUCCESS = "30000";
    /**
     * SING签名出错
     */
    public final static String REQUEST_SING_ERROR = "30001";
    /**
     * 数据库处理失败
     */
    public final static String REQUEST_DATABASE_ERROR = "30002";
    /**
     * ERP信息错误
     */
    public final static String REQUEST_ERP_INFO_ERROR = "30003";

    /**
     * 参数无效
     */
    public final static String REQUEST_INAVLID_PARAMETER = "30004";

    /**
     * 时间格式1
     * yyyy-MM-dd HH:mm:dd
     */
    public final static String TIME_FORMAT_ONE = "yyyy-MM-dd HH:mm:dd";


}