package com.be.kratos.config;

import com.be.kratos.enums.CooperatorEnum;
import com.be.kratos.utils.PropertiesUtils;
import io.restassured.response.Response;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;

public class ContextConfig {
    private static ContextConfig instance;
    private final Map<String, String> contextMap;

    private ContextConfig() {
        contextMap = new HashMap<>();
        initializeKeys();
    }

    public static synchronized ContextConfig getInstance() {
        if (instance == null) {
            instance = new ContextConfig();
        }
        return instance;
    }

    private void initializeKeys() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("context.yml"));
            String line;
            String currentKey = "";
            while ((line = reader.readLine()) != null) {
                // 如果是空行，则继续下一行
                if (line.trim().isEmpty()) {
                    continue;
                }
                // 如果是注释，则继续下一行
                if (line.trim().startsWith("#")) {
                    continue;
                }
                // 移除字符串中的空白字符
                line = line.trim();
                if (line.startsWith("-")) {
                    // 解析配置
                    String[] split = line.split("#")[0].trim().split("-")[1].trim().split("=");
                    String subKey = split[0].split(",")[0];
                    String fullKey = currentKey + "." + subKey;

                    // 初始化有值的配置到map里面
                    if (split.length > 1) {
                        if (Objects.equals(split[1].split(",")[0], "now")) {
                            contextMap.put(fullKey, new SimpleDateFormat("MMdd_HH-mm-ss").format(new Date(System.currentTimeMillis())));
                        } else if (Objects.equals(split[1].split(",")[0], "allNow")) {
                            contextMap.put(fullKey, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                        } else if (split[1].contains("[") && split[1].contains("]")) {
                            String substring = split[1].substring(split[1].indexOf("[") + 1, split[1].indexOf("]"));
                            if (!Objects.equals(substring, "")) {
                                String[] splitValues = substring.split(",");
                                int len = splitValues.length;
                                for (int i = 0; i < len; i++) {
                                    contextMap.put(fullKey+"_"+(i+1), splitValues[i].trim());
                                }
                            }
                        } else {
                            contextMap.put(fullKey, split[1].split(",")[0]);
                        }
                    } else {
                        // 初始化没值的配置到map里面
                        if ("token".equals(subKey)) {
                            contextMap.put(fullKey, getToken(currentKey));
                        } else {
                            contextMap.put(fullKey, null);
                        }
                    }
                } else {
                    currentKey = line.split(":")[0].trim();
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken(String cooperator){
        String token = "";
        if (cooperator.equals(CooperatorEnum.GLITE.getFlag())) {
            Response res = given()
                    .contentType("application/json")
                    .body("{\n" +
                            "    \"username\": \"test\",\n" +
                            "    \"password\": \"123456\"\n" +
                            "}")
                    .post(getHost(cooperator) + "/login")
                    .then()
                    .statusCode(200)
                    .extract().response();
            token = res.path("token").toString();
        } else {
            HashMap<String, String> formData = new HashMap<>(){{
                put("username", "test");
                put("password", "123456");
                put("grant_type", "password");
                put("scope", "test");
            }};
            Response res = given()
                    .header("Authorization", "test")
                    .formParams(formData)
                    .post( (cooperator.equals(CooperatorEnum.GAIAD.getFlag()) ? getGaiaDHost(getHost(cooperator)) : getHost(cooperator)) +"/token")
                    .then().log().body()
                    .statusCode(200)
                    .extract().response();
            token = res.path("type") + " " + res.path("token");
        }

        return token;
    }

    public String getHost(String cooperator) {
        String host = null;
        switch (cooperator) {
            case "leader":
                host = PropertiesUtils.getProperty("leader.host");
                break;
            case "partner":
                host = PropertiesUtils.getProperty("partner.host");
                break;
            case "partner1":
                host = PropertiesUtils.getProperty("partner1.host");
                break;
            default:
                host = PropertiesUtils.getProperty("partnerOther.host");
        }
        return host;
//        return cooperator.equals(CooperatorEnum.LEADER.getFlag()) ? PropertiesUtils.getProperty("leader.host") : PropertiesUtils.getProperty("partner.host");
    }

    public String getGaiaDHost(String host) {
        String[] split = host.split("\\.");
        if (Objects.equals(split[0], "http://10") && Objects.equals(split[1], "99")) {
            split[split.length - 1] = "8";
        }
        return String.join(".", split);
    }

    public String getValue(String key) {
        return contextMap.get(key);
    }

    public void setValue(String key, String value) {
        String[] split = key.split("\\.");
        if (split.length == 2) {
            contextMap.put(key, value);
        } else {
            contextMap.put(split[0]+"."+split[1]+"["+split[2]+"]", value);
        }

    }

    public Map<String, String> getAllKeyValuePairs() {
        return contextMap;
    }

    public void updatecontextMap(String key, String value) {
        if (contextMap.containsKey(key)) {
            contextMap.put(key, value);
        } else {
            System.out.println("Key does not exist!");
        }
    }

    public void removecontextMap(String key) {
        if (contextMap.containsKey(key)) {
            contextMap.remove(key);
        } else {
            System.out.println("Key does not exist!");
        }
    }

    public void someContextConfigOperation() {
        // 在这里可以进行其他服务操作
    }

    public void stopContextConfig() {
        instance = null;
    }

}
