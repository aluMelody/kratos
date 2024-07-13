package com.be.kratos.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class SuitData {
    @ExcelProperty(value = "id")
    private int id;

    @ExcelProperty(value = "scene")
    private String scene;

    @ExcelProperty(value = "run")
    private Boolean run;

    @ExcelProperty(value = "cooperator")
    private String cooperator;

    @ExcelProperty(value = "method")
    private String method;

    @ExcelProperty(value = "url")
    private String url;

    @ExcelProperty(value = "header")
    private String header;

    @ExcelProperty(value = "param")
    private String param;

    @ExcelProperty(value = "body")
    private String body;

    @ExcelProperty(value = "resultAssert")
    private String resultAssert;


    private String packageName;

    private String className;

    private Map<String, Map<String, String>> prepareData;

    private String serviceName;
}
