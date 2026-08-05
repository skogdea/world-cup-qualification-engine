package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;

/**
 * Port for ingesting match results and per-side card stats.
 */
public interface MatchAndCardsProvider {

	/**
	 * Ingest a match result and cards.
	 */
	MatchDto ingest(MatchDto matchDto);

}
