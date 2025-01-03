package com.example.sshd.LocalPort;

import java.util.Scanner;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.net.SshdSocketAddress;

/**
 * @Author zcy
 * @Date 2025/1/2 16:25
 * @Version 1.0
 */
public class LocalPortForwardingExampleTwo {
    public static void main(String[] args) {


        // 配置本地端口转发的参数
        int localPort = 8080; // 本地监听端口
        String remoteHost = "127.0.0.1"; // 远程主机名或IP
        int remotePort = 2222; // 远程主机端口
        // 创建并启动 SSH 客户端
        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        try {
            // 获取用户输入的用户名和密码
            Scanner scanner = new Scanner(System.in);
            System.out.print("请输入用户名：");
            String username = scanner.nextLine();
            System.out.print("请输入密码：");
            String password = scanner.nextLine();

            // 本地验证用户名和密码
            if (!validateCredentials(username, password)) {
                System.out.println("本地验证失败，终止连接。");
                return;
            }
            System.out.println("本地验证通过，开始连接服务器...");

            // 连接到 SSH 服务器
            try (ClientSession session = client.connect(username, "127.0.0.1", 2222).verify().getSession()) {
                session.addPasswordIdentity(password); // 添加密码
                session.auth().verify(); // 进行认证
                // 设置本地端口转发
                SshdSocketAddress localAddress1 = new SshdSocketAddress("localhost", localPort);
                SshdSocketAddress localAddress2 = new SshdSocketAddress("localhost", 8888);
                SshdSocketAddress remoteAddress = new SshdSocketAddress(remoteHost, remotePort);

                session.startLocalPortForwarding(localAddress1, remoteAddress); // 启动本地端口转发
                session.startLocalPortForwarding(localAddress2, remoteAddress); // 启动本地端口转发

                System.out.println("本地端口转发已启动：localhost:" + localPort + " -> " + remoteHost + ":" + remotePort);

                // 保持程序运行，保持端口转发
                Thread.sleep(Long.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 停止 SSH 客户端
            client.stop();
        }
    }

    // 模拟本地验证逻辑
    private static boolean validateCredentials(String username, String password) {
        // 替换为你的验证逻辑，例如查询数据库或调用 API
        System.out.println("客户端 username "+username+"password "+password);
        return ("zcy2".equals(username) && "101072".equals(password))||("zcy3".equals(username) && "101072".equals(password));
    }
}
