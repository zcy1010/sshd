package com.example.sshd;


import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.server.session.ServerSession;


import java.io.IOException;
import java.nio.file.Path;
import org.apache.sshd.sftp.server.FileHandle;
import org.apache.sshd.sftp.server.Handle;
import org.apache.sshd.sftp.server.SftpEventListener;

/**
 * @Author zcy
 * @Date 2024/12/18 15:54
 * @Version 1.0
 */
public class CustomSftpEventListener implements SftpEventListener {

    // 限制的文件类型（后缀） (黑名单)
    private static final Set<String> DISALLOWED_FILE_EXTENSIONS = new HashSet<>(
        Arrays.asList(".zip", ".txt"));
    // 最大允许的文件大小（1MB）
    private static final long MAX_FILE_SIZE = 10*1024 * 1024;
    private static final boolean DISABLE_UPLOAD = true;
    private static final boolean DISABLE_DELETE = true;
    private final Map<Path, Boolean> fileLimitExceeded = new ConcurrentHashMap<>();

    @Override
    public void opening(ServerSession session, String remoteHandle, Handle localHandle) throws IOException {
        Path filePath = localHandle.getFile();
        // 如果文件重新打开（例如新的上传操作），清除超限标记
        if (fileLimitExceeded.containsKey(filePath)) {
            System.out.println("文件重新打开，清除超限标记: " + filePath.getFileName());
            fileLimitExceeded.remove(filePath);
        }
        // 如果是目录，直接返回
        if (Files.isDirectory(filePath)) {
            return;
        }
        // 1. 校验文件类型
        validateFileType(filePath);
    }
    @Override
    public void writing(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen) throws IOException {
        System.out.println("当前线程: " + Thread.currentThread().getName());
        if (DISABLE_UPLOAD) {
            Path filePath = localHandle.getFile();
            System.out.println("writing上传文件: " + filePath.getFileName());
        }

        Path filePath = localHandle.getFile();
        // 如果文件已经被标记为超限，直接抛异常
        if (fileLimitExceeded.getOrDefault(filePath, false)) {
            throw new IOException("文件大小超出限制: " + filePath.getFileName());
        }
        // 校验文件大小

        if(!validateFileSize(offset, dataLen)){
            fileLimitExceeded.put(filePath, true);
            localHandle.close();
            System.out.println("文件大小超出限制" + filePath.toString());
            // 清理本地文件
            cleanUpFile(filePath);
        }

    }
    @Override
    public void written(ServerSession session, String remoteHandle, FileHandle localHandle, long offset, byte[] data, int dataOffset, int dataLen, Throwable thrown) throws IOException {
        if (DISABLE_UPLOAD) {
            Path filePath = localHandle.getFile();
            System.out.println("written上传文件已触发: " + filePath.getFileName());

        }
    }
    @Override
    public void creating(ServerSession session, Path path, Map<String, ?> attrs) throws IOException {
        if (DISABLE_UPLOAD) {
            System.out.println("creating上传文件: " + path.getFileName());
        }
    }

    @Override
    public void created(ServerSession session, Path path, Map<String, ?> attrs, Throwable thrown) throws IOException {
        // 上传完成后，触发此回调。如果禁止上传，可以在此处处理额外逻辑（如果需要）。
        if (DISABLE_UPLOAD) {
            System.out.println("created上传文件已触发: " + path.getFileName());
            // 如果在创建后检测到不合法的操作，也可以删除刚刚创建的文件。
            if (path != null && !thrown.getClass().isAssignableFrom(IOException.class)) {
                Files.deleteIfExists(path);
            }
        }
    }

    @Override
    public void removing(ServerSession session, Path path, boolean isDirectory) throws IOException {
        if (DISABLE_DELETE) {
            System.out.println("removing删除文件: " + path.getFileName());
        }
    }

    @Override
    public void removed(ServerSession session, Path path, boolean isDirectory, Throwable thrown) throws IOException {
        // 如果删除被触发，可以记录日志。
        if (DISABLE_DELETE) {
            System.out.println("removed尝试删除文件被拒绝: " + path.getFileName());
        }
    }
    private void validateFileType(Path filePath) throws IOException {
        // 如果是目录，则跳过检查
        if (Files.isDirectory(filePath)) {
            return;
        }
        String fileName = filePath.getFileName().toString();
        for (String disallowedExtension : DISALLOWED_FILE_EXTENSIONS) {
            if (fileName.endsWith(disallowedExtension)) {
                System.out.println("文件类型不符合要求: " + fileName);
                throw new IOException("文件类型不符合要求: " + fileName + ". 不允许以下类型的文件上传或下载: " + DISALLOWED_FILE_EXTENSIONS);
            }
        }
    }

    private boolean validateFileSize(long offset,int newDataLength) throws IOException {

        // 计算写入后的文件大小
        long finalSize = offset + newDataLength;
        return finalSize <= MAX_FILE_SIZE;

//        if (finalSize > MAX_FILE_SIZE) {
//            System.out.println("文件大小不符合要求"+filePath);
//            throw new IOException("File size exceeds the maximum allowed limit of " + MAX_FILE_SIZE / (1024 * 1024) + " MB.");
//        }
    }

    private void cleanUpFile(Path filePath) {
        try {
            // 判断文件是否存在
            if (Files.exists(filePath)) {
                // 如果是文件夹且为空，删除文件夹
                if (Files.isDirectory(filePath)) {
                    try {
                        Files.delete(filePath); // 删除空目录
                        System.out.println("空目录已删除: " + filePath.toString());
                    } catch (DirectoryNotEmptyException e) {
                        System.err.println("目录不是空的，无法删除: " + filePath.toString());
                    }
                } else {
                    // 如果是文件，直接删除
                    Files.delete(filePath);
                    System.out.println("文件已删除: " + filePath.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("清理文件失败: " + filePath.toString() + " - " + e.getMessage());
        }
    }
}
