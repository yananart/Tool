package cn.yananart.tool.ui.frame;

import cn.yananart.tool.common.Constants;
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
        // TODO 不要使用这个动作 添加一个监听器来实现 在关闭程序前要保持输入项
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(1000, 700));

        // 调整窗口位置
        UiUtil.adjustWindow(this);
    }

}
