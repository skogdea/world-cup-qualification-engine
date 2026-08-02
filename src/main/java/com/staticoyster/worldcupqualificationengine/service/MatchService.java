package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableMatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;
import org.springframework.stereotype.Service;

@Service
public class MatchService {

	private final MatchRepository matchRepository;

	public MatchService(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}

	public void updateMatchResult(Team home, Team away, int homeScore, int awayScore) {
		if (home == null || away == null) {
			throw new IllegalArgumentException("home and away teams are required");
		}
		if (home.getGroup() != away.getGroup()) {
			throw new IllegalArgumentException("home and away must be in the same group");
		}

		Match match = matchRepository.findByHomeAndAway(home, away)
				.orElseGet(() -> Match.Builder.newBuilder()
						.withMatchId(home.name() + "_vs_" + away.name())
						.withHome(home)
						.withAway(away)
						.build());

		match.setHomeScore(homeScore);
		match.setAwayScore(awayScore);
		match.setMatchStatus(MatchStatus.PAST);
		matchRepository.save(match);
	}

	public MatchDto convertToMatchDto(Match model) {
		return ImmutableMatchDto.builder()
				.matchId(model.getMatchId())
				.home(model.getHome())
				.away(model.getAway())
				.homeScore(model.getHomeScore())
				.awayScore(model.getAwayScore())
				.matchStatus(model.getMatchStatus())
				.build();
	}

	public Match convertToMatch(MatchDto dto) {
		return Match.Builder.newBuilder()
				.withMatchId(dto.getMatchId())
				.withHome(dto.getHome())
				.withAway(dto.getAway())
				.withHomeScore(dto.getHomeScore())
				.withAwayScore(dto.getAwayScore())
				.withMatchStatus(dto.getMatchStatus())
				.build();
	}

}
