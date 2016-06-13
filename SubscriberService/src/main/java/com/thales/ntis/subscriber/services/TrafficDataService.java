package com.thales.ntis.subscriber.services;

import com.thales.ntis.subscriber.datex.D2LogicalModel;

public interface TrafficDataService {

    public void handle(
            D2LogicalModel request);

}