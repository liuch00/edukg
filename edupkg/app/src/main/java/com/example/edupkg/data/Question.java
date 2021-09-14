package com.example.edupkg.data;

public class Question{
    String body,ans,A,B,C,D,qbody;
    Question(String b,String a){
        body=b;
        ans=a;
        A="";
    }
    Question(String b,String a,String AA,String BB,String CC,String DD,String qqbody){
        body=b;
        ans=a;
        A=AA;
        B=BB;
        C=CC;
        D=DD;
        qbody=qqbody;
    }
    boolean isChoice() {
        return !A.isEmpty();
    }
}