package cn.yananart.tool.utils;

import javax.swing.*;
import java.awt.*;

/**
 * UI工具
 *
 * @author yananart
 * @date 2022/1/18
 */
public class UiUtil {

    /**
     * 修改主题
     *
     * @param lookAndFeel 主题
     */
    public static void changeTheme(LookAndFeel lookAndFeel) {
        try {
            // 修改一个更优化的LookAndFeel
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            System.out.println("WARN 加载LookAndFeel失败");
            e.printStackTrace();
        }
    }


    /**
     * 调整窗口位置
     */
    public static void adjustWindow(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    }

}
