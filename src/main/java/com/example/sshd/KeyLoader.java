package com.example.sshd;

import java.security.GeneralSecurityException;
import org.apache.sshd.common.config.keys.loader.openssh.OpenSSHKeyPairResourceParser;
import org.apache.sshd.common.util.io.resource.PathResource;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.security.KeyPair;
/**
 * @Author zcy
 * @Date 2024/12/19 15:06
 * @Version 1.0
 */
public class KeyLoader {
    /**
     * 从 PEM 文件加载 KeyPair
     * @param privateKeyPath 私钥文件路径
     * @return KeyPair 对象
     * @throws IOException 如果解析失败
     */
        /**
         * 从 OpenSSH 私钥文件加载 KeyPair
         * @param privateKeyPath 私钥文件路径
         * @return KeyPair 对象
         * @throws IOException 如果解析失败
         */
        public static KeyPair loadKeyPair(Path privateKeyPath)
            throws IOException, GeneralSecurityException {
            // 使用 OpenSSHKeyPairResourceParser 解析 OpenSSH 私钥
            PathResource resource = new PathResource(privateKeyPath);
            return OpenSSHKeyPairResourceParser.INSTANCE.loadKeyPairs(null, resource, null).iterator().next();
        }

}
