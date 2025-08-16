package com.example.sshd.chroot;
import java.io.IOException;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.sftp.server.SftpSubsystem;
import org.apache.sshd.sftp.server.SftpSubsystemConfigurator;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
/**
 * @Author zcy
 * @Date 2025/6/24 20:21
 * @Version 1.0
 */
public class PathMappingSftpSubsystemFactory extends SftpSubsystemFactory{

    // 添加此方法以提供配置器访问
    public SftpSubsystemConfigurator getSftpSubsystemConfigurator() {
        return this; // 工厂自身实现了 SftpSubsystemConfigurator
    }

    @Override
    public Command createSubsystem(ChannelSession channel) throws IOException {
        // 使用自定义的 SftpSubsystem
        PathMappingSftpSubsystem subsystem = new PathMappingSftpSubsystem(
            channel,
            getSftpSubsystemConfigurator()
        );

        // 添加注册的事件监听器
        GenericUtils.forEach(getRegisteredListeners(), subsystem::addSftpEventListener);

        return subsystem;
    }
}
