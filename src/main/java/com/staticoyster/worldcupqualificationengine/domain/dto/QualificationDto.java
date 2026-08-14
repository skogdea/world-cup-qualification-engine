package com.staticoyster.worldcupqualificationengine.domain.dto;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonSerialize(as = ImmutableQualificationDto.class)
@JsonDeserialize(as = ImmutableQualificationDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface QualificationDto {

	@JsonProperty("group_winners")
	List<Team> getGroupWinners();

	@JsonProperty("runners_up")
	List<Team> getRunnersUp();

	@JsonProperty("best_third_place")
	List<StandingDto> getBestThirdPlaceStandings();

	@JsonProperty("qualified_teams")
	List<Team> getQualifiedTeams();

}
