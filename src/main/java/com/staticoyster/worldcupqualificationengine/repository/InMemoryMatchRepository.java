package com.staticoyster.worldcupqualificationengine.repository;

import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMatchRepository implements MatchRepository {

	private final Map<String, Match> matchesById = new ConcurrentHashMap<>();

	@Override
	public Match save(Match match) {
		if (match.getMatchId() == null || match.getMatchId().isBlank()) {
			throw new IllegalArgumentException("matchId is required");
		}
		matchesById.put(match.getMatchId(), match);
		return match;
	}

	@Override
	public Optional<Match> findById(String matchId) {
		return Optional.ofNullable(matchesById.get(matchId));
	}

	@Override
	public Optional<Match> findByHomeAndAway(Team home, Team away) {
		return matchesById.values().stream()
				.filter(match -> match.getHome() == home && match.getAway() == away)
				.findFirst();
	}

	@Override
	public List<Match> findAll() {
		return new ArrayList<>(matchesById.values());
	}

	@Override
	public List<Match> findByGroupAndStatus(Group group, MatchStatus status) {
		return matchesById.values().stream()
				.filter(match -> match.getMatchStatus() == status)
				.filter(match -> match.getHome() != null
						&& match.getAway() != null
						&& match.getHome().getGroup() == group
						&& match.getAway().getGroup() == group)
				.toList();
	}

}
