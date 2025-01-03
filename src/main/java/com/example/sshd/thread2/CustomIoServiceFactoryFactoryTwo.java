package com.example.sshd.thread2;

import org.apache.sshd.common.Factory;
import org.apache.sshd.common.FactoryManager;
import org.apache.sshd.common.io.IoServiceFactory;
import org.apache.sshd.common.io.IoServiceFactoryFactory;
import org.apache.sshd.common.io.nio2.Nio2ServiceFactory;
import org.apache.sshd.common.util.threads.CloseableExecutorService;

/**
 * @Author zcy
 * @Date 2024/12/30 16:41
 * @Version 1.0
 */
public class CustomIoServiceFactoryFactoryTwo  implements IoServiceFactoryFactory {
    private final CloseableExecutorService executorService;
    private final CloseableExecutorService resumeTasksExecutorService;


    public CustomIoServiceFactoryFactoryTwo(CloseableExecutorService executorService,
        CloseableExecutorService resumeTasksExecutorService) {
        this.executorService = executorService;
        this.resumeTasksExecutorService = resumeTasksExecutorService;
    }

    @Override
    public IoServiceFactory create(FactoryManager manager) {
        return new Nio2ServiceFactory(manager, executorService,resumeTasksExecutorService);
    }

    @Override
    public void setExecutorServiceFactory(Factory<CloseableExecutorService> factory) {

    }
}
