package com.staticoyster.worldcupqualificationengine.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableStandingDto.class)
@JsonDeserialize(as = ImmutableStandingDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface StandingDto {

	@JsonProperty("team")
	Team getTeam();

	@JsonProperty("points")
	int getPoints();

	@JsonProperty("goals_for")
	int getGoalsFor();

	@JsonProperty("goals_against")
	int getGoalsAgainst();

	@JsonProperty("goal_difference")
	int getGoalDifference();

	@JsonProperty("team_conduct_score")
	int getTeamConductScore();

}
