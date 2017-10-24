package fpl.hongshi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王伟 on 2017/9/27.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "face.db";//数据库名称
    private static final int SCHEMA_VERSION = 2;//版本号,则是升级之后的

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE face (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, feature BLOB)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {//升级判断,如果再升级就要再加两个判断,从1到3,从2到3
            db.execSQL("ALTER TABLE restaurants ADD phone TEXT;");
        }
    }

    public void insert(String name, byte[] date) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("feature", date);
        getWritableDatabase().insert("face", "name", cv);
    }

    public List<FaceRegist> selectAll(SQLiteDatabase db) {
        Cursor cursor = db.query("face",null,null,null,null,null,null);
        List<FaceRegist> faceRegists=new ArrayList<>();
        List<AFR_FSDKFace> mfaceList=new ArrayList<>();
        while (cursor.moveToNext()) {
            AFR_FSDKFace face=new AFR_FSDKFace();
            FaceRegist faceRegist=new FaceRegist();
            String name = cursor.getString(1);
//            byte[] feature = Base64.decode(cursor.getString(2).getBytes(),Base64.DEFAULT);
            byte[] feature = cursor.getBlob(2);
            face.setFeatureData(feature);
            mfaceList.add(face);
            faceRegist.setmName(name);
            faceRegist.setmFaceList(mfaceList);
            faceRegists.add(faceRegist);
        }

        cursor.close();
        db.close();
        return faceRegists;
    }

    public boolean isNull(SQLiteDatabase db){
        Cursor c=db.rawQuery("SELECT * FROM face",null);
        if(c.getCount()==0){
            return true;
        }else {
            return false;
        }
    }

}
