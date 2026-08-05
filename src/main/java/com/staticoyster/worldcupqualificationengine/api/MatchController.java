package com.staticoyster.worldcupqualificationengine.api;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.ingestion.ManualMatchAndCardsProvider;
import com.staticoyster.worldcupqualificationengine.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

	private final MatchService matchService;
	private final ManualMatchAndCardsProvider manualMatchAndCardsProvider;

	public MatchController(MatchService matchService, ManualMatchAndCardsProvider manualMatchAndCardsProvider) {
		this.matchService = matchService;
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

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MatchDto ingestMatch(@RequestBody MatchDto matchDto) {
		return matchService.convertToMatchDto(
				manualMatchAndCardsProvider.ingest(matchService.convertToMatch(matchDto)));
	}

	@PutMapping("/result")
	public MatchDto updateMatchResult(@RequestBody MatchResultRequest request) {
		return matchService.updateMatchResult(
				request.home(),
				request.away(),
				request.homeScore(),
				request.awayScore());
	}

}
