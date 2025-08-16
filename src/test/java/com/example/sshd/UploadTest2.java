package com.example.sshd;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

/**
 * @Author zcy
 * @Date 2025/2/18 10:12
 * @Version 1.0
 */
public class UploadTest2 {
    /**
     * jsch 上传多个文件
     * 目标文件夹存在
     */
    @Test
    public void testJSCHUpload() {
        String host = "127.0.0.1";
        int port = 2222;
        String user = "zcy";
        String password = "101072";
        String localDirectoryPath = "C:\\Users\\Administrator\\Desktop\\sshd\\test"; // 替换为你的目录路径
        // 生成远程目录路径为年月日时分钟的形式
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String remoteDirectoryPath = "/" + now.format(formatter)+"upload";

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            // 创建会话
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("UserKnownHostsFile", "/dev/null");
            session.connect();

            // 打开 SFTP 通道
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // 检查本地目录是否存在
            File localDirectory = new File(localDirectoryPath);
            if (localDirectory.exists() && localDirectory.isDirectory()) {
                // 确保远程目录存在
                ensureRemoteDirectoryExists(channelSftp, remoteDirectoryPath);
                listFilesRecursively(localDirectory, channelSftp, remoteDirectoryPath);
            } else {
                System.out.println("目录不存在或不是一个有效的目录。");
            }

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            throw new RuntimeException(e);
        } finally {
            // 关闭 SFTP 通道和会话
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    private static void ensureRemoteDirectoryExists(ChannelSftp channelSftp, String remoteDirectoryPath) throws SftpException {
        try {
            // 尝试列出远程目录
            channelSftp.ls(remoteDirectoryPath);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                // 如果目录不存在，创建它
                channelSftp.mkdir(remoteDirectoryPath);
            } else {
                // 其他异常，抛出
                throw e;
            }
        }
    }

    public static void listFilesRecursively(File directory, ChannelSftp channelSftp, String remoteDirectoryPath) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 跳过子目录
                    continue;
                } else {
                    System.out.println("正在上传文件: " + file.getAbsolutePath());
                    try {
                        // 上传文件
                        channelSftp.put(new FileInputStream(file), remoteDirectoryPath + "/" + file.getName());
                        System.out.println("上传成功: " + file.getName());
                    } catch (SftpException | IOException e) {
                        System.err.println("上传失败: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
