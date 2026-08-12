package com.staticoyster.worldcupqualificationengine.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;
import com.staticoyster.worldcupqualificationengine.service.config.DomainDtoConverter;

@Service
public class MatchService {

	private final MatchRepository matchRepository;
	private final DomainDtoConverter domainDtoConverter;

	public MatchService(MatchRepository matchRepository, DomainDtoConverter domainDtoConverter) {
		this.matchRepository = matchRepository;
		this.domainDtoConverter = domainDtoConverter;
	}

	public List<MatchDto> getAllMatches() {
		return domainDtoConverter.toMatchDtos(matchRepository.findAll());
	}

	public Optional<MatchDto> findMatch(String matchId) {
		return matchRepository.findById(matchId).map(domainDtoConverter::toMatchDto);
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
						.withMatchId(matchDto.getMatchId())
						.withHome(home)
						.withAway(away)
						.build());

		match.setHomeScore(matchDto.getHomeScore());
		match.setAwayScore(matchDto.getAwayScore());
		match.setMatchStatus(MatchStatus.PAST);
		if (matchDto.getHomeStats() != null) {
			match.setHomeStats(domainDtoConverter.toTeamMatchStats(matchDto.getHomeStats()));
		}
		if (matchDto.getAwayStats() != null) {
			match.setAwayStats(domainDtoConverter.toTeamMatchStats(matchDto.getAwayStats()));
		}
		matchRepository.save(match);
		return domainDtoConverter.toMatchDto(match);
	}

}
