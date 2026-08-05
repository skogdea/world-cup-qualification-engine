package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableStandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
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

	public Map<Group, List<StandingDto>> getAllGroupStandingsDtos() {
		Map<Group, List<StandingDto>> response = new LinkedHashMap<>();
		calculateAllGroupStandings()
				.forEach((group, standings) -> response.put(group, toStandingDtos(standings)));
		return response;
	}

	public List<StandingDto> getGroupStandingsDtos(Group group) {
		return toStandingDtos(standingCalculator.calculateStandingsInCurrentGroup(group));
	}

	public List<StandingDto> toStandingDtos(List<Standing> standings) {
		return standings.stream()
				.map(this::toStandingDto)
				.toList();
	}

	public StandingDto toStandingDto(Standing standing) {
		return ImmutableStandingDto.builder()
				.team(standing.getTeam())
				.points(standing.getPoints())
				.goalsFor(standing.getGoalsFor())
				.goalsAgainst(standing.getGoalsAgainst())
				.goalDifference(standing.getGoalDifference())
				.teamConductScore(standing.getTeamConductScore())
				.build();
	}

}
