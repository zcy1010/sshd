package com.example.sshd.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.apache.sshd.common.kex.KexProposalOption;
import org.apache.sshd.common.session.SessionContext;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.session.ServerSessionImpl;

/**
 * @Author zcy
 * @Date 2025/5/6 21:12
 * @Version 1.0
 */
public class CustomServerSession extends ServerSessionImpl {
    public CustomServerSession(SshServer server, org.apache.sshd.common.io.IoSession ioSession) throws Exception {
        super(server, ioSession);
    }

    @Override
    protected Map<KexProposalOption, String> createProposal(String hostKeyTypes)
        throws IOException {
        // 获取默认提案
        Map<KexProposalOption, String> proposal = super.createProposal(hostKeyTypes);

        // 强制设置密钥交换算法，仅包含 JSch 支持的算法
        String kexAlgs = GenericUtils.join(
            new String[]{"diffie-hellman-group14-sha1", "diffie-hellman-group1-sha1"},
            ','
        );
        proposal.put(KexProposalOption.ALGORITHMS, kexAlgs);

        return proposal;
    }
}