package com.swu.areaservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"HLTH_CHSA_SYSID","CMNTY_HLTH_SERV_AREA_CODE", "CMNTY_HLTH_SERV_AREA_NAME", "OBJECTID"})
public class ChsaProperty {

	private int HLTH_CHSA_SYSID;
	private String CMNTY_HLTH_SERV_AREA_CODE;
	private String CMNTY_HLTH_SERV_AREA_NAME;
	private int OBJECTID;
}
