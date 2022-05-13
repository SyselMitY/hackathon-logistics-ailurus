package com.trustbit.truckagent.strategies;

import com.trustbit.truckagent.model.CargoOffer;
import com.trustbit.truckagent.model.DecideRequest;
import com.trustbit.truckagent.model.DecideResponse;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AdvancedRatingStrategy implements CargoStrategy {

    //Time/Distance to get to the cargo
    private static final double TO_CARGO_TIME_KM_BALANCE = 80.0;
    private static final double TO_CARGO_COEF = -0.2;

    //Price per Time/Distance to deliver the cargo
    private static final double PRICE_TIME_KM_BALANCE = .01;
    private static final double PRICE_PER_UNIT_COEF = 60.0;

    //Deliveries with better Offers at the destination should be rated higher
    private static final int CARGO_RECURSION_DEPTH = 3;
    private static final double CARGO_RECURSIVE_COEF = 6.5;
    private static final int MAX_AVG_CONSIDERATION = 3;

    private static final double SLEEP_NERF_AMOUNT = -300;
    private static final double SLEEP_NERF_BEGIN = 30;

    //OBSOLETE
    // private static final double SLEEP_NERF_END = 24;

    public static final Comparator<Map.Entry<CargoOffer, Integer>> CARGO_OFFER_COMPARATOR = Map.Entry.comparingByValue();

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
        System.out.println(cargoOfferMap);
        Optional<CargoOffer> bestOffer = getBestOffer();
        if (bestOffer.isPresent() && ableToDeliverWithoutSleeping(request, bestOffer.get())) {
            return DecideResponse.deliver(bestOffer.get().getUid());
        } else if (bestOffer.isPresent() && request.getTruck().getHoursSinceFullRest() != 0) {
            return DecideResponse.sleep(8);
        }
        else {
            //Drive to nice city maybe?? TODO wait for peppi implementationy
            return DecideResponse.route("Berlin");
        }
    }

    private Optional<CargoOffer> getBestOffer() {
        return cargoOfferMap.entrySet().stream()
                .sorted(CARGO_OFFER_COMPARATOR.reversed())
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private int rate(DecideRequest request, CargoOffer offer) {
        int rating = 1000;
        rating += offer.getEtaToCargo() * TO_CARGO_COEF * TO_CARGO_TIME_KM_BALANCE;
        rating += offer.getKmToCargo() * TO_CARGO_COEF;
        rating += offer.getPrice() / (offer.getEtaToDeliver() - offer.getEtaToCargo()) * PRICE_TIME_KM_BALANCE * PRICE_PER_UNIT_COEF;
        rating += offer.getPrice() / (offer.getKmToDeliver() - offer.getKmToCargo()) * PRICE_PER_UNIT_COEF;

//        var timeAwakeWhenDelivered = request.getTruck().getHoursSinceFullRest() + offer.getEtaToDeliver();
//        var timeOverSleepLimit = Math.max(0, timeAwakeWhenDelivered - SLEEP_NERF_BEGIN);
//        var timeMaxOverworkExpected = SLEEP_NERF_END- SLEEP_NERF_BEGIN;
//        double scalingFactor = 0-Math.pow(timeOverSleepLimit / timeMaxOverworkExpected,2);
//        var sleepingPenalizeAmount = SLEEP_NERF_AMOUNT * scalingFactor;

        //rating += ableToDeliverWithoutSleeping(request, offer) ? 0 : SLEEP_NERF_AMOUNT;
        return rating;
    }

    private void doCargoRecursion() {
        //copy the map
        cargoOfferMapBeforeRecursion = new HashMap<>(cargoOfferMap);
        cargoOfferMapBeforeRecursion.forEach((offer, rating) ->
                cargoOfferMap.merge(offer, (int) (getMaxDestRating(offer.getDest()) * CARGO_RECURSIVE_COEF), Integer::sum));
    }

    //returns the avg rating of the top n offers for a given source
    private int getMaxDestRating(String source) {

        return (int) cargoOfferMapBeforeRecursion.entrySet().stream()
                .filter(entry -> entry.getKey().getOrigin().equals(source))
                .sorted(CARGO_OFFER_COMPARATOR.reversed())
                .limit(MAX_AVG_CONSIDERATION)
                .mapToInt(Map.Entry::getValue)
                .average()
                .orElse(0);
    }

    private boolean ableToDeliverWithoutSleeping(DecideRequest request, CargoOffer offer) {
        return request.getTruck().getHoursSinceFullRest() + offer.getEtaToDeliver() < SLEEP_NERF_BEGIN;
    }
}
