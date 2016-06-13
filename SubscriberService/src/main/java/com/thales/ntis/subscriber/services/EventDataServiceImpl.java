package com.thales.ntis.subscriber.services;

import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.datex.MaintenanceWorks;
import com.thales.ntis.subscriber.datex.RoadMaintenanceTypeEnum;
import com.thales.ntis.subscriber.datex.Situation;
import com.thales.ntis.subscriber.datex.SituationPublication;
import com.thales.ntis.subscriber.datex.SituationRecord;
import com.thales.ntis.subscriber.model.FeedType;

/**
 * This is an example service class implementation.
 * 
 */
@Service
public class EventDataServiceImpl implements
        TrafficDataService {

    private static final HashMap<String, SituationRecord> EVENT_CACHE = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(EventDataServiceImpl.class);
    private static final Object LOCK_EVENT_UPDATES = new Object();
    private static final String NUMBER_OF_EVENTS_IN_CACHE = "Number of events in cache: {}";
    private static final String NUMBER_OF_EVENTS_IN_PAYLOAD = "Number of events in payload: {}";
    private static final String PUBLICATION_TYPE = "Event Data Publication";

    @Override
    public void handle(D2LogicalModel d2LogicalModel) {
        LOG.info("{}: received...", PUBLICATION_TYPE);
        LOG.info(NUMBER_OF_EVENTS_IN_CACHE, EVENT_CACHE.size());
        if (isEventFullRefresh(d2LogicalModel)) {
            handleFullEventRefresh(d2LogicalModel);
        } else {
            handleEventUpdate(d2LogicalModel);
        }

        LOG.info("{}: processed successfully.", PUBLICATION_TYPE);
        LOG.info(NUMBER_OF_EVENTS_IN_CACHE, EVENT_CACHE.size());
    }

    private void handleFullEventRefresh(D2LogicalModel request) {
        synchronized (LOCK_EVENT_UPDATES) {
            try {
                LOG.info("Processing Event Data Full Refresh.");
                HashMap<String, SituationRecord> tempEventCache = new HashMap<>(EVENT_CACHE);
                EVENT_CACHE.clear();
                processEventsInFullRefresh(request, tempEventCache);
                processEventsUpdatedAfterFullRefreshPublicationTime(request, tempEventCache);
                processRefreshedEventData();
                tempEventCache.clear();
                LOG.info("Finished processing Event Data Full Refresh.");
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void processEventsInFullRefresh(D2LogicalModel request, HashMap<String, SituationRecord> tempEventCache) {
        SituationPublication situationPublication = (SituationPublication) request.getPayloadPublication();
        if (situationPublication != null) {
            List<Situation> situations = situationPublication.getSituation();
            LOG.info(NUMBER_OF_EVENTS_IN_PAYLOAD, situations.size());
            for (Situation situation : situations) {
                refreshEvent(situation.getSituationRecord().get(0), tempEventCache);
            }
        }
    }

    private void refreshEvent(SituationRecord situationRecord, HashMap<String, SituationRecord> tempEventCache) {
        String eventId = situationRecord.getId();
        SituationRecord cachedSituationRecord = tempEventCache.remove(eventId);
        if (cachedSituationRecord != null) {
            if (isCachedEventNewerThanRefreshedEvent(cachedSituationRecord, situationRecord)) {
                EVENT_CACHE.put(eventId, cachedSituationRecord);
                LOG.info(
                        "Full Refresh version of event: {} is older than cached version, keeping cached version and ignoring the Full Refresh version.",
                        eventId);
            } else {
                EVENT_CACHE.put(eventId, situationRecord);
                LOG.info(
                        "Full Refresh version of event: {} is newer than cached version, keeping the Full Refresh version and ignoring the cached version.",
                        eventId);
            }
        } else {
            EVENT_CACHE.put(eventId, situationRecord);
            LOG.info(
                    "New event: {} received from the Full Refresh and stored in the cache.",
                    eventId);
        }
    }

    private void processEventsUpdatedAfterFullRefreshPublicationTime(D2LogicalModel request,
            HashMap<String, SituationRecord> tempEventCache) {
        DateTime fullRefreshPublicationTime = xmlGregoriaCalanderToDateTime(request.getPayloadPublication().getPublicationTime());
        LOG.info("Full Refresh Publication Time: {}", fullRefreshPublicationTime);
        for (SituationRecord record : tempEventCache.values()) {
            if (isEventUpdatedAfterFullRefreshPublicationTime(record, fullRefreshPublicationTime)) {
                EVENT_CACHE.put(record.getId(), record);
                LOG.info("Keeping cached version of event: {} as it has been updated after the Full Refresh Publication Time.",
                        record.getId());
            } else {
                LOG.info("Discarding cached version of event: {} as it is older than the Full Refresh Publication Time.",
                        record.getId());
            }
        }
    }

    private boolean isEventUpdatedAfterFullRefreshPublicationTime(SituationRecord event, DateTime dateTime) {
        DateTime eventVersionTime = xmlGregoriaCalanderToDateTime(event.getSituationRecordVersionTime());
        return eventVersionTime.isAfter(dateTime);
    }

    private boolean isCachedEventNewerThanRefreshedEvent(SituationRecord cachedEvent, SituationRecord refreshEvent) {
        DateTime cachedVersionTime = xmlGregoriaCalanderToDateTime(cachedEvent.getSituationRecordVersionTime());
        DateTime refreshVersionTime = xmlGregoriaCalanderToDateTime(refreshEvent.getSituationRecordVersionTime());
        return cachedVersionTime.isAfter(refreshVersionTime);
    }

    private void handleEventUpdate(D2LogicalModel d2LogicalModel) {
        synchronized (LOCK_EVENT_UPDATES) {
            try {
                SituationPublication situationPublication = (SituationPublication) d2LogicalModel.getPayloadPublication();
                if (situationPublication != null) {
                    List<Situation> situations = situationPublication.getSituation();

                    LOG.info(NUMBER_OF_EVENTS_IN_PAYLOAD, situations.size());

                    for (Situation situation : situations) {
                        // Only have 1 situationRecord per situation (index=0)
                        SituationRecord situationRecord = situation.getSituationRecord().get(0);
                        EVENT_CACHE.put(situationRecord.getId(), situationRecord);
                        processEventData(situationRecord);
                    }
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
    }

    /**
     * Different types of event/situation record contain some common information
     * and some type-specific data items and should be handled accordingly
     * 
     * @param situationRecord
     */
    private void processEventData(SituationRecord situationRecord) {
        processCommonEventData(situationRecord);
        if (MaintenanceWorks.class.equals(situationRecord.getClass())) {
            processMaintenanceWorksEvent((MaintenanceWorks) situationRecord);
        }
    }

    private void processRefreshedEventData() {
        for (SituationRecord situationRecord : EVENT_CACHE.values()) {
            processEventData(situationRecord);
        }
    }

    private void processCommonEventData(SituationRecord situationRecord) {
        LOG.info("Event ID: {}", situationRecord.getId());
        LOG.info("Version Time: {}", xmlGregoriaCalanderToDateTime(situationRecord.getSituationRecordVersionTime()));
        LOG.info("Severity: {}", situationRecord.getSeverity());
        LOG.info("Current status: {}",
                situationRecord.getValidity().getValidityStatus());
        LOG.info("Overall start time: {}",
                situationRecord.getValidity().getValidityTimeSpecification().getOverallStartTime());
        LOG.info("Overall end time: {}",
                situationRecord.getValidity().getValidityTimeSpecification().getOverallEndTime());
    }

    private void processMaintenanceWorksEvent(MaintenanceWorks maintenanceWorksEvent) {
        if (maintenanceWorksEvent.isUrgentRoadworks())
            LOG.info("Urgent Roadworks!");
        List<RoadMaintenanceTypeEnum> maintenanceTypes = maintenanceWorksEvent.getRoadMaintenanceType();
        for (RoadMaintenanceTypeEnum maintenanceType : maintenanceTypes) {
            LOG.info("Type of maintenance involved: {}", maintenanceType);
        }

        LOG.info("Mobility: {}", maintenanceWorksEvent.getMobility().getMobilityType());
        LOG.info("Scale: {}", maintenanceWorksEvent.getRoadworksScale());
        LOG.info("Roadworks Scheme Name: {}", maintenanceWorksEvent.getMaintenanceWorksExtension().getRoadworksEventDetails()
                .getRoadworksSchemeName());
    }

    private boolean isEventFullRefresh(D2LogicalModel d2LogicalModel) {
        return d2LogicalModel.getPayloadPublication().getFeedType().toLowerCase().contains(FeedType.FULL_REFRESH.lowerCase());
    }

    private DateTime xmlGregoriaCalanderToDateTime(XMLGregorianCalendar calendar) {
        return calendar != null ? new DateTime(calendar.toGregorianCalendar().getTime()) : null;
    }
}