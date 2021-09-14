package com.example.edupkg.data;

//回答类，调用问答接口的返回类型，包含一个value，表示回答的内容，一个Entity，表示回答涉及的实体,一个predicate，表示回答涉及的属性
public class Reply{
    String value="",predicate="";
    Entity entity=null;
    Reply(String v,String subject,String label,String pre){
        value=v;
        entity=new Entity(label,subject);
        predicate=pre;
    }
    Reply(String v){
        value=v;
    }
}

