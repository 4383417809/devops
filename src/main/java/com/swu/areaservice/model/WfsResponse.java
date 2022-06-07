package com.swu.areaservice.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"type", "features", "totalFeatures", "numberMatched", "numberReturned", "timeStamp"})
public class WfsResponse {

	private String type;
	private List<WfsFeature> features;
	private int totalFeatures;
	private int numberMatched;
	private int numberReturned;
	private String timeStamp;
}
