package com.gpsUtil.gpsUtil;


import com.gpsUtil.gpsUtil.model.Location;
import com.gpsUtil.gpsUtil.model.VisitedLocation;
import com.gpsUtil.gpsUtil.service.GpsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class GpsUtilApplicationTests {

	@Autowired
	GpsUtil gpsUtil;
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void getUserLocation() {
		Locale.setDefault(Locale.US);
		double longitude = ThreadLocalRandom.current().nextDouble(-180.0D, 180.0D);
		longitude = Double.parseDouble(String.format("%.6f", longitude));
		double latitude = ThreadLocalRandom.current().nextDouble(-85.05112878D, 85.05112878D);
		latitude = Double.parseDouble(String.format("%.6f", latitude));
		UUID userId = UUID.randomUUID();
		VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(latitude, longitude), new Date());
		assertEquals(gpsUtil.getUserLocation(userId).userId, visitedLocation.userId);
	}


	@Test
	public void getAttractions() throws Exception {
		this.mockMvc.perform(get("/getAttractions")).andDo(print()).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(26)))
	   			.andExpect(jsonPath("$[0].attractionName", is("Disneyland")));
	}

}
