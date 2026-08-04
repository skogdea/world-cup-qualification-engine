package com.staticoyster.worldcupqualificationengine.ingestion;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.model.TeamMatchStats;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class FifaSeedMatchImporter {

	static final String DEFAULT_RESOURCE = "seed/fifa/wc2026_first_stage_discipline_stats.json";

	private final JsonMapper jsonMapper;
	private final FifaMatchAndCardsClient fifaMatchAndCardsClient;

	public FifaSeedMatchImporter(JsonMapper jsonMapper, FifaMatchAndCardsClient fifaMatchAndCardsClient) {
		this.jsonMapper = jsonMapper;
		this.fifaMatchAndCardsClient = fifaMatchAndCardsClient;
	}

	public int importDefaultSeed() {
		return importFromClasspath(DEFAULT_RESOURCE);
	}

	public int importFromClasspath(String resourcePath) {
		try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
			JsonNode matches = jsonMapper.readTree(inputStream).path("matches");
			if (!matches.isArray()) { // Todo
				throw new IllegalArgumentException("Expected 'matches' array in seed file: " + resourcePath);
			}

			int imported = 0;
			for (JsonNode matchNode : matches) {
				fifaMatchAndCardsClient.ingest(toMatch(matchNode));
				imported++;
			}
			return imported;
		}
		catch (IOException exception) {
			throw new IllegalStateException("Failed to read seed file: " + resourcePath, exception);
		}
	}

	private Match toMatch(JsonNode matchNode) {
		int[] score = parseScore(matchNode.path("score").stringValue());
		Team home = resolveTeam(matchNode.path("homeTeam").stringValue());
		Team away = resolveTeam(matchNode.path("awayTeam").stringValue());

		return Match.Builder.newBuilder()
				.withMatchId(matchNode.path("idMatch").stringValue())
				.withHome(home)
				.withAway(away)
				.withHomeScore(score[0])
				.withAwayScore(score[1])
				.withMatchStatus(MatchStatus.PAST)
				.withHomeStats(toStats(matchNode.path("home")))
				.withAwayStats(toStats(matchNode.path("away")))
				.build();
	}

	private TeamMatchStats toStats(JsonNode statsNode) {
		return TeamMatchStats.Builder.newBuilder()
				.withYellowCards(statsNode.path("yellowCards").intValue())
				.withSecondYellowReds(statsNode.path("secondYellowRedCards").intValue())
				.withDirectReds(statsNode.path("directRedCards").intValue())
				.build();
	}

	private int[] parseScore(String score) {
		String[] parts = score.split("-");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid score: " + score);
		}
		return new int[] {
				Integer.parseInt(parts[0].trim()),
				Integer.parseInt(parts[1].trim())
		};
	}

	private Team resolveTeam(String name) {
		for (Team team : Team.values()) {
			if (team.getName().equals(name)) {
				return team;
			}
		}
		throw new IllegalArgumentException("Unknown team: " + name);
	}

}
