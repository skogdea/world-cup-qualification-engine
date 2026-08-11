package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableTeamStatusDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamStatusDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.enums.TeamStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamStatusService {

	private final GroupStageStandingsService groupStageStandingsService;
	private final RoundOf32Service roundOf32Service;

	public TeamStatusService(
			GroupStageStandingsService groupStageStandingsService,
			RoundOf32Service roundOf32Service) {
		this.groupStageStandingsService = groupStageStandingsService;
		this.roundOf32Service = roundOf32Service;
	}

	/**
	 * Provisional group-stage status from current standings and Round of 32 derivation.
	 * Priority: {@link TeamStatus#QUALIFIED} → {@link TeamStatus#ELIMINATED} (4th) → {@link TeamStatus#STILL_ALIVE}.
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

		boolean qualified = roundOf32Service.getQualifiedTeams().contains(team);
		TeamStatus status = resolveStatus(qualified, currentRank);
		Integer bestThirdPlaceSlot = resolveBestThirdPlaceSlot(team, currentRank);

		return ImmutableTeamStatusDto.builder()
				.group(standing.getGroup())
				.team(standing.getTeam())
				.currentRank(currentRank)
				.played(standing.getPlayed())
				.won(standing.getWon())
				.drawn(standing.getDrawn())
				.lost(standing.getLost())
				.goalsFor(standing.getGoalsFor())
				.goalsAgainst(standing.getGoalsAgainst())
				.goalDifference(standing.getGoalDifference())
				.teamConductScore(standing.getTeamConductScore())
				.points(standing.getPoints())
				.bestThirdPlaceSlot(bestThirdPlaceSlot)
				.status(status)
				.build();
	}

	private static TeamStatus resolveStatus(boolean qualified, int currentRank) {
		if (qualified) {
			return TeamStatus.QUALIFIED;
		}
		if (currentRank == 4) {
			return TeamStatus.ELIMINATED;
		}
		return TeamStatus.STILL_ALIVE;
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
