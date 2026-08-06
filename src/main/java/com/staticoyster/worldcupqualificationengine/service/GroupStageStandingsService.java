package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
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

	public Map<Group, List<StandingDto>> calculateAllGroupStandingsDto() {
		Map<Group, List<StandingDto>> standingsByGroup = new LinkedHashMap<>();
		for (Group group : Group.values()) {
			standingsByGroup.put(group, getGroupStandingsDtoInCurrentGroup(group));
		}
		return standingsByGroup;
	}

	public Map<Group, List<StandingDto>> getAllGroupStandingsDtos() {
		return calculateAllGroupStandingsDto();
	}

	public List<StandingDto> getGroupStandingsDtoInCurrentGroup(Group group) {
		return standingCalculator.calculateStandingsDtoInCurrentGroup(group);
	}

}
