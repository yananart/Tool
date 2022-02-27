package cn.yananart.tool.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.yananart.tool.utils.PictureUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xslf.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.*;

/**
 * PPT
 *
 * @author yananart
 * @date 2022/1/25
 */
@Slf4j
public class PptService {

    /**
     * 幻灯片高度
     */
    private static final int HEIGHT = 1080;

    /**
     * 幻灯片宽度
     */
    private static final int WIDTH = 1920;

    /**
     * 图片最高高度
     */
    private static final int PIC_MAX_HEIGHT = 800;

    /**
     * 图片最宽宽度
     */
    private static final int PIC_MAX_WIDTH = 1000;

    private static final PptService INSTANCE = new PptService();

    public static PptService getInstance() {
        return INSTANCE;
    }

    private PptService() {
        // TODO 直接设置了最大 建议优化 压缩图片
        IOUtils.setByteArrayMaxOverride(Integer.MAX_VALUE);
    }

    @Setter
    private JTextArea textArea = null;


    /**
     * 生成PPT
     *
     * @param picturePath      图片文件路径
     * @param outputPath       输出文件路径
     * @param pptFileName      ppt文件名
     * @param splitTag         文件名称分隔符
     * @param enableReduceSize 启用图片压缩
     * @throws IOException IO异常
     */
    public void makePicturePpt(String picturePath,
                               String outputPath,
                               String pptFileName,
                               String splitTag,
                               boolean enableReduceSize) throws IOException {
        // 所有文件
        List<String> fileNameList = FileUtil.listFileNames(picturePath);
        CollUtil.sortByPinyin(fileNameList);
        // 图片文件与其映射关系
        Map<String, List<XSLFPictureData>> picFileMap = new TreeMap<>();

        log.info("开始进行PPT生成逻辑");
        printLog("开始进行PPT生成逻辑");
        // ppt文件对象
        XMLSlideShow ppt = new XMLSlideShow();
        ppt.setPageSize(new Dimension(WIDTH, HEIGHT));

        // 遍历所有文件 获取映射关系
        for (String fileName : fileNameList) {
            // 前面名称部分
            String name = FileNameUtil.getPrefix(fileName);
            // 文件类型
            String fileType = FileNameUtil.getSuffix(fileName).toLowerCase();

            // 查看文件有没有分隔符号
            if (StrUtil.isNotBlank(splitTag) && name.contains(splitTag)) {
                name = name.split(splitTag)[0].trim();
            }

            // 全路径
            String filePath = picturePath + File.separator + fileName;

            if (!picFileMap.containsKey(name)) {
                picFileMap.put(name, new ArrayList<>());
            }

            List<XSLFPictureData> picDataList = picFileMap.get(name);

            File file = FileUtil.file(filePath);

            String message;
            XSLFPictureData pictureData;
            switch (fileType) {
                case "jpg", "jpeg" -> {
                    message = StrUtil.format("读取到 JPG图片 {} <- {}", name, filePath);
                    if (enableReduceSize) {
                        pictureData = ppt.addPicture(resizePicture(file), PictureData.PictureType.JPEG);
                        message += "，执行压缩";
                    } else {
                        pictureData = ppt.addPicture(file, PictureData.PictureType.JPEG);
                    }
                    log.info(message);
                    picDataList.add(pictureData);
                }
                case "png" -> {
                    message = StrUtil.format("读取到 PNG图片 {} <- {}", name, filePath);
                    if (enableReduceSize) {
                        pictureData = ppt.addPicture(resizePicture(file), PictureData.PictureType.JPEG);
                        message += "，执行压缩";
                    } else {
                        pictureData = ppt.addPicture(file, PictureData.PictureType.PNG);
                    }
                    log.info(message);
                    picDataList.add(pictureData);
                }
                case "gif" -> {
                    message = StrUtil.format("读取到 GIF图片 {} <- {}", name, filePath);
                    if (enableReduceSize) {
                        message += "，GIF图片不执行压缩";
                    }
                    log.info(message);
                    picDataList.add(ppt.addPicture(FileUtil.file(filePath), PictureData.PictureType.GIF));
                }
                case "bmp" -> {
                    message = StrUtil.format("读取到 BMP图片 {} <- {}", name, filePath);
                    if (enableReduceSize) {
                        message += "，BMP图片不执行压缩";
                    }
                    log.info(message);
                    picDataList.add(ppt.addPicture(FileUtil.file(filePath), PictureData.PictureType.BMP));
                }
                default -> {
                    message = StrUtil.format("不支持的文件类型 不能识别 {}", filePath);
                    log.warn(message);
                }
            }
            printLog(message);
        }

        log.info("开始处理图片生成PPT");
        printLog("开始处理图片生成PPT");

        int page = 1;
        // 遍历所有归档图片
        for (String name : picFileMap.keySet()) {
            List<XSLFPictureData> picDataList = picFileMap.get(name);
            if (CollUtil.isEmpty(picDataList)) {
                continue;
            }

            String message = StrUtil.format("创建第{}张幻灯片 名称={}", page++, name);
            log.info(message);
            printLog(message);

            // 创建幻灯片
            XSLFSlide slide = ppt.createSlide();

            // 文本框
            XSLFTextBox textBox = slide.createTextBox();
            textBox.setAnchor(new Rectangle(60, 50, 1800, 64));
            XSLFTextParagraph textParagraph = textBox.addNewTextParagraph();
            XSLFTextRun textRun = textParagraph.addNewTextRun();
            textRun.setBold(true);
            textRun.setText(name);
            textRun.setFontSize(64.);

            // 图片处理
            int num = picDataList.size();

            // 计算图片长宽
            double widthInAvg =
                    BigDecimal.valueOf((WIDTH - 120) - (num - 1) * 60L).divide(BigDecimal.valueOf(num),
                            RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP).doubleValue();
            // 按顺序添加到幻灯片中
            // 1~3张横着的图这个逻辑都还可以 竖着的图会有点丑
            // TODO 4张及以上暂时不考虑 4~6张的后续可以做一些
            for (int index = 0; index < num; index++) {
                XSLFPictureData pictureData = picDataList.get(index);
                XSLFPictureShape pictureShape = slide.createPicture(pictureData);
                double height = pictureData.getImageDimension().getHeight();
                double width = pictureData.getImageDimension().getWidth();

                // 调整后的宽度按计算的平均值
                int adjustWidth = (int) widthInAvg;
                // 调整后的高度
                int adjustHeight =
                        BigDecimal.valueOf(height).multiply(BigDecimal.valueOf(widthInAvg)).divide(BigDecimal.valueOf(width), RoundingMode.HALF_UP).intValue();
                // 如果高度过高
                if (adjustHeight > PIC_MAX_HEIGHT) {
                    // 再次调整 把高度缩放到800
                    adjustWidth =
                            BigDecimal.valueOf(adjustWidth).multiply(BigDecimal.valueOf(PIC_MAX_HEIGHT)).divide(BigDecimal.valueOf(adjustHeight), RoundingMode.HALF_UP).intValue();
                    adjustHeight = PIC_MAX_HEIGHT;
                }

                int x =
                        60 * (index + 1) + BigDecimal.valueOf(index + 0.5).multiply(BigDecimal.valueOf(widthInAvg)).subtract(BigDecimal.valueOf(adjustWidth).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)).intValue();
                int y = (HEIGHT / 2 - adjustHeight / 2) + 50;

                pictureShape.setAnchor(new Rectangle(x, y, adjustWidth, adjustHeight));
            }
        }

        log.info("PPT文件导出");

        FileOutputStream outputStream = new FileOutputStream(outputPath + File.separator + pptFileName + ".pptx");
        ppt.write(outputStream);
        outputStream.close();

        log.info("PPT文件导出 结束");
        printLog("PPT文件导出 完成");
    }


    /**
     * 修改图片大小
     *
     * @param file 文件
     * @return 二进制数组
     * @throws IOException IO异常
     */
    private byte[] resizePicture(File file) throws IOException {
        return PictureUtil.resizeAsJpgImage(file, PIC_MAX_HEIGHT, PIC_MAX_WIDTH);
    }


    /**
     * 打印日志
     *
     * @param message message
     */
    private void printLog(String message) {
        if (Objects.nonNull(textArea)) {
            textArea.append(message + "\n");
        }
    }

    public static void main(String[] args) throws IOException {
        String picPath = "/Users/yananart/Desktop/pic";
        getInstance().makePicturePpt(picPath, picPath, "test", "-", false);
    }
}
