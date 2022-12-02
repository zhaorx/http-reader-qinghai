package com.hy.http.model;

import java.util.List;

public class WritterResult {
    private long num;
    private String message;

    private List<Gas> list;

    public List<Gas> getList() {
        return list;
    }

    public void setList(List<Gas> list) {
        this.list = list;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
