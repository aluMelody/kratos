package com.be.kratos.utils;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class AssertUtils {
    public static void assertJsonArraySize(String jsonResponse, String jsonPathExpr, int expectedSize) {
        JsonPath jsonPath = JsonPath.from(jsonResponse);
        Object jsonArrayOrObject = jsonPath.get(jsonPathExpr);

        if (jsonArrayOrObject instanceof List) {
            List<Object> jsonArray = (List<Object>) jsonArrayOrObject;
            Assert.assertEquals(jsonArray.size(), expectedSize);
        } else if (jsonArrayOrObject instanceof Map) {
            Map<String, Object> jsonObject = (Map<String, Object>) jsonArrayOrObject;
            Assert.assertNotNull(jsonObject);
            Assert.assertFalse(jsonObject.isEmpty());
        } else {
            Assert.fail("Invalid JSON structure at given JSON path: " + jsonPathExpr);
        }
    }

    public static void assertJsonArrayItem(String jsonResponse, String jsonPathExpr, int itemIndex, String expectedPropertyName, Object expectedPropertyValue) {
        JsonPath jsonPath = JsonPath.from(jsonResponse);
        List<Map<String, Object>> jsonArray = jsonPath.getList(jsonPathExpr);

        if (itemIndex < 0 || itemIndex >= jsonArray.size()) {
            Assert.fail("Item index out of bounds: " + itemIndex);
        }

        Map<String, Object> item = jsonArray.get(itemIndex);
        Object actualPropertyValue = item.get(expectedPropertyName);
        Assert.assertEquals(actualPropertyValue, expectedPropertyValue);
    }

    public static void assertJsonObjectProperty(String jsonResponse, String jsonPathExpr, String expectedPropertyName, Object expectedPropertyValue) {
        JsonPath jsonPath = JsonPath.from(jsonResponse);
        Map<String, Object> jsonObject = jsonPath.getMap(jsonPathExpr);

        Object actualPropertyValue = jsonObject.get(expectedPropertyName);
        if (actualPropertyValue instanceof Boolean) {
            Assert.assertEquals(actualPropertyValue, "true".equals(expectedPropertyValue));
        } else {
            Assert.assertEquals(actualPropertyValue, expectedPropertyValue);
        }
    }
}
