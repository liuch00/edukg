package com.example.edupkg.data;


import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.*;
import java.net.*;
import java.io.*;
import org.json.*;


//创建一个new FrontEnd()对象来使用接口，调用该对象的函数，使用其他功能前先使用logIn或signUp登录
//只需要调用接口，不必关心数据

public class FrontEnd  {

    //数据域

    final static String SERVER_IP= Entity.SERVER_IP;
    final static int MAX_TIME=Entity.MAX_TIME;
    final static int MAX_CACHE=500;
    private static Vector<String> subjects=new Vector<String>();
    private  String token="";
    static JSONObject CachedSubjectList,CachedEntityList;
    static JSONArray CachedRates;
    final static String SubjectFileName="/mnt/sdcard/cache"+File.separator+"SubjectLists.json";
    final static String EntityFileName="/mnt/sdcard/cache"+File.separator+"EntityLists.json";
    final static String SubjectListFileName="/mnt/sdcard/cache"+File.separator+"Subjects.json";
    final static String RateFileName="/mnt/sdcard/cache"+File.separator+"Rate.json";
    final HashMap<String,String> etoc=new HashMap<String,String>(){
        {
            put("chinese","语文");
            put("math","数学");
            put("english","英语");
            put("physics","物理");
            put("chemistry","化学");
            put("history","历史");
            put("geo","地理");
            put("biology","生物");
            put("politics","政治");
        }
    };


    //构造函数和内部方法（不需要调用）
    FrontEnd() {
    }
    static void  updateCachedSubjectLists() {
        File f=new File(SubjectFileName);
        try {
            FileOutputStream fos=new FileOutputStream(f);
            BufferedOutputStream bos=new BufferedOutputStream(fos);
            String result=CachedSubjectList.toString();
            bos.write(result.getBytes(),0,result.getBytes().length);
            bos.close();
        }
        catch(Exception e) {}
    }
    static void  addCachedRates(int crate) {
        File f=new File(RateFileName);
        try {
            CachedRates.put(crate);
            FileOutputStream fos=new FileOutputStream(f);
            BufferedOutputStream bos=new BufferedOutputStream(fos);
            String result=CachedRates.toString();
            bos.write(result.getBytes(),0,result.getBytes().length);
            bos.close();
        }
        catch(Exception e) {}
    }
    static void addCachedEntityLists(String label,String subject,String token) {
        try {
            String entity=label+":"+subject;
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/service/history");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("authorization", token);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String params="{\"course\":\""+subject+"\",\"label\":\""+label+"\"}";
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            if(CachedEntityList.has(entity))return;
            FrontEnd fe=new FrontEnd();
            fe.token=token;
            Vector<Entity> ve=fe.search(subject,label);
            String result="";
            if(!ve.isEmpty())result=ve.elementAt(0).uri;
            CachedEntityList.put(entity,result);
            if(CachedEntityList.length()>MAX_CACHE)
                CachedEntityList.remove(CachedEntityList.keys().next().toString());
            updateCachedEntityLists();

        }
        catch(Exception e) {}
    }
    static Entity searchByCache(String label,String subject) {
        try {
            Entity e=new Entity(label,subject);
            e.property=new TreeSet<Entry>();
            e.object=new TreeSet<Entry>();
            e.subject=new TreeSet<Entry>();
            String entity=label+":"+subject;
            if(!CachedEntityList.has(entity))return null;
            else {
                String result=CachedEntityList.getString(entity);
                JSONObject jsobj=new JSONObject(result);
                JSONArray prop=jsobj.getJSONArray("property");
                for(int i=0;i<prop.length();i++) {
                    JSONObject entry=prop.getJSONObject(i);
                    e.property.add(new Entry(entry.getString("predicateLabel"),entry.getString("object")));
                }
                JSONArray cont=jsobj.getJSONArray("content");
                for(int i=0;i<cont.length();i++) {
                    JSONObject entry=cont.getJSONObject(i);
                    String obj=entry.getString("object_label");
                    if(obj!="null") {
                        e.object.add(new Entry(entry.getString("predicate_label"),obj));
                    }
                    else {
                        e.subject.add(new Entry(entry.getString("predicate_label"),entry.getString("subject_label")));
                    }
                }
            }
            e.visited=true;
            return e;
        }
        catch(Exception ex) {return null;}
    }
    static void updateCachedEntityLists() {
        File f=new File(EntityFileName);
        try {
            FileOutputStream fos=new FileOutputStream(f);
            BufferedOutputStream bos=new BufferedOutputStream(fos);
            String result=CachedEntityList.toString();
            bos.write(result.getBytes(),0,result.getBytes().length);
            bos.close();
        }
        catch(Exception e) {}
    }
    static void updateCachedSubjects() {
        File f=new File(SubjectListFileName);
        try {
            FileOutputStream fos=new FileOutputStream(f);
            BufferedOutputStream bos=new BufferedOutputStream(fos);
            JSONArray subjectJSON=new JSONArray(subjects);
            String result=subjectJSON.toString();
            bos.write(result.getBytes(),0,result.getBytes().length);
            bos.close();
        }
        catch(Exception e) {}
    }



    //学科列表相关接口，Vector<String>类型的学科列表，

    //获取学科列表
    Vector<String> getSubjects() {
        return subjects;
    }

    //添加学科
    Vector<String> addSubject(String sub) {
        subjects.add(sub);
        updateCachedSubjects();
        return subjects;
    }

    //删除学科
    Vector<String> deleteSubject(String sub) {
        subjects.remove(sub);
        updateCachedSubjects();
        return subjects;
    }


    //第一次创建先调用init()
    public void init(){
        File f=new File(SubjectFileName);
        File fileParent = f.getParentFile();
        if (!fileParent.exists()) {
            Log.d("debugLog-0:",fileParent.toString());
            fileParent.mkdirs();
        }
//        else{
//            if(fileParent.listFiles().length==0)
//                Log.d("debugLog-01:","null");
//           for(File fl:fileParent.listFiles())
//                Log.d("debugLog-01:",fl.toString());
//        }
        HashMap<String,String> hm=new HashMap<String,String>();
        hm.put("null","null");
        try {
            if (!f.exists())
                f.createNewFile();
            CachedSubjectList=new JSONObject(hm);
            //Log.d("debugLog-00:",CachedSubjectList.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String result=reader.readLine();
            if(result!=null) {
                CachedSubjectList=new JSONObject(result);
            }
            reader.close();
        }
        catch(Exception e) {Log.d("debugLog-exception:",e.toString());}
        f=new File(EntityFileName);
        try {
            if (!f.exists())
                f.createNewFile();
            CachedEntityList=new JSONObject(hm);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String result=reader.readLine();
            if(result!=null) {
                CachedEntityList=new JSONObject(result);
            }
            reader.close();
        }
        catch(Exception e) {}
        f=new File(SubjectListFileName);
        try {
            if (!f.exists())
                f.createNewFile();
            JSONArray jsa=new JSONArray();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String result=reader.readLine();
            if(result!=null) {
                jsa=new JSONArray(result);
                for(int i=0;i<jsa.length();i++) {
                    subjects.add(jsa.getString(i));
                }
            }
            else {
                subjects.add("chinese");
                subjects.add("math");
                subjects.add("english");
            }
            reader.close();
        }
        catch(Exception e) {}
        f=new File(RateFileName);
        try {
            if (!f.exists())
                f.createNewFile();
            CachedRates=new JSONArray();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String result=reader.readLine();
            if(result!=null)
                CachedRates=new JSONArray(result);
            reader.close();
        }
        catch(Exception e) {}
    }


    //用户登录相关接口,logIn登录，signUp注册，参数都为用户名username和密码password，成功后即可调用其他接口
    //logOut登出，可以不用参数，此后调用其他接口会失败

    //登录，参数为用户名username和密码password，成功后返回true，否则返回false
    boolean logIn(String username,String password){
        try {
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/auth/token?username="+username+"&password="+password);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                token = bufferReader.readLine();
                Log.d("debugLog-token:",token.toString());
                return true;
            }
            return false;
        }
        catch(Exception e) {
            Log.d("login",e.toString());
            return false;
        }
    }

    //注册，参数为用户名username和密码password，成功后返回true，否则返回false
    boolean signUp(String username,String password) {
        try {
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/auth/account");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String params="{\"username\":\""+username+"\",\"password\":\""+password+"\"}";
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                token = bufferReader.readLine();
                return true;
            }
            return false;
        }
        catch(Exception e) {
            return false;
        }
    }

    //登出
    void logOut() {
        token="";
    }

    //判断是否登录，返回true表示已登陆
    boolean isLogin()
    {
        return !token.isEmpty();
    }


    //功能接口

    //搜索接口，需传入学科subject和关键词key
    //返回null表示搜索失败，返回空vector表示搜索结果为空
    //正常返回结果中的Entity对象只有label,category,course和visited域有效
    Vector<Entity> search(String subject,String key){
        Vector<Entity> l=null;
        try {
            String url=new String("http://"+SERVER_IP+":8080/service/entity-list-by-search?course="+subject+"&key="+java.net.URLEncoder.encode(key,"utf-8"));
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestProperty("authorization", token);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                l=new Vector<Entity>();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                JSONArray jsarr=new JSONArray(result);
                for(int i=0;i<jsarr.length();i++) {
                    JSONObject jsobj=jsarr.getJSONObject(i);
                    l.add(new Entity(jsobj.getString("label"),jsobj.getString("category"),subject,jsobj.getString("uri")));
                }
            }
            return l;
        }
        catch(Exception e) {
            System.out.print(e);
            return null;
        }
    }

    //学科分类列表接口，返回的每个Entity只有label，course和visited域有效，category无效，返回值语义和搜索接口相同
    Vector<Entity> searchBySubject(String subject){
        Vector<Entity> l=new Vector<Entity>();
        try {
            Log.d("debugLog-searchBySubject-connection-token:",token.toString());
            String url=new String("http://"+SERVER_IP+":8080/service/entity-list-by-course?course="+subject);
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestProperty("authorization", token);
            connection.connect();

            Log.d("debugLog-searchBySubject-connection:",connection.toString());

            if (connection.getResponseCode() == 200) {
                l=new Vector<Entity>();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                Log.d("debugLog-searchBySubject-info:",in.toString());
                String result = in.readLine();
                JSONArray jsarr=new JSONArray(result);
                CachedSubjectList.put(subject, result);
                for(int i=0;i<jsarr.length();i++)
                    l.add(new Entity(jsarr.getString(i),subject));
                updateCachedSubjectLists();
            }
            else throw new Exception();
            return l;
        }
        catch(Exception e) {
            if(CachedSubjectList==null)Log.d("debugLog-01null:","null");
            if(CachedSubjectList.has(subject)) {
                try {
                    String result=CachedSubjectList.getString(subject);
                    JSONArray jsarr = new JSONArray(result);
                    l=new Vector<Entity>();
                    for(int i=0;i<jsarr.length();i++)
                        l.add(new Entity(jsarr.getString(i),subject));
                    return l;
                }
                catch(Exception ex) {
                    String err = (ex.getMessage()==null)?"null":e.getMessage();
                    Log.d("debugLog-searchBySubject-Exception:",err);
                    ex.printStackTrace();

                    return new Vector<Entity>();}
            }
            else return new Vector<Entity>();
        }
    }

    //实体详情接口：调用后可以读取实体对象的property，object，subject域
    //返回的对象不一定有category类，不要调category
    //property，object，subject域为HashMap类，property为具实体属性，object为指向实体的关系，subject为实体指向的关系
    //调用失败会返回null
    Entity searchForEntity(Entity e){
        return e.searchForMore(token);
    }
    //用实体名称和科目搜索实体详情页
    Entity searchForEntity(String subject,String name) {
        try {
            Entity e=new Entity(name,subject);
            return searchForEntity(e);
        }
        catch(Exception ex) {
            return null;
        }
    }

    //知识链接接口：调用后返回的实体对象course,label,subject,category和left,right有效,返回值语义和搜索相同
    Vector<Entity> LinkKnowlege(String text,String subject){
        Vector<Entity> l=null;
        try {
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/service/instance-linkage");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("authorization", token);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String params="{\"course\":\""+subject+"\",\"text\":\""+text+"\"}";
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                l=new Vector<Entity>();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                JSONArray jsarr=new JSONObject(result).getJSONArray("results");
                for(int i=0;i<jsarr.length();i++) {
                    JSONObject jsobj=jsarr.getJSONObject(i);
                    l.add(new Entity(jsobj.getString("entity"),jsobj.getString("entity_type"),subject,jsobj.getLong("start_index"),jsobj.getLong("end_index")));
                }
            }
            return l;
        }
        catch(Exception e) {
            return null;
        }
    }


    //习题接口，返回Question类的Vector，每个Question有题干body和答案ans
    //习题接口，返回Question类的Vector，每个Question有题干body和答案ans
    Vector<Question> getExcercise(String name){
        Vector<Question> l=new Vector<Question>();
        try {
            String url=new String("http://"+SERVER_IP+":8080/service/exercise?uriName="+java.net.URLEncoder.encode(name,"utf-8"));
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME+6000);
            connection.setReadTimeout(MAX_TIME+6000);
            connection.setRequestProperty("authorization", token);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                l=new Vector<Question>();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                JSONArray jsarr=new JSONObject(result).getJSONArray("results");
                for(int i=0;i<jsarr.length();i++){
                    JSONObject jsobj=jsarr.getJSONObject(i);
                    String body=jsobj.getString("qBody");
                    String A="",B="",C="",D="",qqbody="";
                    int sepA=body.indexOf("A.");
                    if(sepA>0) {
                        if(sepA>0) {
                            qqbody=body.substring(0,sepA);
                            body=body.substring(0,sepA)+"\n"+body.substring(sepA);
                            A=body.substring(sepA);
                        }
                        int sepB=body.indexOf("B.");
                        if(sepB>0) {
                            body=body.substring(0,sepB)+"\n"+body.substring(sepB);
                            A=A.substring(0,A.indexOf("B."));
                            B=body.substring(sepB);
                        }
                        int sepC=body.indexOf("C.");
                        if(sepC>0) {
                            body=body.substring(0,sepC)+"\n"+body.substring(sepC);
                            B=B.substring(0,B.indexOf("C."));
                            C=body.substring(sepC);
                        }
                        int sepD=body.indexOf("D.");
                        if(sepD>0) {
                            body=body.substring(0,sepD)+"\n"+body.substring(sepD);
                            C=C.substring(0,C.indexOf("D."));
                            D=body.substring(sepD);
                            l.add(new Question(body,jsobj.getString("qAnswer"),A,B,C,D,qqbody));
                        }
                        l.add(new Question(body,jsobj.getString("qAnswer")));
                    }
                    else {
                        sepA=body.indexOf("A．");
                        if(sepA>0) {
                            qqbody=body.substring(0,sepA);
                            body=body.substring(0,sepA)+"\n"+body.substring(sepA);
                            A=body.substring(sepA);
                        }
                        int sepB=body.indexOf("B．");
                        if(sepB>0) {
                            body=body.substring(0,sepB)+"\n"+body.substring(sepB);
                            A=A.substring(0,A.indexOf("B．"));
                            B=body.substring(sepB);
                        }
                        int sepC=body.indexOf("C．");
                        if(sepC>0) {
                            body=body.substring(0,sepC)+"\n"+body.substring(sepC);
                            B=B.substring(0,B.indexOf("C．"));
                            C=body.substring(sepC);
                        }
                        int sepD=body.indexOf("D．");
                        if(sepD>0) {
                            body=body.substring(0,sepD)+"\n"+body.substring(sepD);
                            C=C.substring(0,C.indexOf("D．"));
                            D=body.substring(sepD);
                            Question q=new Question(body,jsobj.getString("qAnswer"),A,B,C,D,qqbody);
                            l.add(new Question(body,jsobj.getString("qAnswer"),A,B,C,D,qqbody));
                        }
                        l.add(new Question(body,jsobj.getString("qAnswer")));
                    }
                }
            }
            return l;
        }
        catch(Exception e) {
            System.out.println(e);
            return new Vector<Question>();
        }
    }


    Vector<Question> getRandomExercise() {
        Random r=new Random();
        Vector<Question> l=new Vector<Question>(),tmp;
        Vector<Entity> ve=getCache();
        int CacheSize=ve.size(),ExerciseSize;
        while(l.size()<10) {
            System.out.println(l.size());
            int rank=r.nextInt(CacheSize);
            tmp=getExcercise(ve.elementAt(rank).label);
            ExerciseSize=tmp.size();
            if(ExerciseSize>0) {
                int erank=r.nextInt(ExerciseSize);
                if(tmp.elementAt(erank).isChoice())
                    l.add(tmp.elementAt(erank));
            }
        }
        return l;
    }


    //问答接口,传入科目和问题文本，返回Reply类，Reply的数据域见Reply类注释
    //调用失败将返回null
    //注意：即使调用成功，返回的Reply对象各个域都可能为空！！！
    //value域为空字符串""表示本题无法回答，Entity域为null表示本题不涉及任何实体，predicate为空字符串""表示不涉及任何属性
    Reply askFor(String subject, String text) {
        Reply r=null;
        try {
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/service/answer-for-question");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("authorization", token);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String params="{\"course\":\""+subject+"\",\"text\":\""+text+"\"}";
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                JSONObject jsobj=new JSONArray(result).getJSONObject(0);
                String v=jsobj.getString("value"),s=jsobj.getString("subject"),p=jsobj.getString("predicate");
                if(v.isEmpty())
                    r=new Reply("");
                else if(s.isEmpty())
                    r=new Reply(v);
                else {
                    if(p.isEmpty())
                        r=new Reply(v,subject,s,"");
                    else
                        r=new Reply(v,subject,s,p);
                }
            }
            return r;
        }
        catch(Exception e) {
            return null;
        }
    }


    //寻路接口，传入source，destination和course，返回Vector<Vector<Entity>>,每项为一条路径
    //注意：有些Entity的label为空，是只有uri没有label的实体
    //得到的每个Entity除了course和label外还可以访问predicate，表示该实体与路径上的上一个实体的关系
    Vector<Vector<Entity>> path(String source,String destination,String course){
        Vector<Vector<Entity>> vve=new Vector<Vector<Entity>>();
        try {
            String srcuri,desturi;
            srcuri=search(course,source).elementAt(0).uri;
            System.out.println(source+":"+srcuri);
            desturi=search(course,destination).elementAt(0).uri;
            System.out.println(destination+":"+desturi);
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/neo/path");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("authorization", token);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String params="{\"s\":\""+srcuri+"\",\"t\":\""+desturi+"\"}";
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                System.out.println(result);
                JSONArray jsarr=new JSONArray(result);
                String pre="开始";
                for(int i=0;i<jsarr.length();i++) {
                    pre="开始";
                    JSONArray path=jsarr.getJSONArray(i);
                    System.out.println(path);
                    Vector<Entity> l=new Vector<Entity>();
                    for(int j=0;j<path.length();j++) {
                        JSONObject jsobj=path.getJSONObject(j);
                        if(jsobj.getString("type").equals("Entity"))
                            if(!jsobj.getString("label").isEmpty())
                                l.add(new Entity(jsobj.getString("label"),course,pre,true));
                            else
                                l.add(new Entity(jsobj.getString("uri"),course,pre,true));
                        else pre=jsobj.getString("label");
                    }
                    vve.add(l);
                }
            }

            else throw new Exception();
        }
        catch(Exception e) {System.out.println(e);}
        return vve;
    }


    //推荐接口，输入学科，返回Vector<Entity>
    Vector<Entity> getRecommendation(String subject){
        Vector<Entity> l=new Vector<Entity>();
        try {
            String url=new String("http://"+SERVER_IP+":8080/neo/recommendation");
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("authorization", token);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            Vector<Entity> ve=getCacheBySubject(subject);
            String entityList="";
            for(Entity e:ve) {
                String uri=e.uri;
                if(!uri.isEmpty())
                    entityList=entityList+",\""+uri+"\"";
            }
            if(!entityList.isEmpty())entityList=entityList.substring(1);
            System.out.println(entityList);
            String params="{\"history\":["+entityList+"],\"course\":\""+subject+"\"}";
            System.out.println(params);
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                System.out.println(result);
                JSONArray jsarr=new JSONArray(result);
                for(int i=0;i<jsarr.length();i++) {
                    JSONObject jsobj=jsarr.getJSONObject(i);
                    l.add(new Entity(jsobj.getString("label"),subject));
                }
            }
            else throw new Exception();
        }
        catch(Exception e) {}
        return l;
    }

    //知识大纲接口，返回的Entity递归地调child数据
    Entity getFramework(String label,String subject) {
        Entity e=new Entity(label,subject),parent,child;
        try {
            e.uri=search(subject,label).elementAt(0).uri;
            System.out.println(e.uri);
            String url=new String("http://"+SERVER_IP+":8080/neo/framework");
            URL httpUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("authorization", token);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String params="{\"uri\":\""+e.uri+"\"}";
            System.out.println(params);
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            Queue<Entity> qe=new LinkedList<Entity>();
            Queue<JSONObject> qj=new LinkedList<JSONObject>();
            if (connection.getResponseCode() == 200) {
                qe.offer(e);
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                System.out.println(result);
                JSONObject jsobj=new JSONObject(result);
                qj.offer(jsobj);
                while(!qe.isEmpty()) {
                    jsobj=qj.poll();
                    parent=qe.poll();
                    JSONArray jsarr=jsobj.getJSONArray("sub");
                    for(int i=0;i<jsarr.length();i++) {
                        jsobj=jsarr.getJSONObject(i);
                        child=new Entity(jsobj.getString("label"),subject,jsobj.getString("rel"),true);
                        parent.child.add(child);
                        qe.offer(child);
                        qj.offer(jsobj);
                    }
                }
            }
            else throw new Exception();
        }
        catch(Exception ex) {}
        return e;
    }


    //得到所有本地缓存的实体
    Vector<Entity> getCache(){
        Vector<Entity> l=new Vector<Entity>();
        String key;
        for(Iterator<String> i=CachedEntityList.keys();i.hasNext();) {
            key=i.next();
            int sep=key.indexOf(':');
            l.add(new Entity(key.substring(0,sep),key.substring(sep+1),true));
        }
        Log.d("CacheLength::",((Integer)l.size()).toString());
        return l;
    }
    Vector<Entity> getCacheBySubject(String sub){
        Vector<Entity> l=new Vector<Entity>();
        try {
            String key;
            for(Iterator<String> i=CachedEntityList.keys();i.hasNext();) {
                key=i.next();
                int sep=key.indexOf(':');
                if(key.substring(sep+1).equals(sub)) {
                    String uri=CachedEntityList.getString(key);
                    l.add(new Entity(key.substring(0,sep),key.substring(sep+1),uri,true,true));
                }
            }
        }
        catch(Exception e) {}
        return l;
    }
    void deleteCache() {
        try {
            CachedSubjectList=new JSONObject("{}");
            CachedEntityList=new JSONObject("{}");
            updateCachedSubjectLists();
            updateCachedEntityLists();
        }
        catch(Exception e) {}
    }

    //得到本地缓存实体的学科分布，返回值是HashMap，key为学科名，value为个数
    HashMap<String,Integer> getSubjectOfCache(){
        HashMap<String,Integer> soc=new HashMap<String,Integer>(){
            {
                put("chinese",0);
                put("math",0);
                put("english",0);
                put("physics",0);
                put("chemistry",0);
                put("history",0);
                put("geo",0);
                put("biology",0);
                put("politics",0);
            }
        };
        String key;
        try {
            for(Iterator<String> i=CachedEntityList.keys();i.hasNext();) {
                key=i.next();
                int sep=key.indexOf(':');
                if(sep>0) {
                    String sub=key.substring(sep+1);
                    int value=soc.get(sub);
                    soc.put(sub, value+1);
                }
            }
        }
        catch(Exception e) {}
        return soc;
    }


    //将英文学科名转为中文
    String switchToChinese(String s){
        return etoc.get(s);
    }

    //添加收藏，返回0成功,1表示已添加过，-1表示请求失败
    int addFavorite(String subject,String label) {
        try {
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/service/favorite");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("authorization", token);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String params="{\"course\":\""+subject+"\",\"label\":\""+label+"\"}";
            out.write(params.getBytes("UTF-8"));
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                return 0;
            }
            else if(connection.getResponseCode() == 400)return 1;
            else throw new Exception();
        }
        catch(Exception e) {
            return -1;
        }
    }
    //删除收藏，返回值语义与添加收藏相同
    int deleteFavorite(String subject,String label) {
        try {
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/service/favorite?course="+subject+"&label="+java.net.URLEncoder.encode(label,"utf-8"));
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("authorization", token);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                return 0;
            }
            else if(connection.getResponseCode() == 400)return 1;
            else throw new Exception();
        }
        catch(Exception e) {
            return -1;
        }
    }
    //收藏列表，调用失败返回null
    Vector<Entity> getFavorite() {
        try {
            Vector<Entity> l=null;
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/service/favorite");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("authorization", token);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                l=new Vector<Entity>();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                JSONArray jsarr=new JSONArray(result);
                for(int i=0;i<jsarr.length();i++) {
                    JSONObject jsobj=jsarr.getJSONObject(i);
                    l.add(new Entity(jsobj.getString("label"),jsobj.getString("course")));
                }
                return l;
            }
            else throw new Exception();
        }
        catch(Exception e) {
            return null;
        }
    }

    //历史列表
    Vector<Entity> getHistory() {
        try {
            Vector<Entity> l=null;
            URL httpUrl = new URL("http://"+SERVER_IP+":8080/service/history");
            HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setConnectTimeout(MAX_TIME);
            connection.setReadTimeout(MAX_TIME);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("authorization", token);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                l=new Vector<Entity>();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String result = in.readLine();
                JSONArray jsarr=new JSONArray(result);
                for(int i=0;i<jsarr.length();i++) {
                    JSONObject jsobj=jsarr.getJSONObject(i);
                    boolean flag=false;
                    Entity tmp=new Entity(jsobj.getString("label"),jsobj.getString("course"));
                    for(Entity en:l)if(en.label.equals(tmp.label)&&en.course.equals(tmp.course)){flag=true;break;}
                    if(flag==false)l.add(tmp);
                }
                return l;
            }
            else throw new Exception();
        }
        catch(Exception e) {
            return null;
        }
    }

    //正确率向量，保证是6个值，不满6次的补足0
    Vector<Integer> getRate(){
        Vector<Integer> vf=new Vector<Integer>();
        try {
            for (int i = CachedRates.length() - 1; i >= 0&&vf.size()<6; i--)
                vf.add(CachedRates.getInt(i));
            while(vf.size()<6)vf.add(0);
        }
        catch(Exception e){}
        return vf;
    }
    //0号元素为本地实体数，1号元素为做题数
    Vector<Integer> getCacheNumber(){
        Vector<Integer> vf=new Vector<Integer>();
        vf.add(CachedEntityList.length());
        vf.add(CachedRates.length()*10);
        return vf;
    }
    /*
	public static void main(String[] args)throws JSONException{
		FrontEnd fe=new FrontEnd();
		//fe.logIn("usersong", "passsong");
		deleteCache();
		//System.out.println(e.label+":"+e.course);

		//System.out.println(CachedEntityList.keys().next());
		fe.logIn("usersong", "passsong");
		System.out.println(fe.token);
		System.out.println("Testing 实体详情:");
		Entity e=fe.searchForEntity("english","do");
		System.out.println("name:"+e.label+"   subject:"+e.course+"   category:"+e.category);
		System.out.println("properties:");
		for(Entry key : e.property) {
			System.out.println(key.key+":"+key.value);
		}
		System.out.println("subjects:");
		for(Entry key : e.subject) {
			System.out.println(key.key+":"+key.value);
		}
		System.out.println("objects:");
		for(Entry key : e.object) {
			System.out.println(key.key+":"+key.value);
		}
		System.out.println("Testing 问答:");
		Reply r=fe.askFor("chinese","杜甫字什么");
		if(!r.value.isEmpty())
			System.out.println("value:"+r.value+"	entity:"+r.entity.label+"	subject:"+r.entity.course+"    predicate"+r.predicate);
		else
			System.out.println("不知道");
		r=fe.askFor("english","do的过去式是什么");
		if(!r.value.isEmpty())
			System.out.println("value:"+r.value+"	entity:"+r.entity.label+"	subject:"+r.entity.course+"    predicate"+r.predicate);
		else
			System.out.println("不知道");
		r=fe.askFor("geo","法国的首都在哪里");
		if(!r.value.isEmpty())
			System.out.println("value:"+r.value+"	entity:"+r.entity.label+"	subject:"+r.entity.course+"    predicate"+r.predicate);
		else
			System.out.println("不知道");
		fe.searchBySubject("chinese");
		fe.searchBySubject("english");
		fe.searchBySubject("geo");
		System.out.println(switchToChinese("english"));
	}
*/
}