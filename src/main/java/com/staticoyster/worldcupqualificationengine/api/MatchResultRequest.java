package com.staticoyster.worldcupqualificationengine.api;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MatchResultRequest(
		@JsonProperty("home") Team home,
		@JsonProperty("away") Team away,
		@JsonProperty("home_score") int homeScore,
		@JsonProperty("away_score") int awayScore) {
}
