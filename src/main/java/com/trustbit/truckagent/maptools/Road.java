package com.trustbit.truckagent.maptools;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class Road {
    private String destination;
    private double km;
    private double kmh;
    private boolean major;

    public double getDuration() {
        return km / kmh;
    }
}
