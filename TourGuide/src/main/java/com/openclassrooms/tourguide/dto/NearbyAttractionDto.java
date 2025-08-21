package com.openclassrooms.tourguide.dto;

import java.io.Serializable;

public class NearbyAttractionDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private final String attractionName;
	private final double attractionLatitude;
	private final double attractionLongitude;

	private final double userLatitude;
	private final double userLongitude;

	private final double distanceMiles;
	private final int rewardPoints;

	public NearbyAttractionDto(String attractionName, double attractionLatitude, double attractionLongitude,
			double userLatitude, double userLongitude, double distanceMiles, int rewardPoints) {
		this.attractionName = attractionName;
		this.attractionLatitude = attractionLatitude;
		this.attractionLongitude = attractionLongitude;
		this.userLatitude = userLatitude;
		this.userLongitude = userLongitude;
		this.distanceMiles = distanceMiles;
		this.rewardPoints = rewardPoints;
	}

	public String getAttractionName() {
		return attractionName;
	}

	public double getAttractionLatitude() {
		return attractionLatitude;
	}

	public double getAttractionLongitude() {
		return attractionLongitude;
	}

	public double getUserLatitude() {
		return userLatitude;
	}

	public double getUserLongitude() {
		return userLongitude;
	}

	public double getDistanceMiles() {
		return distanceMiles;
	}

	public int getRewardPoints() {
		return rewardPoints;
	}
}
