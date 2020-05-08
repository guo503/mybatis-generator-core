package org.mybatis.generator.constant;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author guos
 * @description
 * @date 2020/5/7 10:20
 **/
public class PluginTypeConst {

    public static List<String> customTypes = Lists.newArrayList(
            "org.mybatis.generator.plugins.ExtendModelPlugin",
                      "org.mybatis.generator.plugins.BaseMethodPlugin",
                      "org.mybatis.generator.plugins.ManagePlugin",
                      "org.mybatis.generator.plugins.ServicePlugin",
                      "org.mybatis.generator.plugins.BusinessPlugin",
                      "org.mybatis.generator.plugins.ControllerPlugin");
}
