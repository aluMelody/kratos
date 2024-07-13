package com.be.kratos.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson2.JSON;
import com.be.kratos.entity.SuitData;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class SuitDataListener implements ReadListener<SuitData> {


//  每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
    private static final int BATCH_COUNT = 10;

//    private List<SuitData> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    private List<Object[]> cachedDataList = new ArrayList<>();

    @Override
    public void invoke(SuitData data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
//        cachedDataList.add(data);

//        ArrayList<Object[]> suitDataArrayList = new ArrayList<>();
        cachedDataList.add(new SuitData[]{data});
//        cachedDataList.iterator();



        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
//        if (cachedDataList.size() >= BATCH_COUNT) {
//            saveData();
            // 存储完成清理 list
//            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
//        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
//        saveData();
//        log.info("所有数据解析完成！");


    }

    /**
     * 对外返回读取到的数据
     */
    public Iterator<Object[]> getData() {
        return cachedDataList.iterator();
    }

}
