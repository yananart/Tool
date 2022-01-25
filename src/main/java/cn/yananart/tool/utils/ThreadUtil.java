package cn.yananart.tool.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程
 *
 * @author yananart
 * @date 2022/1/25
 */
public class ThreadUtil {

    private static final Executor EXECUTOR = Executors.newFixedThreadPool(2);

    public static void runOnBack(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }
}
