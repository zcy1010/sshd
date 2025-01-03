package com.example.sshd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;

import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.client.fs.SftpFileSystem;
import org.apache.sshd.sftp.client.fs.SftpPath;


/**
 * 客户端1
 * 命令行操作
 *
 *
 * @Author zcy
 * @Date 2024/12/17 17:25
 * @Version 1.0
 */
public class SshdTest {

    private SshClient client;
    private ClientSession session;

    public static void main(String[] args) {
        SshdTest sshUtil = new SshdTest("127.0.0.1",22,"zcy","101072");
//        System.out.println(sshUtil.execCommand("netstat"));

    }
    /**
     * 上传文件
     * @param uploadFile 上传的文件
     * @param remotePath 远程目录
     */
    public  String uploadFile(File uploadFile,String remotePath) {
        try(SftpClient  sftpClient = SftpClientFactory.instance().createSftpClient(session);
            OutputStream outputStream = sftpClient.write(remotePath)
        ) {
            Files.copy(uploadFile.toPath(), outputStream);
            return remotePath;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 密码登录
     *
     * @param host
     * @param port
     * @param username
     * @param password
     */
    public SshdTest(String host, int port, String username, String password) {
        connect(host, port, username);
        try {
            session.addPasswordIdentity(password); // for password-based authentication

            if (session.auth().verify(5000).isFailure()) {
                throw new RuntimeException("验证失败");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 连接
     *
     * @param host
     * @param port
     * @param username
     */
    private void connect(String host, int port, String username) {
        client = SshClient.setUpDefaultClient();
        client.start();
        try {
            session = client.connect(username, host, port)
                .verify(5000)
                .getSession();
            System.out.println("连接成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 执行命令
     *
     * @param command
     * @return
     */
    public String execCommand(String command) {
        session.resetAuthTimeout();
        System.out.println("exe cmd: " + command);

        // 返回结果流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 错误信息
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try (ChannelExec channelExec = session.createExecChannel(command)) {

            channelExec.setOut(out);
            channelExec.setErr(err);
            // 执行并等待
            channelExec.open();

            Set<ClientChannelEvent> events =
                channelExec.waitFor(
                    EnumSet.of(ClientChannelEvent.CLOSED),
                    TimeUnit.SECONDS.toMillis(100000));
            // 检查请求是否超时
            if (events.contains(ClientChannelEvent.TIMEOUT)) {
                throw new RuntimeException("请求连接超时");
            }
            // 一般退出状态为0表示正常
            int exitStatus = channelExec.getExitStatus();
            if (exitStatus == 1) {
                System.out.println("exitStatus:" + exitStatus);
//				throw new RuntimeException("执行命令失败："+exitStatus);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toString().trim();
    }


}
