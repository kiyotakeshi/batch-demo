package com.example;

import org.apache.commons.io.FileUtils;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

class SftpTests {

    SftpClient sut;

    @BeforeEach
    void beforeAll(@TempDir Path tempDir) throws IOException {
        // int port = SocketUtils.findAvailableTcpPort();
        int port = 10222;
        FileUtils.copyDirectory(new File("src/test/resources/"), tempDir.toFile());
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost("localhost");
        sshd.setPort(port);
        SftpSubsystemFactory factory = new SftpSubsystemFactory.Builder().build();
        sshd.setSubsystemFactories(Collections.singletonList(factory));
        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator
                // $ ssh-keygen -m PEM -t rsa -f dummy
                (new File("src/test/resources/dummy.pub").toPath()));
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());

        // 一時的なディレクトリをSFTPサーバのルートディレクトリに設定
        sshd.setFileSystemFactory(new VirtualFileSystemFactory(tempDir.toAbsolutePath()));
        sshd.start();
        sut = new SftpClient();
    }

    @Test
    void name() throws IOException {
        String csv = sut.getCsv();
        System.out.println(csv);
    }
}
