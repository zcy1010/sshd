package com.example.sshd.server;

import com.example.sshd.CustomFileSystemFactory;
import com.example.sshd.CustomIoServiceEventListener;
import com.example.sshd.CustomSessionListener;
import com.example.sshd.CustomSftpEventListener;
import com.example.sshd.thread2.CustomIoServiceFactoryFactoryTwo;
import com.example.sshd.thread2.CustomThreadPool;
import com.example.sshd.thread3.CustomIoServiceFactoryFactoryThree;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.common.util.threads.ThreadUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

/**
 *
 * 服务端
 * @Author zcy
 * @Date 2024/12/17 17:07
 * @Version 1.0
 */
public class MyServer {

    public static void main(String[] args) throws IOException {
        SshServer sshServer = SshServer.setUpDefaultServer();
        // 设置主机密钥
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
        sshServer.setHost("0.0.0.0");
        sshServer.setPort(2222);
        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
            public boolean authenticate(String username, String password, ServerSession session) {
                System.out.println("服务端 username "+username+"password "+password);

                // 在这里编写密码认证的逻辑
                return ("zcy".equals(username) && "101072".equals(password))||("zcy2".equals(username) && "101072".equals(password))||("zcy3".equals(username) && "101072".equals(password));

            }
        });


        sshServer.setShellFactory(new ProcessShellFactory("powershell.exe", "-Command"));
        sshServer.addSessionListener(new CustomSessionListener());

        sshServer.getProperties().put("auth-timeout", "10000"); // 10秒认证超时
        sshServer.getProperties().put("idle-timeout", "200000"); // 20秒空闲超时
        sshServer.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);

        // 设置用户在 SFTP 中的根目录
        // 使用自定义的文件系统工厂
//        sshServer.setFileSystemFactory(new CustomFileSystemFactory());

//        String workingDir = "C:\\Users\\Administrator\\Desktop\\boctmp\\tmp";
//        String workingDir1 = "C:\\Users\\Administrator\\Desktop\\boctmp\\1";
//        String workingDir2 = "C:\\Users\\Administrator\\Desktop\\boctmp\\2";
//        sshServer.setFileSystemFactory(new VirtualFileSystemFactory());
//        VirtualFileSystemFactory fileSystemFactory=new VirtualFileSystemFactory();
//        fileSystemFactory.setUserHomeDir("zcy",Paths.get(workingDir1));
//        fileSystemFactory.setUserHomeDir("zcy2",Paths.get(workingDir2));
//        fileSystemFactory.setDefaultHomeDir(Paths.get(workingDir));
        sshServer.setFileSystemFactory(new CustomFileSystemFactory());


        // 配置 SFTP 子系统（如果需要的话）
        SftpSubsystemFactory factory = new SftpSubsystemFactory.Builder().build();

        // 注册自定义文件校验监听器
        factory.addSftpEventListener(new CustomSftpEventListener());
        sshServer.setSubsystemFactories(Collections.singletonList(factory));
        //TODO 自定义线程池

        // TODO 1
        // 自定义 NIO Workers 默认cpu核心线程数+1 负责处理网络的 IO 操作。比如处理 Socket 的读写操作，解析协议等。
        sshServer.setNioWorkers(10);
//        sshServer.getNioWorkers();
//        System.out.println(" sshServer.getNioWorkers();"+ sshServer.getNioWorkers());
        // 自定义定时线程池
//        ScheduledExecutorService customScheduler = Executors.newScheduledThreadPool(2);
//        sshServer.setScheduledExecutorService(customScheduler, false);
//        System.out.println("ddddd     "+sshServer.getScheduledExecutorService().toString());
        // 使用自定义的 IoServiceFactoryFactory
//        sshServer.setIoServiceFactoryFactory(new CustomIoServiceFactoryFactory());

        // TODO 2
        // 使用自定义线程池
//        CloseableExecutorService executorService = CustomThreadPool.create(4, "SSHwwD");
        // 创建 resume tasks 线程池
//        CloseableExecutorService resumeTasksExecutorService = ThreadUtils.newFixedThreadPool("dfgdsfgsdfg-tasks-thread-pool", 2);
        // 使用自定义的 IoServiceFactoryFactory
//        sshServer.setIoServiceFactoryFactory(new CustomIoServiceFactoryFactoryTwo(executorService, resumeTasksExecutorService));

        // TODO 3
//        sshServer.setIoServiceFactoryFactory(CustomIoServiceFactoryFactoryThree.createWithCustomPools());
        // 配置自定义线程池
        CustomIoServiceFactoryFactoryThree ioFactory = CustomIoServiceFactoryFactoryThree.createWithCustomPools();
        sshServer.setIoServiceFactoryFactory(ioFactory);
        CustomIoServiceEventListener  customIoServiceEventListener=new CustomIoServiceEventListener();
        sshServer.setIoServiceEventListener(customIoServiceEventListener);
        sshServer.start();
        // 打印线程池状态
//        logThreadPoolStatus(ioFactory);
        System.out.println(sshServer.getNioWorkers());

        System.out.println("SSH server started...");
        try {
            // 在此处，主线程会被阻塞，直到你手动停止
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private static void logThreadPoolStatus(CustomIoServiceFactoryFactoryThree ioFactory) {
        ThreadPoolExecutor mainExecutor = (ThreadPoolExecutor) ioFactory.getMainExecutorService();
        ThreadPoolExecutor resumeExecutor = (ThreadPoolExecutor) ioFactory.getResumeTasksExecutorService();

        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("主线程池状态:");
                    System.out.println("  活跃线程数: " + mainExecutor.getActiveCount());
                    System.out.println("  任务队列大小: " + mainExecutor.getQueue().size());
                    System.out.println("  已完成任务数: " + mainExecutor.getCompletedTaskCount());

                    System.out.println("辅助线程池状态:");
                    System.out.println("  活跃线程数: " + resumeExecutor.getActiveCount());
                    System.out.println("  任务队列大小: " + resumeExecutor.getQueue().size());
                    System.out.println("  已完成任务数: " + resumeExecutor.getCompletedTaskCount());

                    // 每隔 5 秒打印一次
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }).start();
    }

}
