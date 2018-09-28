package com.miracle.clock.constant;

/**
 * Created by hss on 2017/6/21.
 */

public class HttpUrlConstant {
    //    public static final String HOST_NAME = "http://192.168.5.118:80";
//    public static final String HOST_NAME = "http://gbxiaohei.imwork.net:35258";
//    public static final String HOST_NAME = "http://192.168.5.103:8080";
    public static final String HOST_NAME = "http://www.jsds-glx.com";
    public static final String SERVICE_NAME = "/clock";
    public static final String HOST = HOST_NAME + SERVICE_NAME;

    /**
     * 上传
     */
//    public static final String URL_UPLOAD_IMG = "http://yncjyh.com/upload/upload?savePath=img";
    public static final String URL_UPLOAD_IMG = "http://www.jsds-glx.com/upload/upload?savePath=img";
//    public static final String URL_UPLOAD_IMG = "http://gbxiaohei.imwork.net:35258/upload/upload?savePath=img";

    public static final String USER_LOGIN = HOST + "/user/login";
    public static final String LOGIN_THIRD = HOST + "/user/thirdLogin";
    public static final String USER_REGIST = HOST + "/user/regist";
    public static final String USER_INFO = HOST + "/user/getinfo";
    public static final String USER_CHANGEPWD = HOST + "/user/changePassword";
    public static final String USER_UPDATE = HOST + "/user/update";
    public static final String USER_UPDATEPHONE = HOST + "/user/updatephone";
    public static final String USER_LOGINWITHCODE = HOST + "/user/loginByVerify";

    public static final String TAB_GETLIST = HOST + "/tag/getlist";

    public static final String BANNER_GETLIST = HOST + "/banner/getlist";

    public static final String USER_SENDCODE = HOST + "/verify/send";

    public static final String COLLECTION_GETLIST = HOST + "/collection/getlist";
    public static final String COLLECTION_ADD = HOST + "/collection/add";
    public static final String COLLECTION_DELETE = HOST + "/collection/delete";

    public static final String AUDIO_GRTLIST = HOST + "/audio/getlist";
    public static final String AUDIO_GRT = HOST + "/audio/get";
    public static final String ARTICLE_SEARCH = HOST + "/audio/search";

    public static final String MEMORANDUM_GRTLIST = HOST + "/memorandum/getlist";
    public static final String MEMORANDUM_ADD = HOST + "/memorandum/add";
    public static final String MEMORANDUM_GETINFO = HOST + "/memorandum/getinfo";
    public static final String MEMORANDUM_UPDATE = HOST + "/memorandum/update";
    public static final String MEMORANDUM_DELETE = HOST + "/memorandum/delete";

    public static final String ARTICLE_GETINFO = HOST + "/article/getinfo";

    public static final String SUGGEST_ADD = HOST + "/suggest/add";

}
