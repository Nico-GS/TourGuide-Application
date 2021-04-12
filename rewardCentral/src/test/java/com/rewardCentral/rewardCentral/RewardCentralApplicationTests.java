package com.rewardCentral.rewardCentral;

import com.rewardCentral.rewardCentral.service.RewardCentralService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RewardCentralApplicationTests {

	@Autowired
	RewardCentralService rewardCentralService;

	@Test
	public void getAttractionRewardPoints() {

		assertTrue( rewardCentralService.getAttractionRewardPoints(UUID.randomUUID(),UUID.randomUUID()) <= 1000);
	}

}
