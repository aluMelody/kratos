package com.be.kratos.enums;

public enum RunFlagEnum {

    YES(true, "运行"),
    NO(false, "不运行");

    private Boolean code;
    private String desc;

    private RunFlagEnum(final Boolean code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Boolean getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
