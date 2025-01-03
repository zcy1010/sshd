package com.example.sshd.thread3;

import com.example.sshd.thread1.CustomIoServiceFactoryFactory;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.FactoryManager;
import org.apache.sshd.common.io.IoServiceFactory;
import org.apache.sshd.common.io.IoServiceFactoryFactory;
import org.apache.sshd.common.io.nio2.Nio2ServiceFactory;
import org.apache.sshd.common.util.threads.CloseableExecutorService;

/**
 * @Author zcy
 * @Date 2024/12/30 18:51
 * @Version 1.0
 */
public class CustomIoServiceFactoryFactoryThree implements IoServiceFactoryFactory {
    private final CloseableExecutorService mainExecutorService;
    private final CloseableExecutorService resumeTasksExecutorService;


    public CustomIoServiceFactoryFactoryThree(CloseableExecutorService mainExecutorService,
        CloseableExecutorService resumeTasksExecutorService) {
        this.mainExecutorService = mainExecutorService;
        this.resumeTasksExecutorService = resumeTasksExecutorService;
    }

    @Override
    public IoServiceFactory create(FactoryManager factoryManager) {
        return new Nio2ServiceFactory(factoryManager, mainExecutorService, resumeTasksExecutorService);
    }

    @Override
    public void setExecutorServiceFactory(Factory<CloseableExecutorService> factory) {

    }
    public static CustomIoServiceFactoryFactoryThree createWithCustomPools() {
        // 创建主线程池
        CloseableExecutorService mainExecutorService = CustomThreadPoolFactoryThree.create(
            4,                      // 核心线程数
            4,                     // 最大线程数
            60L,                    // 空闲线程存活时间
            2,                    // 队列容量
            "main-thrasasasead-pool",     // 线程池名称
            new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
        );

        // 创建辅助线程池（resume tasks）
        CloseableExecutorService resumeTasksExecutorService = CustomThreadPoolFactoryThree.create(
            2,                      // 核心线程数
            4,                      // 最大线程数
            60L,                    // 空闲线程存活时间
            50,                     // 队列容量
            "resume-thread-pool",   // 线程池名称
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
        );

        return new CustomIoServiceFactoryFactoryThree(mainExecutorService, resumeTasksExecutorService);
    }

    // Getter for main executor service
    public CloseableExecutorService getMainExecutorService() {
        return mainExecutorService;
    }

    // Getter for resume tasks executor service
    public CloseableExecutorService getResumeTasksExecutorService() {
        return resumeTasksExecutorService;
    }
}
