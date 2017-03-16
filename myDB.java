package com.example.dell.lab8;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by dell on 2016/11/17.
 */
public class myDB extends SQLiteOpenHelper {

    //定义库中表的名字
    private static String TABLE_NAME = "Memo";
    //构造函数，定义数据库文件和版本号
    public myDB(Context context) {
        super(context, "database.db", null, 1);
    }

    //创建存储备忘录数据的表Memo
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table if not exists " + TABLE_NAME
                + " (name varchar(10) primary key, birth varchar(10), gift varchar(10))";
        db.execSQL(CREATE_TABLE);
    }

    //插入操作
    public void insert( String name, String birth, String gift) {
        SQLiteDatabase db = getWritableDatabase();
        //查询要插入的元组的主键是否已经存在
        Cursor se = db.rawQuery("select * from Memo where name='"+name+"'", null);
        int count = se.getCount();
        //如果这个人已经存在，返回
        if(count!=0)
            return;
        //否则就进行插入操作
        else{
            String insert_sql = "insert into " + TABLE_NAME + "(name,birth,gift) values('" + name + "','" + birth + "','" + gift + "');";
            db.execSQL(insert_sql);
            return;
        }

    }
    //更新操作
    public void update( String name, String birth, String gift) {
        SQLiteDatabase db = getWritableDatabase();
        String update_sql=null;
        //如果输入生日不为空，更新生日
        if (birth != null){
            update_sql = "update " + TABLE_NAME + " set birth='" + birth + "' where name='" + name + "';";
            db.execSQL(update_sql);
        }
        //如果输入礼物不为空，更新礼物
        if(gift!=null){
            update_sql = "update " + TABLE_NAME + " set gift='" + gift + "' where name='" + name + "';";
            db.execSQL(update_sql);
        }
    }

    //删除操作，根据点击项的姓名删除，之前有插入，所以这里一定存在，不用判断
    public void delete(String name,String birth,String gift){
        SQLiteDatabase db = getWritableDatabase();
        String delete_sql = "delete from "+TABLE_NAME+" where name=\'"+name+"'";
        db.execSQL(delete_sql);
    }

    //重写onUpgrade,数据库版本更新时用到，这里不用进行操作
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
