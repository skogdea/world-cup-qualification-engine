package com.staticoyster.worldcupqualificationengine.ingestion;

import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableMatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.ImmutableTeamMatchStatsDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.MatchDto;
import com.staticoyster.worldcupqualificationengine.domain.dto.TeamMatchStatsDto;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is the seed loader or parser.
 */
@Component
public class FifaSeedMatchImporter {

	static final String DEFAULT_RESOURCE = "seed/fifa/wc2026_first_stage_discipline_stats.json";

	private final JsonMapper jsonMapper;
	private final ManualMatchAndCardsProvider manualMatchAndCardsProvider;

	public FifaSeedMatchImporter(JsonMapper jsonMapper, ManualMatchAndCardsProvider manualMatchAndCardsProvider) {
		this.jsonMapper = jsonMapper;
		this.manualMatchAndCardsProvider = manualMatchAndCardsProvider;
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
				// Offline seed path: manual adapter → MatchService (not live FIFA HTTP).
				manualMatchAndCardsProvider.ingest(toMatchDto(matchNode));
				imported++;
			}
			return imported;
		}
		catch (IOException exception) {
			throw new IllegalStateException("Failed to read seed file: " + resourcePath, exception);
		}
	}

	private MatchDto toMatchDto(JsonNode matchNode) {
		int[] score = parseScore(matchNode.path("score").stringValue());
		Team home = resolveTeam(matchNode.path("homeTeam").stringValue());
		Team away = resolveTeam(matchNode.path("awayTeam").stringValue());

		return ImmutableMatchDto.builder()
				.matchId(matchNode.path("idMatch").stringValue())
				.home(home)
				.away(away)
				.homeScore(score[0])
				.awayScore(score[1])
				.matchStatus(MatchStatus.PAST)
				.homeStats(toStatsDto(matchNode.path("home")))
				.awayStats(toStatsDto(matchNode.path("away")))
				.build();
	}

	private TeamMatchStatsDto toStatsDto(JsonNode statsNode) {
		return ImmutableTeamMatchStatsDto.builder()
				.yellowCards(statsNode.path("yellowCards").intValue())
				.secondYellowReds(statsNode.path("secondYellowRedCards").intValue())
				.directReds(statsNode.path("directRedCards").intValue())
				.fairPlayScore(statsNode.path("fairPlayScore").intValue())
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
