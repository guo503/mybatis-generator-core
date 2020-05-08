package org.mybatis.generator.utils;

import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.List;

/**
 * Description:
 * Author: guos
 * Date: 2019/1/31 14:54
 **/
public class ContextUtils {

    /**
     * 获取对应插件
     * param context
     * param pluginName
     *
     * @return
     */
    public static PluginConfiguration getPlugin(Context context, String pluginName) {
        List<PluginConfiguration> list = context.getPluginConfigurations();
        if (list == null || list.size() == 0) {
            return null;
        }
        if (!StringUtility.stringHasValue(pluginName)) {
            return null;
        }
        for (PluginConfiguration plugin : list) {
            if (plugin.getConfigurationType().contains(pluginName)) {
                return plugin;
            }
        }
        return null;
    }


    /**
     * 获取插件对应属性
     * param context
     * param key
     *
     * @return
     */
    public static String getProperty(PluginConfiguration pluginConfiguration, String key) {
        if (pluginConfiguration == null || !StringUtility.stringHasValue(key)) {
            return null;
        }
        return pluginConfiguration.getProperty(key);
    }

    /**
     * 校验extentModelPlugin插件
     * param context
     *
     * @return
     */
    public static PluginConfiguration checkExtendModelPlugin(Context context) {
        PluginConfiguration extentModelPlugin = ContextUtils.getPlugin(context, CommonConstant.EXTEND_MODEL_PLUGIN);
        if (extentModelPlugin == null) {
            throw new RuntimeException("无法获取" + CommonConstant.EXTEND_MODEL_PLUGIN + "插件");
        }
        return extentModelPlugin;
    }
}
