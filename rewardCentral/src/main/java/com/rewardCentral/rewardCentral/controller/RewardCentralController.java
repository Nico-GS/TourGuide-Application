package com.rewardCentral.rewardCentral.controller;

import com.rewardCentral.rewardCentral.service.RewardCentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RewardCentralController {
    @Autowired
    RewardCentralService rewardCentralService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from RewardCentral!";
    }

    @RequestMapping("/getReward/{attractionId}/{userId}")
    public int getAttractionRewardPoints(@PathVariable UUID attractionId, @PathVariable UUID userId) {
        return rewardCentralService.getAttractionRewardPoints(attractionId, userId);
    }
}
