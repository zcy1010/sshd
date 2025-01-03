package com.example.sshd.test;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.PtyMode;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * @Author zcy
 * @Date 2025/1/3 11:05
 * @Version 1.0
 */
public class SshClientSimulator {
    public static void main(String[] args) {
        int clientCount = 50; // 模拟 50 个客户端
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < clientCount; i++) {
            final int clientId = i;
            executorService.submit(() -> {
                try {
                    simulateClientConnection(clientId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
    }

    /**
     * 模拟单个客户端连接到 SSH 服务端
     */
    private static void simulateClientConnection(int clientId) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        try (ClientSession session = client.connect("zcy", "127.0.0.1", 2222)
            .verify(10000) // 超时时间 10 秒
            .getSession()) {

            session.addPasswordIdentity("101072"); // 设置密码
            session.auth().verify(10000); // 验证认证

            System.out.println("客户端 " + clientId + " 已连接");

            // 执行命令
//            executeCommand(session, clientId);

            // 模拟保持会话一段时间
            Thread.sleep(5000);

        } finally {
            client.stop();
            System.out.println("客户端 " + clientId + " 已断开");
        }
    }

    /**
     * 在 SSH 会话中执行命令
     */
    private static void executeCommand(ClientSession session, int clientId) throws Exception {
        try (ChannelExec channel = session.createExecChannel("echo 'Hello from client " + clientId + "'")) {
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOut(responseStream);

            channel.open().verify(5000); // 打开通道并验证

            // 等待命令执行完成
//            channel.waitFor(ChannelExec.CLOSED, 10000);

            String response = new String(responseStream.toByteArray(), StandardCharsets.UTF_8);
            System.out.println("客户端 " + clientId + " 命令输出: " + response);
        }
    }
}
