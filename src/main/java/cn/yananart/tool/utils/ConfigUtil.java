package cn.yananart.tool.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.Setting;
import cn.yananart.tool.common.Constants;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * 配置
 *
 * @author zhouye25337
 * @date 2022/1/25
 */
public class ConfigUtil {

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
            return setting.getStr("theme", "setting.appearance", "System Default");
        } else {
            return setting.getStr("theme", "setting.appearance", "IntelliJ Light");
        }
    }
}
