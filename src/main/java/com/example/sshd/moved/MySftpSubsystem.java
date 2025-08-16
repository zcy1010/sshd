package com.example.sshd.moved;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.sftp.SftpModuleProperties;
import org.apache.sshd.sftp.server.SftpSubsystem;
import org.apache.sshd.sftp.server.SftpSubsystemConfigurator;

/**
 * @Author zcy
 * @Date 2025/7/10 9:35
 * @Version 1.0
 */
public class MySftpSubsystem extends SftpSubsystem {
    public MySftpSubsystem(ChannelSession channel, SftpSubsystemConfigurator configurator) {
        super(channel, configurator);
    }

    public boolean resolvePathResolutionFollowLinks(int cmd, String extension, Path path) throws IOException {
        if (cmd == 13) {
            return false;
        }
        return (Boolean) SftpModuleProperties.AUTO_FOLLOW_LINKS.getRequired(this.getServerSession());
    }
}
