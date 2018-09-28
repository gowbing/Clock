package com.miracle.clock.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.czsirius.clock.R;
import com.miracle.clock.constant.HttpUrlConstant;
import com.miracle.clock.constant.PreferenceConstants;
import com.miracle.clock.manager.BaseActivity;
import com.miracle.clock.manager.BaseApplication;
import com.miracle.clock.model.event.UploadImgEvent;
import com.miracle.clock.model.normal.EmptyResponse;
import com.miracle.clock.model.normal.TagResponse;
import com.miracle.clock.model.normal.UploadImgResponse;
import com.miracle.clock.model.normal.UserInfoResponse;
import com.miracle.clock.utils.normal.AppUtils;
import com.miracle.clock.utils.normal.DateUtils;
import com.miracle.clock.utils.normal.FileImageUploadUtils;
import com.miracle.clock.utils.normal.ImageUtils;
import com.miracle.clock.utils.normal.PreferenceUtils;
import com.miracle.clock.utils.normal.StringUtils;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.addapp.pickers.entity.City;
import cn.addapp.pickers.entity.Province;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.listeners.OnSingleWheelListener;
import cn.addapp.pickers.picker.SinglePicker;
import cn.addapp.pickers.util.ConvertUtils;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;
    @Bind(R.id.toolbar_back)
    TextView mToolbarBack;
    @Bind(R.id.toolbar_right)
    TextView mToolbarRight;
    @Bind(R.id.toolbar_right_img)
    ImageView mToolbarRightImg;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.iv_head)
    SimpleDraweeView mIvHead;
    @Bind(R.id.rb_1)
    RadioButton mRb1;
    @Bind(R.id.rb_2)
    RadioButton mRb2;
    @Bind(R.id.rg_sex)
    RadioGroup mRgSex;
    @Bind(R.id.tv_year)
    TextView mTvYear;
    @Bind(R.id.tv_month)
    TextView mTvMonth;
    @Bind(R.id.tv_day)
    TextView mTvDay;
    @Bind(R.id.ll_birth)
    LinearLayout mLlBirth;
    @Bind(R.id.tv_country)
    TextView mTvCountry;
    @Bind(R.id.tv_province)
    TextView mTvProvince;
    @Bind(R.id.tv_city)
    TextView mTvCity;
    @Bind(R.id.ll_location)
    LinearLayout mLlLocation;
    @Bind(R.id.tv_type_1)
    TextView mTvType1;
    @Bind(R.id.ll_type_1)
    LinearLayout mLlType1;
    @Bind(R.id.tv_type_2)
    TextView mTvType2;
    @Bind(R.id.ll_type_2)
    LinearLayout mLlType2;
    @Bind(R.id.tv_type_3)
    TextView mTvType3;
    @Bind(R.id.ll_type_3)
    LinearLayout mLlType3;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.ll_year)
    LinearLayout mLlYear;
    @Bind(R.id.ll_month)
    LinearLayout mLlMonth;
    @Bind(R.id.ll_day)
    LinearLayout mLlDay;
    @Bind(R.id.ll_province)
    LinearLayout mLlProvince;
    @Bind(R.id.ll_city)
    LinearLayout mLlCity;

    List<TagResponse.Data> tagList;

    ArrayList<Province> provinces;

    UserInfoResponse.Data data = BaseApplication.getInstance().getUserInfo();

    Dialog dialog;

    private List<TagResponse.Data> tagdata = new ArrayList<>();

    public String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(data.getBirthday() * 1000));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initView();
        initListener();
        Logger.d(date);
    }

    @Override
    public void initView() {
        provinces = new ArrayList<>();
        try {
            String json = ConvertUtils.toString(getAssets().open("city.json"));
            provinces.addAll(JSON.parseArray(json, Province.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tagdata.add(new TagResponse.Data());
        tagdata.add(new TagResponse.Data());
        tagdata.add(new TagResponse.Data());

        mToolbarTitle.setText("个人资料");
        mToolbarBack.setVisibility(View.VISIBLE);
        mToolbarRight.setText("保存");
        mToolbarRight.setTextColor(AppUtils.getColor(R.color.blue));
        getTagList();
        setUserInfo();
    }

    @Override
    public void initListener() {
        mToolbarBack.setOnClickListener(this);
        mIvHead.setOnClickListener(this);
        mLlYear.setOnClickListener(this);
        mLlMonth.setOnClickListener(this);
        mLlDay.setOnClickListener(this);
        mLlProvince.setOnClickListener(this);
        mLlCity.setOnClickListener(this);
        mLlType1.setOnClickListener(this);
        mLlType2.setOnClickListener(this);
        mLlType3.setOnClickListener(this);
        mToolbarRight.setOnClickListener(this);
        tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTextDialog(tvNickname);
            }
        });

    }

    public void setUserInfo() {
        try {
            if (data.getUserid() != 0) {
                mIvHead.setImageURI(Uri.parse(data.getImgUrl()));
                headPath = data.getImgUrl();
                if (data.getSex() == 1) {
                    mRb2.setChecked(true);
                } else {
                    mRb1.setChecked(true);
                }
                Date d = new Date(data.getBirthday() * 1000);
                String birth = new SimpleDateFormat("yyyy-MM-dd").format(d);
                String[] births = birth.split("-");
                mTvYear.setText(births[0]);
                mTvMonth.setText(births[1]);
                mTvDay.setText(births[2]);
                mTvProvince.setText(data.getProvince());
                mTvCity.setText(data.getCity());
                tvNickname.setText(data.getNickname());
                if (data.getFavorite() != null && !data.getFavorite().equals("")) {
                    JSONArray jsondata = new JSONArray(data.getFavorite());
                    JSONObject o = (JSONObject) jsondata.get(0);
                    int ss = jsondata.length();
                    switch (jsondata.length()) {
                        case 0:
                            break;
                        case 1:
                            mTvType1.setText(new JSONObject(jsondata.get(0).toString()).opt("content").toString());
                            tagdata.set(0, new Gson().fromJson(jsondata.get(0).toString(), TagResponse.Data.class));
                            break;
                        case 2:
                            mTvType1.setText(new JSONObject(jsondata.get(0).toString()).opt("content").toString());
                            tagdata.set(0, new Gson().fromJson(jsondata.get(0).toString(), TagResponse.Data.class));
                            mTvType2.setText(new JSONObject(jsondata.get(1).toString()).opt("content").toString());
                            tagdata.set(1, new Gson().fromJson(jsondata.get(1).toString(), TagResponse.Data.class));
                            break;
                        case 3:
                            mTvType1.setText(new JSONObject(jsondata.get(0).toString()).opt("content").toString());
                            tagdata.set(0, new Gson().fromJson(jsondata.get(0).toString(), TagResponse.Data.class));
                            mTvType2.setText(new JSONObject(jsondata.get(1).toString()).opt("content").toString());
                            tagdata.set(1, new Gson().fromJson(jsondata.get(1).toString(), TagResponse.Data.class));
                            mTvType3.setText(new JSONObject(jsondata.get(2).toString()).opt("content").toString());
                            tagdata.set(2, new Gson().fromJson(jsondata.get(2).toString(), TagResponse.Data.class));
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onOptionPicker(final int type, final ArrayList<String> data) {
        SinglePicker<String> picker = new SinglePicker<>(this, data);
        picker.setCanLoop(false);//不禁用循环
        picker.setLineVisible(true);
        picker.setShadowVisible(true);
        picker.setTextSize(18);
        picker.setSelectedIndex(0);
        picker.setWheelModeEnable(true);
        //启用权重 setWeightWidth 才起作用
//        picker.setLabel("分");
        picker.setWeightEnable(true);
        picker.setWeightWidth(1);
        picker.setSelectedTextColor(0xFF279BAA);//前四位值是透明度
        picker.setUnSelectedTextColor(0xFF999999);
        picker.setOnSingleWheelListener(new OnSingleWheelListener() {
            @Override
            public void onWheeled(int index, String item) {
            }
        });
        picker.setOnItemPickListener(new OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                switch (type) {
                    case 11:
                        mTvYear.setText(item);
                        break;
                    case 12:
                        mTvMonth.setText(item);
                        break;
                    case 13:
                        mTvDay.setText(item);
                        break;
                    case 21:
                        mTvProvince.setText(item);
                        break;
                    case 22:
                        mTvCity.setText(item);
                        break;
                    case 31:
                        mTvType1.setText(tagList.get(index).getContent());
                        tagdata.set(0, tagList.get(index));
                        break;
                    case 32:
                        mTvType2.setText(tagList.get(index).getContent());
                        tagdata.set(1, tagList.get(index));
                        break;
                    case 33:
                        mTvType3.setText(tagList.get(index).getContent());
                        tagdata.set(2, tagList.get(index));
                        break;
                }
            }
        });
        picker.show();
    }

    private void getTagList() {
        Map<String, String> map = new HashMap<>();
        getDataFromServer(HttpUrlConstant.TAB_GETLIST, map, TagResponse.class, new Response.Listener<TagResponse>() {
            @Override
            public void onResponse(TagResponse response) {
                if (response.getStatus().equals("ok")) {
                    tagList = response.getResults();
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("网络错误，请稍后再试");
            }
        });
    }

    public void saveUserInfo() {
        String nickname = tvNickname.getText().toString();
        int sex;
        if (mRb1.isChecked()) {
            sex = 1;
        } else {
            sex = 0;
        }
        String province = mTvProvince.getText().toString();
        String city = mTvCity.getText().toString();
        if (StringUtils.isEmpty(province)) {
            showToast("请选择省份");
            return;
        }
        if (StringUtils.isEmpty(mTvYear.getText().toString()) ||
                StringUtils.isEmpty(mTvMonth.getText().toString()) ||
                StringUtils.isEmpty(mTvDay.getText().toString())) {
            showToast("请选择生日");
            return;
        }
        String s = mTvYear.getText().toString() + "-" + mTvMonth.getText().toString() + "-" + mTvDay.getText().toString();
        long t = 0;
        try {
            t = DateUtils.getFormatDate(s, DateUtils.DATE_FORMAT_ONE).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        userUpdate(t, headPath, "中国", province, city, String.valueOf(sex), nickname);
    }

    private void userUpdate(long birthday, String imgUrl, String country, String province, String city, String sex, String nickname) {
        Map<String, String> map = new HashMap<>();
        map.put("userid", String.valueOf(data.getUserid()));
        map.put("userguid", String.valueOf(data.getUserguid()));
        map.put("birthday", birthday + "");
        map.put("imgUrl", imgUrl);
        map.put("country", country);
        map.put("province", province);
        map.put("city", city);
        map.put("nickname", nickname);
        map.put("sex", sex);
        map.put("favorite", new Gson().toJson(tagdata));
        getDataFromServer(HttpUrlConstant.USER_UPDATE, map, EmptyResponse.class, new Response.Listener<EmptyResponse>() {
            @Override
            public void onResponse(EmptyResponse response) {
                if (response.getStatus().equals("ok")) {
                    finish();
                } else {
                    showToast(response.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("网络错误，请稍后再试");
            }
        });
    }

    private void showEditTextDialog(final TextView tvShow) {
        if (dialog == null) {
            dialog = new Dialog(mContext, R.style.LoadingDialog);
            dialog.setContentView(R.layout.dialog_edit_text);

            TextView tv_title = dialog.findViewById(R.id.tv_title);
            final EditText et_text = dialog.findViewById(R.id.et_text);
            TextView tv_commit = dialog.findViewById(R.id.tv_commit);
            TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);

            tv_title.setText("昵称");
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            tv_commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvShow.setText(et_text.getText().toString());
                    dialog.dismiss();
                }
            });
        }
        dialog.show();
    }

    /**
     * 图片上传
     */
    //////////////////////////////////////////////////////////
    String headPath = "";

    Dialog mDialogSelectImage;
    private static final String PHOTO_PATH = getPictureDir();// 拍照存放照片路径
    /**
     * 图片对应目录文件名
     */
    private final static String PICTURE_PATH = "picture";
    /**
     * 资源文件地址
     */
    public final static String ROOT_PATH = "chat";
    private final static int CROP = 150;

    public void heedImageClick() {
        if (mDialogSelectImage == null) {
            mDialogSelectImage = new Dialog(this, R.style.ExitAppDialogStyle);
            LinearLayout localObject = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_select_image_dialog, null);
            localObject.setMinimumWidth(10000);
            TextView tvCamera = (TextView) localObject.findViewById(R.id.tv_camera);
            TextView tvAlbum = (TextView) localObject.findViewById(R.id.tv_album);
            TextView tvCancel = (TextView) localObject.findViewById(R.id.tv_cancel);
            tvCamera.setText("拍照");
            tvCamera.setOnClickListener(this);
            tvAlbum.setText("从相册中选择");
            tvAlbum.setOnClickListener(this);
            tvCancel.setText("取消");
            tvCancel.setOnClickListener(this);

            WindowManager.LayoutParams localLayoutParams = mDialogSelectImage.getWindow().getAttributes();
            localLayoutParams.x = 0;
            localLayoutParams.y = -1000;
            localLayoutParams.gravity = 80;
            mDialogSelectImage.onWindowAttributesChanged(localLayoutParams);
            mDialogSelectImage.setCanceledOnTouchOutside(true);
            mDialogSelectImage.setContentView(localObject);
        }
        mDialogSelectImage.show();
    }

    /**
     * 拍照后保存相片，获得绝对路径
     *
     * @return
     */
    private String getCameraTempFile(String uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(PHOTO_PATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            showToast("无法保存上传的头像，请检查SD卡是否挂载");
        }
        // 照片命名
        String cropFileName = System.currentTimeMillis() + ".png";
        // 裁剪头像的绝对路径
        cropFileName = PHOTO_PATH + cropFileName;
        return cropFileName;
    }

    /**
     * 获取图片文件存放路径
     *
     * @return
     */
    public static String getPictureDir() {
        StringBuffer sb = new StringBuffer();
        if (hasSdcard()) {
            sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        } else {
            sb.append(BaseApplication.getInstance().getFilesDir());
        }
        sb.append(File.separator);
        sb.append(ROOT_PATH);
        sb.append(File.separator);
        sb.append(PICTURE_PATH);
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.d("----------onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:// 如果是直接从相册获取
                    if (data == null) {
                        return;
                    }
                    Uri imageUri = data.getData();
                    startActionCrop(imageUri);// 选图后裁剪
                    break;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:// 如果是调用相机拍照时
                    String imagePath = PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_HEAD_IMAGE_OUT_PATH, "");
                    File outFile = new File(imagePath);
                    startActionCrop(Uri.fromFile(outFile));// 拍照后裁剪
                    break;
                case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:// 图片裁剪
                    String outPath = PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_HEAD_IMAGE_OUT_PATH, "");
                    File cropFile = new File(outPath);
                    if (StringUtils.notEmpty(outPath) && cropFile.exists()) {// 获取头像缩略图
                        Uri uri = Uri.parse(outPath);
                        mIvHead.setImageURI(uri);
                        String url = HttpUrlConstant.URL_UPLOAD_IMG;
                        uploadHead(cropFile, url);// 上传头像
                    }
                    break;
            }
        }
    }

    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     */
    private void startActionCrop(Uri data) {
        String outPath = PreferenceUtils.getPrefString(this, PreferenceConstants.KEY_HEAD_IMAGE_OUT_PATH, "");
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output", Uri.fromFile(new File(outPath)));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        intent.putExtra("noFaceDetection", true);// 是否去除面部检测， 如果你需要特定的比例去裁剪图片，那么这个一定要去掉，因为它会破坏掉特定的比例。
        intent.putExtra("return-data", false);// 若为false则表示不返回数据
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
    }

    /**
     * 上传头像
     */
    private void uploadHead(File file, String url) {
        Logger.d("==============上传头像==============");
        FileImageUploadUtils.handleUpload(file, url);
    }

    @Override
    protected boolean registerEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UploadImgEvent event) {
        Logger.d(event.getIsSuccess());
        Gson gson = new Gson();
        UploadImgResponse response = gson.fromJson(event.getIsSuccess(), UploadImgResponse.class);
        headPath = response.getPath();
        Uri uri = Uri.parse(response.getPath());
        mIvHead.setImageURI(uri);
    }

    //////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View view) {
        ArrayList<String> list = new ArrayList<>();
        for (TagResponse.Data i : tagList) {
            list.add(i.getContent());
        }
        String outPath = getCameraTempFile(null);
        File outFile = new File(outPath);
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_camera:
                if (mDialogSelectImage != null) {
                    mDialogSelectImage.dismiss();
                }
                PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_HEAD_IMAGE_OUT_PATH, outPath);
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
                startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                break;
            case R.id.tv_album:
                if (mDialogSelectImage != null) {
                    mDialogSelectImage.dismiss();
                }
                PreferenceUtils.setPrefString(this, PreferenceConstants.KEY_HEAD_IMAGE_OUT_PATH, outPath);
                intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));
                startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                break;
            case R.id.tv_cancel:
                mDialogSelectImage.dismiss();
                break;

            case R.id.iv_head:
                heedImageClick();
                break;
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.toolbar_right:
                saveUserInfo();
                break;
            case R.id.ll_year:
                list.clear();
                for (int i = 1970; i <= 2100; i++) {
                    list.add("" + i);
                }
                onOptionPicker(11, list);
                break;
            case R.id.ll_month:
                list.clear();
                for (int i = 1; i <= 12; i++) {
                    if (i < 10) {
                        list.add("0" + i);
                    } else {
                        list.add("" + i);
                    }
                }
                onOptionPicker(12, list);
                break;
            case R.id.ll_day:
                if (StringUtils.isEmpty(mTvYear.getText().toString())) {
                    showToast("请您先选择年份");
                    return;
                }
                if (StringUtils.isEmpty(mTvMonth.getText().toString())) {
                    showToast("请您先选择月份");
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.valueOf(mTvYear.getText().toString()));//先指定年份
                calendar.set(Calendar.MONTH, Integer.valueOf(mTvMonth.getText().toString()) - 1);//再指定月份 Java月份从0开始算
                int daysCountOfMonth = calendar.getActualMaximum(Calendar.DATE);//获取指定年份中指定月份有几天
                list.clear();
                for (int i = 1; i <= daysCountOfMonth; i++) {
                    if (i < 10) {
                        list.add("0" + i);
                    } else {
                        list.add("" + i);
                    }
                }
                onOptionPicker(13, list);
                break;
            case R.id.ll_province:
                list.clear();
                for (int i = 0; i < provinces.size(); i++) {
                    list.add(provinces.get(i).getAreaName());
                }
                onOptionPicker(21, list);
                break;
            case R.id.ll_city:
                if (StringUtils.isEmpty(mTvProvince.getText().toString())) {
                    showToast("请您先选择省份");
                    return;
                }
                list.clear();
                List<City> cities = new ArrayList<>();
                for (int i = 0; i < provinces.size(); i++) {
                    if (provinces.get(i).getAreaName().equals(mTvProvince.getText().toString())) {
                        cities.addAll(provinces.get(i).getCities());
                    }
                }
                for (int i = 0; i < cities.size(); i++) {
                    list.add(cities.get(i).getAreaName());
                }
                onOptionPicker(22, list);
                break;
            case R.id.ll_type_1:
                onOptionPicker(31, list);
                break;
            case R.id.ll_type_2:
                onOptionPicker(32, list);
                break;
            case R.id.ll_type_3:
                onOptionPicker(33, list);
                break;
        }
    }

}
