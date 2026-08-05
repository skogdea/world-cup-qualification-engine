package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.service.MatchService;
import org.springframework.stereotype.Component;

@Component
public class FifaMatchAndCardsClient implements MatchAndCardsProvider {

	private final MatchService matchService;

	public FifaMatchAndCardsClient(MatchService matchService) {
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
	}
}
