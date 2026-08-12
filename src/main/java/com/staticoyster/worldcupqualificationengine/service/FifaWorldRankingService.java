package com.staticoyster.worldcupqualificationengine.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Loads {@code seed/fifa/fifa_mens_world_ranking.json} and exposes FIFA ranks for tie-breakers.
 * Lower rank number is better. Seed file ownership stays on the seed branch; this service only consumes it.
 */
@Service
public class FifaWorldRankingService {

	static final String DEFAULT_RESOURCE = "seed/fifa/fifa_mens_world_ranking.json";

	private final Map<Team, Integer> rankByTeam;

	public FifaWorldRankingService(JsonMapper jsonMapper) {
		this.rankByTeam = Collections.unmodifiableMap(loadRanks(jsonMapper, DEFAULT_RESOURCE));
	}

	/**
	 * @return FIFA men's world ranking position (1 = best), or {@link Integer#MAX_VALUE} if unknown
	 */
	public int getRank(Team team) {
		return rankByTeam.getOrDefault(team, Integer.MAX_VALUE);
	}

	/**
	 * Better (lower) FIFA rank first; equal/unknown ranks fall back to team FIFA code.
	 */
	public int compare(Team left, Team right) {
		int cmp = Integer.compare(getRank(left), getRank(right));
		if (cmp != 0) {
			return cmp;
		}
		return left.getCode().compareTo(right.getCode());
	}

	private static Map<Team, Integer> loadRanks(JsonMapper jsonMapper, String resourcePath) {
		try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
			JsonNode rankings = jsonMapper.readTree(inputStream).path("rankings");
			if (!rankings.isArray()) {
				throw new IllegalArgumentException("Expected 'rankings' array in: " + resourcePath);
			}

			Map<Team, Integer> ranks = new EnumMap<>(Team.class);
			Map<String, Team> teamByCode = teamByFifaCode();
			for (JsonNode entry : rankings) {
				String fifaCode = entry.path("fifaCode").stringValue();
				Team team = teamByCode.get(fifaCode);
				if (team != null) {
					ranks.put(team, entry.path("rank").intValue());
				}
			}
			return ranks;
		}
		catch (IOException exception) {
			throw new IllegalStateException("Failed to read FIFA ranking file: " + resourcePath, exception);
		}
	}

	private static Map<String, Team> teamByFifaCode() {
		Map<String, Team> byCode = new HashMap<>();
		for (Team team : Team.values()) {
			byCode.put(team.getCode(), team);
		}
		return byCode;
	}

}
