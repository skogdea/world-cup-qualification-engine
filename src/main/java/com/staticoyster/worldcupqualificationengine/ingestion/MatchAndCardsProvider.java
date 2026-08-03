package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.model.Match;

/**
 * Port for ingesting match results and per-side card stats into the shared match store.
 */
public interface MatchAndCardsProvider {

	/**
	 * Persists a match with scores and optional {@code homeStats} / {@code awayStats}.
	 */
	Match ingest(Match match);

}
