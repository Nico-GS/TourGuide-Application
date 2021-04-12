package com.tripPricer.tripPricer.controller;

import com.tripPricer.tripPricer.model.Provider;
import com.tripPricer.tripPricer.service.TripPricer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class TripPricerController {
    @Autowired
    TripPricer tripPricer;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TripPricer!";
    }

    @RequestMapping("/getPrice/{apiKey}/{attractionId}/{adults}/{children}/{nightsStay}/{rewardsPoints}")
    public List<Provider> getPrice(@PathVariable String apiKey, @PathVariable UUID attractionId, @PathVariable int adults, @PathVariable int children, @PathVariable int nightsStay, @PathVariable int rewardsPoints) {
        return tripPricer.getPrice(apiKey, attractionId, adults, children, nightsStay, rewardsPoints);
    }

    @RequestMapping("/getProviderName/{apiKey}/{adults}")
    public String getProviderName(@PathVariable String apiKey, @PathVariable int adults) {
        return tripPricer.getProviderName(apiKey, adults);
    }
}

