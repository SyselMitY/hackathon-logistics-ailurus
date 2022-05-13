package com.trustbit.truckagent;

import com.trustbit.truckagent.model.*;
import com.trustbit.truckagent.strategies.AdvancedRatingStrategy;
import com.trustbit.truckagent.strategies.CargoStrategy;
import com.trustbit.truckagent.strategies.MostValuableCargoPerTimeStrategy;
import com.trustbit.truckagent.strategies.MostValuableCargoStrategy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TruckAgentController {

    /**
     * See https://app.swaggerhub.com/apis/trustbit/trustbit-sustainable-logistics-simulation/1.0.0 for 
     * a detailed description of this endpoint.
     */
    @PostMapping("/decide")
    public DecideResponse decide(@RequestBody DecideRequest request) {
        CargoStrategy strategy = new AdvancedRatingStrategy();
        return strategy.decide(request);
    }
}
