package ch.gryphus.chainvault.config;

import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

/**
 * The type Sftp target config.
 */
@Configuration
public class SftpTargetConfig {

    private final SftpProperties props;

    /**
     * Instantiates a new Sftp target config.
     *
     * @param props the props
     */
    public SftpTargetConfig(SftpProperties props) {
        this.props = props;
    }

    /**
     * Sftp session factory caching session factory.
     *
     * @return the caching session factory
     */
    @Bean
    public CachingSessionFactory<SftpClient.DirEntry> sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);  // true = allow unknown keys (dev); false = strict in prod

        factory.setHost(props.getHost());
        factory.setPort(props.getPort());
        factory.setUser(props.getUsername());

        if (props.getPassword() != null && !props.getPassword().isEmpty()) {
            factory.setPassword(props.getPassword());
        } else if (props.getPrivateKey() != null && props.getPrivateKey().exists()) {
            factory.setPrivateKey(props.getPrivateKey());
        }

        // Strict host key checking (recommended for compliance)
        if (props.getKnownHosts() != null && props.getKnownHosts().exists()) {
            factory.setKnownHostsResource(props.getKnownHosts());
        } else if (props.isAllowUnknownKeys()) {
            factory.setAllowUnknownKeys(true);
        }

        // Cache sessions for reuse (critical for performance with many uploads)
        return new CachingSessionFactory<>(factory, 10);  // Cache up to 10 sessions
    }

    /**
     * Sftp remote file template sftp remote file template.
     *
     * @param sessionFactory the session factory
     * @return the sftp remote file template
     */
    @Bean
    public SftpRemoteFileTemplate sftpRemoteFileTemplate(SessionFactory<SftpClient.DirEntry> sessionFactory) {
        return new SftpRemoteFileTemplate(sessionFactory);
    }

    /**
     * Gets remote directory.
     *
     * @return the remote directory
     */
    /*
     * helper for other beans/services that need the directory path; keeps
     * SftpProperties encapsulated while still available.
     */
    public String getRemoteDirectory() {
        return props.getRemoteDirectory();
    }
} 