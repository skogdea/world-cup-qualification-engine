package com.staticoyster.worldcupqualificationengine.ingestion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.ingestion.config.FifaApiProperties;
import com.staticoyster.worldcupqualificationengine.service.MatchService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

class FifaMatchAndCardsClientTest {

	private MockRestServiceServer mockServer;
	private FifaMatchAndCardsClient client;
	private JsonMapper jsonMapper;
	private RecordingMatchService matchService;

	@BeforeEach
	void setUp() {
		FifaApiProperties properties = new FifaApiProperties();
		properties.setBaseUrl("https://api.fifa.com");
		properties.setIdCompetition("17");
		properties.setIdSeason("285023");
		properties.setIdStageFirst("289273");

		jsonMapper = JsonMapper.builder().build();
		RestClient.Builder builder = RestClient.builder();
		mockServer = MockRestServiceServer.bindTo(builder).build();
		matchService = new RecordingMatchService();
		client = new FifaMatchAndCardsClient(builder, jsonMapper, matchService, properties);
	}

	@Test
	void mapsLiveMatchPayloadToMatchDto() throws IOException {
		JsonNode root = jsonMapper.readTree(classpath("fixtures/fifa/live_match_400021443.json"));

		MatchDto result = client.toMatchDto(root);

		assertEquals("400021443", result.getMatchId());
		assertEquals(Team.MEXICO, result.getHome());
		assertEquals(Team.SOUTH_AFRICA, result.getAway());
		assertEquals(2, result.getHomeScore());
		assertEquals(0, result.getAwayScore());
		assertEquals(MatchStatus.PAST, result.getMatchStatus());
		assertEquals(1, result.getHomeStats().getYellowCards());
		assertEquals(1, result.getHomeStats().getDirectReds());
		assertEquals(-5, result.getHomeStats().getFairPlayScore());
		assertEquals(2, result.getAwayStats().getYellowCards());
		assertEquals(2, result.getAwayStats().getDirectReds());
		assertEquals(-10, result.getAwayStats().getFairPlayScore());
	}

	@Test
	void mapsLiveMatchPayloadWhenIdsAreNumeric() throws IOException {
		String payload = classpath("fixtures/fifa/live_match_400021443.json")
				.replace("\"IdMatch\": \"400021443\"", "\"IdMatch\": 400021443")
				.replace("\"IdStage\": \"289273\"", "\"IdStage\": 289273");
		JsonNode root = jsonMapper.readTree(payload);

		MatchDto result = client.toMatchDto(root);

		assertEquals("400021443", result.getMatchId());
		assertEquals(Team.MEXICO, result.getHome());
	}

	@Test
	void stringOrNullCoercesNumericAndStringScalars() {
		JsonNode root = jsonMapper.readTree(
				"{\"numeric\":400021443,\"text\":\"289273\",\"blank\":\"\",\"nil\":null}");

		assertEquals("400021443", FifaMatchAndCardsClient.stringOrNull(root.path("numeric")));
		assertEquals("289273", FifaMatchAndCardsClient.stringOrNull(root.path("text")));
		assertNull(FifaMatchAndCardsClient.stringOrNull(root.path("blank")));
		assertNull(FifaMatchAndCardsClient.stringOrNull(root.path("nil")));
		assertNull(FifaMatchAndCardsClient.stringOrNull(root.path("missing")));
	}

	@Test
	void fetchAndMapCallsFifaAllMatchesEndpoint() throws IOException {
		String payload = classpath("fixtures/fifa/live_match_400021443.json");
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=400021443"))
				.andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

		MatchDto result = client.fetchAndMap("400021443");

		assertEquals(Team.MEXICO, result.getHome());
		assertEquals(2, result.getHomeScore());
		mockServer.verify();
	}

	@Test
	void fetchAndMapFailsWhenFifaReturnsNullBody() {
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=999"))
				.andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> client.fetchAndMap("999"));
		assertEquals("FIFA match not found: 999", exception.getMessage());
		mockServer.verify();
	}

	@Test
	void fetchAndMapFailsWhenFifaReturnsMalformedJson() {
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=400021441"))
				.andRespond(withSuccess("{not-json", MediaType.APPLICATION_JSON));

		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> client.fetchAndMap("400021441"));
		assertEquals("FIFA match payload is not valid JSON for match 400021441", exception.getMessage());
		mockServer.verify();
	}

	@Test
	void importFirstStageResultsReadsNumericCalendarIds() throws IOException {
		String calendar = """
				{
				"Results": [
					{"IdMatch": 400021443, "IdStage": 289273, "MatchStatus": 0},
					{"IdMatch": 400021441, "IdStage": 289273, "MatchStatus": 1}
				]
				}
				""";
		String live = classpath("fixtures/fifa/live_match_400021443.json")
				.replace("\"IdMatch\": \"400021443\"", "\"IdMatch\": 400021443");

		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/calendar/matches"
								+ "?idCompetition=17&idSeason=285023&count=200"))
				.andRespond(withSuccess(calendar, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=400021443"))
				.andRespond(withSuccess(live, MediaType.APPLICATION_JSON));

		int imported = client.importFirstStageResults();

		assertEquals(1, imported);
		assertEquals(List.of("400021443"), matchService.persistedMatchIds);
		mockServer.verify();
	}

	@Test
	void importFirstStageResultsContinuesAfterMalformedJsonPayload() throws IOException {
		String calendar = """
				{
				"Results": [
					{"IdMatch": "400021441", "IdStage": "289273", "MatchStatus": 0},
					{"IdMatch": "400021443", "IdStage": "289273", "MatchStatus": 0}
				]
				}
				""";
		String liveOk = classpath("fixtures/fifa/live_match_400021443.json");

		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/calendar/matches"
								+ "?idCompetition=17&idSeason=285023&count=200"))
				.andRespond(withSuccess(calendar, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=400021441"))
				.andRespond(withSuccess("{not-json", MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=400021443"))
				.andRespond(withSuccess(liveOk, MediaType.APPLICATION_JSON));

		int imported = client.importFirstStageResults();

		assertEquals(1, imported);
		assertEquals(List.of("400021443"), matchService.persistedMatchIds);
		mockServer.verify();
	}

	@Test
	void importFirstStageResultsContinuesAfterOneMatchFailure() throws IOException {
		String calendar = """
				{
				"Results": [
					{"IdMatch": "400021443", "IdStage": "289273", "MatchStatus": 0},
					{"IdMatch": "400021441", "IdStage": "289273", "MatchStatus": 0}
				]
				}
				""";
		String liveOk = classpath("fixtures/fifa/live_match_400021443.json");

		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/calendar/matches"
								+ "?idCompetition=17&idSeason=285023&count=200"))
				.andRespond(withSuccess(calendar, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=400021443"))
				.andRespond(withSuccess(liveOk, MediaType.APPLICATION_JSON));
		mockServer.expect(requestTo(
						"https://api.fifa.com/api/v3/live/football/allmatches"
								+ "?idCompetition=17&idSeason=285023&idMatch=400021441"))
				.andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

		int imported = client.importFirstStageResults();

		assertEquals(1, imported);
		assertEquals(List.of("400021443"), matchService.persistedMatchIds);
		mockServer.verify();
	}

	private static String classpath(String path) throws IOException {
		return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
	}

	/** Minimal MatchService stand-in that records persisted match ids. */
	private static final class RecordingMatchService extends MatchService {

		private final List<String> persistedMatchIds = new ArrayList<>();

		RecordingMatchService() {
			super(null, null);
		}

		@Override
		public MatchDto updateMatchResult(MatchDto matchDto) {
			persistedMatchIds.add(matchDto.getMatchId());
			return matchDto;
		}
	}

}
