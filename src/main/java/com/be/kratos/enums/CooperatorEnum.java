package com.be.kratos.enums;

public enum CooperatorEnum {

    LEADER("leader", "发起方"),
    PARTNER("partner", "提供方");


    private String flag;
    private String desc;

    private CooperatorEnum(final String flag, final String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public String getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}
