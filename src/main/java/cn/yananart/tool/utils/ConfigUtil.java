package cn.yananart.tool.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.yananart.tool.common.Constants;
import cn.yananart.tool.config.ConfigSaveAction;
import com.formdev.flatlaf.util.SystemInfo;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 配置
 *
 * @author yananart
 * @date 2022/1/25
 */
public class ConfigUtil {

    /**
     * 外观配置
     */
    private static final String SETTING_APPEARANCE = "setting.appearance";

    /**
     * ppt功能设置
     */
    private static final String SETTING_FUNCTION_PPT = "setting.function.ppt";

    /**
     * 配置保存
     */
    private final Set<ConfigSaveAction> saveActionList = new HashSet<>();

    /**
     * 单例
     */
    private static final ConfigUtil INSTANCE = new ConfigUtil();

    private final Setting setting;

    public static ConfigUtil getInstance() {
        return INSTANCE;
    }

    private ConfigUtil() {
        this.setting = new Setting(FileUtil.touch(Constants.CONFIG_FILE_PATH), CharsetUtil.CHARSET_UTF_8, false);
    }

    /**
     * 获取主题
     *
     * @return string
     */
    public String getTheme() {
        if (SystemInfo.isLinux) {
            return setting.getStr("theme", SETTING_APPEARANCE, "System Default");
        } else {
            return setting.getStr("theme", SETTING_APPEARANCE, "IntelliJ Light");
        }
    }


    /**
     * 添加配置保存动作
     *
     * @param saveAction 保存动作
     */
    public void addSaveAction(ConfigSaveAction saveAction) {
        if (Objects.nonNull(saveAction)) {
            saveActionList.add(saveAction);
        }
    }


    /**
     * save to disk
     */
    public void save() {
        if (CollUtil.isNotEmpty(saveActionList)) {
            for (ConfigSaveAction action : saveActionList) {
                action.doSave();
            }
        }
        this.setting.store(Constants.CONFIG_FILE_PATH);
    }


    /**
     * 获取PPT功能的默认参数
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return value
     */
    public String getPptSetting(String key, String defaultValue) {
        return this.setting.getStr(key, SETTING_FUNCTION_PPT, defaultValue);
    }


    /**
     * 设置PPT功能的参数
     *
     * @param key   key
     * @param value value
     */
    public void setPptSetting(String key, String value) {
        this.setting.setByGroup(key, SETTING_FUNCTION_PPT, value);
    }
}
