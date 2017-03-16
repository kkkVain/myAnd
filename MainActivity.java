package com.example.dell.lab8;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //定义成员变量
    myDB myMemoDB = new myDB(this);
    private  SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //如果是第一次进入应用，先插入一些数据到list和数据库中
        String[] name = new String[]{"zuma", "明天", "嘉欣"};
        final String[] birth = new String[]{"1231", "1122", "0923"};
        String[] gift = new String[]{"二维码", "花花", "修为"};
        for (int i = 0; i < 3; i++) {
            //如果之前已经进入过应用，那么不会插入成功
            myMemoDB.insert(name[i],birth[i],gift[i]);

        }


        SQLiteDatabase db = myMemoDB.getWritableDatabase();
        Cursor se = db.rawQuery("select * from Memo", null);
        //遍历表格，更新listView的数据，并更新数据库
        for(se.moveToFirst();!se.isAfterLast();se.moveToNext()){
            String n = se.getString(0);
            String b = se.getString(1);
            String g = se.getString(2);

            Map<String,Object> temp=new LinkedHashMap<>();
            temp.put("namddde",n);
            temp.put("birth",b);
            temp.put("gift",g);
            //更新List数据
            data.add(temp);
            //更新数据库
            myMemoDB.insert(n,b,g);
        }
        se.close();


   /*     for (int i = 0; i < 3; i++) {
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("name", name[i]);
            temp.put("birth", birth[i]);
            temp.put("gift", gift[i]);
            data.add(temp);
            myMemoDB.insert(name[i],birth[i],gift[i]);

        }*/
        final ListView list = (ListView) findViewById(R.id.myMemo);
        simpleAdapter = new SimpleAdapter(this, data, R.layout.item, new String[]{"name", "birth", "gift"}, new int[]{R.id.name, R.id.birth, R.id.gift});
        list.setAdapter(simpleAdapter);

        //  Toast.makeText(this, "create table if not exists " + "tttt"
        //        + " (name integer primary key, name varchar(10), birth varchar(10), gift varchar(10))", Toast.LENGTH_SHORT).show();

        //点击增加条目按钮，进入第二个页面
        final Button add = (Button) findViewById(R.id.myAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, addClass.class);
                //使用startActivityForResult，接收回调跳转
                MainActivity.this.startActivityForResult(intent, 1);

            }
        });
        //获取联系人列表，相当于全部查找。将电话号码也放在同一个ContentProvider里
       // final Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        //点击list中的某一项
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //寻找自己的dialog布局
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                View myview = factory.inflate(R.layout.clickdialog,null);
                //设定builder样式
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(myview);
                final AlertDialog dialog = builder.show();
                //取出当前被点中的item
                Map<String,String> map = (Map<String,String>)list.getItemAtPosition(position);

                //找到要用的控件
                TextView udname = (TextView)myview.findViewById(R.id.updateName);
                TextView phone = (TextView)myview.findViewById(R.id.phonenumber);
                final EditText udgift = (EditText)myview.findViewById(R.id.addGift);
                final EditText udbirth = (EditText)myview.findViewById(R.id.addBirth);
                //设置姓名
                final String updateName = map.get("name");
                udname.setText(updateName);
             /*   final String number;
                while(cursor.moveToFirst()){
                    String username=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if(username.equals(udname))
                        Toast.makeText(MainActivity.this, username+" ouwer ", Toast.LENGTH_SHORT).show();
                }
                cursor.close();*/


            //    int isHas = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                //找cursor对应的ID
            //    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //将电话号码放在了ContactsContract.CommonDataKinds.Phone.CONTENT_URI
           //     Toast.makeText(MainActivity.this, updateName+" ouwer ", Toast.LENGTH_SHORT).show();

                //查询联系人列表中，名字与item名字相同的元组
                //由于是点击item进来的，所以一定能找到
                String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };
                Cursor cNumber = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                      projection,
                      ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" = '"+ updateName+"'",
                      null,null);
                String number;
                //遍历cursor，找到后设置电话号码
                while(cNumber.moveToNext()) {
                    number = cNumber.getString(cNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + " ";
                  //  Toast.makeText(MainActivity.this, number+" erwrweouwer ", Toast.LENGTH_SHORT).show();
                    phone.setText("电话："+number);
                    break;
                }
                cNumber.close();

                //点击取消修改和保存修改两个按钮
                Button cancel = (Button)myview.findViewById(R.id.myCancel);
                Button save = (Button)myview.findViewById(R.id.mySave);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newGift = udgift.getText().toString();
                        String newBirth = udbirth.getText().toString();
                        //数据库更新
                        myMemoDB.update(updateName, newBirth,newGift);
                        //listView更新
                        data.get(position).put("birth",newBirth);
                        data.get(position).put("gift",newGift);
                        simpleAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

            }
        });



        //长按list某个item,询问是否删除
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                //找到自定义layout,设置为dialog的样式
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View myview = factory.inflate(R.layout.dialog,null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(myview);
                final AlertDialog dialog = builder.show();
                //找到两个按钮控件
                Button no = (Button)myview.findViewById(R.id.myNo);
                Button yes = (Button)myview.findViewById(R.id.myYes);
                //点击后进行响应
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //数据库更新
                        String deleteN = (String)data.get(position).get("name");
                        String deleteB = (String)data.get(position).get("birth");
                        String deleteG = (String)data.get(position).get("gift");

                        myMemoDB.delete(deleteN,deleteB,deleteG);
                        //listView更新
                        data.remove(position);
                        simpleAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                return true;
           //     Map<String,String> map = (Map<String,String>)list.getItemAtPosition(position);
            /*    builder.setTitle("是否删除？");
                builder.setNegativeButton("否",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("是",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.remove(position);
                        simpleAdapter.notifyDataSetChanged();
                    }
                });*/


        //        builder.create().show();


            }
        });
    }
    //重写 onActivityResult，关闭第二个页面时更新数据库和list
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent addintent) {
        //resultcode:B回传的，应该为那边的resultcode
        //requestcode:A请求时发送的码，应该为1
        if(addintent==null)
            return;
        else {
            SQLiteDatabase db = myMemoDB.getWritableDatabase();
            Cursor se = db.rawQuery("select * from Memo", null);
            int nowTotal = se.getCount();
            //找到当前表中的总元组数后跳到该一行
            se.moveToFirst();
            se.moveToPosition(nowTotal - 1);
            //提取该行的数据
            String n = se.getString(0);
            String b = se.getString(1);
            String g = se.getString(2);
            //更新list的数据
            Map<String, Object> temp = new LinkedHashMap<>();
            temp.put("name", n);
            temp.put("birth", b);
            temp.put("gift", g);
            //更新List
            data.add(temp);
            simpleAdapter.notifyDataSetChanged();

            //第一次进来，对的。
         /*   String name = addintent.getStringExtra("name");
            String birth = addintent.getStringExtra("birth");
            String gift = addintent.getStringExtra("gift");

            Map<String,Object> temp=new LinkedHashMap<>();
            temp.put("name",name);
            temp.put("birth",birth);
            temp.put("gift",gift);
            data.add(temp);*/

      //      Toast.makeText(this, n+ " ppp "+b+" hhh "+g, Toast.LENGTH_SHORT).show();
          /*  for(se.moveToFirst();!se.isAfterLast();se.moveToNext()){
                String n = se.getString(0);
                String b = se.getString(1);
                String g = se.getString(2);

                Map<String,Object> temp=new LinkedHashMap<>();
                temp.put("name",n);
                temp.put("birth",b);
                temp.put("gift",g);
                data.add(temp);

                Toast.makeText(this, n+ " ppp "+b+" hhh "+g, Toast.LENGTH_SHORT).show();
            }*/


         //   Toast.makeText(this, " mmmm ", Toast.LENGTH_SHORT).show();

        }
    }


}
