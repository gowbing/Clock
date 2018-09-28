package com.miracle.clock.manager;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.AppConstant;
import com.miracle.clock.constant.PreferenceConstants;
import com.miracle.clock.greendao.gen.DaoMaster;
import com.miracle.clock.greendao.gen.DaoSession;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.utils.normal.PreferenceUtils;
import com.miracle.clock.utils.normal.StringUtils;
import com.mob.MobSDK;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by hss on 2017/6/21.
 */

public class BaseApplication extends Application implements Thread.UncaughtExceptionHandler {

    private static BaseApplication mInstance;
    private static int mMainThreadId = -1;
    private static Thread mMainThread;
    private static Handler mMainThreadHandler;

    private Stack<BaseActivity> mActivityStack = new Stack<>();

    private RequestQueue mRequestQueue = null;
    private Gson mGson = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mMainThreadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainThreadHandler = new Handler();

        mGson = new Gson();

        mRequestQueue = Volley.newRequestQueue(this, null);

        if (AppConstant.EXTERNAL_RELEASE) {
            Logger.init(getPackageName())
                    .hideThreadInfo()//default show
                    .methodOffset(2)//default 2
                    .logLevel(LogLevel.NONE);//Use LogLevel.NONE for the release versions,default LogLevel.FULL
        } else {
            Logger.init(getPackageName())
                    .hideThreadInfo()//default show
                    .methodOffset(2)//default 2
                    .logLevel(LogLevel.FULL);//Use LogLevel.NONE for the release versions,default LogLevel.FULL
        }

        MobSDK.init(this, "20749b4d60020", "51c83c2ac418c7c42722c1998b59bb35");

        /**
         * 初始化fresco
         */
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true).build();
        Fresco.initialize(this, config);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/HYQiH2312F45.ttf").setFontAttrId(R.attr.fontPath).build());

        Thread.setDefaultUncaughtExceptionHandler(this);

        setDatabase();
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public void addActivity(BaseActivity activity) {
        mActivityStack.add(activity);
        Logger.d("在activity栈中添加(" + activity.getClass().getSimpleName() + ")");
    }

    public void finishActivity(BaseActivity activity) {
        if (mActivityStack.contains(activity)) {
            mActivityStack.remove(activity);
            Logger.d("从activity栈中移除(" + activity.getClass().getSimpleName() + ")");
        }
    }

    public Stack<BaseActivity> getActivityStack() {
        return mActivityStack;
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivityByClass(Class<?> cls) {
        if (mActivityStack != null) {
            for (BaseActivity activity : mActivityStack) {
                if (activity.getClass().equals(cls)) {
                    mActivityStack.remove(activity);
                    finishActivity(activity);
                    break;
                }
            }
        }

    }

    public BaseActivity getLastActivity() {
        return mActivityStack.lastElement();
    }

    public void finishAllActivity() {
        StringBuilder stringBuilder = new StringBuilder("移除所有的activity:");
        for (BaseActivity activity : mActivityStack) {
            if (activity != null) {
                stringBuilder.append("==").append(activity.getClass().getSimpleName()).append("==");
                activity.finish();
            }
        }
        Logger.d(stringBuilder.toString());
        mActivityStack.clear();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this, null);
        }
        return mRequestQueue;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        Log.i("Exception", "listener");
        Log.i("thread", thread.toString());
        Log.i("Looper.getMainLooper", Looper.getMainLooper().getThread().toString());
        if (thread == Looper.getMainLooper().getThread()) {
//
            String s = "";
            for (StackTraceElement traceElement : ex.getStackTrace()) {
                s += "\n" + traceElement.toString();
            }

            Map<String, String> stringErrorMap = new HashMap<>();
            stringErrorMap.put("error_name", "MainThreadException");
            stringErrorMap.put("error_message", ex.getMessage());
            stringErrorMap.put("error_line", s);

            JSONObject object = new JSONObject(stringErrorMap);
            PreferenceUtils.setPrefString(mInstance, PreferenceConstants.KEY_IS_APP_FINISHED, object.toString());

            finishAllActivity();
//            Log.e("ATTApplication", ex.getMessage());
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ex.printStackTrace(new PrintStream(baos));
//            String exception = baos.toString();
//            Log.e("ATTApplication", exception);
//            ex.printStackTrace();
//            System.exit(0);
////            new AlertDialog.Builder(this).setTitle("提示").setCancelable(false)
////                    .setMessage("程序崩溃了...").setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
////                @Override
////                public void onClick(DialogInterface dialog, int which) {
////                    System.exit(0);
////                }
////            }).create().show();
//        } else {
//            Log.e(thread.getName() + "ATTApplication", ex.getMessage());
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ex.printStackTrace(new PrintStream(baos));
//            String exception = baos.toString();
//            Log.e("ATTApplication", exception);
//            ex.printStackTrace();
        }
    }

//    private String carType = "";

//    public List<CarListResponse.Data> getCarList() {
//        if (StringUtils.isEmpty(carType)) {
//            carType = PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_CAR_TYPE, "");
//            if (StringUtils.isEmpty(carType)) {
//                return null;
//            }
//        }
//        CarListResponse carListResponse = mGson.fromJson(carType, CarListResponse.class);
//        return carListResponse.getResults();
//    }
//
//    public void setCarList(CarListResponse s) {
//        carType = mGson.toJson(s);
//        PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_CAR_TYPE, carType);
//    }

//    String userInfo = "";
//
//    public void setUserInfo(UserInfoResponse.Data s) {
//        userInfo = mGson.toJson(s);
//        PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_USER_INFO, userInfo);
//    }

//    public UserInfoResponse.Data getUserInfo() {
//        if (StringUtils.isEmpty(userInfo)) {
//            userInfo = PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_USER_INFO, "");
//            if (StringUtils.isEmpty(userInfo)) {
//                return null;
//            }
//        }
//        return mGson.fromJson(userInfo, UserInfoResponse.Data.class);
//    }

//    private String location = "";
//
//    public BDLocation getLocation() {
//        if (StringUtils.isEmpty(location)) {
//            location = PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_LOCATION, "");
//            if (StringUtils.isEmpty(location)) {
//                return null;
//            }
//        }
//        return mGson.fromJson(location, BDLocation.class);
//    }

//    public void setLocation(BDLocation location) {
//        this.location = mGson.toJson(location);
//        PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_LOCATION, this.location);
//    }
//
//    public String getToken() {
//        return PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_TOKEN, "");
//    }
//
//    public void setToken(String token) {
//        PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_TOKEN, token);
//    }

//    public String getNearBy() {
//        return PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_NEAR_BY, "");
//    }
//
//    public void setNearBy(String token) {
//        PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_NEAR_BY, token);
//    }

    String userInfo = "";

    public void setUserInfo(UserInfoResponse.Data s) {
        userInfo = mGson.toJson(s);
        PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_USER_INFO, userInfo);
    }

    public UserInfoResponse.Data getUserInfo() {
        if (StringUtils.isEmpty(userInfo)) {
            userInfo = PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_USER_INFO, "");
            if (StringUtils.isEmpty(userInfo)) {
                return new UserInfoResponse.Data();
            }
        }
        return mGson.fromJson(userInfo, UserInfoResponse.Data.class);
    }

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
