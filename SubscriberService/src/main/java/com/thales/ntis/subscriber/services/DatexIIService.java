package com.thales.ntis.subscriber.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.D2LogicalModel;

@Service
public class DatexIIService extends AbstractDatexService {

    private static final Logger LOG = LoggerFactory.getLogger(DatexIIService.class);

    public synchronized void handle(D2LogicalModel request) {
        LOG.info("Validate D2Logical Model - Started");

        if (!validate(request)) {
            LOG.info("D2Logical Model is not valid");
            throw new RuntimeException("Incoming request does not appear to be valid!");
        }

        LOG.info("Validate D2Logical Model - Completed Successfuly");

        TrafficDataServiceFactory.newInstance(request).handle(request);

    }
}
