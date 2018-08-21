package com.isoftstone.smartsite.http;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.isoftstone.smartsite.common.App;
import com.isoftstone.smartsite.http.aqi.DataQueryVoBean;
import com.isoftstone.smartsite.http.aqi.DataQueryVoBeanPage;
import com.isoftstone.smartsite.http.aqi.EQIMonitoring;
import com.isoftstone.smartsite.http.aqi.EQIRankingBean;
import com.isoftstone.smartsite.http.aqi.MonthlyComparisonBean;
import com.isoftstone.smartsite.http.aqi.WeatherConditionBean;
import com.isoftstone.smartsite.http.aqi.WeatherLiveBean;
import com.isoftstone.smartsite.http.cookies.CookiesManager;
import com.isoftstone.smartsite.http.message.BeforeNMessageBean;
import com.isoftstone.smartsite.http.message.MessageBean;
import com.isoftstone.smartsite.http.message.MessageBeanPage;
import com.isoftstone.smartsite.http.message.MessageOperation;
import com.isoftstone.smartsite.http.message.MobileHomeBean;
import com.isoftstone.smartsite.http.muckcar.ArchMonthFlowBean;
import com.isoftstone.smartsite.http.muckcar.BayonetGrabInfoBeanPage;
import com.isoftstone.smartsite.http.muckcar.CarInfoBean;
import com.isoftstone.smartsite.http.muckcar.EvidencePhotoBean;
import com.isoftstone.smartsite.http.muckcar.EvidencePhotoBeanPage;
import com.isoftstone.smartsite.http.muckcar.MapMarkersVoBean;
import com.isoftstone.smartsite.http.muckcar.MuckCarOperation;
import com.isoftstone.smartsite.http.muckcar.UpdatePhotoInfoBean;
import com.isoftstone.smartsite.http.pageable.PageableBean;
import com.isoftstone.smartsite.http.patrolinfo.DepartmentMonthDataBean;
import com.isoftstone.smartsite.http.patrolinfo.DepartmentsMonthTasks;
import com.isoftstone.smartsite.http.patrolinfo.PatrolInfoOperation;
import com.isoftstone.smartsite.http.patrolinfo.ReportDataBean;
import com.isoftstone.smartsite.http.patrolinfo.UserTaskCountBean;
import com.isoftstone.smartsite.http.patrolplan.PatrolPlanBean;
import com.isoftstone.smartsite.http.patrolplan.PatrolPlanBeanPage;
import com.isoftstone.smartsite.http.patrolplan.PatrolPlanCommitBean;
import com.isoftstone.smartsite.http.patrolplan.PatrolPlanOperation;
import com.isoftstone.smartsite.http.patrolreport.DictionaryBean;
import com.isoftstone.smartsite.http.patrolreport.PatrolBean;
import com.isoftstone.smartsite.http.patrolreport.PatrolBeanPage;
import com.isoftstone.smartsite.http.patrolreport.ReportBean;
import com.isoftstone.smartsite.http.patrolreport.ReportOperation;
import com.isoftstone.smartsite.http.patroltask.PatrolTaskBean;
import com.isoftstone.smartsite.http.patroltask.PatrolTaskBeanPage;
import com.isoftstone.smartsite.http.patroltask.PatrolTaskOperation;
import com.isoftstone.smartsite.http.patroluser.PatrolUserOperation;
import com.isoftstone.smartsite.http.patroluser.UserTrackBean;
import com.isoftstone.smartsite.http.result.ResultBean;
import com.isoftstone.smartsite.http.taskcenter.TaskCenterOperation;
import com.isoftstone.smartsite.http.taskcenter.TaskNumberBean;
import com.isoftstone.smartsite.http.user.BaseUserBean;
import com.isoftstone.smartsite.http.user.CompanyBean;
import com.isoftstone.smartsite.http.user.InstallBean;
import com.isoftstone.smartsite.http.user.LoginBean;
import com.isoftstone.smartsite.http.user.UserBean;
import com.isoftstone.smartsite.http.user.UserLogin;
import com.isoftstone.smartsite.http.video.DevicesBean;
import com.isoftstone.smartsite.http.video.DevicesBeanPage;
import com.isoftstone.smartsite.utils.NetworkUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Created by guowei on 2017/10/14.
 */

public class HttpPost {
    public static final  int HTTP_LOGIN_TIME_OUT = 401;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient mClient = null;
    //public static String URL = "http://111.47.21.51:19090";//生产
    public static String URL = "http://139.159.237.223:19090/ctess";//忠德

    private String LOGIN_URL = URL + "/login";                        //登录
    private String GET_LOGIN_USER = URL + "/user/getLoginUser";      //获取登录用户信息
    private String GET_LOGIN_USER_BYID = URL + "/user/get/";      //获取登录用户信息
    private String USER_UPDATE = URL + "/user/update";              //用户信息更改
    private String USER_UPLOAD = URL + "/user/upload";              //用户头像修改
    private String GET_SYSTEM_CONFIG = URL + "/systemConfig/4";

    private String GET_VIDEO_CONFIG = URL + "/mobile/video/config";   //获取视频服务器参数
    private String MOBILE_HOME = URL + "/mobile/home";                //获取首页数据

    private String EQI_DATA_RANKING = URL + "/eqi/dataRanking";      //区域月度综合排名
    private String EQI_DATA_COMPARISON = URL + "/eqi/dataComparison";  //区域月度数据对比
    private String EQI_DAYS_PROPORTION = URL + "/eqi/daysProportion";  //优良天数占比
    private String EQI_LIST = URL + "/eqi/list";                       //单设备PM数据列表
    private String EQI_BYDEVICE_HISTORY = URL + "/eqi/byDevice/history/";  //获取一台设备历史参数
    private String EQI_BYDEVICE_DAYS = URL + "/eqi/byDevice/days";        //获取一台设备24小时数据


    private String ESS_DEVICE_LIST = URL + "/EssDevice/list";           //获取设备数据


    private String MESSAGE_LIST = URL + "/message/list";                //获取消息列表
    private String MESSAGE_ID_READ = URL + "/message/{id}/read";        //消息读取
    private String MESSAGE_LIST_N = URL +"/message/types"; //获取前N调消息

    private String PATROL_LIST = URL + "/patrol/list";        //获取报告列表
    private String ADD_PATROL_REPORT = URL + "/patrol";      //新增巡查报告
    private String GET_PATROL_REPORT = URL + "/patrol/";      //获取巡查报告
    private String ADD_REPORT = URL + "/report";            //新增巡查报告回复 回访  验收
    private String IMAGE_UPLOAD = URL + "/report/attach/mobile";  //图片上传
    private String DICTIONARY_LIST = URL + "/dictionary/list";   //获取报告类型
    private String GET_PATROL_ADDRESS = URL + "/patrol/addresses";//获取巡查报告地点


    private String GET_CAR_DAY_FLOW = URL + "/mcFlow/getDayFlow";     //路段渣土车的日流量查询
    private String GET_ARCH_MONTH_FLOW = URL + "/mcFlow/getArchMonthFlow"; //路段月度渣土车的日流量查询
    private String GET_ALARM_DATA = URL + "/mcFlow/getAlarmData";     //路段月度渣土车流量对比
    private String REC_FOR_MOBILE = URL + "/manualrec/recForMobile";   //保存渣土车的识别结果
    private String GET_UNREC_LIST = URL + "/manualrec/unRecList";   //查询所有未识别的渣土车列表
    private String GET_TRACK_LIST = URL + "/track/list";//获取渣土车列表
    private String GET_PHOTO_LIST = URL + "/track/getPhotoList";//获取卡口摄像机抓拍图片列表(获取取证图片用下一个接口)
    private String GET_EVIDENCE_PHONE_LIST = URL + "/track/getEvidencePhotoList";//获取人工取证列表
    private String GET_TRACK_EVIDENCE_DATE_LIST = URL + "/track/getEvidenceDateList/";//获取渣土车车辆轨迹可选时间列表
    private String GET_MAP_MARKERS = URL + "/track/getMapMarkers"; //获取车辆轨迹信息
    private String UPLOAD_PHOTOS = URL + "/track/uploadPhotos"; //人工图片上传
    private String TRACK_ADD_PHOTO = URL + "/track/addPhoto";//人工取证上传信息
    private String GET_USERTRACK_LIST = URL + "/userTrack/list";  //获取执行中的任务的所有人员
    private String GET_FINDBY_USERID_TASKID = URL + "/userTrack/findByUserIdAndTaskId";//巡查人员详细信息
    private String GET_PATROL_PLAN_PAGING = URL + "/patrol/plan/paging"; //获取巡查计划列表
    private String PATROL_PLAN_THROUGHT = URL + "/patrol/plan/through"; //巡查计划审批通过
    private String PATROL_PLAN_REFUSE = URL + "/patrol/plan/refuse"; //巡查计划审批通过
    private String PATROL_PLAN_COMMIT = URL + "/patrol/plan/tasks/commit";//提交巡查计划
    private String GET_PATROLTASK_LIST = URL + "/patrolTask/list";//获取巡查任务列表
    private String GET_PATROLTASK_LIST_ALL = URL + "/patrolTask/listAll"; //查询巡查计划创建的所有任务列表
    private String ADD_PATROLTASK = URL + "/patrolTask/save";   //新增巡查任务
    private String PATROL_TASK_FINDONE = URL + "/patrolTask/findOne"; //获取一个任务详情
    private String UPDATE_TASK_START = URL + "/patrolTask/updateTaskStart"; //巡查任务开始
    private String UPDATE_TASK_END = URL + "/patrolTask/executeTask"; //巡查任务开始
    private String UPDATE_PATROL_POSITION_STATUS = URL + "/patrolTask/updatePatrolPositionStatus"; //巡查点完成接口
    private String USER_TRACK = URL + "/userTrack";  //上报巡查点位
    private String GET_LOCALUSER_ALL = URL + "/localUser/findUserAll";  //获取巡查人员列表
    private String QUERYPENDING_PLAN = URL + "/message/queryPendingPlan"; //查询待处理消息数量
    private String FEEDBACK = URL + "/feedback";//用户反馈


    private String GET_PATROL_REPORT_DATA = URL + "/patrolReport/getPatrolReportData";//单位月度任务排名
    private String GET_DEPARTMENT_USER_TASK_DATA = URL + "/patrolReport/getDepartmentUserTaskData";//单位月度任务排名
    private String GET_DEPARTMENT_MONTH_DAT = URL + "/patrolReport/getDepartmentMonthData";//单位月度任务排名
    private String GET_DEPARTMENTS_MONTH_TASKS = URL + "/patrolReport/getDepartmentsMonthTasks";//单位月度任务排名
    private String GET_DEPARTMENT_REPORT = URL + "/patrolReport/getDepartmentReport";//单位月度任务排名
    public static boolean mVideoIsLogin = false;


    public static LoginBean mLoginBean = null;
    public static CookiesManager mCookiesManager = null;
    public static ArrayList<CompanyBean> companyNameList;

    public HttpPost() {
        if (mClient == null) {
            mCookiesManager = new CookiesManager(App.getAppContext());
            mClient = new OkHttpClient.Builder()
                    .cookieJar(mCookiesManager)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
        }
    }

    public static <T> ArrayList<T> stringToList(String json, Class<T> cls) {
        Gson gson = new Gson();
        ArrayList<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        for (final JsonElement elem : array) {
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }

    public static  void autoLogin(){
        HttpPost httpPost = new HttpPost();
        if(mLoginBean != null){}
        httpPost.login(mLoginBean.getmName(),mLoginBean.getmPassword(),mLoginBean.getRegisterId());
    }


    public static boolean isConnected() {
        return NetworkUtils.isConnected();
    }


    /*
    登录专用接口，传入用户名，密码，以及设备ID号
     */
    public LoginBean login(String username, String password, String mobileDeviceId) {

        mCookiesManager.removeAll();

        if (mLoginBean == null) {
            mLoginBean = new LoginBean();
        }

        LoginBean loginBean = UserLogin.login(LOGIN_URL, mClient, username, password, mobileDeviceId);
        if (loginBean != null) {
            if (loginBean.isLoginSuccess()) {
                mLoginBean.setLoginSuccess(true);
                mLoginBean.setmName(username);
                mLoginBean.setmPassword(password);
                mLoginBean.setRegisterId(mobileDeviceId);
            } else {
                mLoginBean.setmErrorCode(loginBean.getmErrorCode());
            }
        }
        return mLoginBean;
    }


    /*
     区域月度综合排名
   */
    public EQIRankingBean eqiDataRanking(String archId, String time) {
        return EQIMonitoring.eqiDataRanking(EQI_DATA_RANKING, mClient, archId, time);
    }


    /*
     区域月度数据对比
     */
    public MonthlyComparisonBean carchMonthlyComparison(String archId, String time, String type) {
        return EQIMonitoring.carchMonthlyComparison(EQI_DATA_COMPARISON, mClient, archId, time, type);
    }

    /*
    优良天数占比
    "29","2017-10"
     */
    public ArrayList<WeatherConditionBean> getWeatherConditionDay(String archId, String time) {
        return EQIMonitoring.getWeatherConditionDay(EQI_DAYS_PROPORTION, mClient, archId, time);
    }

    /*
    2.2	单设备PM数据列表  "[1,2]","0","2017-10-01 00:00:00","2017-10-11 00:00:00"
    */
    public ArrayList<DataQueryVoBean> onePMDevicesDataList(String deviceIdsStr, String dataType, String beginTime, String endTime) {
        return EQIMonitoring.onePMDevicesDataList(EQI_LIST, mClient, deviceIdsStr, dataType, beginTime, endTime);
    }

    public DataQueryVoBeanPage onePMDevicesDataListPage(String deviceIdsStr, String dataType, String beginTime, String endTime,PageableBean pageableBean) {
        return EQIMonitoring.onePMDevicesDataListPage(EQI_LIST, mClient, deviceIdsStr, dataType, beginTime, endTime,pageableBean);
    }

    /*
    2.3	单设备PM历史数据    测试数据 "1"
     */
    public ArrayList<DataQueryVoBean> getOneDevicesHistoryData(String id) {
        return EQIMonitoring.getOneDevicesHistoryData(EQI_BYDEVICE_HISTORY, mClient, id);
    }


    /*
    2.5	单设备某天24小时数据  测试数据 "2","2017-10-10 10:10:10"
     */

    public ArrayList<DataQueryVoBean> onePMDevices24Data(String deviceIdsStr, String pushTime) {
        return EQIMonitoring.onePMDevices24Data(EQI_BYDEVICE_DAYS, mClient, deviceIdsStr, pushTime);
    }

    /*
    //获取设备列表—文昊炅  测试 "","","",""
     */
    public ArrayList<DevicesBean> getDevices(String deviceType, String deviceName, String archId, String deviceStatus) {
        return EQIMonitoring.getDevicesList(ESS_DEVICE_LIST, mClient, deviceType, deviceName, archId, deviceStatus);
    }
    public DevicesBeanPage getDevicesListPage(String deviceType, String deviceName, String archId, String deviceStatus, PageableBean pageableBean) {
        return EQIMonitoring.getDevicesListPage(ESS_DEVICE_LIST, mClient, deviceType, deviceName, archId, deviceStatus,pageableBean);
    }

    /*
     //获取消息列表   测试"","","","2"
     */
    public ArrayList<MessageBean> getMessage(String title, String type, String status, String module) {

        return MessageOperation.getMessage(MESSAGE_LIST, mClient, title, type, status, module);
    }

    public MessageBeanPage getMessagePage(String title, String type, String status, String module,PageableBean pageableBean) {

        return MessageOperation.getMessagePage(MESSAGE_LIST, mClient, title, type, status, module,pageableBean);
    }

    /*
    消息阅读已经完成
     */
    public ResultBean readMessage(String id) {
        return MessageOperation.readMessage(MESSAGE_ID_READ, mClient, id);
    }

    /*
     2.2	获取视频配置
     */
    public boolean getVideoConfig() {
        boolean flag = false;
        LoginBean.VideoParameter videoParameter = UserLogin.getVideoConfig(GET_VIDEO_CONFIG, mClient);
        if (videoParameter != null) {
            mLoginBean.setmVideoParameter(videoParameter);
            flag = true;
        }
        return flag;
    }

    /*
     获取巡查报告列表  测试数据 1
     */
    public ArrayList<PatrolBean> getPatrolReportList(String status, String address,PageableBean pageableBean) {
        String departmentId = "";
        if (mLoginBean != null && mLoginBean.getmUserBean() != null) {
            departmentId = mLoginBean.getmUserBean().getLoginUser().getDepartmentId();
        }
        return ReportOperation.getPatrolReportList(PATROL_LIST, mClient, status, address,departmentId, pageableBean);
    }

    /*
    获取验收报告列表
     */
    public ArrayList<PatrolBean> getCheckReportList(String status, String address,PageableBean pageableBean) {
        return ReportOperation.getCheckReportList(PATROL_LIST, mClient,status, address,pageableBean);
    }

    /*
    新增巡查报告
     */
    public PatrolBean addPatrolReport(PatrolBean reportBean) {
        return ReportOperation.addPatrolReport(ADD_PATROL_REPORT, mClient, reportBean);
    }


    /*
    获取一个巡查报告   测试"75"
     */
    public PatrolBean getPatrolReport(String id) {
        return ReportOperation.getPatrolReport(GET_PATROL_REPORT, mClient, id);
    }

    /*
    新增巡查回访
     */
    public ResultBean addPatrolVisit(ReportBean reportBean) {
        return ReportOperation.addPatrolVisit(ADD_REPORT, mClient, reportBean);
    }

    /*
    新增巡查回复
     */
    public ResultBean addPatrolReply(ReportBean reportBean) {
        return ReportOperation.addPatrolReply(ADD_REPORT, mClient, reportBean);
    }

    /*
    新增巡查验收
     */
    public ResultBean addPatrolCheck(ReportBean reportBean) {
        return  ReportOperation.addPatrolCheck(ADD_REPORT, mClient, reportBean);
    }

    /*
    报告上传文件
     */
    public ResultBean reportFileUpload(String filepath, int id) {
        return ReportOperation.reportFileUpload(IMAGE_UPLOAD, mClient, filepath, id);
    }


    //下载报告图片，需要传入id和服务器获取的到路径
    public long downloadReportFile(int id, String filename) {

        String url = getFileUrl(filename);
        String name = getFileName(filename);
        String sdpath = Environment.getExternalStorageDirectory().getPath();
        String storagePath = "/isoftstone/" + mLoginBean.getmName() + "/report/" + id;
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.exists())
            Log.i("Test", "url  " + url + "  storagePath  " + storagePath + " name " + name + "   " + getReportPath(id, filename));
        return ReportOperation.downloadfile(url, storagePath, name);
    }

    /*
    获取用户信息
    */
    public UserBean getLoginUser() {
        UserBean userBean = null;
        if (mLoginBean != null) {
            if (mLoginBean.getmUserBean() != null) {
                userBean = UserLogin.getLoginUserById(GET_LOGIN_USER_BYID + mLoginBean.getmUserBean().getLoginUser().getId(), mClient);
            } else {
                BaseUserBean user = new BaseUserBean();
                user.setAccount(mLoginBean.getmName());
                user.setPassword(mLoginBean.getmPassword());
                userBean = UserLogin.getLoginUser(GET_LOGIN_USER, mClient, user);
            }
        }
        mLoginBean.setmUserBean(userBean);
        return userBean;
    }

    //更改用户信息
    public ResultBean userUpdate(BaseUserBean userBean) {
        return  UserLogin.userUpdate(USER_UPDATE, mClient, userBean);
    }

    //上传用户头像
    public ResultBean userImageUpload(Bitmap bit, Bitmap.CompressFormat format) {
        return UserLogin.userImageUpload(USER_UPLOAD, mClient, bit, format);
    }

    //下载用户图片  服务器获取的到路径
    public void downloadUserImage(String filename) {

        String url = getFileUrl(filename);
        String name = getFileName(filename);
        String sdpath = Environment.getExternalStorageDirectory().getPath();
        String storagePath = "/isoftstone/" + mLoginBean.getmName() + "/usericon";
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (file.exists())
            Log.i("Test", "url  " + url + "  storagePath  " + storagePath + " name " + name + "   " + getImagePath(filename));
        ReportOperation.downloadfile(url, storagePath, name);
    }

    /*
    获取主界面数据
     */
    public MobileHomeBean getMobileHomeData() {
        return UserLogin.getMobileHomeData(MOBILE_HOME, mClient);
    }


    //获取下载文件、图片的URL
    public String getFileUrl(String filename) {
        if (filename != null) {
            filename = filename.replaceAll("\\\\", "/");
        }
        return URL + "/" + filename;
    }

    //获取用户图片本地保存绝对路径
    public String getImagePath(String imageName) {

        String sdpath = Environment.getExternalStorageDirectory().getPath();
        String storagePath = sdpath + "/isoftstone/" + mLoginBean.getmName() + "/usericon/" + getFileName(imageName);
        return storagePath;
    }

    //获取报告图片、文件本地保存绝对路径
    public String getReportPath(int id, String imageName) {

        String sdpath = Environment.getExternalStorageDirectory().getPath();
        String storagePath = sdpath + "/isoftstone/" + mLoginBean.getmName() + "/report/" + id + "/" + getFileName(imageName);
        return storagePath;
    }


    private String getFileName(String filename) {
        int index = filename.lastIndexOf("\\");
        if (index > 0) {
            return filename.substring(index + 1);
        }
        return filename;
    }

    /*
    获取巡查类型列表
     */
    public ArrayList<DictionaryBean> getDictionaryList(String lang) {
        return ReportOperation.getDictionaryList(DICTIONARY_LIST, mClient, lang);
    }

    /*
    获取巡查地址
     */
    public ArrayList<String> getPatrolAddress() {
        return ReportOperation.getPatrolAddress(GET_PATROL_ADDRESS, mClient);
    }

    /*
    获取版本信息，包含版本号、版本下载绝对路径、是否强制更新
     */
    public InstallBean getSystemConifg() {
        return UserLogin.getSystemConifg(GET_SYSTEM_CONFIG, mClient);
    }

    /*
    获取公司列表
     */
    public ArrayList<CompanyBean> getCompanyList(String lang) {
        if(companyNameList == null || companyNameList.size() == 0){
            companyNameList = UserLogin.getCompanyList(DICTIONARY_LIST, mClient, "zh");
        }
        return companyNameList;
    }

    /*
    通过公司id获取公司名称   对应用户为部门id
     */
    public String getCompanyNameByid(int id) {
        String companyName = "未找到公司，ID为："+id;
        if (companyNameList != null) {
            for (int i = 0; i < companyNameList.size(); i++) {
                CompanyBean companyBean = companyNameList.get(i);
                if (companyBean.getValue().equals(id + "")) {
                    companyName = companyBean.getContent();
                    break;
                }
            }
        }
        return companyName;
    }

    /*
    路段渣土车日、月流量查询
     */
    public ArrayList<CarInfoBean> getDayFlow(String time, String parentId, String timeMonth, int flag) {
        Log.e("test", parentId + " " + time + "  " + timeMonth + "  " + flag);
        return MuckCarOperation.getDayFlow(GET_CAR_DAY_FLOW, mClient, time, parentId, timeMonth, flag);
    }

    /*
    路段月度渣土车的日流量查询
     */
    public ArchMonthFlowBean getArchMonthFlow(String time, String timeMonth, Long archId, int flag) {
        //
        Log.e("test", archId + "  " + time + "  " + timeMonth + " " + flag);
        return MuckCarOperation.getArchMonthFlow(GET_ARCH_MONTH_FLOW, mClient, time, timeMonth, archId, flag);
    }
    /*
    获取渣土车流量路段对比
     */
    public ArchMonthFlowBean getAlarmData(String time, String timeMonth, long[] archIds, int flag) {
        //
        return MuckCarOperation.getAlarmData(GET_ALARM_DATA, mClient, time, timeMonth, archIds, flag);
    }

    public ResultBean recForMobile(String carLicence, int recResult) {
        return  MuckCarOperation.recForMobile(REC_FOR_MOBILE, mClient, carLicence, recResult);
    }

    /*
    获取未识别的渣土车列表
    目前缺陷  page从1开始
    licence 可以为空
     */
    public BayonetGrabInfoBeanPage getUnRecList(String licence, PageableBean pageableBean) {
        return MuckCarOperation.getUnRecList(GET_UNREC_LIST, mClient, licence, pageableBean);
    }

    /*
    获取渣土车列表
     licence 可以为空
     */
    public BayonetGrabInfoBeanPage getTrackList(String licence, PageableBean pageableBean) {
        return MuckCarOperation.getTrackList(GET_TRACK_LIST, mClient, licence, pageableBean);
    }

    /*
    获取摄像头抓图片列表   不支持分页
    licence 车牌号  不能
     */
    public ArrayList<EvidencePhotoBean> getPhontoList(String licence, String photoType, String takePhotoTime, String deviceCoding) {
        return MuckCarOperation.getPhontoList(GET_PHOTO_LIST, mClient, licence, photoType, takePhotoTime, deviceCoding);
    }

    /*
    获取车辆人工抓拍记录
     */
    public EvidencePhotoBeanPage getEvidencePhotoList(String licence, PageableBean pageableBean) {
        return MuckCarOperation.getEvidencePhotoList(GET_EVIDENCE_PHONE_LIST, mClient, licence, pageableBean);
    }

    /*
    获取车辆轨迹可选时间列表
     */
    public ArrayList<String> getEvidenceDateList(String licence) {
        return MuckCarOperation.getEvidenceDateList(GET_TRACK_EVIDENCE_DATE_LIST, mClient, licence);
    }

    /*
    获取渣土车抓拍轨迹
     */
    public ArrayList<MapMarkersVoBean> getMapMarkers(String licence, String takePhotoTime) {
        return MuckCarOperation.getMapMarkers(GET_MAP_MARKERS, mClient, licence, takePhotoTime);
    }

    /*
    上传图片
    文件列表需要使用绝对路径
     */
    public String uploadPhotos(ArrayList<String> list) {
        return MuckCarOperation.uploadPhotos(UPLOAD_PHOTOS, mClient, list);
    }

    /*

     */
    public EvidencePhotoBean addPhoto(UpdatePhotoInfoBean updatePhotoInfoBean) {
        return MuckCarOperation.addPhoto(TRACK_ADD_PHOTO, mClient, updatePhotoInfoBean);
    }

    /*
    11.1.	巡查人员实时监控
    获取正在执行任务的人员列表
     */
    public ArrayList<UserTrackBean> getUserTrack() {
        return PatrolUserOperation.getUserTrack(GET_USERTRACK_LIST, mClient);
    }

    /*
     获取任务人员轨迹信息
     taskId   必填字段
     userId   必填字段
     */
    public ArrayList<UserTrackBean> findByUserIdAndTaskId(UserTrackBean userTrackBean) {
        return PatrolUserOperation.findByUserIdAndTaskId(GET_FINDBY_USERID_TASKID, mClient, userTrackBean);
    }

    /*
    获取巡查计划列表
     */
    public PatrolPlanBeanPage getPlanPaging(PatrolPlanBean patrolPlanBean, PageableBean pageableBean,String sort) {
        return PatrolPlanOperation.getPlanPaging(GET_PATROL_PLAN_PAGING, mClient, patrolPlanBean, pageableBean,sort);
    }

    /*
    计划审批通过 计划ID非常重要
     */
    public ResultBean planThrough(PatrolPlanBean patrolPlanBean) {
        return PatrolPlanOperation.planThrough(PATROL_PLAN_THROUGHT, mClient, patrolPlanBean);
    }

    /*
    计划审批拒绝  计划ID非常重要
     */
    public ResultBean planRefuse(PatrolPlanBean patrolPlanBean) {
        return PatrolPlanOperation.planRefuse(PATROL_PLAN_REFUSE, mClient, patrolPlanBean);
    }

    /*
    计划提交，目前存在问题
     */
    public ResultBean patrolPlanCommit(PatrolPlanCommitBean patrolPlanCommitBean) {
        return PatrolPlanOperation.patrolPlanCommit(PATROL_PLAN_COMMIT, mClient, patrolPlanCommitBean);
    }

    /*
    获取巡查任务列表
     */
    public PatrolTaskBeanPage getPatrolTaskList(Long userId, String taskName, String address, String taskTimeStart, String taskTimeEnd, PageableBean pageableBean) {
        return PatrolTaskOperation.getPatrolTaskList(GET_PATROLTASK_LIST, mClient, userId, taskName, address, taskTimeStart, taskTimeEnd, pageableBean);
    }


    /*
    新增巡查任务
     */
    public PatrolTaskBean patrolTaskSave(PatrolTaskBean patrolTaskBean) {
        return PatrolTaskOperation.patrolTaskSave(ADD_PATROLTASK, mClient, patrolTaskBean);
    }

    /*
    获取一个任务详情
     */
    public PatrolTaskBean patrolTaskFindOne(long taskId) {
        return PatrolTaskOperation.patrolTaskFindOne(PATROL_TASK_FINDONE, mClient, taskId);
    }


    /*
    开始一个巡查任务
     */
    public ResultBean updateTaskStart(long taskId, String taskName) {
        return PatrolTaskOperation.updateTaskStart(UPDATE_TASK_START, mClient, taskId, taskName);
    }

    /*
    结束一个巡查任务
     */
    public ResultBean executeTask(long taskId, String taskName) {
        return PatrolTaskOperation.executeTask(UPDATE_TASK_END, mClient, taskId, taskName);
    }

    /*
    巡查点完成
     */
    public ResultBean updatePatrolPositionStatus(long id, String position) {
        return  PatrolTaskOperation.updatePatrolPositionStatus(UPDATE_PATROL_POSITION_STATUS, mClient, id, position);
    }

    /*
    上报巡查轨迹
     */
    public ResultBean userTrack(long userId, long taskId, double longitude, double latitude) {
        return PatrolTaskOperation.userTrack(USER_TRACK, mClient, userId, taskId, longitude, latitude);
    }


    /*
    查询信息中心待处理数量
     */
    public TaskNumberBean queryPendingPlan() {
        return TaskCenterOperation.queryPendingPlan(QUERYPENDING_PLAN, mClient);
    }


    /*
    用户提交建议
     */
    public ResultBean feedback(long userId, String content) {
        return UserLogin.feedback(FEEDBACK, mClient, userId, content);
    }

    /*
    获取巡查人员列表
     */
    public ArrayList<BaseUserBean> findUserAll() {
        return PatrolTaskOperation.findUserAll(GET_LOCALUSER_ALL, mClient);
    }


    /*
    单位月度任务排名   时间字段不可以空  格式为：yyyy-mm
     */
    public ArrayList<ReportDataBean> getPatrolReportData(String time) {
        return PatrolInfoOperation.getPatrolReportData(GET_PATROL_REPORT_DATA, mClient, time);
    }

    /*
    单位人员月度任务量排名  连个字段都不可以空  时间格式为：yyyy-mm
     */
    public ArrayList<UserTaskCountBean> getDepartmentUserTaskData(String time, String departmentId) {
        return PatrolInfoOperation.getDepartmentUserTaskData(GET_DEPARTMENT_USER_TASK_DATA, mClient, time, departmentId);
    }

    /*
    单位月度任务曲线
     */
    public DepartmentMonthDataBean getDepartmentMonthDat(String time, String departmentId) {
        return PatrolInfoOperation.getDepartmentMonthDat(GET_DEPARTMENT_MONTH_DAT, mClient, time, departmentId);
    }

    /*
   单位月度总任务量对比
     */
    public DepartmentsMonthTasks getDepartmentsMonthTasks(String time, String[] departmentIds) {
        return PatrolInfoOperation.getDepartmentsMonthTasks(GET_DEPARTMENTS_MONTH_TASKS, mClient, time, departmentIds);
    }

    /*
    单位月度回访报告量对比
     */
    public DepartmentsMonthTasks getDepartmentReport(String time, String[] departmentIds) {
        return PatrolInfoOperation.getDepartmentReport(GET_DEPARTMENT_REPORT, mClient, time, departmentIds);
    }

    /*
    查询巡查计划创建的所有任务列表
     */
    public ArrayList<PatrolTaskBean> getPatrolTaskListAll(String userId, String planId, String planStatus, String taskType, String taskStatus, String taskTimeStart, String taskTimeEnd, PageableBean pageableBean) {
        return PatrolTaskOperation.getPatrolTaskListAll(GET_PATROLTASK_LIST_ALL, mClient, userId, planId, planStatus, taskType, taskStatus, taskTimeStart, taskTimeEnd, pageableBean);
    }

    /*
    获取前N条未读消息
     */
    public BeforeNMessageBean getBeforeNMessageList(){
        return MessageOperation.getBeforeNMessageList(MESSAGE_LIST_N,mClient);
    }
}