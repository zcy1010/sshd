package com.example.sshd.thread3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.common.util.threads.SshThreadPoolExecutor;
import org.apache.sshd.common.util.threads.SshdThreadFactory;
/**
 * @Author zcy
 * @Date 2024/12/30 18:50
 * @Version 1.0
 */
public class CustomThreadPoolFactoryThree {

    /**
     * 创建自定义线程池
     *
     * @param corePoolSize       核心线程数
     * @param maximumPoolSize    最大线程数
     * @param keepAliveTime      空闲线程存活时间
     * @param queueCapacity      阻塞队列容量
     * @param poolName           线程池名称
     * @param rejectionHandler   拒绝策略
     * @return 自定义的 CloseableExecutorService
     */
    public static CloseableExecutorService create(
        int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        int queueCapacity,
        String poolName,
        RejectedExecutionHandler rejectionHandler) {

        BlockingQueue<Runnable> workQueue;

        // 如果阻塞队列容量为 0，使用 SynchronousQueue，否则使用 ArrayBlockingQueue
        if (queueCapacity == 0) {
            workQueue = new SynchronousQueue<>();
        } else {
            workQueue = new ArrayBlockingQueue<>(queueCapacity);
        }

        // 创建线程池
        return new SshThreadPoolExecutor(
            corePoolSize,                            // 核心线程数
            maximumPoolSize,                         // 最大线程数
            keepAliveTime, TimeUnit.SECONDS,         // 空闲线程存活时间
            workQueue,                               // 阻塞队列
            new SshdThreadFactory(poolName),         // 自定义线程工厂
            rejectionHandler                         // 自定义拒绝策略
        );
    }
}
