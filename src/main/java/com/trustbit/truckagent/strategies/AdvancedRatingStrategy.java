package com.trustbit.truckagent.strategies;

import com.trustbit.truckagent.model.CargoOffer;
import com.trustbit.truckagent.model.DecideRequest;
import com.trustbit.truckagent.model.DecideResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdvancedRatingStrategy implements CargoStrategy {

    //Time/Distance to get to the cargo
    private static final double ETA_TO_CARGO_COEF = -4.0;
    private static final double KM_TO_CARGO_COEF = -1.0;

    //Price per Time/Distance to deliver the cargo
    private static final double PRICE_PER_TIME_COEF = 7.0;
    private static final double PRICE_PER_KM_COEF = 0.5;

    //Deliveries with better Offers at the destination should be rated higher
    private static final int CARGO_RECURSION_DEPTH = 1;
    private static final double CARGO_RECURSIVE_COEF = 6.0;
    private static final int MAX_AVG_CONSIDERATION = 3;

    private final Map<CargoOffer, Integer> cargoOfferMap;
    private Map<CargoOffer, Integer> cargoOfferMapBeforeRecursion;

    public AdvancedRatingStrategy() {
        this.cargoOfferMap = new HashMap<>();
    }

    @Override
    public DecideResponse decide(DecideRequest request) {
        request.getOffers().forEach(offer -> cargoOfferMap.putIfAbsent(offer, rate(request, offer)));
        for (int i = 0; i < CARGO_RECURSION_DEPTH; i++) {
            doCargoRecursion();
        }
        Optional<CargoOffer> bestOffer = getBestOffer();
        return bestOffer
                .map(cargoOffer -> DecideResponse.deliver(cargoOffer.getUid()))
                .orElseGet(() -> DecideResponse.sleep(1));
    }

    private Optional<CargoOffer> getBestOffer() {
        return cargoOfferMap.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private int rate(DecideRequest request, CargoOffer offer) {
        int rating = 1000;
        rating += offer.getEtaToCargo() * ETA_TO_CARGO_COEF;
        rating += offer.getKmToCargo() * KM_TO_CARGO_COEF;
        rating += offer.getPrice() / offer.getEtaToDeliver() * PRICE_PER_TIME_COEF;
        rating += offer.getPrice() / offer.getKmToDeliver() * PRICE_PER_KM_COEF;
        return rating;
    }

    private void doCargoRecursion() {
        //copy the map
        cargoOfferMapBeforeRecursion = new HashMap<>(cargoOfferMap);
        cargoOfferMapBeforeRecursion.forEach((offer, rating) -> {
            cargoOfferMap.merge(offer, (int) (getMaxDestRating(offer.getDest()) * CARGO_RECURSIVE_COEF), Integer::sum);
        });
    }

    //returns the avg rating of the top n offers for a given source
    private int getMaxDestRating(String source) {
        return (int) cargoOfferMapBeforeRecursion.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(MAX_AVG_CONSIDERATION)
                .mapToInt(Map.Entry::getValue)
                .average()
                .orElse(0);
    }
}
