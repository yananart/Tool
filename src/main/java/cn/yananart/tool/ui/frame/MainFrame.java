package cn.yananart.tool.ui.frame;

import cn.hutool.core.thread.ThreadUtil;
import cn.yananart.tool.common.Constants;
import cn.yananart.tool.ui.listener.FrameListener;
import cn.yananart.tool.ui.menu.TopMenuBar;
import cn.yananart.tool.utils.UiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * 主窗口
 *
 * @author yananart
 * @date 2022/1/25
 */
public class MainFrame extends JFrame {

    private static MainFrame instance = null;

    /**
     * 获取单例
     *
     * @return instance
     */
    public static synchronized MainFrame getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MainFrame();
        }
        return instance;
    }

    private MainFrame() {
    }

    public void init() {
        this.setName(Constants.APP_NAME);
        this.setTitle(Constants.APP_NAME);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setMinimumSize(new Dimension(1000, 700));

        TopMenuBar menuBar = TopMenuBar.getInstance();
        menuBar.init();
        this.setJMenuBar(menuBar);

        // 调整窗口位置
        UiUtil.adjustWindow(this);

        // 添加窗口监听
        ThreadUtil.execute(FrameListener::addListeners);
    }

}
