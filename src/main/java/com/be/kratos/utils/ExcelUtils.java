package com.be.kratos.utils;

import com.alibaba.excel.EasyExcel;
import com.be.kratos.entity.SuitData;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExcelUtils {

    /**
     * 获取excel里面的记录封装到map里面，通过map.get(suitData.id)来获取对应的第几行记录的值
     * @param path excel所在的路径， eg："/testData/xxxModule1/DemoTest1/DemoTest1.csv"
     * @return Map<Integer, SuitData>
     */

    public Map<Integer, SuitData> getSuitData(String path) {
        String dataPath = Objects.requireNonNull(this.getClass().getResource(path)).getFile();
        List<SuitData> suitDataList = EasyExcel.read(dataPath).head(SuitData.class).sheet().doReadSync();
        return suitDataList.stream().collect(Collectors.toMap(SuitData::getId, Function.identity(), (k1, k2) -> k2));
    }
}
