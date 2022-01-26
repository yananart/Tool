package cn.yananart.tool.utils;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * 图片工具
 *
 * @author yananart
 * @date 2022/1/26
 */
public class PictureUtil {

    /**
     * 缩小文件大小
     *
     * @param file   file
     * @param height height
     * @param width  width
     * @return 缩小后图片的二进制数据
     */
    public static byte[] resizeImage(File file, int height, int width) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file)
                .size(width, height)
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

    /**
     * 缩小文件大小
     *
     * @param file   file
     * @param height height
     * @param width  width
     * @return 缩小后图片的二进制数据 并处理为JPG
     */
    public static byte[] resizeAsJpgImage(File file, int height, int width) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file)
                .size(width, height)
                .outputFormat("jpg")
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }
}
