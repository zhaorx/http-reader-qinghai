package com.hy.http.model;

import java.util.List;

public class Result {
    public Boolean Status;
    public Integer Code;
    public String Message;
    public List<DataItem> Data;

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public List<DataItem> getData() {
        return Data;
    }

    public void setData(List<DataItem> data) {
        Data = data;
    }
}
