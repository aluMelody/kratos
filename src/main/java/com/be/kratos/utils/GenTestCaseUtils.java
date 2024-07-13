package com.be.kratos.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GenTestCaseUtils {

    public static void genTestCase(String className, String methodName) throws IOException {
        if (genCode(className, methodName) && genData(className, methodName)) {
            System.out.println("TestCase 生成成功！！！");
        } else {
            System.out.println("TestCase 生成失败！类名需要带上包名，eg：xxx.xxx");
        }
    }

    public static Boolean genCode(String className, String methodName) throws IOException {
        String template = "package com.be.${packageName};\n" +
                "\n" +
                "import com.be.kratos.core.Api;\n" +
                "import com.be.kratos.core.TestBase;\n" +
                "import com.be.kratos.entity.SuitData;\n" +
                "import com.be.kratos.listener.IHookableImp;\n" +
                "import io.qameta.allure.Story;\n" +
                "import io.restassured.response.Response;\n" +
                "import org.testng.annotations.Listeners;\n" +
                "import org.testng.annotations.Test;\n" +
                "\n" +
                "@Listeners(IHookableImp.class)\n" +
                "public class ${className} extends TestBase {\n" +
                "\n" +
                "    @Test(dataProvider = \"testData\")\n" +
                "    @Story(\"\")\n" +
                "    public void ${methodName}(SuitData suitData) {\n" +
                "        Response response = Api.request(suitData);\n" +
                "    }\n" +
                "}\n";

        if (!className.contains(".")) {
            return false;
        } else {
            String[] split = className.split("\\.");
            String classNameStr = split[split.length - 1];
            String[] split_copy = Arrays.copyOf(split, split.length - 1);
            String packageNameStr = String.join(".", split_copy);
            String packagePath = String.join("/", split_copy);
            String replace = template.replace("${packageName}", packageNameStr).replace("${className}", classNameStr).replace("${methodName}", methodName);
            String basePath = Paths.get("").toAbsolutePath().toString() + Paths.get("/src/test/java/com/be/" + packagePath).toAbsolutePath().toString();
            Path pathCreate = Files.createDirectories(Paths.get(basePath)).toAbsolutePath();
            FileWriter fileWriter = new FileWriter(pathCreate +"/"+ classNameStr + ".java");
            fileWriter.write(replace);
            fileWriter.close();
            return true;
        }
    }

    public static Boolean genData(String className, String methodName) throws IOException {
        String template = "id,scene,run,cooperator,method,url,header,param,body,resultAssert\n" +
                "1,,true,leader,,,\"\",\"\",\"\",\n";

        if (!className.contains(".")) {
            return false;
        } else {
            String[] split = className.split("\\.");
            String classNameStr = split[split.length - 1];
            String[] split_copy = Arrays.copyOf(split, split.length - 1);
            String packageNameStr = split_copy[split_copy.length - 1];
            String basePath = Paths.get("").toAbsolutePath().toString() + Paths.get("/src/test/resources/testData/" + packageNameStr + "/" + classNameStr).toAbsolutePath().toString();
            Path pathCreate = Files.createDirectories(Paths.get(basePath)).toAbsolutePath();
            FileWriter fileWriter = new FileWriter(pathCreate +"/"+ classNameStr + ".csv");
            fileWriter.write(template);
            fileWriter.close();
            return true;
        }
    }

}
