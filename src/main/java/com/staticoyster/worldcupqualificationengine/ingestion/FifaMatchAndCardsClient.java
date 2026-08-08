package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableMatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableTeamMatchStatsDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamMatchStatsDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.FairPlayEvent;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.ingestion.config.FifaApiProperties;
import com.staticoyster.worldcupqualificationengine.service.MatchService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Live FIFA adapter: fetches match score + bookings from {@code api.fifa.com}, maps to {@link MatchDto},
 * then persists through {@link MatchService}.
 *
 * <p><b>Live API vs {@code wc2026_bookings_raw.json}</b> — the seed file is a <em>normalized</em> view,
 * not a wire-format dump. Live FIFA uses PascalCase property names; the seed uses camelCase aliases
 * plus denormalized fields. For HTTP parsing, live names are authoritative:
 * <ul>
 *   <li>match id: live {@code IdMatch} ↔ seed {@code idMatch}</li>
 *   <li>booking card: live {@code Card} ↔ seed {@code cardCode}</li>
 *   <li>player/coach: live {@code IdPlayer}/{@code IdCoach} ↔ seed {@code idPlayer}/{@code idCoach}</li>
 *   <li>minute/period/reason: live {@code Minute}/{@code Period}/{@code Reason} ↔ seed lower-camel</li>
 *   <li>seed-only: {@code team}, {@code side}, {@code personKey}, {@code player}</li>
 *   <li>live-only: {@code IdTeam}, {@code IdStaff}, {@code IdEvent}, {@code EventNumber}</li>
 *   <li>score/teams: live nested {@code HomeTeam}/{@code AwayTeam} (+ {@code Score}, {@code Bookings})
 *       ↔ seed flat {@code home}/{@code away}/{@code score}/{@code bookings}</li>
 * </ul>
 */
@Component
public class FifaMatchAndCardsClient implements MatchAndCardsProvider {

	private final RestClient restClient;
	private final JsonMapper jsonMapper;
	private final MatchService matchService;
	private final FifaApiProperties fifaApiProperties;

	public FifaMatchAndCardsClient(
			RestClient.Builder restClientBuilder,
			JsonMapper jsonMapper,
			MatchService matchService,
			FifaApiProperties fifaApiProperties) {
		this.restClient = restClientBuilder.baseUrl(fifaApiProperties.getBaseUrl()).build();
		this.jsonMapper = jsonMapper;
		this.matchService = matchService;
		this.fifaApiProperties = fifaApiProperties;
	}

	/**
	 * Uses {@code matchDto.matchId} only; scores and cards come from FIFA live data.
	 */
	@Override
	public MatchDto ingest(MatchDto matchDto) {
		validate(matchDto);
		MatchDto fromFifa = fetchAndMap(matchDto.getMatchId());
		return matchService.updateMatchResult(fromFifa);
	}

	/**
	 * Probe + bulk-import finished first-stage matches from FIFA calendar + live endpoints.
	 *
	 * @return number of matches persisted
	 */
	public int importFirstStageResults() {
		JsonNode results = fetchCalendarResults();
		if (!results.isArray() || results.isEmpty()) {
			throw new IllegalStateException("FIFA calendar returned no matches");
		}

		int imported = 0;
		for (JsonNode calendarMatch : results) {
			if (!isFinishedFirstStage(calendarMatch)) {
				continue;
			}
			String matchId = stringOrNull(calendarMatch.path("IdMatch"));
			if (matchId == null) {
				continue;
			}
			matchService.updateMatchResult(fetchAndMap(matchId));
			imported++;
		}
		if (imported == 0) {
			throw new IllegalStateException("FIFA calendar had no finished first-stage matches");
		}
		return imported;
	}

	MatchDto fetchAndMap(String matchId) {
		String body;
		try {
			body = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/api/v3/live/football/allmatches")
							.queryParam("idCompetition", fifaApiProperties.getIdCompetition())
							.queryParam("idSeason", fifaApiProperties.getIdSeason())
							.queryParam("idMatch", matchId)
							.build())
					.retrieve()
					.body(String.class);
		}
		catch (RestClientException exception) {
			throw new IllegalStateException("FIFA HTTP request failed for match " + matchId, exception);
		}

		if (body == null || body.isBlank() || "null".equalsIgnoreCase(body.trim())) {
			throw new IllegalStateException("FIFA match not found: " + matchId);
		}

		JsonNode root = jsonMapper.readTree(body);
		if (root == null || root.isNull() || root.path("IdMatch").isMissingNode()) {
			throw new IllegalStateException("FIFA match payload missing IdMatch for: " + matchId);
		}
		return toMatchDto(root);
	}

	MatchDto toMatchDto(JsonNode root) {
		String matchId = root.path("IdMatch").stringValue();
		JsonNode statusNode = root.path("MatchStatus");
		if (statusNode.isMissingNode() || !statusNode.isNumber()
				|| !MatchStatus.PAST.matchesFifaCode(statusNode.intValue())) {
			throw new IllegalStateException(
					"FIFA match " + matchId + " is not finished (MatchStatus=" + statusNode + ")");
		}

		JsonNode homeTeam = root.path("HomeTeam");
		JsonNode awayTeam = root.path("AwayTeam");
		if (!homeTeam.path("Score").isNumber() || !awayTeam.path("Score").isNumber()) {
			throw new IllegalStateException("FIFA match " + matchId + " is missing team scores");
		}
		Team home = resolveTeam(homeTeam);
		Team away = resolveTeam(awayTeam);

		return ImmutableMatchDto.builder()
				.matchId(matchId)
				.home(home)
				.away(away)
				.homeScore(homeTeam.path("Score").intValue())
				.awayScore(awayTeam.path("Score").intValue())
				.matchStatus(MatchStatus.PAST)
				.homeStats(toStatsDto(homeTeam.path("Bookings")))
				.awayStats(toStatsDto(awayTeam.path("Bookings")))
				.build();
	}

	private JsonNode fetchCalendarResults() {
		String body;
		try {
			body = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.path("/api/v3/calendar/matches")
							.queryParam("idCompetition", fifaApiProperties.getIdCompetition())
							.queryParam("idSeason", fifaApiProperties.getIdSeason())
							.queryParam("count", 200)
							.build())
					.retrieve()
					.body(String.class);
		}
		catch (RestClientException exception) {
			throw new IllegalStateException("FIFA calendar HTTP request failed", exception);
		}
		if (body == null || body.isBlank() || "null".equalsIgnoreCase(body.trim())) {
			throw new IllegalStateException("FIFA calendar returned empty body");
		}
		JsonNode root = jsonMapper.readTree(body);
		return root.path("Results");
	}

	private boolean isFinishedFirstStage(JsonNode calendarMatch) {
		JsonNode statusNode = calendarMatch.path("MatchStatus");
		if (!statusNode.isNumber() || !MatchStatus.PAST.matchesFifaCode(statusNode.intValue())) {
			return false;
		}
		String idStage = stringOrNull(calendarMatch.path("IdStage"));
		return fifaApiProperties.getIdStageFirst().equals(idStage);
	}

	private TeamMatchStatsDto toStatsDto(JsonNode bookings) {
		int yellowCards = 0;
		int secondYellowReds = 0;
		int directReds = 0;

		if (bookings != null && bookings.isArray()) {
			for (JsonNode booking : bookings) {
				// Live field is "Card" (seed alias is "cardCode")
				JsonNode cardNode = booking.path("Card");
				if (!cardNode.isNumber()) {
					throw new IllegalStateException("FIFA booking missing numeric Card field: " + booking);
				}
				FairPlayEvent event = FairPlayEvent.fromFifaCardCode(cardNode.intValue());
				switch (event) {
					case YELLOW_CARD -> yellowCards++;
					case SECOND_YELLOW_RED -> secondYellowReds++;
					case DIRECT_RED -> directReds++;
				}
			}
		}

		int fairPlayScore = -(yellowCards * FairPlayEvent.YELLOW_CARD.getPenalty()
				+ secondYellowReds * FairPlayEvent.SECOND_YELLOW_RED.getPenalty()
				+ directReds * FairPlayEvent.DIRECT_RED.getPenalty());

		return ImmutableTeamMatchStatsDto.builder()
				.yellowCards(yellowCards)
				.secondYellowReds(secondYellowReds)
				.directReds(directReds)
				.fairPlayScore(fairPlayScore)
				.build();
	}

	private Team resolveTeam(JsonNode teamNode) {
		String code = firstNonBlank(
				stringOrNull(teamNode.path("IdCountry")),
				stringOrNull(teamNode.path("Abbreviation")));
		if (code != null) {
			for (Team team : Team.values()) {
				if (team.getCode().equalsIgnoreCase(code)) {
					return team;
				}
			}
		}

		String name = stringOrNull(teamNode.path("ShortClubName"));
		if (name != null) {
			for (Team team : Team.values()) {
				if (team.getName().equalsIgnoreCase(name)) {
					return team;
				}
			}
		}

		throw new IllegalArgumentException(
				"Unknown FIFA team (code=" + code + ", name=" + name + ")");
	}

	private static String stringOrNull(JsonNode node) {
		if (node == null || node.isMissingNode() || node.isNull()) {
			return null;
		}
		String value = node.stringValue();
		return value == null || value.isBlank() ? null : value;
	}

	private static String firstNonBlank(String first, String second) {
		if (first != null && !first.isBlank()) {
			return first;
		}
		if (second != null && !second.isBlank()) {
			return second;
		}
		return null;
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
