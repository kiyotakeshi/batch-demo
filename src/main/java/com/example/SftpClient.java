package com.example;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

@Slf4j
public class SftpClient {

    public String getCsv() throws IOException {
        Session session = null;
        ChannelSftp sftp = null;

        try {
            session = createSession();
            session.connect();
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();

            // TODO: read from config
            sftp.cd("/");
            Vector<ChannelSftp.LsEntry> entries = sftp.ls(".");
            SortedSet<String> files = entries.stream()
                    .map(ChannelSftp.LsEntry::getFilename)
                    .filter(fileName -> fileName.endsWith(".csv"))
                    .collect(Collectors.toCollection(TreeSet::new));

            return IOUtils.toString(sftp.get(files.last()), String.valueOf(StandardCharsets.UTF_8));

        } catch (JSchException | SftpException e) {
            throw new RuntimeException(e);
        }
    }

    private Session createSession() throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity("src/test/resources/dummy");
        Session session = jsch.getSession("test","localhost");
        session.setConfig("StrictHostKeyChecking", "no");
        // TODO: read from config
        session.setPort(10222);
        return session;
    }
}
