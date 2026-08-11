package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamStatusDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.TeamStatus;
import com.staticoyster.worldcupqualificationengine.service.api.DomainDtoConverter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamStatusService {

	private final GroupStageStandingsService groupStageStandingsService;
	private final RoundOf32Service roundOf32Service;
	private final DomainDtoConverter domainDtoConverter;

	public TeamStatusService(
			GroupStageStandingsService groupStageStandingsService,
			RoundOf32Service roundOf32Service,
			DomainDtoConverter domainDtoConverter) {
		this.groupStageStandingsService = groupStageStandingsService;
		this.roundOf32Service = roundOf32Service;
		this.domainDtoConverter = domainDtoConverter;
	}

	/**
	 * Group-stage status from current standings.
	 * Rank 1–2 → QUALIFIED; rank 4 → ELIMINATED; otherwise (typically 3rd) → STILL_ALIVE.
	 */
	public TeamStatusDto getTeamStatus(Team team) {
		List<StandingDto> groupStandings =
				groupStageStandingsService.getGroupStandingsDtoInCurrentGroup(team.getGroup());

		int currentRank = -1;
		StandingDto standing = null;
		for (int i = 0; i < groupStandings.size(); i++) {
			if (groupStandings.get(i).getTeam() == team) {
				currentRank = i + 1;
				standing = groupStandings.get(i);
				break;
			}
		}
		if (standing == null) {
			throw new IllegalStateException("No standings row for team: " + team.getCode());
		}

		TeamStatus model = TeamStatus.Builder.newBuilder()
				.withGroup(standing.getGroup())
				.withTeam(standing.getTeam())
				.withCurrentRank(currentRank)
				.withPlayed(standing.getPlayed())
				.withWon(standing.getWon())
				.withDrawn(standing.getDrawn())
				.withLost(standing.getLost())
				.withGoalsFor(standing.getGoalsFor())
				.withGoalsAgainst(standing.getGoalsAgainst())
				.withGoalDifference(standing.getGoalDifference())
				.withTeamConductScore(standing.getTeamConductScore())
				.withPoints(standing.getPoints())
				.withBestThirdPlaceSlot(resolveBestThirdPlaceSlot(team, currentRank))
				.withStatus(resolveStatus(currentRank))
				.build();

		return domainDtoConverter.toTeamStatusDto(model);
	}

	private static com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus resolveStatus(
			int currentRank) {
		if (currentRank == 1 || currentRank == 2) {
			return com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus.QUALIFIED;
		}
		if (currentRank == 4) {
			return com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus.ELIMINATED;
		}
		return com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus.STILL_ALIVE;
	}

	private Integer resolveBestThirdPlaceSlot(Team team, int currentRank) {
		if (currentRank != 3) {
			return null;
		}
		List<StandingDto> rankedThirds = roundOf32Service.getRankedThirdPlaceStandingsDtos();
		for (int i = 0; i < rankedThirds.size(); i++) {
			if (rankedThirds.get(i).getTeam() == team) {
				return i + 1;
			}
		}
		return null;
	}

}
