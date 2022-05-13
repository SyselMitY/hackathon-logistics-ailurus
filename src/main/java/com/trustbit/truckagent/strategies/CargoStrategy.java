package com.trustbit.truckagent.strategies;

import com.trustbit.truckagent.model.DecideRequest;
import com.trustbit.truckagent.model.DecideResponse;

public interface CargoStrategy {
    DecideResponse decide(DecideRequest request);
}
