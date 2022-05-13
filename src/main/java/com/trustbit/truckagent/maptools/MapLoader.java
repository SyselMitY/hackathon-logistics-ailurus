package com.trustbit.truckagent.maptools;

import java.io.IOException;

public interface MapLoader {
    StreetMap loadMap(String filename) throws IOException;
}
