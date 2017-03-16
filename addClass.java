package com.example.dell.lab8;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by dell on 2016/11/17.
 */
public class addClass extends AppCompatActivity {
    //定义成员变量
    myDB myMemoDB = new myDB(this);
    private static String TABLE_NAME = "Memo";
    private String addname;
    private String addbirth;
    private String addgift;

    // chage it NOW for git
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new);

        //找到三个EditText
        final EditText name = (EditText) findViewById(R.id.addName);
        final EditText birth =(EditText) findViewById(R.id.addBirth);
        final EditText gift = (EditText)findViewById(R.id.addGift);
        //获取SQLiteDatabase实例对数据库进行读写操作
        final SQLiteDatabase db = myMemoDB.getWritableDatabase();



        String[] columns = new String[] {"name", "birth","gift"};
        String selection = "name=?";
        String[] selectionArgs = new String[]{"烧萌"};
        Cursor t = db.query(TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        Cursor se=db.rawQuery("select * from Memo",null);
    //    if(se==null)
    //        Toast.makeText(addClass.this, " wer ", Toast.LENGTH_SHORT).show();
    //    else
     //       Toast.makeText(addClass.this, " oooo ", Toast.LENGTH_SHORT).show();
      /*  int cNmae = se.getColumnIndex("name");
        int cBirth = se.getColumnIndex("birth");
        int cGift = se.getColumnIndex("gift");*/

    /*    for(se.moveToFirst();!se.isAfterLast();se.moveToNext()){
            String n = se.getString(0);
            String b = se.getString(1);
            Toast.makeText(this, n+ " ppp "+b, Toast.LENGTH_SHORT).show();
        }
        /*
        while(se.moveToNext())
        {
            String strValue= re.getString(3);
            Toast.makeText(this, strValue, Toast.LENGTH_SHORT).show();
        }*/      //       intent.setClass(addClass.this,MainActivity.class);

        //点击增加按钮
        final Button add = (Button)findViewById(R.id.addCon);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的内容
                addname = name.getText().toString();
                addbirth= birth.getText().toString();
                addgift = gift.getText().toString();

                //判断要增加的名字是否符合要求
                if(addname.equals(""))
                    Toast.makeText(addClass.this, "名字为空,请完善", Toast.LENGTH_SHORT).show();
                else if(!addname.equals("")){
                    //查询数据库，判断是否重复
                    Cursor se = db.rawQuery("select * from Memo where name='"+addname+"'", null);
                    int count = se.getCount();
                    if(count!=0)
                        Toast.makeText(addClass.this, "名字重复啦，请检查", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(addClass.this,MainActivity.class);
                        myMemoDB.insert(addname,addbirth,addgift);
                        int code=2;
                        addClass.this.setResult(code,intent);
                        finish();
                    }
                    se.close();
                }
            }
        });

    }
  /*  @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        Bundle result = new Bundle();
        result.putString("hh","rrr");

        result.putString("name",addname);
        result.putString("gift",addgift);
        result.putString("birth",addbirth);

        setResult(RESULT_OK, intent);
        intent.putExtras(result);
        super.onBackPressed();
    }*/
}
