package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.model.Standing;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupStageStandingsService {

	private final StandingCalculator standingCalculator;

	public GroupStageStandingsService(StandingCalculator standingCalculator) {
		this.standingCalculator = standingCalculator;
	}

	public Map<Group, List<Standing>> calculateAllGroupStandings() {
		Map<Group, List<Standing>> standingsByGroup = new LinkedHashMap<>();
		for (Group group : Group.values()) {
			standingsByGroup.put(group, standingCalculator.calculateStandingsInCurrentGroup(group));
		}
		return standingsByGroup;
	}

}
