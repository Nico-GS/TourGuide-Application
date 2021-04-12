package tourGuide.controller;

import java.util.List;


import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jsoniter.output.JsonStream;

import tourGuide.model.Provider;
import tourGuide.model.VisitedLocation;
import tourGuide.DTO.AllUsersCurrentLocations;
import tourGuide.DTO.NearAttractionsDTO;
import tourGuide.service.TourGuideService;
import tourGuide.model.User;
import tourGuide.model.UserReward;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @ApiOperation(value = "Home TourGuide")
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @ApiOperation(value = "Request Location of user with username")
    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.getLocation());
    }

    @ApiOperation(value = "Request attractions near the user with username")
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        tourGuide.model.VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
    }

    @ApiOperation(value = "Request get near five attraction with username")
    @RequestMapping("/getNearFiveAttractions")
    public List<NearAttractionsDTO> getDistance(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getNearFiveAttractions(visitedLocation, this.getUser(userName));

    }

    @ApiOperation(value = "Get Rewards by username")
    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideService.getUserRewards(getUser(userName));
    }

    @ApiOperation(value = "Get all current locations of all users")
    @RequestMapping("/getAllCurrentLocations")
    public List<AllUsersCurrentLocations> getAllCurrentLocations() {
        return tourGuideService.getAllCurrentLocations();
    }

    @ApiOperation(value = "Get trip deal with username, tripDuration, number of adults & children")
    @RequestMapping("/getTripDeals")
    @ResponseBody
    public List<Provider> getTripDeals(@RequestParam(value = "userName") String userName, @RequestParam(value = "tripDuration") int tripDuration, @RequestParam(value = "numberOfAdults") int numberOfAdults, @RequestParam(value = "numberOfChildren") int numberOfChildren) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName), tripDuration, numberOfAdults, numberOfChildren);
        return providers;
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }

}