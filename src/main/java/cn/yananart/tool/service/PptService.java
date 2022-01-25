package cn.yananart.tool.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.*;
import java.util.List;

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
     * @param picturePath 图片文件路径
     * @param outputPath  输出文件路径
     * @param pptFileName ppt文件名
     * @param splitTag    文件名称分隔符
     * @throws IOException IO异常
     */
    public void makePicturePpt(String picturePath, String outputPath, String pptFileName, String splitTag) throws IOException {
        // 所有文件
        List<String> fileNameList = FileUtil.listFileNames(picturePath);
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

            String message;
            switch (fileType) {
                case "jpg":
                case "jpeg":
                    message = StrUtil.format("读取到 JPG图片 {} <- {}", name, filePath);
                    log.info(message);
                    picDataList.add(ppt.addPicture(FileUtil.file(filePath), PictureData.PictureType.JPEG));
                    break;
                case "png":
                    message = StrUtil.format("读取到 PNG图片 {} <- {}", name, filePath);
                    log.info(message);
                    picDataList.add(ppt.addPicture(FileUtil.file(filePath), PictureData.PictureType.PNG));
                    break;
                case "gif":
                    message = StrUtil.format("读取到 GIF图片 {} <- {}", name, filePath);
                    log.info(message);
                    picDataList.add(ppt.addPicture(FileUtil.file(filePath), PictureData.PictureType.GIF));
                    break;
                default:
                    message = StrUtil.format("不支持的文件类型 不能识别 {}", filePath);
                    log.warn(message);
                    break;
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

            // 文本框 TODO 优化
            XSLFTextBox textBox = slide.createTextBox();
            textBox.setAnchor(new Rectangle(60, 50, 1800, 64));
            XSLFTextParagraph textParagraph = textBox.addNewTextParagraph();
            XSLFTextRun textRun = textParagraph.addNewTextRun();
            textRun.setBold(true);
            textRun.setText(name);
            textRun.setFontSize(64.);

            // 图片处理
            int num = picDataList.size();
            // TODO if num>3 not support

            // 计算图片长宽
            double widthInAvg = BigDecimal.valueOf((WIDTH - 120) - (num - 1) * 60L)
                    .divide(BigDecimal.valueOf(num), RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
            // 按顺序添加到幻灯片中
            // TODO 2~3张这个逻辑是可以的 1张就太大了
            // TODO 4张及以上暂时不考虑 4~6张的后续可以做一些
            for (int index = 0; index < num; index++) {
                XSLFPictureData pictureData = picDataList.get(index);
                XSLFPictureShape pictureShape = slide.createPicture(pictureData);
                double height = pictureData.getImageDimension().getHeight();
                double width = pictureData.getImageDimension().getWidth();

                height = BigDecimal.valueOf(height)
                        .multiply(BigDecimal.valueOf(widthInAvg))
                        .divide(BigDecimal.valueOf(width), RoundingMode.HALF_UP)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();

                int x = 60 * (index + 1) + (int) (index * widthInAvg);
                int y = (int) (HEIGHT / 2 - height / 2) + 50;

                pictureShape.setAnchor(new Rectangle(x, y, (int) widthInAvg, (int) height));
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
        getInstance().makePicturePpt(picPath, picPath, "test", "-");
    }
}
