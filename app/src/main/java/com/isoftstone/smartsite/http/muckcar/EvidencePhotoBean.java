package com.isoftstone.smartsite.http.muckcar;

import com.isoftstone.smartsite.User;
import com.isoftstone.smartsite.http.UserBean;

/**
 * Created by gone on 2017/11/16.
 */
/*
取证照片
 */
public class EvidencePhotoBean {
    private String licence;  //车牌号
    private String takePhotoTime; //拍照时间
    private String uploadPeople;  //上传人
    private String photoSrc;    //图片路径
    private String smallPhotoSrc; //
    private String addr;
    private String speed;
    private UserBean takePhoroUser;

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getTakePhotoTime() {
        return takePhotoTime;
    }

    public void setTakePhotoTime(String takePhotoTime) {
        this.takePhotoTime = takePhotoTime;
    }

    public String getUploadPeople() {
        return uploadPeople;
    }

    public void setUploadPeople(String uploadPeople) {
        this.uploadPeople = uploadPeople;
    }

    public String getPhotoSrc() {
        return photoSrc;
    }

    public void setPhotoSrc(String photoSrc) {
        this.photoSrc = photoSrc;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public UserBean getTakePhoroUser() {
        return takePhoroUser;
    }

    public void setTakePhoroUser(UserBean takePhoroUser) {
        this.takePhoroUser = takePhoroUser;
    }

    public String getSmallPhotoSrc() {
        return smallPhotoSrc;
    }

    public void setSmallPhotoSrc(String smallPhotoSrc) {
        this.smallPhotoSrc = smallPhotoSrc;
    }
}
