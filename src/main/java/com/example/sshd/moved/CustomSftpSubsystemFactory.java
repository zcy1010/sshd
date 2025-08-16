package com.example.sshd.moved;

import java.io.IOException;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.sftp.server.SftpErrorStatusDataHandler;
import org.apache.sshd.sftp.server.SftpFileSystemAccessor;
import org.apache.sshd.sftp.server.SftpSubsystem;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.ObjectBuilder;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.common.util.threads.ManagedExecutorServiceSupplier;
import org.apache.sshd.server.channel.ChannelDataReceiver;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.subsystem.SubsystemFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory.Builder;
import org.apache.sshd.sftp.server.UnsupportedAttributePolicy;

/**
 * @Author zcy
 * @Date 2025/7/10 14:43
 * @Version 1.0
 */
public class CustomSftpSubsystemFactory extends SftpSubsystemFactory {

    // 重写 createSubsystem 方法，创建自定义的 SftpSubsystem
    @Override
    public Command createSubsystem(ChannelSession channel) throws IOException {
        // 创建 MySftpSubsystem 实例
        SftpSubsystem subsystem = new MySftpSubsystem(channel, this);

        // 将所有已注册的事件监听器添加到 MySftpSubsystem 中
        GenericUtils.forEach(this.getRegisteredListeners(), subsystem::addSftpEventListener);

        // 返回自定义的 SftpSubsystem
        return subsystem;
    }

    // 内部 Builder 类，用于创建 CustomSftpSubsystemFactory 实例
    public static class Builder extends SftpSubsystemFactory.Builder {
        private Supplier<? extends CloseableExecutorService> executorsProvider;
        private UnsupportedAttributePolicy policy;
        private SftpFileSystemAccessor fileSystemAccessor;
        private SftpErrorStatusDataHandler errorStatusDataHandler;
        private ChannelDataReceiver errorChannelDataReceiver;
        public Builder() {
            super();
            this.policy = SftpSubsystemFactory.DEFAULT_POLICY;  // Use default policy
            this.fileSystemAccessor = SftpFileSystemAccessor.DEFAULT;  // Use default file system accessor
            this.errorStatusDataHandler = SftpErrorStatusDataHandler.DEFAULT;  // Use default error handler

        }

        // 重写 build 方法
        @Override
        public CustomSftpSubsystemFactory build() {
            // 创建 CustomSftpSubsystemFactory 实例
            CustomSftpSubsystemFactory factory = new CustomSftpSubsystemFactory();
            // 使用 Builder 中的配置设置实例
            factory.setExecutorServiceProvider(this.executorsProvider);
            factory.setUnsupportedAttributePolicy(this.policy);
            factory.setFileSystemAccessor(this.fileSystemAccessor);
            factory.setErrorStatusDataHandler(this.errorStatusDataHandler);
            factory.setErrorChannelDataReceiver(this.errorChannelDataReceiver);
            // 注册事件监听器
            GenericUtils.forEach(this.getRegisteredListeners(), factory::addSftpEventListener);
            return factory;
        }
        public CustomSftpSubsystemFactory.Builder withExecutorServiceProvider(Supplier<? extends CloseableExecutorService> provider) {
            this.executorsProvider = provider;
            return this;
        }

        public CustomSftpSubsystemFactory.Builder withUnsupportedAttributePolicy(UnsupportedAttributePolicy p) {
            this.policy = (UnsupportedAttributePolicy)Objects.requireNonNull(p, "No policy");
            return this;
        }

        public CustomSftpSubsystemFactory.Builder withFileSystemAccessor(SftpFileSystemAccessor accessor) {
            this.fileSystemAccessor = (SftpFileSystemAccessor)Objects.requireNonNull(accessor, "No accessor");
            return this;
        }

        public CustomSftpSubsystemFactory.Builder withSftpErrorStatusDataHandler(SftpErrorStatusDataHandler handler) {
            this.errorStatusDataHandler = (SftpErrorStatusDataHandler)Objects.requireNonNull(handler, "No error status handler");
            return this;
        }

        public CustomSftpSubsystemFactory.Builder withErrorChannelDataReceiver(ChannelDataReceiver receiver) {
            this.errorChannelDataReceiver = receiver;
            return this;
        }

    }
}
