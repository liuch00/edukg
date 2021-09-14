package com.example.edupkg.data;

import java.util.*;
import java.net.*;
import java.io.*;
import org.json.*;



//实体类Entity，注意在Entity的一些域调用相应的接口后才有意义
//不需要调用Entity的接口，只要读取Entity的数据域即可
//label为实体名，category为实体类型（学科列表中不能得到category），course为实体学科
//property为属性，object为指向该实体的关系，subject为该实体指向的关系，三者均为TreeSet<Entry>，且调用实体详情接口后才有意义
//缓存功能完成，现在各种方式得到的Entity对象的visited域都有意义了
//start_idx,end_idx仅在知识链接中有意义，代表实体在文本中出现的位置

public class Entity{
    final static String SERVER_IP="123.60.58.194";
    final static int MAX_TIME=8000;
    boolean visited=false;
    String label,category,course,predicate,uri;
    TreeSet<Entry> property=null;
    TreeSet<Entry> object=null;
    TreeSet<Entry> subject=null;
    long start_idx,end_idx;
    Vector<Entity> child=new Vector<Entity>();
    Entity(String lab,String cat,String cour,String uuri){
        label=lab;
        category=cat;
        course=cour;
        uri=uuri;
        if(FrontEnd.CachedEntityList.has(lab+":"+cour))visited=true;
    }
    Entity(String lab,String cour){
        label=lab;
        course=cour;
        if(FrontEnd.CachedEntityList.has(lab+":"+cour))visited=true;
    }
    Entity(String lab,String cat,String cour,long left,long right){
        label=lab;
        category=cat;
        course=cour;
        start_idx=left;
        end_idx=right;
        if(FrontEnd.CachedEntityList.has(lab+":"+cour))visited=true;
    }
    Entity(String lab,String cour,boolean v){
        label=lab;
        course=cour;
        visited=v;
    }

    Entity(String lab,String cour,String pre,boolean v2){
        label=lab;
        course=cour;
        predicate=pre;
    }
    Entity(String lab,String cour,String pre,boolean v2,boolean v3){
        label=lab;
        course=cour;
        uri=pre;
        visited=true;
    }
    Entity searchForMore(String token){
        visited=true;
        try {
            String url=new String("http://"+SERVER_IP+":8080/service/entity-detail?course="+course+"&name="+java.net.URLEncoder.encode(label,"utf-8"));
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestProperty("authorization", token);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                property=new TreeSet<Entry>();
                object=new TreeSet<Entry>();
                subject=new TreeSet<Entry>();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                JSONObject jsobj=new JSONObject(result);
                uri=jsobj.getString("uri");
                FrontEnd.addCachedEntityLists(this.label, this.course, token);
                JSONArray prop=jsobj.getJSONArray("property");
                for(int i=0;i<prop.length();i++) {
                    JSONObject entry=prop.getJSONObject(i);
                    property.add(new Entry(entry.getString("predicateLabel"),entry.getString("object")));
                }
                JSONArray cont=jsobj.getJSONArray("content");
                for(int i=0;i<cont.length();i++) {
                    JSONObject entry=cont.getJSONObject(i);
                    String obj=entry.getString("object_label");
                    if(obj!="null") {
                        object.add(new Entry(entry.getString("predicate_label"),obj));
                    }
                    else {
                        subject.add(new Entry(entry.getString("predicate_label"),entry.getString("subject_label")));
                    }
                }
            }
            else throw new Exception();
            return this;
        }
        catch(Exception e) {
            return FrontEnd.searchByCache(this.label,this.course);
        }
    }
}
