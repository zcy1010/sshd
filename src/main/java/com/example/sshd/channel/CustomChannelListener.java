package com.example.sshd.channel;

import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.session.Session;

/**
 * @Author zcy
 * @Date 2025/1/6 13:57
 * @Version 1.0
 */

public class CustomChannelListener implements ChannelListener {
    @Override
    public void channelInitialized(Channel channel) {
        Session session = channel.getSession();
        System.out.println(String.format("[Channel Initialized] Channel: %s, Type: %s, Channel Type: %s, Session: %s",
            channel, channel.getClass().getSimpleName(), getChannelType(channel), getSessionInfo(session)));
    }

    @Override
    public void channelOpenSuccess(Channel channel) {
        Session session = channel.getSession();
        System.out.println(String.format("[Channel Open Success] Channel: %s, Type: %s, Channel Type: %s, Session: %s",
            channel, channel.getClass().getSimpleName(), getChannelType(channel), getSessionInfo(session)));
    }

    @Override
    public void channelOpenFailure(Channel channel, Throwable reason) {
        Session session = channel.getSession();
        System.out.println(String.format("[Channel Open Failure] Channel: %s, Type: %s, Channel Type: %s, Session: %s, Reason: %s",
            channel, channel.getClass().getSimpleName(), getChannelType(channel), getSessionInfo(session), reason.getMessage()));
    }

    @Override
    public void channelStateChanged(Channel channel, String hint) {
        Session session = channel.getSession();
        System.out.println(String.format("[Channel State Changed] Channel: %s, Type: %s, Channel Type: %s, Session: %s, Hint: %s",
            channel, channel.getClass().getSimpleName(), getChannelType(channel), getSessionInfo(session), hint));
    }

    @Override
    public void channelClosed(Channel channel, Throwable reason) {
        Session session = channel.getSession();
        System.out.println(String.format("[Channel Closed] Channel: %s, Type: %s, Channel Type: %s, Session: %s, Reason: %s",
            channel, channel.getClass().getSimpleName(), getChannelType(channel),
            getSessionInfo(session),
            reason != null ? reason.getMessage() : "No reason provided"));
    }

    /**
     * Helper method to extract session information for logging.
     */
    private String getSessionInfo(Session session) {
        if (session == null) {
            return "Unknown Session";
        }
        return String.format("IOSessionID=%s,SessionID=%s, Username=%s", session.getIoSession().getId(),session.getIoSession().getId(), session.getUsername());
    }

    /**
     * Helper method to determine the channel type for logging.
     */
    private String getChannelType(Channel channel) {
       long channelId= channel.getChannelId();
        if (channel == null) {
            return "Unknown Channel";
        }

        // 判断通道的具体类型
        if (channel.getClass().getSimpleName().contains("Session")) {
            return " channelId: "+channelId+"  Shell/Exec Channel";
        } else if (channel.getClass().getSimpleName().contains("SftpSubsystem")) {
            return "SFTP Channel";
        } else if (channel.getClass().getSimpleName().contains("TcpipForward")) {
            return "TCP Forwarding Channel";
        } else if (channel.getClass().getSimpleName().contains("X11Forward")) {
            return "X11 Forwarding Channel";
        } else {
            return "Unknown/Custom Channel Type";
        }
    }
}
