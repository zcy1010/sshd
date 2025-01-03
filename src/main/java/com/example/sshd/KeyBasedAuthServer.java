package com.example.sshd;

import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.auth.pubkey.CachingPublicKeyAuthenticator;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeyBasedAuthServer {
    public static void main(String[] args) throws IOException {
        // 初始化 SSH 服务端
        SshServer sshServer = SshServer.setUpDefaultServer();

        // 设置主机密钥（用于会话加密）
        sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));

        // 配置监听地址和端口
        sshServer.setHost("localhost");
        sshServer.setPort(22);

        // 创建 PublickeyAuthenticator，支持缓存功能
        ConcurrentHashMap<String, Boolean> authenticationCache = new ConcurrentHashMap<>();
        CachingPublicKeyAuthenticator cachingAuthenticator = new CachingPublicKeyAuthenticator(
            (username, key, session) -> authenticateUser(username, key, session, authenticationCache)
        );
        sshServer.setPublickeyAuthenticator(cachingAuthenticator);

        // 启动服务端
        sshServer.start();
        System.out.println("SSH Server started...");
        try {
            // 在此处，主线程会被阻塞，直到你手动停止
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义用户认证逻辑
     */
    private static boolean authenticateUser(String username, PublicKey key, ServerSession session, Map<String, Boolean> cache) {
        try {
            // 读取 authorized_keys 文件
            String authorizedKeysPath = "G:\\MyJob\\sshd\\sshd\\.ssh\\authorized_keys";
            String fingerprint = KeyUtils.getFingerPrint(key);

            if (cache.containsKey(fingerprint)) {
                System.out.println("Cache hit for key: " + fingerprint);
                return cache.get(fingerprint);
            }

            System.out.println("Authenticating user: " + username + ", Key fingerprint: " + fingerprint);
            boolean authenticated = Files.lines(Paths.get(authorizedKeysPath))
                .anyMatch(line -> line.contains(fingerprint));

            cache.put(fingerprint, authenticated); // 缓存认证结果
            return authenticated;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
