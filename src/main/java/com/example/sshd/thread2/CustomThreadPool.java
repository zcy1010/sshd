package com.example.sshd.thread2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.common.util.threads.ThreadUtils;

/**
 * @Author zcy
 * @Date 2024/12/27 8:57
 * @Version 1.0
 */
public class CustomThreadPool {
    public static CloseableExecutorService create(
        int corePoolSize, String poolName) {
        // 使用 ThreadUtils.newFixedThreadPool 创建线程池
        return ThreadUtils.newFixedThreadPool(poolName, corePoolSize);
    }
}
