package org.mybatis.generator.plugins;

import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * Description: 自定义model
 * Author: guos
 * Date: 2019/2/1 11:31
 **/
public class ExtendModelPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
