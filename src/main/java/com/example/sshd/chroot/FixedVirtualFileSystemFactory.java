package com.example.sshd.chroot;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.common.file.root.RootedFileSystemProvider;
import org.apache.sshd.common.session.SessionContext;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
/**
 * @Author zcy
 * @Date 2025/6/23 20:18
 * @Version 1.0
 */
@Deprecated
public class FixedVirtualFileSystemFactory extends VirtualFileSystemFactory {
    @Override
    public FileSystem createFileSystem(SessionContext session) throws IOException {
        Path userHomeDir = getUserHomeDir(session);

        if (userHomeDir == null) {
            throw new IOException("User home directory not found for session: " + session.getUsername());
        }

        // 创建 chroot 的 RootedFileSystem（让用户看到的是 "/"）
        return new RootedFileSystemProvider().newFileSystem(userHomeDir, Collections.emptyMap());
    }
}
