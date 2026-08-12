package com.staticoyster.worldcupqualificationengine.repository;

import java.util.List;
import java.util.Optional;

import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import com.staticoyster.worldcupqualificationengine.domain.model.Match;

public interface MatchRepository {

	Match save(Match match);

	Optional<Match> findById(String matchId);

	Optional<Match> findByHomeAndAway(Team home, Team away);

	List<Match> findAll();

	List<Match> findByGroupAndStatus(Group group, MatchStatus status);

}
