package com.swu.areaservice.service;

import static org.junit.jupiter.api.Assertions.*;

import com.swu.areaservice.service.GeoService;

import org.junit.jupiter.api.Test;

class GeoServiceTest {
	final static String Area_Name = "Downtown Victoria/Vic West";
	final static String Coordinate = "-123.3646335+48.4251378";

	@Test
	void testGetGeo() {
		GeoService geoService = new GeoService();
		
		try {
			assertEquals(Area_Name, geoService.getGeo(Coordinate));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

}
