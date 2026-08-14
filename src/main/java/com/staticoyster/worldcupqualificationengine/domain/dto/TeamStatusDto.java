package com.staticoyster.worldcupqualificationengine.domain.dto;

import org.immutables.value.Value;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableTeamStatusDto.class)
@JsonDeserialize(as = ImmutableTeamStatusDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface TeamStatusDto {

	@JsonProperty("group")
	Group getGroup();

	@JsonProperty("team")
	Team getTeam();

	@JsonProperty("current_rank")
	int getCurrentRank();

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

	/**
	 * 1-based advancing best-third rank (at most {@code BEST_THIRD_PLACE_SLOTS});
	 * {@code null} when the team is not among the advancing thirds.
	 */
	@Nullable
	@JsonProperty("best_third_place_rank")
	Integer getBestThirdPlaceRank();

	@JsonProperty("team_status")
	TeamStatus getTeamStatus();

}
