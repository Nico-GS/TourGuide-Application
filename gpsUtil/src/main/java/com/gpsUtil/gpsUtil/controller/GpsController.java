package com.gpsUtil.gpsUtil.controller;

import com.gpsUtil.gpsUtil.model.Attraction;
import com.gpsUtil.gpsUtil.model.VisitedLocation;
import com.gpsUtil.gpsUtil.service.GpsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class GpsController {

    @Autowired
    GpsUtil gpsUtil;

    @RequestMapping("/")
    public String index() {
        return "Home GpsUtil - Please request /getLocation or /getAttractions";
    }

    @RequestMapping("/getLocation/{userID}")
    public VisitedLocation getUserLocation(@PathVariable UUID userID) {
        return gpsUtil.getUserLocation(userID);
    }

    @RequestMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return gpsUtil.getAttractions();
    }
}
