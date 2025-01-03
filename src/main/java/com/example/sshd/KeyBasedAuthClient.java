package com.example.sshd;

import java.security.GeneralSecurityException;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.io.resource.PathResource;
import org.apache.sshd.common.config.keys.loader.openssh.OpenSSHKeyPairResourceParser;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.util.Collections;
import java.util.Set;

public class KeyBasedAuthClient {
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // 加载私钥文件
        String privateKeyPath = "G:\\MyJob\\sshd\\sshd\\.ssh\\id_rsa";
        KeyPair keyPair = loadKeyPair(privateKeyPath);

        // 连接到 SSH 服务端
        sshConnection("localhost", 22, "zcy", keyPair);
    }

    /**
     * 加载私钥文件
     */
    private static KeyPair loadKeyPair(String privateKeyPath)
        throws IOException, GeneralSecurityException {
        PathResource resource = new PathResource(Paths.get(privateKeyPath));
        return OpenSSHKeyPairResourceParser.INSTANCE.loadKeyPairs(null, resource, null).iterator().next();
    }

    /**
     * 使用密钥登录 SSH 服务端
     */
    private static void sshConnection(String host, int port, String username, KeyPair keyPair) throws IOException {
        try (SshClient client = SshClient.setUpDefaultClient()) {
            client.setServerKeyVerifier((clientSession, remoteAddress, serverKey) -> true); // 跳过主机密钥验证（测试环境）
            client.start();

            try (ClientSession session = client.connect(username, host, port)
                .verify(5000)
                .getSession()) {
                // 添加密钥身份
                session.addPublicKeyIdentity(keyPair);
                session.auth().verify(10000);
                System.out.println("Authentication successful!");

                // 执行命令
                try (ClientChannel channel = session.createExecChannel("ls /")) {
                    channel.setOut(System.out);
                    channel.setErr(System.err);
                    channel.open().verify();

                    Set<ClientChannelEvent> events = channel.waitFor(
                        Collections.singletonList(ClientChannelEvent.CLOSED), 0);
                    if (events.contains(ClientChannelEvent.TIMEOUT)) {
                        System.err.println("Command execution timed out.");
                    }
                }
            } finally {
                client.stop();
            }
        }
    }
}
