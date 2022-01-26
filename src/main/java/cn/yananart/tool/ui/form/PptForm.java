package cn.yananart.tool.ui.form;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.yananart.tool.config.ConfigSaveAction;
import cn.yananart.tool.service.PptService;
import cn.yananart.tool.utils.ConfigUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author yananart
 * @date 2022/1/25
 */
@Slf4j
public class PptForm implements ConfigSaveAction {
    @Getter
    private JPanel pptPanel;

    /**
     * 图片文件夹路径
     */
    private JTextField picturePathField;

    /**
     * 输出文件夹路径
     */
    private JTextField outputPathField;

    /**
     * 选择图片文件夹路径
     */
    private JButton selectPicturePath;

    /**
     * 选择输出路径
     */
    private JButton selectOutputPath;

    /**
     * 执行按钮
     */
    private JButton doActionButton;

    /**
     * 信息输出框
     */
    private JTextArea messageArea;

    /**
     * 等待图标
     */
    private JPanel loadingPanel;

    /**
     * 文件名称
     */
    private JTextField filenameField;

    /**
     * 分隔符号
     */
    private JTextField splitTagField;

    /**
     * 文件选择 图片文件夹
     */
    private final JFileChooser pictureChooser = new JFileChooser();

    /**
     * 文件选择 输出文件夹
     */
    private final JFileChooser outputChooser = new JFileChooser();

    /* 配置参数键 */
    private static final String SET_KEY_PICTURE_PATH = "sourceFolderPath";
    private static final String SET_KEY_OUTPUT_PATH = "outputFolderPath";
    private static final String SET_KEY_PPT_FILENAME = "outputPptFileName";
    private static final String SET_KEY_SPLIT_TAG = "sourceFileSplitTag";


    public PptForm() {
        pictureChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        outputChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        init();
    }


    /**
     * 初始化动作
     */
    private void init() {
        initSettings();

        // 初始化时等待图标不展示
        loadingPanel.setVisible(false);

        PptService.getInstance().setTextArea(messageArea);

        addActionListener();
    }


    /**
     * 初始化配置
     */
    public void initSettings() {
        final ConfigUtil configUtil = ConfigUtil.getInstance();
        // 初始化参数
        String picturePath = configUtil.getPptSetting(SET_KEY_PICTURE_PATH, "");
        picturePathField.setText(picturePath);
        if (StrUtil.isNotBlank(picturePath) && FileUtil.exist(picturePath) && FileUtil.isDirectory(picturePath)) {
            // 必须在UI线程更新
            SwingUtilities.invokeLater(() -> pictureChooser.setCurrentDirectory(FileUtil.file(picturePath)));
        }
        String outputPath = configUtil.getPptSetting(SET_KEY_OUTPUT_PATH, "");
        outputPathField.setText(outputPath);
        if (StrUtil.isNotBlank(outputPath) && FileUtil.exist(outputPath) && FileUtil.isDirectory(outputPath)) {
            // 必须在UI线程更新
            SwingUtilities.invokeLater(() -> outputChooser.setCurrentDirectory(FileUtil.file(outputPath)));
        }
        filenameField.setText(configUtil.getPptSetting(SET_KEY_PPT_FILENAME, ""));
        splitTagField.setText(configUtil.getPptSetting(SET_KEY_SPLIT_TAG, "-"));

        // 配置保存动作
        configUtil.addSaveAction(this);
    }


    /**
     * 添加动作监听
     */
    public void addActionListener() {
        selectPicturePath.addActionListener(action -> {
            int status = pictureChooser.showOpenDialog(PptForm.this.pptPanel);
            if (status == JFileChooser.APPROVE_OPTION) {
                File file = pictureChooser.getSelectedFile();
                picturePathField.setText(file.getAbsolutePath());
            }
        });

        selectOutputPath.addActionListener(action -> {
            int status = outputChooser.showOpenDialog(PptForm.this.pptPanel);
            if (status == JFileChooser.APPROVE_OPTION) {
                File file = outputChooser.getSelectedFile();
                outputPathField.setText(file.getAbsolutePath());
            }
        });

        doActionButton.addActionListener(action -> {
            messageArea.setText("");
            // 图片路径
            final String picturePath = picturePathField.getText().trim();
            if (StrUtil.isBlank(picturePath)) {
                messageArea.append("图片文件夹输入为空\n");
                return;
            }
            if (!FileUtil.exist(picturePath)) {
                messageArea.append(StrUtil.format("文件夹路径不存在 {}\n", picturePath));
                return;
            }
            // 输出路径
            final String outputPath = outputPathField.getText().trim();
            // 文件名
            String pptFileNameTmp = filenameField.getText().trim();
            final String pptFileName;
            if (StrUtil.isBlank(pptFileNameTmp)) {
                pptFileName = DateUtil.format(new Date(), "yyyyMMdd");
                messageArea.append(StrUtil.format("输出文件名称为空，使用默认文件名[{}]\n", pptFileName));
            } else if (FileNameUtil.containsInvalid(pptFileNameTmp)) {
                pptFileName = FileNameUtil.cleanInvalid(pptFileNameTmp);
                messageArea.append(StrUtil.format("输出文件名称有非法字符，转换名称为[{}]\n", pptFileName));
            } else {
                pptFileName = pptFileNameTmp;
            }
            // 分隔符号
            final String splitTag = splitTagField.getText().trim();

            doActionButton.setEnabled(false);
            loadingPanel.setVisible(true);

            ThreadUtil.execute(() -> {
                try {
                    String out = outputPath;
                    if (StrUtil.isBlank(out)) {
                        messageArea.append("输出文件夹路径为空，默认使用图片文件夹路径\n");
                        out = picturePath;
                    } else if (!FileUtil.exist(out)) {
                        messageArea.append("输出文件夹路径不存在，自动创建\n");
                        FileUtil.mkdir(out);
                    }
                    PptService.getInstance().makePicturePpt(picturePath, out, pptFileName, splitTag);
                } catch (IOException ioException) {
                    log.warn("IO异常", ioException);
                    messageArea.append(StrUtil.format("文件读写异常 {}\n", ioException.getMessage()));
                } catch (Exception e) {
                    log.error("未预期异常", e);
                    messageArea.append(StrUtil.format("程序未预期异常 {}\n", e.getMessage()));
                }

                doActionButton.setEnabled(true);
                loadingPanel.setVisible(false);
            });
        });
    }


    @Override
    public void doSave() {
        String picturePath = picturePathField.getText().trim();
        String outputPath = outputPathField.getText().trim();
        String pptFileName = filenameField.getText().trim();
        String splitTag = splitTagField.getText().trim();

        final ConfigUtil configUtil = ConfigUtil.getInstance();

        configUtil.setPptSetting(SET_KEY_PICTURE_PATH, picturePath);
        configUtil.setPptSetting(SET_KEY_OUTPUT_PATH, outputPath);
        configUtil.setPptSetting(SET_KEY_PPT_FILENAME, pptFileName);
        configUtil.setPptSetting(SET_KEY_SPLIT_TAG, splitTag);
    }


    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        pptPanel = new JPanel();
        pptPanel.setLayout(new GridLayoutManager(6, 7, new Insets(10, 10, 0, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("图片目录");
        pptPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("生成目录");
        pptPanel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        picturePathField = new JTextField();
        pptPanel.add(picturePathField, new GridConstraints(0, 1, 1, 5, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        outputPathField = new JTextField();
        pptPanel.add(outputPathField, new GridConstraints(1, 1, 1, 5, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        selectPicturePath = new JButton();
        selectPicturePath.setText("选择");
        pptPanel.add(selectPicturePath, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectOutputPath = new JButton();
        selectOutputPath.setText("选择");
        pptPanel.add(selectOutputPath, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doActionButton = new JButton();
        doActionButton.setText("执行生成");
        pptPanel.add(doActionButton, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        pptPanel.add(spacer1, new GridConstraints(4, 5, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        pptPanel.add(scrollPane1, new GridConstraints(5, 0, 1, 7, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0,
                false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "生成信息",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        scrollPane1.setViewportView(messageArea);
        loadingPanel = new JPanel();
        loadingPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        loadingPanel.setEnabled(true);
        pptPanel.add(loadingPanel, new GridConstraints(4, 2, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0,
                false));
        final JLabel label3 = new JLabel();
        label3.setIcon(new ImageIcon(getClass().getResource("/icons/loading_dark.gif")));
        label3.setText("");
        loadingPanel.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("PPT文件名称");
        pptPanel.add(label4, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filenameField = new JTextField();
        pptPanel.add(filenameField, new GridConstraints(2, 1, 1, 3, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("(不需要带文件后缀,默认按日期命名)");
        pptPanel.add(label5, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        pptPanel.add(spacer2, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("图片名分隔符");
        pptPanel.add(label6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        splitTagField = new JTextField();
        splitTagField.setText("-");
        pptPanel.add(splitTagField, new GridConstraints(3, 1, 1, 3, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("(如图片名称为XX-YYY,取XX开头为一组)");
        pptPanel.add(label7, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1.setLabelFor(picturePathField);
        label2.setLabelFor(outputPathField);
        label4.setLabelFor(filenameField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return pptPanel;
    }

}
