package com.staticoyster.worldcupqualificationengine.service;

import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchPoints;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.model.Standing;
import com.staticoyster.worldcupqualificationengine.repository.MatchRepository;
import com.staticoyster.worldcupqualificationengine.service.api.DomainDtoConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StandingCalculator {

	private final MatchRepository matchRepository;
	private final DomainDtoConverter domainDtoConverter;
	private final FifaWorldRankingService fifaWorldRankingService;

	public StandingCalculator(
			MatchRepository matchRepository,
			DomainDtoConverter domainDtoConverter,
			FifaWorldRankingService fifaWorldRankingService) {
		this.matchRepository = matchRepository;
		this.domainDtoConverter = domainDtoConverter;
		this.fifaWorldRankingService = fifaWorldRankingService;
	}

	// Mutable models for accumulation; StandingDto is immutable. Convert at the public boundary.
	public List<Match> retrieveAllPastMatchesInCurrentGroup(Group group) {
		return matchRepository.findByGroupAndStatus(group, MatchStatus.PAST);
	}

	public List<StandingDto> calculateStandingsDtoInCurrentGroup(Group group) {
		// Mutable models for accumulation; StandingDto is immutable. Convert at the public boundary.
		List<Match> pastMatches = retrieveAllPastMatchesInCurrentGroup(group);
		Map<Team, Standing> standingsByTeam = initializeStandings(group);

		for (Match match : pastMatches) {
			applyMatchResult(standingsByTeam, match);
		}

		for (Standing standing : standingsByTeam.values()) {
			standing.setGoalDifference(standing.getGoalsFor() - standing.getGoalsAgainst());
		}

		return domainDtoConverter.toStandingDtos(rankStandings(new ArrayList<>(standingsByTeam.values()), pastMatches));
	}

	private Map<Team, Standing> initializeStandings(Group group) {
		Map<Team, Standing> standingsByTeam = new EnumMap<>(Team.class);
		for (Team team : teamsInGroup(group)) {
			standingsByTeam.put(team, Standing.Builder.newBuilder()
					.withGroup(group)
					.withTeam(team)
					.withPlayed(0)
					.withWon(0)
					.withDrawn(0)
					.withLost(0)
					.withGoalsFor(0)
					.withGoalsAgainst(0)
					.withGoalDifference(0)
					.withTeamConductScore(0)
					.withPoints(0)
					.build());
		}
		return standingsByTeam;
	}

	private void applyMatchResult(Map<Team, Standing> standingsByTeam, Match match) {
		Standing home = standingsByTeam.get(match.getHome());
		Standing away = standingsByTeam.get(match.getAway());
		if (home == null || away == null) {
			return;
		}

		// Accumulate goals across all past matches in the group (running totals).
		home.setPlayed(home.getPlayed() + 1);
		away.setPlayed(away.getPlayed() + 1);

		home.setGoalsFor(home.getGoalsFor() + match.getHomeScore());
		home.setGoalsAgainst(home.getGoalsAgainst() + match.getAwayScore());
		away.setGoalsFor(away.getGoalsFor() + match.getAwayScore());
		away.setGoalsAgainst(away.getGoalsAgainst() + match.getHomeScore());

		// TCS: sum of per-match fair-play scores from Match.homeStats / awayStats.
		if (match.getHomeStats() != null) {
			home.setTeamConductScore(home.getTeamConductScore() + match.getHomeStats().getFairPlayScore());
		}
		if (match.getAwayStats() != null) {
			away.setTeamConductScore(away.getTeamConductScore() + match.getAwayStats().getFairPlayScore());
		}

		if (match.getHomeScore() > match.getAwayScore()) {
			home.setWon(home.getWon() + 1);
			away.setLost(away.getLost() + 1);
			home.setPoints(home.getPoints() + MatchPoints.WIN.getValue());
		}
		else if (match.getHomeScore() < match.getAwayScore()) {
			away.setWon(away.getWon() + 1);
			home.setLost(home.getLost() + 1);
			away.setPoints(away.getPoints() + MatchPoints.WIN.getValue());
		}
		else {
			home.setDrawn(home.getDrawn() + 1);
			away.setDrawn(away.getDrawn() + 1);
			home.setPoints(home.getPoints() + MatchPoints.DRAW.getValue());
			away.setPoints(away.getPoints() + MatchPoints.DRAW.getValue());
		}
	}

	private List<Standing> rankStandings(List<Standing> standings, List<Match> pastMatches) {
		Map<Integer, List<Standing>> byPoints = standings.stream()
				.sorted(Comparator.comparingInt(Standing::getPoints).reversed())
				.collect(Collectors.groupingBy(
						Standing::getPoints,
						LinkedHashMap::new,
						Collectors.toCollection(ArrayList::new)));

		List<Standing> ranked = new ArrayList<>();
		for (List<Standing> tiedOnPoints : byPoints.values()) {
			ranked.addAll(breakTies(tiedOnPoints, pastMatches));
		}
		return ranked;
	}

	private List<Standing> breakTies(List<Standing> tied, List<Match> pastMatches) {
		if (tied.size() <= 1) {
			return tied;
		}

		List<Team> tiedTeams = tied.stream().map(Standing::getTeam).toList();
		List<Match> miniMatches = pastMatches.stream()
				.filter(match -> tiedTeams.contains(match.getHome()) && tiedTeams.contains(match.getAway()))
				.toList();

		Map<Team, int[]> h2h = new EnumMap<>(Team.class);
		for (Team team : tiedTeams) {
			h2h.put(team, new int[] {0, 0, 0}); // points, gd, gf
		}
		for (Match match : miniMatches) {
			int[] homeStats = h2h.get(match.getHome());
			int[] awayStats = h2h.get(match.getAway());
			// GF:
			homeStats[2] += match.getHomeScore();
			awayStats[2] += match.getAwayScore();
			// GD:
			homeStats[1] += match.getHomeScore() - match.getAwayScore();
			awayStats[1] += match.getAwayScore() - match.getHomeScore();
			// Pts:
			if (match.getHomeScore() > match.getAwayScore()) {
				homeStats[0] += MatchPoints.WIN.getValue();
			}
			else if (match.getHomeScore() < match.getAwayScore()) {
				awayStats[0] += MatchPoints.WIN.getValue();
			}
			else {
				homeStats[0] += MatchPoints.DRAW.getValue();
				awayStats[0] += MatchPoints.DRAW.getValue();
			}
		}

		// FIFA in-group tie-breakers when equal on overall points:
		// H2H Pts → H2H GD → H2H GF → overall GD → overall GF → TCS → FIFA ranking
		// https://www.fifa.com/en/tournaments/mens/worldcup/canadamexicousa2026/articles/groups-how-teams-qualify-tie-breakers
		List<Standing> sorted = new ArrayList<>(tied);
		sorted.sort((left, right) -> { // Todo: .sort(a, b)
			int[] leftH2h = h2h.get(left.getTeam());
			int[] rightH2h = h2h.get(right.getTeam());
			// Compare H2H Pts:
			int cmp = Integer.compare(rightH2h[0], leftH2h[0]);
			if (cmp != 0) {
				return cmp;
			}
			// Compare H2H GD:
			cmp = Integer.compare(rightH2h[1], leftH2h[1]);
			if (cmp != 0) {
				return cmp;
			}
			// Compare H2H GF:
			cmp = Integer.compare(rightH2h[2], leftH2h[2]);
			if (cmp != 0) {
				return cmp;
			}
			// Compare overall GD:
			cmp = Integer.compare(right.getGoalDifference(), left.getGoalDifference());
			if (cmp != 0) {
				return cmp;
			}
			// Compare overall GF:
			cmp = Integer.compare(right.getGoalsFor(), left.getGoalsFor());
			if (cmp != 0) {
				return cmp;
			}
			// Compare TCS:
			cmp = Integer.compare(right.getTeamConductScore(), left.getTeamConductScore());
			if (cmp != 0) {
				return cmp;
			}
			// FIFA ranking (lower rank number is better); team FIFA code fallback if ranks are equal/unknown.
			return fifaWorldRankingService.compare(left.getTeam(), right.getTeam());
		});
		return sorted;
	}

	private List<Team> teamsInGroup(Group group) {
		return Arrays.stream(Team.values())
				.filter(team -> team.getGroup() == group)
				.toList();
	}

}
