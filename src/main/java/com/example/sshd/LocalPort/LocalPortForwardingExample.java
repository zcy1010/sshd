package com.example.sshd.LocalPort;

import java.util.Scanner;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.forward.DefaultForwarder;
import org.apache.sshd.common.forward.PortForwardingEventListener;
import org.apache.sshd.common.util.net.SshdSocketAddress;

/**
 * @Author zcy
 * @Date 2024/12/31 17:17
 * @Version 1.0
 */public class LocalPortForwardingExample {
    public static void main(String[] args) {


        // 配置本地端口转发的参数
        int localPort = 8088; // 本地监听端口
        String remoteHost = "127.0.0.1"; // 远程主机名或IP
        int remotePort = 2222; // 远程主机端口
        // 创建并启动 SSH 客户端
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        try (ClientSession session = client.connect("zcy", "127.0.0.1", 2222)
            .verify()
            .getSession()) {
            session.addPasswordIdentity("101072"); // 设置密码认证
            session.auth().verify(); // 验证身份

            // 设置本地端口转发
            SshdSocketAddress localAddress1 = new SshdSocketAddress("localhost", localPort);
            SshdSocketAddress localAddress2 = new SshdSocketAddress("localhost", 8888);
            SshdSocketAddress remoteAddress1 = new SshdSocketAddress(remoteHost, remotePort);
            SshdSocketAddress remoteAddress2 = new SshdSocketAddress(remoteHost, 222);

            session.startLocalPortForwarding(localAddress1, remoteAddress1); // 启动本地端口转发
            session.startLocalPortForwarding(localAddress2, remoteAddress1); // 启动本地端口转发
//            session.startLocalPortForwarding(localAddress1, remoteAddress2); // 启动本地端口转发
//            session.startLocalPortForwarding(localAddress2, remoteAddress2); // 启动本地端口转发

            System.out.println("本地端口转发已启动：localhost:" + localPort + " -> " + remoteHost + ":" + remotePort);

            // 保持连接运行
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.stop(); // 停止SSH客户端
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
