package cn.yananart.tool.common;

import java.io.File;

/**
 * 常量
 *
 * @author yananart
 * @date 2022/1/25
 */
public class Constants {

    /**
     * 应用名
     */
    public static final String APP_NAME = "Tool";

    /**
     * 操作系统的名称
     */
    public static final String OS_NAME = System.getProperty("os.name");
    /**
     * 操作系统的架构
     */
    public static final String OS_ARCH = System.getProperty("os.arch");
    /**
     * 虚拟机实现供应商
     */
    public static final String VM_VENDOR = System.getProperty("java.vm.vendor");
    /**
     * 用户目录
     */
    public static final String USER_HOME = System.getProperty("user.home");
    /**
     * 应用目录
     */
    public static final String APP_HOME = USER_HOME + File.separator + ".Yananart";
    /**
     * 配置
     */
    public static final String CONFIG_FILE_PATH = APP_HOME + File.separator + "config.setting";
    /**
     * 日志
     */
    public final static String LOG_DIR = APP_HOME + File.separator + "logs" + File.separator;

}
