package cn.yananart.tool.utils;

import cn.yananart.tool.common.Constants;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * 系统
 *
 * @author yananart
 * @date 2022/1/25
 */
public class SystemUtil {

    /**
     * 是否为JetBrains的RunTime
     *
     * @return true if it is
     */
    public static boolean isJBR() {
        return Constants.VM_VENDOR.contains("JetBrains");
    }


    /**
     * 为在Mac操作系统上设置系统菜单
     */
    public static void setSystemMenuForMac() {
        if (SystemInfo.isMacOS) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", Constants.APP_NAME);
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", Constants.APP_NAME);
            System.setProperty("apple.awt.application.appearance", "system");

            // 设置应用程序 退出 选单
            FlatDesktop.setQuitHandler(FlatDesktop.QuitResponse::performQuit);
        }
    }
}
