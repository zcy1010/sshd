package com.example.sshd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.session.ClientSession;

public class MyClient {

    public static void main(String[] args) throws IOException {
        SshClient client = SshClient.setUpDefaultClient();
        client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE); // 忽略服务端主机密钥校验

        client.start();

        // 连接服务器
        try (ClientSession session = client.connect("zcy", "localhost", 2222).verify(5000).getSession()) {
            // 加载私钥文件
            KeyPair keyPair = loadPrivateKey("C:\\Users\\Administrator\\.ssh\\id_rsa");
            session.addPublicKeyIdentity(keyPair);

            // 验证身份
            if (!session.auth().verify(5000).isSuccess()) {
                throw new IOException("Authentication failed");
            }

            System.out.println("Authentication succeeded!");

            // 执行命令
            String response = session.executeRemoteCommand("ls");
            System.out.println("Command response: " + response);

            session.close(false);
        } finally {
            client.stop();
        }
    }

    private static KeyPair loadPrivateKey(String privateKeyPath) throws IOException {
        try {
            String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyPath)), "UTF-8")
                .replace("-----BEGIN OPENSSH PRIVATE KEY-----", "")
                .replace("-----END OPENSSH PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // 移除多余字符

            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            return new KeyPair(null, privateKey); // 只加载私钥，公钥为 null
        } catch (Exception e) {
            throw new IOException("Failed to load private key from " + privateKeyPath, e);
        }
    }
}