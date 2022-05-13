package com.trustbit.truckagent.strategies;

import com.trustbit.truckagent.model.DecideRequest;
import com.trustbit.truckagent.model.DecideResponse;

import java.util.Comparator;

public class MostValuableCargoPerTimeStrategy implements CargoStrategy {
    @Override
    public DecideResponse decide(DecideRequest request) {
        var offers = request.getOffers();
        var mostValuable = offers.stream()
                .filter(offer -> offer.getKmToCargo() < 300)
                .max(Comparator.comparingDouble(offer -> offer.getPrice() / offer.getEtaToDeliver()))
                .orElse(null);
        if (mostValuable == null) {
            return DecideResponse.sleep(1);
        }
        return DecideResponse.deliver(mostValuable.getUid());
    }
}
