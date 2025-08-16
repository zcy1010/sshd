//package com.example.sshd.moved;
//
//import org.apache.sshd.common.Property;
//import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
//import org.apache.sshd.common.util.GenericUtils;
//import org.apache.sshd.common.util.io.IoUtils;
//import org.apache.sshd.server.SshServer;
//import org.apache.sshd.server.auth.AsyncAuthException;
//import org.apache.sshd.server.auth.password.PasswordAuthenticator;
//import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
//import org.apache.sshd.server.channel.ChannelSession;
//import org.apache.sshd.server.command.Command;
//import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
//import org.apache.sshd.server.session.ServerSession;
//import org.apache.sshd.sftp.SftpModuleProperties;
//import org.apache.sshd.sftp.server.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.nio.file.LinkOption;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Collections;
//
///**
// * @Author zcy
// * @Date 2025/7/10 9:38
// * @Version 1.0
// */
//public class SFTPServer {
//
//    static Logger logger = LoggerFactory.getLogger(SFTPServer.class);
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//        SshServer sshServer = SshServer.setUpDefaultServer();
//
//        SimpleGeneratorHostKeyProvider keyPairProvider = new SimpleGeneratorHostKeyProvider();
//        // 生成密钥的算法
////        keyPairProvider.setPath(path);
//        keyPairProvider.setAlgorithm("RSA"); // Explicitly set RSA algorithm
//        keyPairProvider.setKeySize(2048); // Recommended RSA key size
//        sshServer.setKeyPairProvider(keyPairProvider);
//
//        sshServer.setPort(10022);
//
//        VirtualFileSystemFactory fileSystemFactory = new VirtualFileSystemFactory(
//            Paths.get("/data/test"));
//        sshServer.setFileSystemFactory(fileSystemFactory);
//        sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {
//            @Override
//            public boolean authenticate(String s, String s1, ServerSession serverSession)
//                throws PasswordChangeRequiredException, AsyncAuthException {
//                return true;
//            }
//        });
//
//        //配置 SFTP 子系统
////        SftpSubsystemFactory factory = new SftpSubsystemFactory.Builder().build();
////        sshServer.setSubsystemFactories(Collections.singletonList(factory));
//
//        SftpSubsystemFactory factory = new CustomSftpSubsystemFactory.Builder().build();  // 创建工厂实例
//        factory.addSftpEventListener(new CustomSftpEventListener());
//        factory.addSftpEventListener(new PompSftpEventListener());
//        factory.setErrorStatusDataHandler(new DetailedSftpErrorStatusDataHandler());
//        sshServer.setSubsystemFactories(Collections.singletonList(factory));
////        sshServer.getProperties().put("sftp-auto-follow-links",false);
//
//        sshServer.start();
//        logger.info("SSH server started...");
//        while (true) {
//            Thread.sleep(10000);
//        }
//
//
//    }
//}
