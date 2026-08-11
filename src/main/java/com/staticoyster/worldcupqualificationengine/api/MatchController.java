package com.staticoyster.worldcupqualificationengine.api;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.ingestion.FifaMatchAndCardsClient;
import com.staticoyster.worldcupqualificationengine.ingestion.ManualMatchAndCardsProvider;
import com.staticoyster.worldcupqualificationengine.service.MatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController {

	private static final Logger log = LoggerFactory.getLogger(MatchController.class);

	private final MatchService matchService;
	private final FifaMatchAndCardsClient fifaMatchAndCardsClient;
	private final ManualMatchAndCardsProvider manualMatchAndCardsProvider;

	public MatchController(
			MatchService matchService,
			FifaMatchAndCardsClient fifaMatchAndCardsClient,
			ManualMatchAndCardsProvider manualMatchAndCardsProvider) {
		this.matchService = matchService;
		this.fifaMatchAndCardsClient = fifaMatchAndCardsClient;
		this.manualMatchAndCardsProvider = manualMatchAndCardsProvider;
	}

	@GetMapping
	public List<MatchDto> getAllMatches() {
		return matchService.getAllMatches();
	}

	@GetMapping("/{matchId}")
	public MatchDto getMatch(@PathVariable String matchId) {
		return matchService.findMatch(matchId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found: " + matchId));
	}

	/**
	 * Prefer live FIFA ingestion; fall back to the request body via the manual provider.
	 * Each provider persists through {@link MatchService#updateMatchResult(MatchDto)}.
	 */
	@PutMapping("/result")
	public MatchDto updateMatchResult(@RequestBody MatchDto matchDto) {
		try {
			return fifaMatchAndCardsClient.ingest(matchDto);
		}
		catch (RuntimeException exception) {
			log.warn("FIFA ingest failed for match {}; falling back to manual provider: {}",
					matchDto != null ? matchDto.getMatchId() : null,
					exception.getMessage());
			return manualMatchAndCardsProvider.ingest(matchDto);
		}
	}

}
