package com.staticoyster.worldcupqualificationengine.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.ingestion.FifaMatchAndCardsClient;
import com.staticoyster.worldcupqualificationengine.repository.InMemoryMatchRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private InMemoryMatchRepository matchRepository;

	@MockitoBean
	private FifaMatchAndCardsClient fifaMatchAndCardsClient;

	@BeforeEach
	void setUp() {
		matchRepository.clear();
		when(fifaMatchAndCardsClient.ingest(any(MatchDto.class)))
				.thenThrow(new IllegalStateException("live FIFA disabled in tests"));
	}

	@Test
	void putMatchResultFallsBackToManualProviderAndCanBeReadBack() throws Exception {
		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-a-1", "MEXICO", "SOUTH_AFRICA", 2, 0)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.match_id").value("it-a-1"))
				.andExpect(jsonPath("$.home").value("MEXICO"))
				.andExpect(jsonPath("$.away").value("SOUTH_AFRICA"))
				.andExpect(jsonPath("$.home_score").value(2))
				.andExpect(jsonPath("$.away_score").value(0));

		mockMvc.perform(get("/api/v1/matches/it-a-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.home_score").value(2));

		mockMvc.perform(get("/api/v1/matches"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].match_id").value("it-a-1"));
	}

	@Test
	void putMatchResultFallsBackWhenFifaThrowsIllegalArgumentException() throws Exception {
		when(fifaMatchAndCardsClient.ingest(any(MatchDto.class)))
				.thenThrow(new IllegalArgumentException("Unknown FIFA team (code=XXX, name=null)"));

		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-a-1", "MEXICO", "SOUTH_AFRICA", 2, 0)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.match_id").value("it-a-1"))
				.andExpect(jsonPath("$.home_score").value(2));
	}

	@Test
	void putMatchResultFallsBackWhenFifaThrowsUnwrappedJacksonError() throws Exception {
		when(fifaMatchAndCardsClient.ingest(any(MatchDto.class)))
				.thenThrow(new RuntimeException("Unrecognized token in FIFA payload"));

		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-a-1", "MEXICO", "SOUTH_AFRICA", 2, 0)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.match_id").value("it-a-1"))
				.andExpect(jsonPath("$.home").value("MEXICO"));
	}

	@Test
	void getMissingMatchReturns404() throws Exception {
		mockMvc.perform(get("/api/v1/matches/does-not-exist"))
				.andExpect(status().isNotFound());
	}

	@Test
	void putCrossGroupMatchReturns400() throws Exception {
		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-bad", "MEXICO", "CANADA", 1, 0)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void standingsAndQualificationReflectPostedGroupResult() throws Exception {
		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-a-1", "MEXICO", "SOUTH_AFRICA", 2, 0)))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/v1/standings/groups/a"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].team").value("MEXICO"))
				.andExpect(jsonPath("$[0].points").value(3))
				.andExpect(jsonPath("$[0].played").value(1));

		mockMvc.perform(get("/api/v1/standings"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.A[0].team").value("MEXICO"));

		mockMvc.perform(get("/api/v1/qualification"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.group_winners[0]").value("MEXICO"))
				.andExpect(jsonPath("$.qualified_teams[0]").value("MEXICO"));

		mockMvc.perform(get("/api/v1/qualification/round-of-32"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0]").value("MEXICO"));

		mockMvc.perform(get("/api/v1/qualification/best-third-place"))
				.andExpect(status().isOk());
	}

	@Test
	void teamStatusUsesEnumNameAndRejectsFifaCode() throws Exception {
		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-a-1", "MEXICO", "SOUTH_AFRICA", 2, 0)))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/v1/status/teams/MEXICO"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.team").value("MEXICO"))
				.andExpect(jsonPath("$.group").value("A"))
				.andExpect(jsonPath("$.current_rank").value(1))
				.andExpect(jsonPath("$.team_status").value("QUALIFIED"));

		mockMvc.perform(get("/api/v1/status/teams/IR_IRAN"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.team").value("IR_IRAN"))
				.andExpect(jsonPath("$.group").value("G"));

		mockMvc.perform(get("/api/v1/status/teams/ir_iran"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.team").value("IR_IRAN"));

		mockMvc.perform(get("/api/v1/status/teams/IRN"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("enum name")));
	}

	@Test
	void putMatchResultRejectsFifaCodeInJsonBody() throws Exception {
		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-a-1", "MEX", "RSA", 2, 0)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("enum name")));
	}

	@Test
	void invalidGroupPathReturns400WithoutTeamGuidance() throws Exception {
		mockMvc.perform(get("/api/v1/standings/groups/Z"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("group")))
				.andExpect(content().string(containsString("Z")))
				.andExpect(content().string(not(containsString("MEXICO"))))
				.andExpect(content().string(not(containsString("FIFA"))));
	}

	@Test
	void malformedJsonBodyReturns400WithoutTeamGuidance() throws Exception {
		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Invalid JSON body")))
				.andExpect(content().string(not(containsString("MEXICO"))))
				.andExpect(content().string(not(containsString("FIFA"))));
	}

	@Test
	void invalidMatchStatusJsonReturns400WithoutTeamGuidance() throws Exception {
		mockMvc.perform(put("/api/v1/matches/result")
				.contentType(MediaType.APPLICATION_JSON)
				.content(matchJson("it-a-1", "MEXICO", "SOUTH_AFRICA", 2, 0)
						.replace("PAST", "FINISHED")))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(containsString("Invalid JSON body")))
				.andExpect(content().string(not(containsString("MEXICO"))))
				.andExpect(content().string(not(containsString("FIFA"))));
	}

	private static String matchJson(String matchId, String home, String away, int homeScore, int awayScore) {
		return String.format(
				"{\"match_id\":\"%s\",\"home\":\"%s\",\"away\":\"%s\","
						+ "\"home_score\":%d,\"away_score\":%d,\"match_status\":\"PAST\"}",
				matchId, home, away, homeScore, awayScore);
	}

}
