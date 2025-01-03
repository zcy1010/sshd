package com.example.sshd;
import java.io.InputStream;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * 客户端
 * 下载和上传
 *
 *
 * @Author zcy
 * @Date 2024/12/18 10:32
 * @Version 1.0
 */
public class SftpClientExample {
    public static void main(String[] args) {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        try (ClientSession session = client.connect("zcy", "127.0.0.1", 2222)
            .verify(5000)
            .getSession()) {

            // 设置登录密码
            session.addPasswordIdentity("101072");
            session.auth().verify(5000);

            // 创建 SFTP 客户端
            try (SftpClient sftpClient = SftpClientFactory.instance().createSftpClient(session)) {
                // 文件上传
                uploadFile(sftpClient, "C:\\Users\\Administrator\\Desktop\\boctmp\\2\\2.7z", "\\34.7z");


                // 文件下载
//                downloadFile(sftpClient, "C:\\Users\\Administrator\\Desktop\\boctmp\\tmp\\2.txt", "C:\\Users\\Administrator\\Desktop\\boctmp\\3.txt");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.stop();
        }
    }

    /**
     * 上传文件
     *
     * @param sftpClient SFTP 客户端实例
     * @param localFilePath 本地文件路径
     * @param remoteFilePath 远程文件路径
     * @throws IOException 如果发生错误
     */
    private static void uploadFile(SftpClient sftpClient, String localFilePath, String remoteFilePath) throws IOException {
        Path localPath = Paths.get(localFilePath);
        try (SftpClient.CloseableHandle handle = sftpClient.open(remoteFilePath, SftpClient.OpenMode.Create, SftpClient.OpenMode.Write)) {
            sftpClient.write(handle, 0, Files.readAllBytes(localPath));
            System.out.println("File uploaded: " + localFilePath + " -> " + remoteFilePath);
        }
    }

    /**
     * 下载文件
     *
     * @param sftpClient SFTP 客户端实例
     * @param remoteFilePath 远程文件路径
     * @param localFilePath 本地文件路径
     * @throws IOException 如果发生错误
     */
    private static void downloadFile(SftpClient sftpClient, String remoteFilePath, String localFilePath) throws IOException {
        Path localPath = Paths.get(localFilePath);

        // 确保目标文件的父目录存在
        Path parentDir = localPath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir); // 创建不存在的目录
        }

        // 获取远程文件大小
        SftpClient.Attributes remoteAttributes = sftpClient.stat(remoteFilePath);
        long remoteFileSize = remoteAttributes.getSize();

        // 检查本地文件大小（用于断点续传）
        long localFileSize = Files.exists(localPath) ? Files.size(localPath) : 0;

        if (localFileSize >= remoteFileSize) {
            System.out.println("File already fully downloaded: " + localFilePath);
            return;
        }

        // 设置缓冲区大小（动态调整）
        int bufferSize = calculateBufferSize(remoteFileSize);
        byte[] buffer = new byte[bufferSize];

        // 打开远程文件句柄，从上次中断的位置继续
        try (SftpClient.CloseableHandle handle = sftpClient.open(remoteFilePath, SftpClient.OpenMode.Read);
            java.io.OutputStream outputStream = Files.newOutputStream(localPath, localFileSize > 0 ? java.nio.file.StandardOpenOption.APPEND : java.nio.file.StandardOpenOption.CREATE)) {

            long offset = localFileSize; // 从上次中断的位置继续
            int bytesRead;

            System.out.println("Starting download: " + remoteFilePath + " -> " + localFilePath);

            // 循环读取远程文件并写入本地
            while ((bytesRead = sftpClient.read(handle, offset, buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
                offset += bytesRead;

                // 显示进度
                System.out.printf("Progress: %.2f%%\n", (offset / (double) remoteFileSize) * 100);
            }

            System.out.println("Download complete: " + localFilePath);
        }
    }
    private static int calculateBufferSize(long fileSize) {
        // 根据文件大小动态调整缓冲区大小（示例逻辑，可调整）
        if (fileSize < 1_000_000) {
            return 4096; // 小文件使用 4KB
        } else if (fileSize < 100_000_000) {
            return 8192; // 中等文件使用 8KB
        } else {
            return 65536; // 大文件使用 64KB
        }
    }
}
