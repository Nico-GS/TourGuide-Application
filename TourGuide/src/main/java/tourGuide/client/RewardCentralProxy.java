package tourGuide.client;

        import org.springframework.cloud.openfeign.FeignClient;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.RequestMapping;

        import java.util.UUID;

@FeignClient(name = "rewardCentral", url = "localhost:8082")
public interface RewardCentralProxy {

    @RequestMapping("/getReward/{attractionId}/{userId}")
    int getAttractionRewardPoints(@PathVariable("attractionId") UUID attractionId, @PathVariable("userId") UUID userId);
}
