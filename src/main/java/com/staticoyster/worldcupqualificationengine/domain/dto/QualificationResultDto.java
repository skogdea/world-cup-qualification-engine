package com.staticoyster.worldcupqualificationengine.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonSerialize(as = ImmutableQualificationResultDto.class)
@JsonDeserialize(as = ImmutableQualificationResultDto.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface QualificationResultDto {

	@JsonProperty("group_winners")
	List<Team> getGroupWinners();

	@JsonProperty("runners_up")
	List<Team> getRunnersUp();

	@JsonProperty("best_third_place")
	List<StandingDto> getBestThirdPlaceStandings();

	@JsonProperty("qualified_teams")
	List<Team> getQualifiedTeams();

}
