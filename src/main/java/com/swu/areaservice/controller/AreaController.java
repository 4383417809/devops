package com.swu.areaservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swu.areaservice.service.GeoService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/area")
public class AreaController {

	@Autowired
	GeoService geoService;
	
    @GetMapping("/{coordinate}")
    public ResponseEntity<Mono<String>> findAreaNameByLocation(@PathVariable("coordinate") 
    	String coordinate) {
    	
        if(coordinate.matches("-1\\d{2}\\.\\d{1,7}\\+\\d{2}\\.\\d{1,7}")) {
        	try {
        		String areaName = geoService.getAreaName(coordinate);
        		
        		if (null==areaName) 
                	return ResponseEntity
              		      .status(HttpStatus.BAD_REQUEST)
              		      .body(Mono.just("Invaild location, please try again"));
        		else 	
        			return ResponseEntity
                		  .status(HttpStatus.OK)
                		  .body(Mono.just(areaName));
        		
			} catch (Exception e) {
	        	return ResponseEntity
	        		      .status(HttpStatus.BAD_GATEWAY)
	        		      .body(Mono.just("Something went wrong, please try again later"));
			}
        } else {
        	return ResponseEntity
        		      .status(HttpStatus.BAD_REQUEST)
        		      .body(Mono.just("Invaild location, please try again"));
        }
        
    }
}
