package com.example.sshd;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.session.ClientSession;

/**
 * @Author zcy
 * @Date 2024/12/19 9:28
 * @Version 1.0
 */
public class ExecTest {
    public static void main(String[] arg) throws IOException {
        SshClient sshClient = SshClient.setUpDefaultClient();
        sshClient.setServerKeyVerifier((clientSession, remoteAddress, serverKey) -> true);

        sshClient.start();
        ClientSession session = sshClient.connect("zcy", "localhost", 22).verify(5 , TimeUnit.SECONDS).getSession();
        session.addPasswordIdentity("101072");
        session.auth().verify();
//        String commands = String.join(";", "ip addr", "pwd", "ping -c 5 127.0.0.1", "ll", "ls");
        String commands = String.join(";", "dir");
        System.out.println("执行命令列表：" + commands);
        try (ChannelExec channel = session.createExecChannel(commands)) {
            channel.open().verify(5, TimeUnit.SECONDS);

            // 处理标准输出
            try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(channel.getInvertedOut(), "GBK"))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println("STDOUT: " + line);
                }
            }

            // 处理错误输出
            try (BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(channel.getInvertedErr(), "GBK"))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println("STDERR: " + errorLine);
                }
            }

            // 确保命令完全执行完毕
            while (!channel.isClosed()) {
                Thread.sleep(500);
            }

            System.out.println("Exit status: " + channel.getExitStatus());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        session.close();
        sshClient.close();
    }

}
