package com.example.sshd;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.session.SessionListener;

/**
 * @Author zcy
 * @Date 2024/12/25 10:26
 * @Version 1.0
 */
public class CustomSessionListener implements SessionListener {

    /**
     * 最大连接数
     */
    private static final int MAX_GLOBAL_CONNECTIONS = 100;

    /**
     * 每个用户的最大会话数
     */
    private static final int MAX_SESSIONS_PER_USER = 20;

    /**
     * 当前连接计数
     */
    private final AtomicInteger globalConnectionCount = new AtomicInteger(0);

    /**
     * 用户连接数映射
     */
    private final ConcurrentHashMap<String, AtomicInteger> userSessionCounts = new ConcurrentHashMap<>();

    /**
     * 生命周期阶段：会话对象刚刚被创建时调用。
     * 说明：此时会话对象已初始化，但尚未完成握手，也没有建立连接。
     *
     * @param session
     */
    @Override
    public void sessionCreated(Session session) {
        // 全局连接计数 +1
        int currentGlobalConnections = globalConnectionCount.incrementAndGet();

        if (currentGlobalConnections > MAX_GLOBAL_CONNECTIONS) {
            System.out.println("超过全局连接数，关闭当前session: " + session);
            session.close(true); // 强制关闭会话
            globalConnectionCount.decrementAndGet(); // 减回计数
        } else {
            System.out.println("当前全局连接数为：" + currentGlobalConnections);
        }
        String remoteAddress = session.getIoSession().getRemoteAddress().toString();
        System.out.println(
            "Session 开始创建: IP 为 " + remoteAddress + ", 时间为 " + java.time.Instant.now());

    }

    /**
     * 生命周期阶段：会话完全建立后调用。
     * 说明：SSH 握手完成，密钥交换和认证完成，连接已经可以使用。
     * 常用用途：可以在此处记录完整的会话信息，比如 IP
     *
     * @param session
     */
    @Override
    public void sessionEstablished(Session session) {
        // 打印成功建立会话的日志
        String remoteAddress = session.getIoSession().getRemoteAddress().toString();
        String timestamp = java.time.Instant.now().toString();

        System.out.println("连接建立成功: ");
        System.out.println("IP: " + remoteAddress);
        System.out.println("时间戳: " + timestamp);
    }

    /**
     * 生命周期阶段：会话完全关闭时调用。
     * 说明：资源清理完成，连接完全关闭。
     * 常用用途：用于清理资源或记录关闭事件。
     *
     * @param session
     */
    @Override
    public void sessionClosed(Session session) {
        // **全局计数 -1**
        globalConnectionCount.decrementAndGet();

        String username = session.getUsername();
        if (username != null && !username.isEmpty()) {
            // **用户会话计数 -1**
            userSessionCounts.computeIfPresent(username, (key, counter) -> {
                counter.decrementAndGet();
                return counter.get() > 0 ? counter : null; // 如果计数为 0，则移除用户记录
            });
        }

        System.out.println(
            "连接关闭，当前全局连接数为： " + globalConnectionCount.get());
    }

    /**
     * 生命周期阶段：关键事件发生时调用。
     * 说明：触发的事件包括密钥交换完成（KeyEstablished）、认证完成（Authenticated）、协商完成（KexCompleted）。
     * 常用用途：可以用来监控这些关键事件的发生。
     *
     * @param session
     * @param event
     */
    @Override
    public void sessionEvent(Session session, Event event) {
        if (event == Event.Authenticated) {
            String username = session.getUsername();
            if (username == null || username.isEmpty()) {
                System.out.println("认证完成，但用户名为空，忽略处理。");
                return;
            }

            // **用户会话计数 +1**
            userSessionCounts.putIfAbsent(username, new AtomicInteger(0));
            int userConnections = userSessionCounts.get(username).incrementAndGet(); // 对用户计数加1

            if (userConnections > MAX_SESSIONS_PER_USER) {
                System.out.println("用户 " + username + " 创建过多连接，关闭当前session。");
                session.close(true); // 强制关闭会话
                userSessionCounts.get(username).decrementAndGet(); // 减回计数
            } else {
                System.out.println("当前用户 " + username + " 的连接数为：" + userConnections);
            }
        }
    }

    @Override
    public void sessionException(Session session, Throwable t) {
        System.err.println("发生错误，session为： " + session);
        t.printStackTrace();
    }

    /**
     * 生命周期阶段：会话断开时调用。
     * 说明：无论是客户端还是服务器主动断开，都会触发。
     * 常用用途：记录断开原因、时间和相关会话信息。
     * @param session
     * @param reason
     * @param msg
     * @param language
     * @param initiator
     */
    @Override
    public void sessionDisconnect(Session session, int reason, String msg, String language,
        boolean initiator) {
        System.out.println("连接断开，连接断开原因： Reason: " + reason + ", 相关信息为： " + msg);
    }
}
