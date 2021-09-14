package com.example.edupkg.data;

//Entry类，存属性详情页每一项
//只需要读取数据
public class Entry implements Comparable<Entry>{
    String key,value;
    Entry(String a,String b){
        key=a;
        value=b;
    }
    public int compareTo(Entry other) {
        if(key.compareTo(other.key)!=0)
            return key.compareTo(other.key);
        else return value.compareTo(other.value);
    }
}
