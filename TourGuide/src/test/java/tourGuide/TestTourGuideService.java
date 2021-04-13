package tourGuide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.model.Provider;
import tourGuide.model.VisitedLocation;
import tourGuide.DTO.AllUsersCurrentLocations;
import tourGuide.DTO.NearAttractionsDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.client.GpsUtilProxy;
import tourGuide.client.RewardCentralProxy;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TestTourGuideService {

    @Autowired
    TourGuideService tourGuideService;

    @Autowired
    GpsUtilProxy gpsUtilProxy;

    @Autowired
    RewardCentralProxy rewardCentralProxy;


    @Test
    public void getUserLocation() throws NumberFormatException {
        Locale.setDefault(Locale.US);
        RewardsService rewardsService = new RewardsService(gpsUtilProxy, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilProxy, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();
        assertTrue(visitedLocation.getUserId().equals(user.getUserId()));
    }

    @Test
    public void addUser() {
        RewardsService rewardsService = new RewardsService(gpsUtilProxy, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilProxy, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrivedUser = tourGuideService.getUser(user.getUserName());
        User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

        assertEquals(user, retrivedUser);
        assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getAllUsers() {

        RewardsService rewardsService = new RewardsService(gpsUtilProxy, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilProxy, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        tourGuideService.tracker.stopTracking();

        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() {
        Locale.setDefault(Locale.US);
        RewardsService rewardsService = new RewardsService(gpsUtilProxy, rewardCentralProxy);
        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilProxy, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

        tourGuideService.tracker.stopTracking();

        assertEquals(user.getUserId(), visitedLocation.getUserId());
    }


    @Test
    public void getNearbyAttractions() {
        Locale.setDefault(Locale.US);
        InternalTestHelper.setInternalUserNumber(0);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        List<NearAttractionsDTO> nearAttractions = tourGuideService.getNearFiveAttractions(visitedLocation, user);
        tourGuideService.tracker.stopTracking();

        assertEquals(5, nearAttractions.size());
    }

    @Test
    public void getAllCurrentUsersLocationsTest() {
        List<AllUsersCurrentLocations> nearAttractions = tourGuideService.getAllCurrentLocations();
        tourGuideService.tracker.stopTracking();

        assertEquals(100, nearAttractions.size());
    }

    @Test
    public void getTripDeals() {

        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user, 1, 2, 2);

        tourGuideService.tracker.stopTracking();

        assertEquals(user.getTripDeals(), providers);
        assertEquals(5, providers.size());
    }

}
