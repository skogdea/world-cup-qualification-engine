package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FifaMatchAndCardsClientTest {

	private MockRestServiceServer mockServer;
	private FifaMatchAndCardsClient client;
	private JsonMapper jsonMapper;

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
		client = new FifaMatchAndCardsClient(builder, jsonMapper, null, properties);
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

	private static String classpath(String path) throws IOException {
		return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
	}

}
