package com.staticoyster.worldcupqualificationengine.repository;

import com.staticoyster.worldcupqualificationengine.domain.model.Match;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {

	Match save(Match match);

	Optional<Match> findById(String matchId);

	Optional<Match> findByHomeAndAway(Team home, Team away);

	List<Match> findAll();

	List<Match> findByGroupAndStatus(Group group, MatchStatus status);

}
