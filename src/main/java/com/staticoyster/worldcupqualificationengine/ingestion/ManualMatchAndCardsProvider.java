package com.staticoyster.worldcupqualificationengine.ingestion;

import org.springframework.stereotype.Component;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.service.MatchService;

@Component
public class ManualMatchAndCardsProvider implements MatchAndCardsProvider {

	private final MatchService matchService;

	public ManualMatchAndCardsProvider(MatchService matchService) {
		this.matchService = matchService;
	}

	@Override
	public MatchDto ingest(MatchDto matchDto) {
		validate(matchDto);
		return matchService.updateMatchResult(matchDto);
	}

	private void validate(MatchDto matchDto) {
		if (matchDto == null) {
			throw new IllegalArgumentException("match is required");
		}

		String matchId = matchDto.getMatchId();
		if (matchId == null || matchId.isBlank()) {
			throw new IllegalArgumentException("matchId is required");
		}

		Team home = matchDto.getHome();
		Team away = matchDto.getAway();
		if (home == null || away == null) {
			throw new IllegalArgumentException("home and away teams are required");
		}
		if (home.getGroup() != away.getGroup()) {
			throw new IllegalArgumentException("home and away must be in the same group");
		}
	}
}
