package com.thales.ntis.subscriber.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.services.DatexIIService;

/**
 * This is a reference SubscriberServiceEndpoint. Business logic is delegated to
 * separate service classes.
 */

@Endpoint("subscriberServiceEndpoint")
public class SubscriberServiceEndpoint {

    private Logger log = LoggerFactory.getLogger(SubscriberServiceEndpoint.class);

    @Autowired
    private DatexIIService datexIIService;

    @PayloadRoot(namespace = "http://datex2.eu/schema/2/2_0", localPart = "d2LogicalModel")
    public void handle(@RequestPayload D2LogicalModel request) {
        log.info("Received request for subscription");
        datexIIService.handle(request);
    }
}
