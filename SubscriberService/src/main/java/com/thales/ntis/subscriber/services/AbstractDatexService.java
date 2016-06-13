package com.thales.ntis.subscriber.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thales.ntis.subscriber.datex.D2LogicalModel;

public abstract class AbstractDatexService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDatexService.class);

    public boolean validate(D2LogicalModel request) {

        // D2LogicalModel is at the base element of the request so must not be
        // null.
        if (request != null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("D2LogicalModel is " + request);
            }

        } else {
            LOG.error("D2LogicalModel is null! Incoming request does not appear to be valid!");
            return false;
        }

        // Exchange must not be null.
        if (request.getExchange() != null) {
            LOG.info("Country is "
                    + request.getExchange().getSupplierIdentification()
                            .getCountry().value());
            LOG.info("National Identifier is "
                    + request.getExchange().getSupplierIdentification()
                            .getNationalIdentifier());
        } else {
            LOG.error("Exchange is null! Incoming request does not appear to be valid!");
            return false;
        }
        return true;
    }

}
