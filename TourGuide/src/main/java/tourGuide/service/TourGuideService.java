package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourGuide.model.Attraction;
import tourGuide.model.Location;
import tourGuide.model.Provider;
import tourGuide.model.VisitedLocation;
import tourGuide.DTO.AllUsersCurrentLocations;
import tourGuide.DTO.NearAttractionsDTO;
import tourGuide.helper.InternalTestHelper;
import tourGuide.client.GpsUtilProxy;
import tourGuide.client.RewardCentralProxy;
import tourGuide.client.TripPricerProxy;
import tourGuide.tracker.Tracker;
import tourGuide.model.User;
import tourGuide.model.UserPreferences;
import tourGuide.model.UserReward;


@Service
public class TourGuideService {
    ExecutorService executorService = Executors.newFixedThreadPool(100);
    private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
    private final RewardsService rewardsService;
    public final Tracker tracker;
    boolean testMode = true;

    @Autowired
    GpsUtilProxy gpsUtilProxy;

    @Autowired
    RewardCentralProxy rewardCentralProxy;

    @Autowired
    TripPricerProxy tripPricerProxy;

    /**
     * TourGuideService, enable or note test mode and initializing users
     *
     * @param gpsUtilProxy   the GpsUtil API
     * @param rewardsService the RewardsService API
     */
    public TourGuideService(GpsUtilProxy gpsUtilProxy, RewardsService rewardsService) {
        this.gpsUtilProxy = gpsUtilProxy;
        this.rewardsService = rewardsService;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }
        tracker = new Tracker(this);
        addShutDownHook();
    }

    /**
     * Get the user rewards
     *
     * @param user the user
     * @return rewards of the user
     */
    public List<UserReward> getUserRewards(User user) {
        rewardsService.calculateRewards(user);
        return user.getUserRewards();
    }

    /**
     * Get location of user
     * @param user the user to locate
     * @return the location of the user
     */
    public VisitedLocation getUserLocation(User user) {
        VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user);
        return visitedLocation;
    }

    /**
     * Get user
     * @param userName the user to get
     * @return the user
     */
    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    /**
     * Get all users
     * @return all users
     */
    public List<User> getAllUsers() {
        return internalUserMap.values().stream().collect(Collectors.toList());
    }

    /**
     * Add a user
     * @param user to add
     */
    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    /**
     * Get the trip deals, with the username, duration of trip and numbers of adults & children
     * @param user the user
     * @param tripDuration the trip duration
     * @param numberOfAdults the number of adults
     * @param numberOfChildren the number of children
     * @return the trip deals
     */
    public List<Provider> getTripDeals(User user, int tripDuration, int numberOfAdults, int numberOfChildren) {
        int rewardCumul = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        UserPreferences preferences = new UserPreferences();
        preferences.setTripDuration(tripDuration);
        preferences.setNumberOfAdults(numberOfAdults);
        preferences.setNumberOfChildren(numberOfChildren);
        user.setUserPreferences(preferences);
        List<Provider> providers = tripPricerProxy.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), rewardCumul);
        user.setTripDeals(providers);
        return providers;
    }

    /**
     * Track user location and calculate rewards
     * @param user the user to track
     * @return the visited location by user
     */
    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtilProxy.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    /**
     * Track user location with thread
     * @param user to track
     */
    public void trackUserLocationWithThread(User user) {
        executorService.execute(new Runnable() {
            public void run() {
                trackUserLocation(user);
            }
        });
    }

    /**
     * Shutdown the tracker
     * @throws InterruptedException exception
     */
    public void shutdown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    }

    /**
     * Get the near attraction
     * @param visitedLocation the location visited
     * @return the near attractions
     */
    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtilProxy.getAttractions()) {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.getLocation())) {
                nearbyAttractions.add(attraction);
            }
        }

        return nearbyAttractions;
    }

    /**
     * Sort the near attractions
     * @param visitedLocation the visited location
     * @param user the user
     * @return near attractions
     */
    public List<NearAttractionsDTO> getNeardistance(VisitedLocation visitedLocation, User user) {
        List<NearAttractionsDTO> nearAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtilProxy.getAttractions()) {
            double distance = rewardsService.getDistance(new Location(attraction.getLatitude(), attraction.getLongitude()), new Location(visitedLocation.getLocation().getLatitude(), visitedLocation.getLocation().getLongitude()));
            NearAttractionsDTO nearAttraction = new NearAttractionsDTO(attraction.getAttractionName(), distance, attraction.getLatitude(), attraction.getLongitude(), visitedLocation.getLocation(), rewardCentralProxy.getAttractionRewardPoints(attraction.getAttractionId(), user.getUserId()));
            nearAttractions.add(nearAttraction);
            Collections.sort(nearAttractions, Comparator.comparingDouble(NearAttractionsDTO::getDistance));
        }
        return nearAttractions;
    }

    /**
     * Get five near attractions of user
     * @param visitedLocation the visited location
     * @param user the user
     * @return five near attractions
     */
    public List<NearAttractionsDTO> getNearFiveAttractions(VisitedLocation visitedLocation, User user) {
        List<NearAttractionsDTO> nearAttractions = this.getNeardistance(visitedLocation, user);
        List<NearAttractionsDTO> fiveNearAttractions = new ArrayList<>();

        for (NearAttractionsDTO nearAttractionsDTO : nearAttractions) {
            for (int i = 0; fiveNearAttractions.size() < 5; i++) {
                fiveNearAttractions.add(nearAttractionsDTO);
                break;
            }
        }
        return fiveNearAttractions;

    }

    /**
     * Get all locations of all users
     * @return all the locations
     */
    public List<AllUsersCurrentLocations> getAllCurrentLocations() {
        List<User> users = this.getAllUsers();
        List<AllUsersCurrentLocations> currentLocations = new ArrayList<>();
        for (User user : users) {
            this.generateUserLocationHistory(user);
            VisitedLocation lastLocation = user.getLastVisitedLocation();
            AllUsersCurrentLocations currentLocation = new AllUsersCurrentLocations(lastLocation.getUserId(), lastLocation.getLocation().getLatitude(), lastLocation.getLocation().getLongitude());
            currentLocations.add(currentLocation);
        }
        return currentLocations;

    }


    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
