package com.trustbit.truckagent.maptools;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

public class StreetMapLoader implements MapLoader {

    @Override
    public StreetMap loadMap(String filename) throws IOException {
        var is = StreetMapLoader.class.getResourceAsStream(filename);
        if (is == null) {
            throw new IOException("Cannot find resource file " + filename);
        }
        var tokener = new JSONTokener(is);
        var arr = new JSONArray(tokener);
        var map = new StreetMap();

        for (int i = 0; i < arr.length(); i++) {
            var cityJson = arr.getJSONObject(i);

            var cityName = cityJson.getString("city");
            var cityPop = (int) cityJson.getDouble("population");
            var city = new City(cityName, cityPop);

            var roadsJson = cityJson.getJSONArray("roads");

            var roads = new ArrayList<Road>();

            for(int j = 0; j < roadsJson.length(); j++) {
                var destJson = roadsJson.getJSONObject(j);
                var destName = destJson.getString("dest");
                var destKm = destJson.getDouble("km");
                var destKmh = destJson.getDouble("kmh");
                var destMajor = destJson.getBoolean("major");
                roads.add(new Road(destName, destKm, destKmh, destMajor));
            }

            map.addCity(city, roads);
        }
        return map;
    }

    /*public static void main(String[] args) throws IOException {
        var sml = new StreetMapLoader();
        var map = sml.loadMap("/map.json");
        var smu = new MapUtils(map);
        System.out.println(smu.getLargestNeighbor("Vienna"));
    }*/
}
