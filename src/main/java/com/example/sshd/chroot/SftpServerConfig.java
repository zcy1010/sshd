package com.example.sshd.chroot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.session.ServerSession;

/**
 * @Author zcy
 * @Date 2025/6/24 20:44
 * @Version 1.0
 */
public class SftpServerConfig {
    public void configure(SshServer sshd) {
        // 使用自定义的 SFTP 子系统工厂
        PathMappingSftpSubsystemFactory factory = new PathMappingSftpSubsystemFactory();
        sshd.setSubsystemFactories(Collections.singletonList(factory));

        // 设置文件系统工厂（处理chroot）
//        sshd.setFileSystemFactory(new VirtualFileSystemFactory() {
//            @Override
//            public Path getUserHomeDir(SessionContext session) {
//                return Paths.get("F:\\test\\" + session.getUsername());
//            }
//        });
    }
}
