package com.example.sshd.chroot;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

import java.nio.file.Paths;
/**
 * @Author zcy
 * @Date 2025/6/23 20:21
 * @Version 1.0
 */
@Deprecated
public class CustomPasswordAuthenticator implements PasswordAuthenticator{
    private final FixedVirtualFileSystemFactory fileSystemFactory;

    public CustomPasswordAuthenticator(FixedVirtualFileSystemFactory fileSystemFactory) {
        this.fileSystemFactory = fileSystemFactory;
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session) {
        // 你自己的用户认证逻辑
        String homeDirectory = "/share/file_batch/" + username;
        fileSystemFactory.setUserHomeDir(username, Paths.get(homeDirectory));
        if(username.equals("zcy")&&password.equals("123")){
            return true;
        }
        // 返回 true 表示认证通过
        return true;
    }
}
