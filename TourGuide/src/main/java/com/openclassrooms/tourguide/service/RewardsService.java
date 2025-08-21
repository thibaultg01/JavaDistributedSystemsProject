package com.openclassrooms.tourguide.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public void calculateRewards(User user) {
		final List<VisitedLocation> visitedSnapshot = List.copyOf(user.getVisitedLocations());
	    final List<Attraction> attractions = gpsUtil.getAttractions();
		
	    final Set<UUID> rewardedIds = user.getUserRewards().stream()
	            .map(r -> r.getAttraction().attractionId)   // <-- utiliser l'ID
	            .collect(Collectors.toCollection(HashSet::new));
	    
	    final List<UserReward> toAdd = new ArrayList<>();
	    
	    for (VisitedLocation visited : visitedSnapshot) {
	        for (Attraction attraction : attractions) {
	            if (!nearAttraction(visited, attraction)) continue;

	            // déjà récompensé (avant OU plus tôt dans CE run) ? on saute
	            if (rewardedIds.contains(attraction.attractionId)) continue;

	            int points = rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());

	            // marquer tout de suite pour bloquer les doublons intra-run
	            rewardedIds.add(attraction.attractionId);
	            toAdd.add(new UserReward(visited, attraction, points));
	        }
	        }
	    if (!toAdd.isEmpty()) {
	    	user.getUserRewards().addAll(toAdd);
	    }
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}
	
	public double getDistance(Attraction attraction, Location location) {
        return getDistance(new Location(attraction.latitude, attraction.longitude), location);
    }

    public double getDistance(Attraction attraction, VisitedLocation visitedLocation) {
        return getDistance(attraction, visitedLocation.location);
    }

}
