package com.thales.ntis.subscriber.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.datex.MeasuredDataPublication;
import com.thales.ntis.subscriber.datex.MeasuredValue;
import com.thales.ntis.subscriber.datex.SiteMeasurements;
import com.thales.ntis.subscriber.datex.TravelTimeData;

/**
 * This is an example service class implementation.
 * 
 */
@Service
public class ANPRTrafficDataServiceImpl implements
        TrafficDataService {

    private static final Logger LOG = LoggerFactory.getLogger(ANPRTrafficDataServiceImpl.class);
    private static final String PUBLICATION_TYPE = "ANPR Publication";

    @Override
    public void handle(
            D2LogicalModel d2LogicalModel) {

        LOG.info(PUBLICATION_TYPE + ": received...");

        MeasuredDataPublication measuredDataPublication = null;

        try {
            measuredDataPublication = (MeasuredDataPublication) d2LogicalModel.getPayloadPublication();
            if (measuredDataPublication != null) {
                List<SiteMeasurements> siteMeasurementsInPayload = measuredDataPublication.getSiteMeasurements();

                LOG.info("Number of Site Measurements in payload: " + siteMeasurementsInPayload.size());

                for (SiteMeasurements measurementsForSite : siteMeasurementsInPayload) {
                    extractTravelTimesFromSiteMeasurements(measurementsForSite);
                }
                LOG.info(PUBLICATION_TYPE + ": processed successfully.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private void extractTravelTimesFromSiteMeasurements(SiteMeasurements siteMeasurements) {
        String anprRouteId = siteMeasurements.getMeasurementSiteReference().getId();
        // Should only be one travel time value per SiteMeasurements
        // element (index=0)
        MeasuredValue value = siteMeasurements.getMeasuredValue().get(0).getMeasuredValue();
        if (TravelTimeData.class.equals(value.getBasicData().getClass()))
        {
            TravelTimeData ttData = (TravelTimeData) value.getBasicData();
            LOG.info("Travel Time for ANPR Route " + anprRouteId + " : " + ttData.getTravelTime().getDuration() + "s");
        }
    }
}