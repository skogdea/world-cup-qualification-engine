package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;
import org.springframework.stereotype.Component;

@Component
public class FifaMatchAndCardsClient implements MatchAndCardsProvider {

	private final MatchRepository matchRepository;

	public FifaMatchAndCardsClient(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}

	@Override
	public Match ingest(Match match) {
		if (match == null) {
			throw new IllegalArgumentException("match is required");
		}
		if (match.getMatchId() == null || match.getMatchId().isBlank()) {
			throw new IllegalArgumentException("matchId is required");
		}
		return matchRepository.save(match);
	}

}
