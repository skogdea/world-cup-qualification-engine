package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;
import org.springframework.stereotype.Component;

@Component
public class ManualMatchAndCardsProvider implements MatchAndCardsProvider {

	private final MatchRepository matchRepository;

	public ManualMatchAndCardsProvider(MatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}

	@Override
	public Match ingest(Match match) {
		validate(match);
		return matchRepository.save(match);
	}

	private void validate(Match match) {
		if (match == null) {
			throw new IllegalArgumentException("match is required");
		}
		if (match.getMatchId() == null || match.getMatchId().isBlank()) {
			throw new IllegalArgumentException("matchId is required");
		}
		if (match.getHome() == null || match.getAway() == null) {
			throw new IllegalArgumentException("home and away teams are required");
		}
		if (match.getHome().getGroup() != match.getAway().getGroup()) {
			throw new IllegalArgumentException("home and away must be in the same group");
		}
	}

}
