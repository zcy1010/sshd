package com.example.sshd;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.sshd.common.file.FileSystemFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.common.util.io.IoUtils;

/**
 * @Author zcy
 * @Date 2024/12/24 9:30
 * @Version 1.0
 */
public class CustomFileSystemFactory implements FileSystemFactory {

    String workyouingDir = "C:\\Users\\Administrator\\Desktop\\boctmp\\tmp";
    String workingDir1 = "C:\\Users\\Administrator\\Desktop\\boctmp\\1";
    String workingDir2 = "C:\\Users\\Administrator\\Desktop\\boctmp\\2";

    @Override
    public Path getUserHomeDir(SessionContext sessionContext) throws IOException {
        String username = sessionContext.getUsername();
        Path homeDir;

        if ("zcy".equals(username)) {
            homeDir = Paths.get("C:\\Users\\Administrator\\Desktop\\boctmp\\1");
        } else if ("zcy2".equals(username)) {
            homeDir = Paths.get("C:\\Users\\Administrator\\Desktop\\boctmp\\2");
        } else {
            homeDir = Paths.get("C:\\Users\\Administrator\\Desktop\\boctmp\\tmp");
        }

        System.out.println("User: " + username + ", Home directory: " + homeDir);

        // 返回标准化的绝对路径
        return homeDir.toAbsolutePath().normalize();
    }

    @Override
    public FileSystem createFileSystem(SessionContext sessionContext) throws IOException {
        Path userHomeDir = getUserHomeDir(sessionContext);

        if (!Files.exists(userHomeDir)) {
            throw new IOException("Home directory does not exist: " + userHomeDir);
        }

        if (!Files.isDirectory(userHomeDir)) {
            throw new IOException("Home path is not a directory: " + userHomeDir);
        }

        System.out.println("Creating file system for user: " + sessionContext.getUsername());

        // 使用 VirtualFileSystemFactory 创建虚拟文件系统
        return new VirtualFileSystemFactory(userHomeDir).createFileSystem(sessionContext);
    }

}