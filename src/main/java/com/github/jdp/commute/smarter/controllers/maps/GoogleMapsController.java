package com.github.jdp.commute.smarter.controllers.maps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.newrelic.api.agent.NewRelic;

@Controller
@RequestMapping(value={"/api/v1/maps"})
public class GoogleMapsController implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(GoogleMapsController.class);

    private static final String GOOGLE_MAPS_API_URL_FORMAT = "https://maps.googleapis.com/maps/api/js?key=%s&signed_in=true&callback=initMap";

    @Value("${GOOGLE_API_KEY}")
    private String googleApiKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub

    }

    @RequestMapping(value="map.js", produces={"application/javascript"})
    public void loadMap(final HttpServletResponse response) {
        try {
            log.info("Looking up map...");
            //TODO use something better than URLConnection
            final URL url = new URL(String.format(GOOGLE_MAPS_API_URL_FORMAT, googleApiKey));
            final URLConnection connection = url.openConnection();
            final InputStream is = connection.getInputStream();
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            log.info("Response flushed to UI.");
        } catch (final IOException  e) {
            NewRelic.noticeError(e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
