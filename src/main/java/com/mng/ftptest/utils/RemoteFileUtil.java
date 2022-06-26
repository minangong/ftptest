package com.mng.ftptest.utils;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Airflow job - Remote File Util.
 *
 * @author wh
 * @date 2022/06/05
 */
@Component
@Slf4j
public class RemoteFileUtil {
    private static Session sshSession = null;


    private static String host;
    private static String username;
    private static String password;
    @Value("${workflow.airflow.host}")
    private void setHost(String h) {
        host = h;
    }
    @Value("${workflow.airflow.username}")
    private void setUsername(String un) {
        username = un;
    }
    @Value("${workflow.airflow.password}")
    private void setPassword(String pw) {
        password = pw;
    }

    @PostConstruct
    private void initSession() {
        JSch jsch = new JSch();
        //获取sshSession  账号-ip-端口
        try {
            sshSession = jsch.getSession(username, host);
            //添加密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            //开启sshSession链接
            sshSession.connect();
            if(!sshSession.isConnected()){
                log.info("连接失败");
            }else{
                log.info("连接成功");
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    /**
     * Description: 向FTP服务器上传文件
     */
    public static boolean uploadFile(String filePath, String context) {
        InputStream input = IOUtils.toInputStream(context, StandardCharsets.UTF_8);
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            //设置为被动模式
            ftp.enterLocalPassiveMode();
            //设置上传文件的类型为二进制类型
            sftp.put(input, filePath);
            input.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
        return result;
    }


    /**
     * Description: 删除服务器文件
     */
    public static boolean deleteFile(String filePath) {
        boolean result = false;
        FTPClient ftp = new FTPClient();
        try {
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            ChannelSftp sftp = (ChannelSftp) channel;
            //设置为被动模式
            ftp.enterLocalPassiveMode();
            //设置上传文件的类型为二进制类型
            sftp.rm(filePath);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
        return result;
    }
}