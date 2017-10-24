package fpl.hongshi;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.List;

/**
 * 作者 王伟
 * 公司 深圳菲普莱体育
 * 密级 绝密
 * Created on 2017/9/28.
 */

public class FaceRegist {


    private String mName;
    private List<AFR_FSDKFace> mFaceList;

    public FaceRegist() {
    }

    public FaceRegist(String mName, List<AFR_FSDKFace> mFaceList) {
        super();
        this.mName = mName;
        this.mFaceList = mFaceList;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public List<AFR_FSDKFace> getmFaceList() {
        return mFaceList;
    }

    public void setmFaceList(List<AFR_FSDKFace> mFaceList) {
        this.mFaceList = mFaceList;
    }

    @Override
    public String toString() {
        return "FaceRegist{" +
                "mName='" + mName + '\'' +
                ", mFaceList=" + mFaceList +
                '}';
    }
}
