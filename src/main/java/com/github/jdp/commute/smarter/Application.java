package com.github.jdp.commute.smarter;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;

import com.github.jdp.commute.smarter.config.ApplicationConfiguration;
import com.newrelic.api.agent.NewRelic;

/**
 * Application entry point. Launches a spring boot application.
 * The application is configured via {@link ApplicationConfiguration}.
 */
@EnableAutoConfiguration
@Import({ApplicationConfiguration.class})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        // Notice any uncaught exceptions at runtime.
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread t, final Throwable e) {
                log.error(e.getMessage(), e);
                NewRelic.noticeError(e);
            }
        });

        // Launch the application via Spring-Boot.
        SpringApplication.run(Application.class, args);
    }
}