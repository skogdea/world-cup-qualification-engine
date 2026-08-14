package com.staticoyster.worldcupqualificationengine.domain.dto;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableTeamMatchStatsDto.class)
@JsonDeserialize(as = ImmutableTeamMatchStatsDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface TeamMatchStatsDto {

	@JsonProperty("yellow_cards")
	int getYellowCards();

	@JsonProperty("second_yellow_reds")
	int getSecondYellowReds();

	@JsonProperty("direct_reds")
	int getDirectReds();

	@JsonProperty("fair_play_score")
	int getFairPlayScore();

}
