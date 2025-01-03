package com.example.sshd.thread1;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.FactoryManager;
import org.apache.sshd.common.io.BuiltinIoServiceFactoryFactories;
import org.apache.sshd.common.io.DefaultIoServiceFactoryFactory;
import org.apache.sshd.common.io.IoServiceFactory;
import org.apache.sshd.common.io.IoServiceFactoryFactory;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.common.util.threads.SshThreadPoolExecutor;
import org.apache.sshd.common.util.threads.ThreadUtils;

/**
 * @Author zcy
 * @Date 2024/12/27 10:13
 * @Version 1.0
 */
public class CustomIoServiceFactoryFactory implements IoServiceFactoryFactory {

    private Factory<CloseableExecutorService> executorServiceFactory;



    @Override
    public IoServiceFactory create(FactoryManager factoryManager) {
        System.out.println("1Using custom IoServiceFactoryFactory based on NIO2");

        // 使用自定义的线程池工厂
        if (executorServiceFactory != null) {
            CloseableExecutorService executorService = executorServiceFactory.create();
            System.out.println("Custom thread pool created by executorServiceFactory");
        }
        // 使用内置的 NIO2 实现作为基础
        IoServiceFactoryFactory defaultFactory = BuiltinIoServiceFactoryFactories.NIO2.create();
//        IoServiceFactoryFactory defaultFactory2 =BuiltinIoServiceFactoryFactories.MINA.create();
//        return defaultFactory2.create(factoryManager);


        System.out.println("2Using custom IoServiceFactoryFactory based on NIO2");
        return defaultFactory.create(factoryManager);
    }

    @Override
    public void setExecutorServiceFactory(Factory<CloseableExecutorService> factory) {
        this.executorServiceFactory = factory;

    }

    public CustomIoServiceFactoryFactory() {
        // 设置自定义线程池工厂
        this.executorServiceFactory = () -> {
            System.out.println("Creating custom thread pool...");

            // 自定义线程池
            int corePoolSize = 4; // 核心线程数
            int maximumPoolSize = 10; // 最大线程数
            long keepAliveTime = 60L; // 非核心线程存活时间
            TimeUnit timeUnit = TimeUnit.SECONDS;
            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100); // 阻塞队列

            // 自定义线程工厂
            ThreadFactory customThreadFactory = new ThreadFactory() {
                private int threadCount = 0;

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Cust12121212121212om-Ssh-ThreadPool-" + (++threadCount));
                    System.out.println("线程池被创建: " + thread.getName());
                    return thread;
                }
            };
            // 使用 SshThreadPoolExecutor 创建自定义线程池
            return new SshThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                timeUnit,
                workQueue,
                customThreadFactory, // 传递正确的 ThreadFactory
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
            );

        };
    }

}
