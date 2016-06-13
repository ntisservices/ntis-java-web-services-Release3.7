package com.thales.ntis.subscriber.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thales.ntis.subscriber.datex.D2LogicalModel;
import com.thales.ntis.subscriber.datex.TextPage;
import com.thales.ntis.subscriber.datex.VmsDatexPictogramEnum;
import com.thales.ntis.subscriber.datex.VmsMessage;
import com.thales.ntis.subscriber.datex.VmsPictogram;
import com.thales.ntis.subscriber.datex.VmsPublication;
import com.thales.ntis.subscriber.datex.VmsTextLineIndexVmsTextLine;
import com.thales.ntis.subscriber.datex.VmsUnit;
import com.thales.ntis.subscriber.datex.VmsUnitVmsIndexVms;

/**
 * This is an example service class implementation.
 * 
 */
@Service
public class VMSTrafficDataServiceImpl implements TrafficDataService {

    private static final Logger LOG = LoggerFactory.getLogger(VMSTrafficDataServiceImpl.class);
    private static final String PUBLICATION_TYPE = "VMS/Matrix Signal Status Publication";

    @Override
    public void handle(D2LogicalModel d2LogicalModel) {

        LOG.info(PUBLICATION_TYPE + ": received...");

        VmsPublication vmsPublication = null;

        try {
            vmsPublication = (VmsPublication) d2LogicalModel.getPayloadPublication();
            if (vmsPublication != null) {
                List<VmsUnit> vmsUnits = vmsPublication.getVmsUnit();

                LOG.info("Number of VMS/Matrix Units in payload: " + vmsUnits.size());

                // The publication can contain status info for more than one
                // unit
                for (VmsUnit vmsUnit : vmsUnits) {
                    extractStatusInformationFromUnitData(vmsUnit);
                }
                LOG.info(PUBLICATION_TYPE + ": processed successfully.");
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private void extractStatusInformationFromUnitData(VmsUnit vmsUnit) {

        // Typically, the service should refer to the NTIS reference Model to
        // determine what type (VMS or Matrix Signal) the unit is; and hence how
        // to process the data. For this simple example, however, the ID of the
        // unit table reference is used.
        String vmsUnitType = vmsUnit.getVmsUnitTableReference().getId();

        if ("NTIS_VMS_Units".equals(vmsUnitType)) {
            LOG.info("VMS Unit ID: " + vmsUnit.getVmsUnitReference().getId());

            // There is only ever 1 VMS/Matrix unit per vmsUnit element - at
            // index 0
            VmsUnitVmsIndexVms unit = vmsUnit.getVms().get(0);

            // There is only ever 1 message per unit
            VmsMessage message = unit.getVms().getVmsMessage().get(0).getVmsMessage();

            LOG.info("Set by: " + message.getMessageSetBy().getValues().getValue().get(0).getValue());
            LOG.info("Last set at: " + message.getTimeLastSet());

            // There is only ever 1 text page specified, with multiple text lines
            TextPage textPage = message.getTextPage().get(0);
            List<VmsTextLineIndexVmsTextLine> textLines = textPage.getVmsText().getVmsTextLine();
            for (VmsTextLineIndexVmsTextLine textLine : textLines)
                LOG.info("Text Line #" + (textLine.getLineIndex() + 1) + ": " + textLine.getVmsTextLine().getVmsTextLine());

            // There is only ever 1 pictogram display area, with 1 pictogram
            LOG.info("Pictogram Displayed: "
                    + message.getVmsPictogramDisplayArea().get(0).getVmsPictogramDisplayArea().getVmsPictogram().get(0)
                            .getVmsPictogram().getPictogramDescription().get(0).toString());

        } else if ("NTIS_Matrix_Units".equals(vmsUnitType)) {
            LOG.info("Matrix Unit ID: " + vmsUnit.getVmsUnitReference().getId());

            // There is only ever 1 VMS/Matrix unit per vmsUnit element - at
            // index 0
            VmsUnitVmsIndexVms unit = vmsUnit.getVms().get(0);

            // There is only ever 1 message per unit
            VmsMessage message = unit.getVms().getVmsMessage().get(0).getVmsMessage();

            LOG.info("Last set at: " + message.getTimeLastSet());

            // There is only ever 1 pictogram display area, with 1 pictogram
            VmsPictogram pictogram = message.getVmsPictogramDisplayArea().get(0).getVmsPictogramDisplayArea().getVmsPictogram()
                    .get(0).getVmsPictogram();
            
            // If the pictogram is 'other', then the actual pictogram displayed is defined by the vmsPictogramEUK 
            // extension (applies to Matrix Signals only)
            VmsDatexPictogramEnum pictogramType = pictogram.getPictogramDescription().get(0);
            String pictogramDesc = null;
            if (pictogramType.equals(VmsDatexPictogramEnum.OTHER)) {
                pictogramDesc = pictogram.getVmsPictogramExtension().getVmsPictogramUK().getPictogramDescriptionUK();
            } else {
                pictogramDesc = pictogramType.toString();
            }
            LOG.info("Pictogram Displayed: " + pictogramDesc);

        } else {
            LOG.error("Invalid unit type received in publication: " + vmsUnitType);
        }
    }
}
