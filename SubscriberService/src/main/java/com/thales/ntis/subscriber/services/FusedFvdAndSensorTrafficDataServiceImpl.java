package com.thales.ntis.subscriber.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.BasicData;
import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.datex.ElaboratedData;
import com.thales.ntis.subscriber.datex.ElaboratedDataPublication;
import com.thales.ntis.subscriber.datex.LocationByReference;
import com.thales.ntis.subscriber.datex.TrafficSpeed;
import com.thales.ntis.subscriber.datex.TravelTimeData;

/**
 * This is an example service class implementation.
 * 
 */
@Service
public class FusedFvdAndSensorTrafficDataServiceImpl implements TrafficDataService {

    private static final Logger LOG = LoggerFactory.getLogger(FusedFvdAndSensorTrafficDataServiceImpl.class);
    private static final String PUBLICATION_TYPE = "Fused FVD and Sensor Traffic Data Publication";

    @Override
    public void handle(D2LogicalModel d2LogicalModel) {

        LOG.info(PUBLICATION_TYPE + ": received...");

        ElaboratedDataPublication elaboratedDataPublication = null;

        try {
            elaboratedDataPublication = (ElaboratedDataPublication) d2LogicalModel.getPayloadPublication();
            if (elaboratedDataPublication != null) {
                List<ElaboratedData> elaboratedDataList = elaboratedDataPublication.getElaboratedData();
                
                LOG.info("Number of data items in the publication: " + elaboratedDataList.size());
                
                for(ElaboratedData dataItem : elaboratedDataList) {
                    extractTrafficDataFromElaboratedData(dataItem);
                }
                LOG.info(PUBLICATION_TYPE + ": processed successfully.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private void extractTrafficDataFromElaboratedData(ElaboratedData dataItem) {

        // Location is always specified as LocationByReference (referenced to a
        // single Network Link)
        LocationByReference location = (LocationByReference) dataItem.getBasicData().getPertinentLocation();
        LOG.info("Data for Network Link: " + location.getPredefinedLocationReference().getId());
        
        BasicData basicData = dataItem.getBasicData();

        // Determine what class (type) of traffic data is contained in the basic data
        if (TrafficSpeed.class.equals(basicData.getClass())) {
            TrafficSpeed speed = (TrafficSpeed)basicData;
            
            // There are 2 types of speed data - current and forecast, determined by 
            // whether a measurementOrCalculationTime element exists in the data item.
            if(speed.getMeasurementOrCalculationTime() == null) {
                LOG.info("Fused Average Speed: " + speed.getAverageVehicleSpeed().getSpeed());
                LOG.info("FVD-Only Average Speed: " + speed.getTrafficSpeedExtension().getSpeedFvdOnly().getSpeed());
            } else {
                LOG.info("Fused Average Speed (forecast for " + speed.getMeasurementOrCalculationTime().toString() + "): "
                        + speed.getAverageVehicleSpeed().getSpeed());
            }
            
        } else if (TravelTimeData.class.equals(basicData.getClass())) {
                TravelTimeData travelTimeData = (TravelTimeData)basicData;

            // There are 2 types of travel time data - current and forecast,
            // determined by whether a measurementOrCalculationTime element
            // exists in the data item.
            if (travelTimeData.getMeasurementOrCalculationTime() == null) {
                LOG.info("Travel Time: " + travelTimeData.getTravelTime().getDuration());
                LOG.info("Free Flow Travel Time: " + travelTimeData.getFreeFlowTravelTime().getDuration());
                LOG.info("Normally Expected Travel Time: " + travelTimeData.getNormallyExpectedTravelTime().getDuration());
            } else {
                LOG.info("Travel Time (forecast for " + travelTimeData.getMeasurementOrCalculationTime().toString() + "): "
                        + travelTimeData.getTravelTime().getDuration());
            }

        } else {
            LOG.error("Unexpected traffic data type contained in publication: " + dataItem.getClass().getSimpleName());
        }
    }
}
