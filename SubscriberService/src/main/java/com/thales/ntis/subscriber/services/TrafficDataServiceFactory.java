package com.thales.ntis.subscriber.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.model.FeedType;

public class TrafficDataServiceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TrafficDataService.class);

    public static TrafficDataService newInstance(D2LogicalModel request) {

        String feedType = request.getPayloadPublication().getFeedType();

        LOG.info("FeedType is : " + feedType);

        if (feedType.toLowerCase().contains(FeedType.ANPR.lowerCase())) {
            return new ANPRTrafficDataServiceImpl();

        } else if (feedType.toLowerCase().contains(FeedType.MIDAS.lowerCase())) {
            return new MIDASTrafficDataServiceImpl();

        } else if (feedType.toLowerCase().contains(FeedType.TMU.lowerCase())) {
            return new TMUTrafficDataServiceImpl();

        } else if (feedType.toLowerCase().contains(FeedType.FUSED_SENSOR_ONLY.lowerCase())) {
            return new FusedSensorOnlyTrafficDataServiceImpl();

        } else if (feedType.toLowerCase().contains(FeedType.FUSED_FVD_AND_SENSOR_PTD.lowerCase())) {
            return new FusedFvdAndSensorTrafficDataServiceImpl();

        } else if (feedType.toLowerCase().contains(FeedType.VMS.lowerCase())) {
            return new VMSTrafficDataServiceImpl();

        } else if (feedType.toLowerCase().contains(FeedType.NTIS_MODEL_UPDATE_NOTIFICATION.lowerCase())) {
            return new NtisModelNotificationDataServiceImpl();

        } else if (feedType.toLowerCase().contains(FeedType.EVENT_DATA.lowerCase())) {
            return new EventDataServiceImpl();

        } else {
            LOG.error("Unrecognised Feed Type: " + feedType);
            return null;
        }
    }
}
