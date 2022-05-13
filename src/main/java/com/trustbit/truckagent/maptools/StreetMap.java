package com.trustbit.truckagent.maptools;

import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ToString
public class StreetMap {
    private final Map<City, List<Road>> map;

    public StreetMap() {
        map = new HashMap<>();
    }

    public void addCity(City city, List<Road> destinations) {
        map.put(city, destinations);
    }

    public Optional<City> getCityByName(String name) {
        return map.keySet().stream()
                .filter(x -> x.getName().equals(name))
                .findFirst();


    }

    public List<Road> getRoadsOfCity(String cityName) {
        var city = getCityByName(cityName);

        if(city.isEmpty())
            return List.of();

        return map.get(city.get());
    }

    public int numberOfCities() {
        return map.size();
    }
}
