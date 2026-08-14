package com.staticoyster.worldcupqualificationengine.domain.dto;

import org.immutables.value.Value;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableMatchDto.class)
@JsonDeserialize(as = ImmutableMatchDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface MatchDto {

	@JsonProperty("match_id")
	String getMatchId();

	@JsonProperty("home")
	Team getHome();

	@JsonProperty("away")
	Team getAway();

	@JsonProperty("home_score")
	int getHomeScore();

	@JsonProperty("away_score")
	int getAwayScore();

	@JsonProperty("match_status")
	MatchStatus getMatchStatus();

	@Nullable
	@JsonProperty("home_stats")
	TeamMatchStatsDto getHomeStats();

	@Nullable
	@JsonProperty("away_stats")
	TeamMatchStatsDto getAwayStats();

}
