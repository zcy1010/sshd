package com.example.sshd;

import java.io.IOException;
import java.net.SocketAddress;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.io.IoAcceptor;
import org.apache.sshd.common.io.IoConnector;
import org.apache.sshd.common.io.IoServiceEventListener;
import org.apache.sshd.common.session.Session;

/**
 * @Author zcy
 * @Date 2025/1/3 15:13
 * @Version 1.0
 */
public class CustomIoServiceEventListener implements IoServiceEventListener {
    @Override
    public void connectionEstablished(IoConnector connector, SocketAddress local, AttributeRepository context, SocketAddress remote) throws IOException {
        // 当客户端连接成功建立时触发
        System.out.println("[连接建立] 本地地址: " + local + ", 远程地址: " + remote);
    }

    @Override
    public void abortEstablishedConnection(IoConnector connector, SocketAddress local, AttributeRepository context, SocketAddress remote, Throwable reason) throws IOException {
        // 当连接被异常中止时触发
        System.err.println("[连接中止] 本地地址: " + local + ", 远程地址: " + remote + ", 异常: " + reason.getMessage());
        reason.printStackTrace();
    }

    @Override
    public void connectionAccepted(IoAcceptor acceptor, SocketAddress local, SocketAddress remote, SocketAddress service) throws IOException {
        // 当服务端接受客户端连接时触发
        System.out.println("[连接接受] 本地地址: " + local + ", 远程地址: " + remote + ", 服务地址: " + service);
    }

    @Override
    public void abortAcceptedConnection(IoAcceptor acceptor, SocketAddress local, SocketAddress remote, SocketAddress service, Throwable reason) throws IOException {
        // 当接受的连接被异常中止时触发
        System.err.println("[接受连接中止] 本地地址: " + local + ", 远程地址: " + remote + ", 服务地址: " + service + ", 异常: " + reason.getMessage());
        reason.printStackTrace();
    }
}
