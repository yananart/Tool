package cn.yananart.tool;

import cn.yananart.tool.common.Constants;
import cn.yananart.tool.ui.Init;
import cn.yananart.tool.ui.form.LoadingForm;
import cn.yananart.tool.ui.form.MainForm;
import cn.yananart.tool.ui.frame.MainFrame;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

/**
 * 应用入口
 *
 * @author yananart
 * @date 2022/1/25
 */
@Slf4j
public class App {

    /**
     * main
     *
     * @param args args
     */
    public static void main(String[] args) {
        log.info("App [{}] 启动 ...", Constants.APP_NAME);

        Init.initTheme();

        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.init();

        // 加个Loading
        JPanel loadingPanel = new LoadingForm().getLoadingPanel();
        mainFrame.add(loadingPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);

        Init.initWindowSize(mainFrame);

        mainFrame.setContentPane(new MainForm().getMainPanel());

        // 移除Loading
        mainFrame.remove(loadingPanel);
        mainFrame.setVisible(true);

        log.info("App [{}] 启动成功 ...", Constants.APP_NAME);
    }

}
