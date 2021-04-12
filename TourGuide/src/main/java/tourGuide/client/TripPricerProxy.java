package tourGuide.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tourGuide.model.Provider;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "tripPricer", url = "localhost:8083")
public interface TripPricerProxy {
    @RequestMapping("/getPrice/{apiKey}/{attractionId}/{adults}/{children}/{nightsStay}/{rewardsPoints}")
    List<Provider> getPrice(@PathVariable("apiKey") String apiKey, @PathVariable("attractionId") UUID attractionId, @PathVariable("adults") int adults, @PathVariable("children") int children, @PathVariable("nightsStay") int nightsStay, @PathVariable("rewardsPoints") int rewardsPoints);

    @RequestMapping("/getProviderName/{apiKey}/{adults}")
    String getProviderName(@PathVariable("apikey") String apiKey, @PathVariable("adults") int adults);
}
