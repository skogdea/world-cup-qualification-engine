package com.staticoyster.worldcupqualificationengine.domain.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableStandingDto.class)
@JsonDeserialize(as = ImmutableStandingDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface StandingDto {

	@JsonProperty("group")
	Group getGroup();

	@JsonProperty("team")
	Team getTeam();

	@JsonProperty("played")
	int getPlayed();

	@JsonProperty("won")
	int getWon();

	@JsonProperty("drawn")
	int getDrawn();

	@JsonProperty("lost")
	int getLost();

	@JsonProperty("goals_for")
	int getGoalsFor();

	@JsonProperty("goals_against")
	int getGoalsAgainst();

	@JsonProperty("goal_difference")
	int getGoalDifference();

	@JsonProperty("team_conduct_score")
	int getTeamConductScore();

	@JsonProperty("points")
	int getPoints();

}
