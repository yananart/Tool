package cn.yananart.tool.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static final PptService INSTANCE = new PptService();

    public static PptService getInstance() {
        return INSTANCE;
    }

    private PptService() {
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
        ppt.setPageSize(new Dimension(1920, 1080));

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

            XSLFSlide slide = ppt.createSlide();
            int num = picDataList.size();

            for (int index = 0; index < num; index++) {
                XSLFPictureData pictureData = picDataList.get(index);
                XSLFPictureShape pictureShape = slide.createPicture(pictureData);
                pictureShape.setAnchor(new Rectangle(100 * (index + 1) + index * 300, 300, 300, 200));
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
