package com.example.sshd.chroot;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

/**
 * @Author zcy
 * @Date 2025/6/23 20:22
 * @Version 1.0
 */
public class SftpServerLauncher {
    public static void main(String[] args) throws IOException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(2222);

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));

        FixedVirtualFileSystemFactory fileSystemFactory = new FixedVirtualFileSystemFactory();
        sshd.setFileSystemFactory(fileSystemFactory);

        sshd.setPasswordAuthenticator(new CustomPasswordAuthenticator(fileSystemFactory));

//        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        new SftpServerConfig().configure(sshd);
//        SftpSubsystemFactory factory = new SftpSubsystemFactory();
//        factory.addSftpEventListener(new CustomSftpEventListener());
// 添加事件监听器
//        sshd.setSubsystemFactories(Collections.singletonList(factory));

            sshd.start();

        System.out.println("SFTP server started on port 2222");
        try {
            // 在此处，主线程会被阻塞，直到你手动停止
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
