package com.github.jdp.commute.smarter.config;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class ApplicationConfiguration {

    /**
     * A Jetty HTTP server. The port is based on the PORT environment variable
     * configured at startup, otherwise a default port of 10430 is used.
     *
     * @param port port number
     * @return Jetty server
     */
    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(@Value("${PORT:10430}") final String port,
            @Value("${jetty.threadPool.maxThreads:200}") final Integer maxThreads,
            @Value("${jetty.threadpool.minThreads:8}") final Integer minThreads,
            @Value("${jetty.threadpool.idleTimeout:60000}") final Integer idleTimeout) {
        final JettyEmbeddedServletContainerFactory factory =  new JettyEmbeddedServletContainerFactory(Integer.parseInt(port));
        factory.addServerCustomizers(new JettyServerCustomizerImpl(maxThreads, minThreads, idleTimeout));
        return factory;
    }

    /**
     * Custom implementation of the {@link JettyServerCustomizer} interface.
     */
    private static class JettyServerCustomizerImpl implements JettyServerCustomizer {

        /**
         * Idle connection timeout.
         */
        private final int idleTimeout;

        /**
         * Maximum number of connection threads.
         */
        private final int maxThreads;

        /**
         * Minimum number of connection threads.
         */
        private final int minThreads;

        /**
         * Creates a new {@link JettyServerCustomizerImpl} implementation.
         * @param maxThreads Maximum number of connection threads.
         * @param minThreads Minimum number of connection threads.
         * @param idleTimeout Idle connection timeout.
         */
        JettyServerCustomizerImpl(final int maxThreads, final int minThreads, final int idleTimeout) {
            this.idleTimeout = idleTimeout;
            this.maxThreads = maxThreads;
            this.minThreads = minThreads;
        }

        @Override
        public void customize(final Server server) {
            // Expose Jetty managed beans to the JMX platform server provided by Spring
            final MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
            server.addBean(mbContainer);

            // Tweak the connection pool used by Jetty to handle incoming HTTP connections
            final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
            threadPool.setMaxThreads(maxThreads);
            threadPool.setMinThreads(minThreads);
            threadPool.setIdleTimeout(idleTimeout);
        }
    }
}