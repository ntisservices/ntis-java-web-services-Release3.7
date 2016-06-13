package com.thales.ntis.subscriber.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.BasicData;
import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.datex.MeasuredDataPublication;
import com.thales.ntis.subscriber.datex.MeasuredValue;
import com.thales.ntis.subscriber.datex.MultilingualStringValue;
import com.thales.ntis.subscriber.datex.SiteMeasurements;
import com.thales.ntis.subscriber.datex.SiteMeasurementsIndexMeasuredValue;
import com.thales.ntis.subscriber.datex.TrafficConcentration;
import com.thales.ntis.subscriber.datex.TrafficFlow;
import com.thales.ntis.subscriber.datex.TrafficHeadway;
import com.thales.ntis.subscriber.datex.TrafficSpeed;

/**
 * This is an example service class implementation.
 * 
 */
@Service
public class MIDASTrafficDataServiceImpl implements
        TrafficDataService {

    private static final Logger LOG = LoggerFactory.getLogger(MIDASTrafficDataServiceImpl.class);
    private static final String PUBLICATION_TYPE = "MIDAS Traffic Data Publication";

    @Override
    public void handle(D2LogicalModel d2LogicalModel) {

        LOG.info(PUBLICATION_TYPE + ": received...");

        MeasuredDataPublication measuredDataPublication = null;

        try {
            measuredDataPublication = (MeasuredDataPublication) d2LogicalModel.getPayloadPublication();
            if (measuredDataPublication != null) {
                List<SiteMeasurements> siteMeasurementsInPayload = measuredDataPublication.getSiteMeasurements();

                LOG.info("Number of Site Measurements in payload: " + siteMeasurementsInPayload.size());

                for (SiteMeasurements measurementsForSite : siteMeasurementsInPayload) {
                    extractTrafficDataFromSiteMeasurements(measurementsForSite);
                }
                LOG.info(PUBLICATION_TYPE + ": processed successfully.");
            }
        } catch (Exception e) {
                LOG.error(e.getMessage());
        }
    }

    private void extractTrafficDataFromSiteMeasurements(SiteMeasurements measurementsForSite) {

        String siteGUID = measurementsForSite.getMeasurementSiteReference().getId();
        LOG.info("MIDAS site ID: " + siteGUID);
        LOG.info("Number of measurements for MIDAS site: " + measurementsForSite.getMeasuredValue().size());

        // There can be a number of measured values reported for the site
        for (SiteMeasurementsIndexMeasuredValue measuredValue : measurementsForSite.getMeasuredValue()) {

            MeasuredValue mv = measuredValue.getMeasuredValue();
            BasicData basicData = mv.getBasicData();
            
            // The index number of the site measurement is important - as this relates the data
            // to the NTIS reference model, which adds context to the value (e.g. lane information, 
            // or vehicle characteristics)
            int index = measuredValue.getIndex();

            // Determine what class (type) of traffic data is contained in the basic data
            if (TrafficFlow.class.equals(basicData.getClass())) {
                TrafficFlow flow = (TrafficFlow)basicData;
                LOG.info("[Measurement Index : " + index + "] Vehicle Flow Rate: " + flow.getVehicleFlow().getVehicleFlowRate());
                
                if(flow.getVehicleFlow().isDataError()) {
                    List<MultilingualStringValue> errorReason = flow.getVehicleFlow().getReasonForDataError().getValues().getValue();
                    for(MultilingualStringValue value : errorReason) {
                        LOG.info("    Data in error. Reason: \"" + value.getValue() + "\"");
                    }
                }

            } else if (TrafficSpeed.class.equals(basicData.getClass())) {
                TrafficSpeed speed = (TrafficSpeed) basicData;
                LOG.info("[Measurement Index : " + index + "] Average Speed: " + speed.getAverageVehicleSpeed().getSpeed());

            } else if (TrafficHeadway.class.equals(basicData.getClass())) {
                TrafficHeadway headway = (TrafficHeadway) basicData;
                LOG.info("[Measurement Index : " + index + "] Average Headway: " + headway.getAverageTimeHeadway().getDuration());

            } else if (TrafficConcentration.class.equals(basicData.getClass())) {
                TrafficConcentration concentration = (TrafficConcentration) basicData;
                LOG.info("[Measurement Index : " + index + "] Traffic Occupancy (%): "
                        + concentration.getOccupancy().getPercentage());

            } else {
                LOG.error("Unexpected traffic data type contained in publication: " + basicData.getClass().getSimpleName());
            }
        }
    }
}