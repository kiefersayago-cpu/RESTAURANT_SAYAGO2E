package com.scc.model;

public class ActivityLog (
    int id,
    int userid,
    String username,
    String action,
    String timestamp
){
    @Override
    public String toString(){
        return String.format("[%s] user '%s' (ID: %d) -> Action %s", timestamp, username, userId, action);
    }
}

