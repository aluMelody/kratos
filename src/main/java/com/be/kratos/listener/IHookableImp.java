package com.be.kratos.listener;

import com.be.kratos.config.ContextConfig;
import com.be.kratos.entity.SuitData;
import com.be.kratos.enums.RunFlagEnum;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;

import java.util.Objects;

public class IHookableImp implements IHookable {
    @Override
    public void run(IHookCallBack iHookCallBack, ITestResult iTestResult) {
        for (Object obj : iTestResult.getParameters()) {
            SuitData suitData = (SuitData) obj;
            if (RunFlagEnum.YES.getCode().equals(suitData.getRun())) {
                String token = ContextConfig.getInstance().getValue(suitData.getCooperator()+".token");
                String header = suitData.getHeader() == null ? ("Authorization=" + token) : ("Authorization=" + token+ "&" + suitData.getHeader());
                if (suitData.getMethod().equals("post")) {
                    if (Objects.equals(suitData.getBody(), null)) {
                        header = header + "&Content-Type=application/json";
                    } else if (!suitData.getBody().contains("file=")) {
                        header = header + "&Content-Type=application/json";
                    }
                }
                suitData.setHeader(header);
                iHookCallBack.runTestMethod(iTestResult);
            } else {
                iTestResult.setStatus(3);
                System.out.println("@Test方法："+iTestResult.getInstanceName() + "." + iTestResult.getName() + "() case-" + suitData.getId() + " "+ suitData.getScene() + "场景跳过！");
            }
        }

//        System.out.println((SuitData)iHookCallBack.getParameters()[0]); //返回@test注解测试方法传递的参数
//        System.out.println((SuitData)iTestResult.getParameters()[0]);   //返回@test注解测试方法传递的参数
//        System.out.println((SuitData)iTestResult.getFactoryParameters()[0]);  //与上面不一样 会报错说明不是SuitData
    }
}
