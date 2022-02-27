package cn.yananart.tool.ui.listener;

import cn.yananart.tool.App;
import cn.yananart.tool.common.Constants;
import cn.yananart.tool.ui.frame.MainFrame;
import cn.yananart.tool.utils.ConfigUtil;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author yananart
 * @date 2022/1/13
 */
@Slf4j
public class FrameListener {

    /**
     * 窗口监听器
     */
    public static void addListeners() {
        MainFrame.getInstance().addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                log.info("收到窗口关闭事件");
                if (SystemInfo.isWindows) {
                    MainFrame.getInstance().setVisible(false);
                } else {
                    MainFrame.getInstance().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                log.info("保存应用配置信息");
                // 保存配置
                ConfigUtil.getInstance().save();
                log.info("关闭应用 [{}]", Constants.APP_NAME);
                // 关闭程序
                App.shutdown();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }
        });

    }

}
