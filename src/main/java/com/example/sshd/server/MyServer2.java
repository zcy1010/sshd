//package com.example.sshd.server;
//
//import com.example.sshd.CustomFileSystemFactory;
//import com.example.sshd.CustomSessionListener;
//import com.example.sshd.CustomSftpEventListener;
//import com.example.sshd.thread2.CustomIoServiceFactoryFactoryTwo;
//import com.example.sshd.thread2.CustomThreadPool;
//import com.example.sshd.thread3.CustomIoServiceFactoryFactoryThree;
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import org.apache.sshd.client.ClientBuilder;
//import org.apache.sshd.common.BaseBuilder;
//import org.apache.sshd.common.NamedFactory;
//import org.apache.sshd.common.cipher.BuiltinCiphers;
//import org.apache.sshd.common.cipher.Cipher;
//import org.apache.sshd.common.kex.BuiltinDHFactories;
//import org.apache.sshd.common.kex.KeyExchangeFactory;
//import org.apache.sshd.common.kex.extension.KexExtensionHandler;
//import org.apache.sshd.common.mac.BuiltinMacs;
//import org.apache.sshd.common.mac.Mac;
//import org.apache.sshd.common.session.Session;
//import org.apache.sshd.common.session.SessionContext;
//import org.apache.sshd.common.util.buffer.Buffer;
//import org.apache.sshd.common.util.threads.CloseableExecutorService;
//import org.apache.sshd.common.util.threads.ThreadUtils;
//import org.apache.sshd.server.ServerBuilder;
//import org.apache.sshd.server.SshServer;
//import org.apache.sshd.server.auth.password.PasswordAuthenticator;
//import org.apache.sshd.server.forward.AcceptAllForwardingFilter;
//import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
//import org.apache.sshd.server.session.ServerSession;
//import org.apache.sshd.server.session.ServerSessionImpl;
//import org.apache.sshd.server.session.SessionFactory;
//import org.apache.sshd.server.shell.ProcessShellFactory;
//import org.apache.sshd.sftp.server.SftpSubsystemFactory;
//
///**
// * @Author zcy
// * @Date 2025/1/2 16:30
// * @Version 1.0
// */
//public class MyServer2 {
//
//    public static void main(String[] args) throws IOException {
//        SshServer sshServer = SshServer.setUpDefaultServer();
//        // 设置主机密钥
//        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
//        sshServer.setHost("0.0.0.0");
//        sshServer.setPort(2222);
//        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
//            public boolean authenticate(String username, String password, ServerSession session) {
//                System.out.println("服务端 username "+username+"password "+password);
//
//                // 在这里编写密码认证的逻辑
//                return ("zcy".equals(username) && "101072".equals(password))||("zcy2".equals(username) && "101072".equals(password))||("zcy3".equals(username) && "101072".equals(password));
//
//            }
//        });
//
//
//
//        List<KeyExchangeFactory> kexFactories = NamedFactory.setUpTransformedFactories(
//            false,
//            Arrays.asList(BuiltinDHFactories.dhg14),
//            ClientBuilder.DH2KEX
//        );
//        sshServer.setKeyExchangeFactories(kexFactories);
//
//
//
//
//        // 获取当前 MAC 工厂
//        List<NamedFactory<Mac>> currentMacFactories = sshServer.getMacFactories();
//
//// 创建新列表，包含当前工厂
//        List<NamedFactory<Mac>> newMacFactories = new ArrayList<>(currentMacFactories);
//
//// 添加额外的 MAC 算法
//        newMacFactories.add(BuiltinMacs.hmacmd5);      // hmac-md5
//        newMacFactories.add(BuiltinMacs.hmacmd596);    // hmac-md5-96
//        newMacFactories.add(BuiltinMacs.hmacsha196);   // hmac-sha1-96
//
//// 设置更新后的 MAC 工厂列表
//        sshServer.setMacFactories(newMacFactories);
//
////        List<KeyExchangeFactory> kexFactories = NamedFactory.setUpTransformedFactories(
////            false,
////            Arrays.asList(BuiltinDHFactories.dhg14, BuiltinDHFactories.dhg1,BuiltinDHFactories.dhg14_256),
////            ServerBuilder.DH2KEX
////        );
////        sshServer.setKeyExchangeFactories(kexFactories);
//
//        List<NamedFactory<Cipher>> cipherFactories = Arrays.asList(
//            BuiltinCiphers.aes128ctr
//        );
//        sshServer.setCipherFactories(cipherFactories);
//
//        sshServer.setSessionFactory(new SessionFactory(sshServer) {
//            @Override
//            protected ServerSessionImpl createSession(org.apache.sshd.common.io.IoSession ioSession) throws Exception {
//                return new CustomServerSession(sshServer, ioSession);
//            }
//        });
//
//
//
//        List<NamedFactory<Mac>> macFactories = Arrays.asList(
//            BuiltinMacs.hmacsha1,
//            BuiltinMacs.hmacmd5
//        );
//        sshServer.setMacFactories(macFactories);
//
//        sshServer.setShellFactory(new ProcessShellFactory("powershell.exe", "-Command"));
//        sshServer.addSessionListener(new CustomSessionListener());
//
//        sshServer.getProperties().put("auth-timeout", "10000"); // 10秒认证超时
//        sshServer.getProperties().put("idle-timeout", "200000"); // 20秒空闲超时
//        sshServer.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
//
//        // 设置用户在 SFTP 中的根目录
//        // 使用自定义的文件系统工厂
////        sshServer.setFileSystemFactory(new CustomFileSystemFactory());
//
////        String workingDir = "C:\\Users\\Administrator\\Desktop\\boctmp\\tmp";
////        String workingDir1 = "C:\\Users\\Administrator\\Desktop\\boctmp\\1";
////        String workingDir2 = "C:\\Users\\Administrator\\Desktop\\boctmp\\2";
////        sshServer.setFileSystemFactory(new VirtualFileSystemFactory());
////        VirtualFileSystemFactory fileSystemFactory=new VirtualFileSystemFactory();
////        fileSystemFactory.setUserHomeDir("zcy",Paths.get(workingDir1));
////        fileSystemFactory.setUserHomeDir("zcy2",Paths.get(workingDir2));
////        fileSystemFactory.setDefaultHomeDir(Paths.get(workingDir));
//        sshServer.setFileSystemFactory(new CustomFileSystemFactory());
//
//
//        // 配置 SFTP 子系统（如果需要的话）
//        SftpSubsystemFactory factory = new SftpSubsystemFactory.Builder().build();
//
//        // 注册自定义文件校验监听器
//        factory.addSftpEventListener(new CustomSftpEventListener());
//        sshServer.setSubsystemFactories(Collections.singletonList(factory));
//        //TODO 自定义线程池
//
//        // TODO 1
//        // 自定义 NIO Workers 默认cpu核心线程数+1 负责处理网络的 IO 操作。比如处理 Socket 的读写操作，解析协议等。
////        sshServer.setNioWorkers(10);
////        sshServer.getNioWorkers();
////        System.out.println(" sshServer.getNioWorkers();"+ sshServer.getNioWorkers());
//        // 自定义定时线程池
////        ScheduledExecutorService customScheduler = Executors.newScheduledThreadPool(2);
////        sshServer.setScheduledExecutorService(customScheduler, false);
////        System.out.println("ddddd     "+sshServer.getScheduledExecutorService().toString());
//        // 使用自定义的 IoServiceFactoryFactory
////        sshServer.setIoServiceFactoryFactory(new CustomIoServiceFactoryFactory());
//
//        // TODO 2
//        // 使用自定义线程池
//        CloseableExecutorService executorService = CustomThreadPool.create(4, "SSHwwD");
//        // 创建 resume tasks 线程池
//        CloseableExecutorService resumeTasksExecutorService = ThreadUtils.newFixedThreadPool("dfgdsfgsdfg-tasks-thread-pool", 2);
//        // 使用自定义的 IoServiceFactoryFactory
//        sshServer.setIoServiceFactoryFactory(new CustomIoServiceFactoryFactoryTwo(executorService, resumeTasksExecutorService));
//
//        // TODO 3
//        sshServer.setIoServiceFactoryFactory(CustomIoServiceFactoryFactoryThree.createWithCustomPools());
//
////        sshServer.setKexExtensionHandler(new KexExtensionHandler() {
////            @Override
////            public boolean handleKexExtensionRequest(Session session, int cmd, Buffer buffer) {
////                return false; // 禁用所有 KEX 扩展
////            }
////            @Override
////            public List<String> getKexExtensionAlgorithms(SessionContext session) {
////                return Collections.emptyList(); // 不添加扩展算法
////            }
////        });
//        sshServer.start();
//        System.out.println("SSH server started...");
//        try {
//            // 在此处，主线程会被阻塞，直到你手动停止
//            Thread.sleep(Long.MAX_VALUE);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
