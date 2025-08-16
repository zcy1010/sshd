package com.example.sshd.chroot;

import java.io.IOException;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.util.buffer.Buffer;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.sftp.server.SftpFileSystemAccessor;
import org.apache.sshd.sftp.server.SftpSubsystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.sshd.sftp.server.SftpSubsystemConfigurator;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;

/**
 * @Author zcy
 * @Date 2025/6/24 20:05
 * @Version 1.0
 */
public class PathMappingSftpSubsystem extends SftpSubsystem{

    public PathMappingSftpSubsystem(ChannelSession channel, SftpSubsystemConfigurator configurator) {
        super(channel, configurator);
    }

    @Override
    protected void doProcess(Buffer buffer, int length, int type, int id) throws IOException {
        // 只处理删除命令 (SSH_FXP_REMOVE = 13)
        if (type == 13) {
            // 保存当前读位置
            int rpos = buffer.rpos();

            // 读取原始路径
            String rawPath = buffer.getString();

            // 标准化路径
            String normalizedPath = normalizePath(getServerSession(), rawPath);

            // 回退缓冲区指针到路径开始位置
            buffer.rpos(rpos);

            // 写入标准化后的路径（覆盖原始路径）
            buffer.putString(normalizedPath);

            // 更新缓冲区写位置
            buffer.wpos(rpos + 4 + normalizedPath.length()); // 4字节长度 + 字符串长度
        }

        // 调用父类处理
        super.doProcess(buffer, length, type, id);
    }

    private String normalizePath(ServerSession session, String rawPath) {
        String username = session.getUsername();
        String chrootRoot = "/share/file_batch/" + username;

        // 处理绝对路径输入
        if (rawPath.startsWith(chrootRoot)) {
            // 转换为相对路径
            String relativePath = rawPath.substring(chrootRoot.length());

            // 确保路径以斜杠开头
            if (relativePath.isEmpty()) {
                relativePath = "/";
            } else if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }

            if (log.isDebugEnabled()) {
                log.debug("Mapped path: {} -> {}", rawPath, relativePath);
            }
            return relativePath;
        }

        // 其他路径直接返回
        return rawPath;
    }

}

