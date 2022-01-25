package cn.yananart.tool.utils;

import cn.yananart.tool.common.Constants;

/**
 * 系统
 *
 * @author zhouye25337
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
}
