package com.example.vanient.mycontacts.domain.entity;

/**
 * Created by Vanient on 2018/2/3.
 */

public class Group {
    private String groupName, phNo, phDisplayName, phType, email;
    private String groupid;
    private int groupNumber;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public String getPhDisplayName() {
        return phDisplayName;
    }

    public void setPhDisplayName(String phDisplayName) {
        this.phDisplayName = phDisplayName;
    }

    public String getPhType() {
        return phType;
    }

    public void setPhType(String phType) {
        this.phType = phType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }
}
