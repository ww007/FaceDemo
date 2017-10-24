package fpl.hongshi;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by ww on 2017/9/7.
 */

public class SoundPlayUtils {
    // SoundPool对象
    public static SoundPool mSoundPlayer = new SoundPool(10,
            AudioManager.STREAM_SYSTEM, 5);
    public static SoundPlayUtils soundPlayUtils;
    // 上下文
    static Context mContext;

    /**
     * 初始化
     *
     * @param context
     */
    public static SoundPlayUtils init(Context context) {
        if (soundPlayUtils == null) {
            soundPlayUtils = new SoundPlayUtils();
        }

        // 初始化声音
        mContext = context;

        mSoundPlayer.load(mContext, R.raw.pass, 1);// 1
        mSoundPlayer.load(mContext, R.raw.fail, 1);// 2
//        mSoundPlayer.load(mContext, R.raw.diang, 1);// 3
//        mSoundPlayer.load(mContext, R.raw.ding, 1);// 4
//        mSoundPlayer.load(mContext, R.raw.gone, 1);// 5
//        mSoundPlayer.load(mContext, R.raw.popup, 1);// 6
//        mSoundPlayer.load(mContext, R.raw.water, 1);// 7
//        mSoundPlayer.load(mContext, R.raw.ying, 1);// 8

        return soundPlayUtils;
    }

    /**
     * 播放声音
     *
     * @param soundID
     */
    public static void play(int soundID) {
        mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
    }

    public static void release(){
        mSoundPlayer.release();
    }

}
