package com.example.sshd.err;

import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.sshd.sftp.common.SftpException;
import org.apache.sshd.sftp.server.SftpErrorStatusDataHandler;
import org.apache.sshd.sftp.server.SftpSubsystemEnvironment;
import org.slf4j.MDC;

/**
 * @Author zcy
 * @Date 2025/1/6 16:29
 * @Version 1.0
 */
public class DetailedSftpErrorStatusDataHandler implements SftpErrorStatusDataHandler {
    public static final DetailedSftpErrorStatusDataHandler INSTANCE = new DetailedSftpErrorStatusDataHandler();

    public DetailedSftpErrorStatusDataHandler() {
        super();
    }

    @Override
    public String resolveErrorMessage(
        SftpSubsystemEnvironment sftpSubsystem, int id, Throwable e, int subStatus, int cmd, Object... args) {
        Map mdcMap = new HashMap<>();
        Map<String, String> expect = MDC.getCopyOfContextMap();
        if (expect != null) {
            mdcMap.putAll(expect);
        }
//        mdcMap.put(LogEnvConstants.ERR_CODE, ExceptionCode.SshdException.INVOKE_SFTP_ERROR.getCode());
        MDC.setContextMap(mdcMap);
        if (e instanceof FileSystemException) {
            FileSystemException fse = (FileSystemException) e;
            String file = fse.getFile();
            String otherFile = fse.getOtherFile();
            String message = fse.getReason();

            String errorMsg =  e.getClass().getSimpleName()
                + "[file=" + file + "]"
                + (Objects.equals(file, otherFile) ? "" : "[other=" + otherFile + "]")
                + ": " + message;
            System.out.println("1212"+errorMsg+e);
            return errorMsg;
        } else if (e instanceof SftpException) {
            System.out.println("1212"+""+e);
            return e.toString();
        } else {
            System.out.println("1212"+""+e);
            return "Internal " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }
}