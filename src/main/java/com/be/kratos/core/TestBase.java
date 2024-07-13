package com.be.kratos.core;

import com.alibaba.excel.EasyExcel;
import com.be.kratos.config.ContextConfig;
import com.be.kratos.entity.SuitData;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class TestBase {

    @DataProvider(name = "testData")
    public Iterator<Object[]> excelData3(Class clazz) {
        String[] name = clazz.getName().split("\\.");
        int len = name.length;
        String packageName = name[len-2];
        String sep = File.separator;
        String relativePath = sep+"testData"+sep+packageName+sep+name[len-1]+sep+name[len-1]+".csv";

        String dataPath = this.getClass().getResource(relativePath).getFile();
        List<SuitData> suitDataList = EasyExcel.read(dataPath).head(SuitData.class).sheet().doReadSync();
        List<Object[]> data = new ArrayList<>();
        for (SuitData suitData : suitDataList) {
            suitData.setPackageName(packageName);
            suitData.setClassName(name[len-1]);
            data.add(new SuitData[]{suitData});
        }
        return data.iterator();
    }

    @AfterSuite
    public void getContextMap() {
        ArrayList<String> commonList = new ArrayList<>();
        ArrayList<String> leaderList = new ArrayList<>();
        ArrayList<String> partnerList = new ArrayList<>();
//        ArrayList<String> arrayList = new ArrayList<>();
        for (Map.Entry<String, String> entry : ContextConfig.getInstance().getAllKeyValuePairs().entrySet()) {
            switch (entry.getKey().split("\\.")[0]) {
                case "common":
                    commonList.add(entry.getKey() + " = " + entry.getValue());
                    break;
                case "leader":
                    leaderList.add(entry.getKey() + " = " + entry.getValue());
                    break;
                case "partner":
                    partnerList.add(entry.getKey() + " = " + entry.getValue());
                    break;
            }
        }

        ArrayList<String> extracted = extracted(commonList);
        if (!extracted(leaderList).isEmpty()) extracted.addAll(extracted(leaderList));
        if (!extracted(partnerList).isEmpty()) extracted.addAll(extracted(partnerList));

        try {
            // 创建一个打印流，指向要写入的文件
            PrintStream printStream = new PrintStream(new FileOutputStream(".runtime.properties", false));

            // 将系统的输出重定向到printStream
            System.setOut(printStream);

            // 写入文件的测试输出
            System.out.println("# 上下文依赖数据如下：");
            System.out.println("# ===============================================");
            // 删除最后一条分割线
            extracted.remove(extracted.size() - 1);
            for (String s : extracted) {
                System.out.println(s);
            }
            System.out.println("# ===============================================");

            // 重定向结束后关闭printStream
            System.setOut(printStream);
            printStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> extracted(ArrayList<String> list) {
        ArrayList<String> result = new ArrayList<>();
        if (list.isEmpty()) return result;
        Map<String, List<String>> map = new HashMap<>();
        for (String s : list) {
            if (s.contains("[") && s.contains("]")) {
                int endIndex = s.indexOf('[');
                String key = s.substring(0, endIndex);
                if (!map.containsKey(key)) {
                    map.put(key, new ArrayList<>());
                }
                map.get(key).add(s);
            } else {
                result.add(s);
            }
        }
        if (!map.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                Collections.sort(entry.getValue(), new StringComparator());
                //            打印数组带角标的
                //            leaderList.add(entry.getKey() + " = " + entry.getValue().toString());
                ArrayList<String> valueList = new ArrayList<>();
                for (String s : entry.getValue()) {
                    valueList.add(s.split("=")[1].trim());
                }
                result.add(entry.getKey() + " = " + valueList.toString());
            }
        }
        result.add("# -----------------------------------------------");
        return result;
    }

    // 定义一个比较器，根据字符串中的数字部分来比较
    static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            // 提取字符串中的数字部分
            int num1 = extractNumber(s1);
            int num2 = extractNumber(s2);
            // 根据数字部分比较字符串
            return Integer.compare(num1, num2);
        }

        // 提取字符串中的数字部分
        private int extractNumber(String s) {
            int startIndex = s.indexOf('[') + 1;
            int endIndex = s.indexOf(']');
            String number = s.substring(startIndex, endIndex);
            return Integer.parseInt(number);
        }
    }

}
