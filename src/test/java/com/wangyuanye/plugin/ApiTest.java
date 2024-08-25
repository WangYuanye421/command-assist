package com.wangyuanye.plugin;


import com.intellij.testFramework.LightPlatformTestCase;

/**
 *
 * LightPlatformTestCase: 适用于大多数插件测试，提供轻量级的环境。
 * HeavyPlatformTestCase: 提供更完整的 IDEA 环境，适合需要复杂环境的测试。
 * UsefulTestCase: 提供了一些常用的断言和帮助方法，适用于独立于 IDEA 环境的测试。
 */

public class ApiTest extends LightPlatformTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // 在这里进行初始化操作
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            // 在这里进行清理操作
        } finally {
            super.tearDown();
        }
    }


//    @Test
//    public void test01() {
//        System.out.println("项目名称: " + getProject().getName());
//        System.out.println("homePath: " + getHomePath());
//
//    }


}
