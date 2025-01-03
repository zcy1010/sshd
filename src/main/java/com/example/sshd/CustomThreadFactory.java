package com.example.sshd;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author zcy
 * @Date 2024/12/30 16:33
 * @Version 1.0
 */
public class CustomThreadFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String namePrefix;

    public CustomThreadFactory(String poolName) {
        this.namePrefix = "custom-" + poolName + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, namePrefix + counter.incrementAndGet());
        t.setDaemon(true); // 设置线程为守护线程
        t.setPriority(Thread.NORM_PRIORITY); // 设置默认优先级
        return t;
    }
}
