package com.staticoyster.worldcupqualificationengine.service.config;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableMatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableQualificationResultDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableStandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableTeamMatchStatsDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.QualificationResultDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamMatchStatsDto;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.model.QualificationResult;
import com.staticoyster.worldcupqualificationengine.domain.model.Standing;
import com.staticoyster.worldcupqualificationengine.domain.model.TeamMatchStats;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Shared model ↔ DTO mapping for the service layer.
 * Keep conversion here so MatchService, StandingCalculator, and related services stay DRY.
 */
@Component
public class DomainDtoConverter {

	public MatchDto toMatchDto(Match model) {
		ImmutableMatchDto.Builder builder = ImmutableMatchDto.builder()
				.matchId(model.getMatchId())
				.home(model.getHome())
				.away(model.getAway())
				.homeScore(model.getHomeScore())
				.awayScore(model.getAwayScore())
				.matchStatus(model.getMatchStatus());
		if (model.getHomeStats() != null) {
			builder.homeStats(toTeamMatchStatsDto(model.getHomeStats()));
		}
		if (model.getAwayStats() != null) {
			builder.awayStats(toTeamMatchStatsDto(model.getAwayStats()));
		}
		return builder.build();
	}

	public Match toMatch(MatchDto dto) {
		Match.Builder builder = Match.Builder.newBuilder()
				.withMatchId(dto.getMatchId())
				.withHome(dto.getHome())
				.withAway(dto.getAway())
				.withHomeScore(dto.getHomeScore())
				.withAwayScore(dto.getAwayScore())
				.withMatchStatus(dto.getMatchStatus());
		if (dto.getHomeStats() != null) {
			builder.withHomeStats(toTeamMatchStats(dto.getHomeStats()));
		}
		if (dto.getAwayStats() != null) {
			builder.withAwayStats(toTeamMatchStats(dto.getAwayStats()));
		}
		return builder.build();
	}

	public List<MatchDto> toMatchDtos(List<Match> models) {
		return models.stream().map(this::toMatchDto).toList();
	}

	public StandingDto toStandingDto(Standing model) {
		return ImmutableStandingDto.builder()
				.team(model.getTeam())
				.points(model.getPoints())
				.goalsFor(model.getGoalsFor())
				.goalsAgainst(model.getGoalsAgainst())
				.goalDifference(model.getGoalDifference())
				.teamConductScore(model.getTeamConductScore())
				.build();
	}

	public Standing toStanding(StandingDto dto) {
		return Standing.Builder.newBuilder()
				.withTeam(dto.getTeam())
				.withPoints(dto.getPoints())
				.withGoalsFor(dto.getGoalsFor())
				.withGoalsAgainst(dto.getGoalsAgainst())
				.withGoalDifference(dto.getGoalDifference())
				.withTeamConductScore(dto.getTeamConductScore())
				.build();
	}

	public List<StandingDto> toStandingDtos(List<Standing> models) {
		return models.stream().map(this::toStandingDto).toList();
	}

	public List<Standing> toStandings(List<StandingDto> dtos) {
		return dtos.stream().map(this::toStanding).toList();
	}

	public TeamMatchStatsDto toTeamMatchStatsDto(TeamMatchStats model) {
		return ImmutableTeamMatchStatsDto.builder()
				.yellowCards(model.getYellowCards())
				.secondYellowReds(model.getSecondYellowReds())
				.directReds(model.getDirectReds())
				.fairPlayScore(model.getFairPlayScore())
				.build();
	}

	public TeamMatchStats toTeamMatchStats(TeamMatchStatsDto dto) {
		return TeamMatchStats.Builder.newBuilder()
				.withYellowCards(dto.getYellowCards())
				.withSecondYellowReds(dto.getSecondYellowReds())
				.withDirectReds(dto.getDirectReds())
				.build();
	}

	public QualificationResultDto toQualificationResultDto(QualificationResult model) {
		return ImmutableQualificationResultDto.builder()
				.groupWinners(model.getGroupWinners())
				.runnersUp(model.getRunnersUp())
				.bestThirdPlaceStandings(toStandingDtos(model.getBestThirdPlaceStandings()))
				.qualifiedTeams(model.getQualifiedTeams())
				.build();
	}
}
