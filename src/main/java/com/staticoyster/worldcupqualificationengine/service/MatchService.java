package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableMatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableTeamMatchStatsDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamMatchStatsDto;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.model.TeamMatchStats;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatchService {

	private final MatchRepository matchRepository;

	public MatchService(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}

	public List<MatchDto> getAllMatches() {
		return matchRepository.findAll().stream()
				.map(this::convertToMatchDto)
				.toList();
	}

	public Optional<MatchDto> findMatch(String matchId) {
		return matchRepository.findById(matchId).map(this::convertToMatchDto);
	}

	public MatchDto updateMatchResult(MatchDto matchDto) {
		Team home = matchDto.getHome();
		Team away = matchDto.getAway();
		if (home == null || away == null) {
			throw new IllegalArgumentException("home and away teams are required");
		}
		if (home.getGroup() != away.getGroup()) {
			throw new IllegalArgumentException("home and away must be in the same group");
		}

		Match match = matchRepository.findByHomeAndAway(home, away)
				.orElseGet(() -> Match.Builder.newBuilder() // Lazy execution, find-or-create pattern
						.withMatchId(home.name() + "_vs_" + away.name())
						.withHome(home)
						.withAway(away)
						.build());

		match.setHomeScore(matchDto.getHomeScore());
		match.setAwayScore(matchDto.getAwayScore());
		match.setMatchStatus(MatchStatus.PAST);
		matchRepository.save(match);
		return convertToMatchDto(match);
	}

	public MatchDto convertToMatchDto(Match model) {
		ImmutableMatchDto.Builder builder = ImmutableMatchDto.builder()
				.matchId(model.getMatchId())
				.home(model.getHome())
				.away(model.getAway())
				.homeScore(model.getHomeScore())
				.awayScore(model.getAwayScore())
				.matchStatus(model.getMatchStatus());
		if (model.getHomeStats() != null) {
			builder.homeStats(convertToTeamMatchStatsDto(model.getHomeStats()));
		}
		if (model.getAwayStats() != null) {
			builder.awayStats(convertToTeamMatchStatsDto(model.getAwayStats()));
		}
		return builder.build();
	}

	public Match convertToMatch(MatchDto dto) {
		Match.Builder builder = Match.Builder.newBuilder()
				.withMatchId(dto.getMatchId())
				.withHome(dto.getHome())
				.withAway(dto.getAway())
				.withHomeScore(dto.getHomeScore())
				.withAwayScore(dto.getAwayScore())
				.withMatchStatus(dto.getMatchStatus());
		if (dto.getHomeStats() != null) {
			builder.withHomeStats(convertToTeamMatchStats(dto.getHomeStats()));
		}
		if (dto.getAwayStats() != null) {
			builder.withAwayStats(convertToTeamMatchStats(dto.getAwayStats()));
		}
		return builder.build();
	}

	public TeamMatchStatsDto convertToTeamMatchStatsDto(TeamMatchStats model) {
		return ImmutableTeamMatchStatsDto.builder()
				.yellowCards(model.getYellowCards())
				.secondYellowReds(model.getSecondYellowReds())
				.directReds(model.getDirectReds())
				.fairPlayScore(model.getFairPlayScore())
				.build();
	}

	public TeamMatchStats convertToTeamMatchStats(TeamMatchStatsDto dto) {
		return TeamMatchStats.Builder.newBuilder()
				.withYellowCards(dto.getYellowCards())
				.withSecondYellowReds(dto.getSecondYellowReds())
				.withDirectReds(dto.getDirectReds())
				.build();
	}

}
