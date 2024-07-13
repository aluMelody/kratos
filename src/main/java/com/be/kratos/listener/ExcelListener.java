package com.be.kratos.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelListener<T> extends AnalysisEventListener<T> {

    private List<Object[]> datas = new ArrayList<>();

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        datas.add(new Object[]{t});
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public Iterator<Object[]> getDatas() {
        return datas.iterator();
    }

    public void setDatas(List<Object[]> datas) {
        this.datas = datas;
    }
}
