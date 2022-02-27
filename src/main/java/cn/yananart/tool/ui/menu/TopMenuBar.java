package cn.yananart.tool.ui.menu;

import cn.yananart.tool.App;
import cn.yananart.tool.common.Constants;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 顶部菜单栏
 *
 * @author yananart
 * @date 2022/2/27
 */
@Slf4j
public class TopMenuBar extends JMenuBar {

    private static TopMenuBar instance;

    private TopMenuBar() {
    }

    public synchronized static TopMenuBar getInstance() {
        if (instance == null) {
            instance = new TopMenuBar();
        }
        return instance;
    }


    /**
     * 初始化菜单栏
     */
    public void init() {
        addAppMenu();
    }


    /**
     * 应用程序菜单
     */
    public void addAppMenu() {
        // ---------App
        JMenu appMenu = new JMenu();
        appMenu.setText("Tool程序");

        // Show logs
        JMenuItem logMenuItem = new JMenuItem();
        logMenuItem.setText("查看日志");
        logMenuItem.addActionListener(e -> logActionPerformed());
        appMenu.add(logMenuItem);

        // Exit
        JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("退出程序");
        exitMenuItem.addActionListener(e -> App.shutdown());
        appMenu.add(exitMenuItem);
        getInstance().add(appMenu);
    }


    /**
     * 显示日志
     */
    private void logActionPerformed() {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(Constants.LOG_DIR));
        } catch (Exception e) {
            log.error("Show log failed", e);
        }
    }
}
