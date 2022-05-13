package com.trustbit.truckagent.maptools;

import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.Optional;

@AllArgsConstructor
public class MapUtils {
    private final StreetMap map;

    public Optional<String> getLargestNeighbor(String origin) {
        var cityOptional = map.getCityByName(origin);
        if(cityOptional.isEmpty())
            return Optional.empty();
        var city = cityOptional.get();
        var roads = map.getRoadsOfCity(origin);
        var greatestNeighbor = roads.stream()
                .map(x -> map.getCityByName(x.getDestination()))
                .filter(Optional::isPresent)

                .map(Optional::get)
                .max(Comparator.comparing(City::getPopulation));

        if(greatestNeighbor.isPresent() && greatestNeighbor.get().getPopulation() > city.getPopulation())
            return Optional.of(greatestNeighbor.get().getName());
        return Optional.empty();
    }
}
