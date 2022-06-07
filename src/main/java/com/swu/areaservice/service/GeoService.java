package com.swu.areaservice.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swu.areaservice.data.Request;
import com.swu.areaservice.model.WfsResponse;
import com.swu.areaservice.repository.RequestRepository;

import reactor.core.publisher.Mono;


@Service
public class GeoService {

	private WebClient webclient;
	
	@Autowired
	private RequestRepository requestRepository;

	static final String FEATURE_ID = "WHSE_ADMIN_BOUNDARIES.BCHA_CMNTY_HEALTH_SERV_AREA_SP.120";
	
	static final String BASE_URL = "https://openmaps.gov.bc.ca";
	
	static final String GEO_URL = "/geo/pub/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=pub%3AWHSE_ADMIN_BOUNDARIES.BCHA_CMNTY_HEALTH_SERV_AREA_SP&srsname=EPSG%3A4326&" 
			+ "cql_filter=INTERSECTS(SHAPE%2CSRID%3D4326%3BPOINT(";
	
	static final String SEARCH_URL = "))&propertyName=CMNTY_HLTH_SERV_AREA_CODE%2CCMNTY_HLTH_SERV_AREA_NAME%2CSHAPE&outputFormat=application%2Fjson";

	public void getGeoResponse(String coordinate) {

		RestTemplate restTemplate = new RestTemplate();
		
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();        
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));        
		messageConverters.add(converter);
		
		restTemplate.setMessageConverters(messageConverters); 
		ResponseEntity entity = restTemplate.getForEntity("https://openmaps.gov.bc.ca" + GEO_URL + coordinate + SEARCH_URL, String.class);
		String body = (String) entity.getBody();
		 
//		MediaType contentType = entity.getHeaders().getContentType();
//		HttpStatus statusCode = entity.getStatusCode();
		
	}

	public Mono<String> getGeoLocator(String coordinate) {

	    ExchangeStrategies strategies = ExchangeStrategies
	            .builder()
	            .codecs(clientDefaultCodecsConfigurer -> {
	                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(new ObjectMapper(), MediaType.APPLICATION_JSON));
	                clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(new ObjectMapper(), MediaType.APPLICATION_JSON));

	            }).build();

		this.webclient = WebClient.builder()
				  .exchangeStrategies(strategies)
				  .baseUrl("https://openmaps.gov.bc.ca")
				  .defaultCookie("cookieKey", "cookieValue")
				  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			      .exchangeStrategies(ExchangeStrategies.builder().codecs((configurer) -> {
			            configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
			            configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
			        }).build())
				  .build();
		
		 
		Mono<WfsResponse> response =  this.webclient.get()
				.uri(GEO_URL + coordinate + SEARCH_URL)
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.retrieve()
				.bodyToMono(WfsResponse.class)
				.log();
		
		WfsResponse wfsResponse = response.block();
		String tt = wfsResponse.getFeatures().get(0).getProperties().get(0).getCMNTY_HLTH_SERV_AREA_NAME();
		
		return Mono.just(tt);
		
	}

	public String getAreaName(String coordinate) throws Exception {
		//save request
		addNewRequest(coordinate);
		
		//send request
		return getGeo(coordinate);
		
	}


	public String getGeo(String coordinate) throws Exception {
		
		URL url = new URL(BASE_URL + GEO_URL + coordinate + SEARCH_URL);
		String areaName = null;
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		//check the response code
		int responsecode = conn.getResponseCode();
		if (responsecode != 200) {
		    throw new RuntimeException("HttpResponseCode: " + responsecode);
		} else {
		  
		    String response = "";
		    Scanner scanner = new Scanner(url.openStream());
		  
		    while (scanner.hasNext()) {
		       response += scanner.nextLine();
		    }
		    
		    //close the scanner
		    scanner.close();

		    int start = response.indexOf("CMNTY_HLTH_SERV_AREA_NAME");
		    
		    //location not found
		    if (start<0) return areaName;
		    
		    start += "CMNTY_HLTH_SERV_AREA_NAME\": ".length();
		    int end = response.indexOf("\"", start);
		    
		    areaName = response.substring(start, end);
	    
		}
		
		//close the connection
		conn.disconnect();

		return areaName;
		
	}

	private void addNewRequest(String coordinate) throws Exception {
		
		Date date = new Date();
		
		Request request = new Request();
		request.setFeatureid(FEATURE_ID);
		request.setCoordinate(coordinate);
		request.setRequesttime(new Timestamp(date.getTime()));
		
		requestRepository.save(request);
		
	}

}
