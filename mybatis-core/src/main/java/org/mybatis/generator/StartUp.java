package org.mybatis.generator;
 
/**
 * Created by 草帽boy on 2017/2/16.
 * 启动文件，只需要点击运行就行
 */
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartUp {
    public static void main(String[] args) {
        try {
            List<String> warnings = new ArrayList<String>();
            boolean overwrite = true;
            //直接获取generatorConfig.xml的文件路径 根据具体情况查看
            File configFile = new File("D:\\project\\test\\generator-template\\test-generatorConfig.xml");
            //File configFile = new File("D:\\project\\generator2\\test-generatorConfig.xml");
            //File configFile = new File("D:\\project\\ac-generator\\user-generatorConfig.xml");
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
