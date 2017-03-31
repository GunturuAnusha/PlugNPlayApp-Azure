package com.example.plugnplay;

/**
 * Created by Anusha on 11/1/2016.
 */
public class user {


    @com.google.gson.annotations.SerializedName("userName")
    private String userName;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @com.google.gson.annotations.SerializedName("id")
    private String Id;

    private String emailAddress;

    @com.google.gson.annotations.SerializedName("password")
    private String password;

    private String age;

    private String phone;

    private String subscriptionDesc;

    public boolean ismComplete() {
        return mComplete;
    }

    public void setmComplete(boolean mComplete) {
        this.mComplete = mComplete;
    }

    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;

    public user(){

    }

    public user(String id, String username, String password){
        this.Id = id;
        this.userName = username;
        this.password =  password;
    }


    public user(String id, String emailAddress, String username, String password, String age, String phone){
        this.Id = id;
        this.emailAddress=emailAddress;
        this.userName = username;
        this.password =  password;
        this.age = age;
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getSubscriptionDesc() {
        return subscriptionDesc;
    }

    public void setSubscriptionDesc(String subscriptionDesc) {
        this.subscriptionDesc = subscriptionDesc;
    }





}
