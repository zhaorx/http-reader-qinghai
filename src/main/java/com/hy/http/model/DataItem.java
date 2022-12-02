package com.hy.http.model;

import java.util.List;

public class DataItem {
    public String LineName ;
    public String LineCode ;
    public List<Param> Parameters ;

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getLineCode() {
        return LineCode;
    }

    public void setLineCode(String lineCode) {
        LineCode = lineCode;
    }

    public List<Param> getParameters() {
        return Parameters;
    }

    public void setParameters(List<Param> parameters) {
        Parameters = parameters;
    }
}
