package com.thales.ntis.subscriber.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.datex.GenericPublication;
import com.thales.ntis.subscriber.datex.GenericPublicationExtensionType;
import com.thales.ntis.subscriber.datex.NtisModelVersionInformation;

/**
 * This is an example service class implementation.
 * 
 */
@Service
public class NtisModelNotificationDataServiceImpl implements TrafficDataService {

    private static final Logger LOG = LoggerFactory.getLogger(NtisModelNotificationDataServiceImpl.class);
    private static final String PUBLICATION_TYPE = "NTIS Model Update Notification";

    @Override
    public void handle(D2LogicalModel d2LogicalModel) {

        LOG.info(PUBLICATION_TYPE + ": received...");

        GenericPublication genericPublication = null;

        try {
            genericPublication = (GenericPublication) d2LogicalModel.getPayloadPublication();
            
            if (genericPublication != null) {
                GenericPublicationExtensionType genericPublicationExtension = genericPublication.getGenericPublicationExtension();
                NtisModelVersionInformation ntisModelVersionInformation = genericPublicationExtension
                        .getNtisModelVersionInformation();
                LOG.info("Model file name: " + ntisModelVersionInformation.getModelFilename());
                LOG.info("Model version: v" + ntisModelVersionInformation.getModelVersion());
                LOG.info("Model publication time: " + ntisModelVersionInformation.getModelPublicationTime());

                LOG.info(PUBLICATION_TYPE + ": processed successfully.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
