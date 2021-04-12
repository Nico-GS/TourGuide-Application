package tourGuide;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.model.Attraction;
import tourGuide.model.Location;
import tourGuide.model.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.client.GpsUtilProxy;
import tourGuide.client.RewardCentralProxy;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tourGuide.model.UserReward;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRewardsService {

    @Autowired
    GpsUtilProxy gpsUtilProxy;

    @Autowired
    RewardCentralProxy rewardCentralProxy;

    @Autowired
    TourGuideService tourGuideService;

    @Test
    public void userGetRewards() {
        Locale.setDefault(Locale.US);
        RewardsService rewardsService = new RewardsService(gpsUtilProxy, rewardCentralProxy);

        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilProxy, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsUtilProxy.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(attraction.getLatitude(), attraction.getLongitude()), new Date()));
        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        tourGuideService.tracker.stopTracking();
        assertTrue(userRewards.size() == 1);
    }

    @Test
    public void isWithinAttractionProximity() {

        RewardsService rewardsService = new RewardsService(gpsUtilProxy, rewardCentralProxy);
        Attraction attraction = gpsUtilProxy.getAttractions().get(0);
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, new Location(attraction.getLatitude(), attraction.getLongitude())));
    }


    @Test
    public void nearAllAttractions() {

        RewardsService rewardsService = new RewardsService(gpsUtilProxy, rewardCentralProxy);
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        User user = tourGuideService.getAllUsers().get(0);
        rewardsService.calculateRewards(user);
        List<UserReward> userRewards = tourGuideService.getUserRewards(user);
        tourGuideService.tracker.stopTracking();

        assertEquals(gpsUtilProxy.getAttractions().size(), userRewards.size());
    }

}
