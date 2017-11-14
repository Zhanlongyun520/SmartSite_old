package com.isoftstone.smartsite;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.isoftstone.smartsite.common.NewKeepAliveService;
import com.isoftstone.smartsite.http.DictionaryBean;
import com.isoftstone.smartsite.http.HttpPost;
import com.isoftstone.smartsite.http.LoginBean;
import com.isoftstone.smartsite.http.PatrolBean;
import com.isoftstone.smartsite.http.ReportBean;
import com.isoftstone.smartsite.http.UserBean;
import com.uniview.airimos.listener.OnLoginListener;
import com.uniview.airimos.manager.ServiceManager;
import com.uniview.airimos.parameter.LoginParam;
import com.uniview.airimos.service.KeepaliveService;
import android.Manifest;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends Activity implements OnClickListener,OnLoginListener ,KeepaliveService.OnKeepaliveListener{
	protected static final String TAG = "LoginActivity";
	private LinearLayout mLoginLinearLayout; // 登录内容的容器
	private LinearLayout mUserIdLinearLayout; // 将下拉弹出窗口在此容器下方显示
	private Animation mTranslate; // 位移动画
	private Dialog mLoginingDlg; // 显示正在登录的Dialog
	private EditText mIdEditText; // 登录ID编辑框
	private EditText mPwdEditText; // 登录密码编辑框
	private Button mLoginButton; // 登录按钮
	private String mIdString;
	private String mPwdString;
	private ArrayList<User> mUsers; // 用户列表
    private HttpPost mHttpPost = null;
	private static final  int HANDLER_LOGIN_START = 1;
	private static final  int HANDLER_LOGIN_END = 2;
	private static final int HANDLER_SHOW_TOAST = 3;
	private String mLoginResult = "";
	private Boolean isLogin_1 = false;
	private Boolean isLogin_2 = false;

	private KeepaliveService mKeepService = null;
	private boolean isBound = false;
	private View mIdView = null;
	private View mPwdView = null;
	private ImageView mIdImageView = null;
	private ImageView mPwdImageView = null;
    private String mJpushId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();

		mLoginLinearLayout.startAnimation(mTranslate); // Y轴水平移动

		/* 获取已经保存好的用户密码 */
		mUsers = UserUtils.getUserList(LoginActivity.this);

		if (mUsers.size() > 0) {
			//将列表中的第一个user显示在编辑框
			Log.i("test","mUsers.get(0).getId() ="+mUsers.get(0).getId());
			mIdEditText.setText(mUsers.get(0).getId());
			mPwdEditText.setText(mUsers.get(0).getPwd());
			//Message message = new Message();
			//message.what = HANDLER_LOGIN_START;
			//mHandler.sendMessage(message);
		}
		mHttpPost = new HttpPost();
		mJpushId = JPushInterface.getRegistrationID(getApplicationContext());
	}


	private void setListener() {
		mIdEditText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				mIdString = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		mPwdEditText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				mPwdString = s.toString();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});

	}

	private void initView() {
		mIdView = (View) findViewById(R.id.view_1);
		mPwdView = (View) findViewById(R.id.view_2);
		mIdImageView = (ImageView) findViewById(R.id.imageView_1);
		mPwdImageView = (ImageView)findViewById(R.id.imageView_2);
		mIdEditText = (EditText) findViewById(R.id.login_edtId);
		mPwdEditText = (EditText) findViewById(R.id.login_edtPwd);
		mLoginButton = (Button) findViewById(R.id.login_btnLogin);
		mLoginLinearLayout = (LinearLayout) findViewById(R.id.login_linearLayout);
		mTranslate = AnimationUtils.loadAnimation(this, R.anim.my_translate); // 初始化动画对象
		initLoginingDlg();

		mLoginButton.setOnClickListener(this);
		setListener();

		//mIdEditText.setText("admin");
		//mPwdEditText.setText("bmeB4000");

		mIdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
					mIdView.setBackgroundColor(LoginActivity.this.getResources().getColor(R.color.mainColor));
					mIdImageView.setImageDrawable(getResources().getDrawable(R.drawable.loginuser_blue));
				}else{
					mIdView.setBackgroundColor(LoginActivity.this.getResources().getColor(R.color.hit_text_color));
					mIdImageView.setImageDrawable(getResources().getDrawable(R.drawable.loginuser_gray));
				}
			}
		});

		mPwdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if(hasFocus){
					mPwdView.setBackgroundColor(LoginActivity.this.getResources().getColor(R.color.mainColor));
					mPwdImageView.setImageDrawable(getResources().getDrawable(R.drawable.loginpassword_blue));
				}else{
					mPwdView.setBackgroundColor(LoginActivity.this.getResources().getColor(R.color.hit_text_color));
					mPwdImageView.setImageDrawable(getResources().getDrawable(R.drawable.loginpassword_gray));
				}
			}
		});
	}

	/* 初始化正在登录对话框 */
	private void initLoginingDlg() {

		mLoginingDlg = new Dialog(this, R.style.loginingDlg);
		mLoginingDlg.setContentView(R.layout.logining_dlg);

		mLoginingDlg.setCanceledOnTouchOutside(false); // 设置点击Dialog外部任意区域关闭Dialog
	}

	/* 显示正在登录对话框 */
	private void showLoginingDlg() {
		if (mLoginingDlg != null)
			mLoginingDlg.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	/* 关闭正在登录对话框 */
	private void closeLoginingDlg() {
		if (mLoginingDlg != null && mLoginingDlg.isShowing())
			mLoginingDlg.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.login_btnLogin:
				mIdString = mIdEditText.getText().toString();
				mPwdString = mPwdEditText.getText().toString();
				if (mIdString == null || mIdString.equals("")) { // 账号为空时
					Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT)
							.show();
				} else if (mPwdString == null || mPwdString.equals("")) {// 密码为空时
					Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT)
							.show();
				} else {// 账号和密码都不为空时
					Message message = new Message();
					message.what = HANDLER_LOGIN_START;
					mHandler.sendMessage(message);
				}
				break;
			default:
				break;
		}

	}

	/* 退出此Activity时保存users */
	@Override
	public void onPause() {
		super.onPause();
		try {
			UserUtils.saveUserList(LoginActivity.this, mUsers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case HANDLER_LOGIN_START:{
					showLoginingDlg(); // 显示"正在登录"对话框,因为此Demo没有登录到web服务器,所以效果可能看不出.可以结合情况使用
					new Thread(){
						@Override
						public void run() {
							loggin(mIdString,mPwdString,mJpushId);
							if(isLogin_1){
								logginVideo();
								/*Intent intent = new Intent();
								intent.setClass(LoginActivity.this,MainActivity.class);
								LoginActivity.this.startActivity(intent);
								mLoginResult = "登录成功";
								Toast.makeText(getApplication(),mLoginResult,Toast.LENGTH_LONG).show();
								finish();*/
							}

						}
					}.start();
				}
				break;
				case HANDLER_LOGIN_END:{
					closeLoginingDlg();// 关闭对话框
					if(isLogin_1 && isLogin_2){
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this,MainActivity.class);
						LoginActivity.this.startActivity(intent);
						//mLoginResult = "登录成功";
						//Toast.makeText(getApplication(),mLoginResult,Toast.LENGTH_SHORT).show();
						finish();
					}
				}
				break;
				case HANDLER_SHOW_TOAST:{
					Toast.makeText(getApplication(),mLoginResult,Toast.LENGTH_SHORT).show();
				}
				break;
			}

		}
	};
	private void loggin(String mIdString,String mPwdString,String jpushId){
		     // 启动登录
			 LoginBean loginBean = null;
			 loginBean = mHttpPost.login(mIdString,mPwdString,jpushId);
             /*
             String day = mHttpPost.carchMonthlyComparison("29","2017-10","1").getBeforeMonth().get(4).getPushTimeOneDay();
             Log.i("test",day);
             */
			/*
			int size = mHttpPost.getWeatherConditionDay("29","2017-10").size();
			Log.i("test",size+" size ");
			*/


			/*
			mHttpPost.onePMDevicesDataList("[1,2]","0","2017-10-01 00:00:00","2017-10-11 00:00:00");
			 */


			/*
			int size = mHttpPost.getOneDevicesHistoryData("1").size();
			Log.i("test",size+" getOneDevicesHistoryData size ");
			*/


			/*
			int size =  mHttpPost.onePMDevices24Data("2","2017-10-10 10:10:10").size();
            Log.i("test",size+" size ");
            */

			/*
			int size = mHttpPost.getDevices("","","","").size();
			Log.i("test",size+" size ");
             */


			/*
			mHttpPost.readMessage("7");
			int size = mHttpPost.getMessage("","","","1").size();
			Log.i("test",size+" size ");
			*/

            /*
            String str = mHttpPost.getWeatherLive("47","2017-10").getDataTrend().get(1).getPm10();
			Log.i("test","getWeatherLive  ---------"+str);
			*/


			/*
			PatrolBean bean = new PatrolBean();
			bean.setAddress("武汉大软件元");
			bean.setCompany("wuanhan  wxa");
            mHttpPost.addPatrolReport(bean);
            */


            /*
            String address = mHttpPost.getPatrolReport("76").getAddress();
			Log.i("test","address  ---------"+address);
			*/



			/*ReportBean bean = new ReportBean();
			bean.setName("我是一个");
            bean.setContent("<p>一切正常-----------------</p>");
			bean.setCreator("我是一个");
			bean.setDate("2017-10-30 14:22:44");
			PatrolBean patrol = new PatrolBean();
			patrol.setId(27);
			bean.setPatrol(patrol);
			bean.setCategory(2);
			bean.setPatrolUser("马化腾马化腾");
			bean.setPatrolDateEnd("2017-10-30 14:23");
			bean.setPatrolDateStart("2017-10-29 14:23");
			mHttpPost.addPatrolVisit(bean);*/


			//mHttpPost.getPatrolReportList(3);





			/*
			mHttpPost.getMobileHomeData();
			 */

			//mHttpPost.imageUpload("/storage/emulated/0/test.png",39);
			//mHttpPost.reportFileUpload("/storage/emulated/0/k.log",39);

            /*UserBean user_1 = new UserBean();
			user_1.setId(1l);
			user_1.setAccount("admin");
			user_1.setPassword("bmeB4000");
			user_1.setName("isoftstone");

			mHttpPost.userUpdate(user_1);
            */

			/*String name = mHttpPost.getLoginUser().getName();
			Log.i("test","name  ---------"+name);
            */

			//mHttpPost.downloadUserImage("img\\logo.png");
			//mHttpPost.downloadReportFile(1,"img\\logo.png");

			/*Bitmap bitmap = BitmapFactory.decodeFile("/storage/emulated/0/test.png");
			mHttpPost.userImageUpload(bitmap,Bitmap.CompressFormat.PNG);
            */

			//mHttpPost.getDictionaryList("zh",2);

			/*ArrayList<String> addresslist = mHttpPost.getPatrolAddress();
			for (String str:addresslist){
				Log.i("Test",str);
			}*/


			/*ArrayList<DictionaryBean> list = mHttpPost.getDictionaryList("zh");
			for (DictionaryBean str:list){
				Log.i("Test",str.getContent()+" "+str.getValue());
			}*/
			if(loginBean.isLoginSuccess()){
				 boolean mIsSave = false;
				UserBean userBean = mHttpPost.getLoginUser();
				HttpPost.mLoginBean.setmUserBean(userBean);
				 try {
					 Log.i(TAG, "保存用户列表");
					 /*for (User user : mUsers) { // 判断本地文档是否有此ID用户
						 if (user.getId().equals(mIdString)) {
							 mIsSave = false;
							 break;
						 }
					 }*/
				//if (mIsSave) { // 将新用户加入users
				//	User user = new User(mIdString, mPwdString);
				//	mUsers.add(user);
				//}
					 mUsers.clear();
					 User user = new User(mIdString, mPwdString);
					 mUsers.add(user);
					 UserUtils.saveUserList(getBaseContext(),mUsers);
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
				 isLogin_1 = true;
			 }else{
				 mLoginResult = loginBean.getmErrorInfo();
				 if(mLoginResult == null){
					 mLoginResult = "登录失败";
				 }
				 isLogin_1 = false;
				mHandler.sendEmptyMessage(HANDLER_LOGIN_END);
			 }

	}

	public void logginVideo(){

        if(mHttpPost.getVideoConfig()){
            LoginParam params = new LoginParam();
			params.setServer(mHttpPost.mLoginBean.getmVideoParameter().getIp());
			params.setPort(Integer.parseInt(mHttpPost.mLoginBean.getmVideoParameter().getPort()));
			params.setUserName(mHttpPost.mLoginBean.getmVideoParameter().getLoginName());
			params.setPassword(mHttpPost.mLoginBean.getmVideoParameter().getLoginPass());
            //调用登录接口
            ServiceManager.login(params, LoginActivity.this);
        }else{
            isLogin_2 = false;
            mLoginResult = "登录失败：与后台服务连接异常";
            mHandler.sendEmptyMessage(HANDLER_SHOW_TOAST);
            Message message = new Message();
            message.what = HANDLER_LOGIN_END;
            mHandler.sendMessage(message);
        }
	}
	/**
	 * 登录结果返回
	 */
	@Override
	public void onLoginResult(long errorCode, String errorDesc)
	{
		Log.i("zyf","onLoginResult   errorCode=" + errorCode);
		//成功为0，其余为失败错误码
		if (errorCode == 0)
		{
			startKeepaliveService();
			isLogin_2 = true;
			HttpPost.mVideoIsLogin = true;
		}
		else
		{
			mLoginResult = "登录失败：" + errorCode + "," + errorDesc;
			isLogin_2 = false;
			HttpPost.mVideoIsLogin = false;
			mHandler.sendEmptyMessage(HANDLER_SHOW_TOAST);


		}
		Message message = new Message();
		message.what = HANDLER_LOGIN_END;
		mHandler.sendMessage(message);
	}

	//启动保活服务
	public void startKeepaliveService(){

		Intent toService = new Intent(this, NewKeepAliveService.class);

		startService(toService);
	}

	protected void onDestroy() {
		super.onDestroy();

		//stopKeepaliveService();
	}

	public void stopKeepaliveService(){
		Intent intent = new Intent(this, NewKeepAliveService.class);
		stopService(intent);
		return;
	}

	public ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			KeepaliveService.KeepaliveBinder binder = (KeepaliveService.KeepaliveBinder) service;
			mKeepService =  binder.getService();
			mKeepService.start(LoginActivity.this);
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
		}
	};

	@Override
	public void onKeepaliveFailed() {
		//Toast.makeText(LoginActivity.this, "保活失败，请重新登录", Toast.LENGTH_LONG).show();
	}
}
