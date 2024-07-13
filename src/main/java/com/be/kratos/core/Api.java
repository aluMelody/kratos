package com.be.kratos.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.be.kratos.config.ContextConfig;
import com.be.kratos.entity.SuitData;
import com.be.kratos.utils.AssertUtils;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Api {

    private static RequestSpecification request;

    public static Response request(SuitData suitData) {
        return "get".equals(suitData.getMethod()) ? doGet(suitData) : doPost(suitData);
    }

    public static Response doPost(SuitData suitData) {
        request = given()
                .headers(parsingParameters(suitData, suitData.getHeader()));

        String sep = File.separator;
        String packageName = suitData.getPackageName();
        if (!Objects.equals(suitData.getBody(), null) && suitData.getBody().contains("file=")) {
            String[] split = suitData.getBody().split("=");
            String filePath = Paths.get("src" + sep + "test" + sep + "resources" + sep + "testData" + sep + packageName + sep + suitData.getClassName() + sep + split[1]).toString();
            request.multiPart(new File(filePath)).formParams(parsingParameters(suitData, suitData.getParam()));
        } else {
            Path path = Paths.get("src" + sep + "test" + sep + "resources" + sep + "testData" + sep + packageName + sep + suitData.getClassName() + sep + suitData.getBody() + ".json");
            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(path);
                String strBody = reader.lines().map(String::trim).collect(Collectors.joining(""));
                // 参数替换
                strBody = getString(strBody);
                // 字符串转json
                char c = strBody.charAt(0);
                if (c == '[') {
                    List<String> jsonBody = JSON.parseArray(strBody, String.class);
                    System.out.println(jsonBody.toString());
                    request.body(jsonBody);
                } else {
                    JSONObject jsonBody = JSON.parseObject(strBody);
                    System.out.println(jsonBody.toString());
                    request.body(jsonBody);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 替换路径变量
        String url = getString(suitData.getUrl());
        // 获取host地址
        String host = ContextConfig.getInstance().getHost(suitData.getCooperator());
        // 发起请求
        ValidatableResponse then = ((Objects.equals(suitData.getMethod(), "post") ? request.when().post(host + url) : request.when().put(host + url)).then());
        // 组装断言
        assemblyAssertion(suitData, then);
        // 返回响应体
        return then.log().body().extract().response();

    }

    public static Response doGet(SuitData suitData) {
        request = given()
                .headers(parsingParameters(suitData, suitData.getHeader()))
                .queryParams(parsingParameters(suitData, suitData.getParam()));

        // 替换路径变量
        String url = getString(suitData.getUrl());
        // 获取host地址
        String host = ContextConfig.getInstance().getHost(suitData.getCooperator());
        // 发起请求
        ValidatableResponse then = request.when().get(host + url).then();
        // 组装断言
        assemblyAssertion(suitData, then);
        // 返回响应体
        return then.log().body().extract().response();
    }

    //参数替换
    public static String getString(String strBody) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(strBody);
        ContextConfig contextConfig = ContextConfig.getInstance();
        while (matcher.find()) {
            strBody = strBody.replace("${" + matcher.group(1) + "}", contextConfig.getValue(matcher.group(1)));
        }
        return strBody;
    }

    private static Map<String, String> parsingParameters(SuitData suitData, String parameter) {
        HashMap<String, String> map = new HashMap<>();
        if (parameter == null) {
            return map;
        }
        String realParameter = getString(parameter);
        String[] splits = realParameter.split("&");
        for (String split : splits) {
            String[] kv = split.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    private static void assemblyAssertion(SuitData suitData, ValidatableResponse then) {
        if (suitData.getResultAssert() == null) {
            return;
        }
        String[] resultAsserts = suitData.getResultAssert().split("&");
        for (String resultAssert : resultAsserts) {
            String[] kv = resultAssert.split("=");
            String[] split = kv[0].split("\\.");
            int len = split.length;
            String assertType = split[len - 1];
            String attr = "";
            String itemStr = "";
            String parentNode = "";
            if (Stream.of("arrayStr", "jsonStr", "arraySize").anyMatch(resultAssert::contains)) {
                parentNode = split[0];
                attr = split[len - 2];
                itemStr = assertType.equals("arraySize") ? "" : split[len - 3];
            }
            String response = then.extract().response().jsonPath().getString(parentNode);
            switch (assertType) {
                case "arrayStr":
                    int start = itemStr.indexOf("[");
                    int end = itemStr.indexOf("]");
                    int index = Integer.parseInt(itemStr.substring(start + 1, end));
                    String itemStrNoindex = itemStr.substring(0, start);
                    AssertUtils.assertJsonArrayItem(response, itemStrNoindex, index, attr, kv[1]);
                    break;
                case "jsonStr":
                    AssertUtils.assertJsonObjectProperty(response, "$", attr, kv[1]);
                    break;
                case "arraySize":
                    AssertUtils.assertJsonArraySize(response, attr, Integer.parseInt(kv[1]));
                    break;
                default:
                    Object value = getValue(kv[1]);
                    then.body(kv[0], equalTo(value));
            }
        }
    }

    private static Object getValue(String s) {
        Object value = null;
        String noTrimStr = s.trim();
        char type = Character.toUpperCase(noTrimStr.charAt(0));
        String realStr = noTrimStr.substring(2, noTrimStr.length() - 1);
        switch (type) {
            case 'S':
                value = realStr;
                break;
            case 'I':
                value = Integer.parseInt(realStr);
                break;
            case 'B':
                value = Boolean.valueOf(realStr);
                break;
            default:
                System.out.println("断言结果类型非法！不支持的结果类型：" + type);
        }
        return value;
    }

}
