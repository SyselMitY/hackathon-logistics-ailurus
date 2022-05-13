package com.trustbit.truckagent.strategies;

import com.trustbit.truckagent.model.CargoOffer;
import com.trustbit.truckagent.model.DecideRequest;
import com.trustbit.truckagent.model.DecideResponse;

import java.util.Comparator;

public class MostValuableCargoStrategy implements CargoStrategy {
    @Override
    public DecideResponse decide(DecideRequest request) {
        var offers = request.getOffers();
        var mostValuable = offers.stream()
                .max(Comparator.comparingDouble(CargoOffer::getPrice))
                .orElse(null);
        if (mostValuable == null) {
            return DecideResponse.sleep(1);
        }
        return DecideResponse.deliver(mostValuable.getUid());
    }
}
