package cn.yananart.tool.ui;

import cn.yananart.tool.App;
import cn.yananart.tool.ui.frame.MainFrame;
import cn.yananart.tool.utils.ConfigUtil;
import cn.yananart.tool.utils.SystemUtil;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * 初始化
 *
 * @author yananart
 * @date 2022/1/25
 */
@Slf4j
public class Init {

    /**
     * init look and feel
     */
    public static void initTheme() {
        try {
            switch (ConfigUtil.getInstance().getTheme()) {
                case "System Default" -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                case "Flat Light" -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    FlatLightLaf.install();
                }
                case "Flat IntelliJ" -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatIntelliJLaf");
                }
                case "Flat Dark" -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
                }
                case "Darcula", "Darcula(Recommended)", "Flat Darcula(Recommended)" -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
                    UIManager.put("PopupMenu.background", UIManager.getColor("Panel.background"));
                }
                case "Dark purple" -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    IntelliJTheme.setup(App.class.getResourceAsStream("/theme/DarkPurple.theme.json"));
                }
                case "IntelliJ Cyan" -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    IntelliJTheme.setup(App.class.getResourceAsStream("/theme/Cyan.theme.json"));
                }
                case "IntelliJ Light" -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    IntelliJTheme.setup(App.class.getResourceAsStream("/theme/Light.theme.json"));
                }
                default -> {
                    if (SystemUtil.isJBR()) {
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);
                    }
                    UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
                }
            }
        } catch (Exception e) {
            log.error("init theme error", e);
        }
    }


    /**
     * 初始化窗口大小
     *
     * @param frame 窗口
     */
    public static void initWindowSize(Frame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.getWidth() <= 1366) {
            // The window is automatically maximized at low resolution
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }
}
