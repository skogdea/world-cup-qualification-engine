package com.staticoyster.worldcupqualificationengine.domain.model;

import com.staticoyster.worldcupqualificationengine.domain.enums.MatchStatus;
import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

import java.util.Objects;

public class Match {

	private String matchId;
	private Team home;
	private Team away;
	private int homeScore;
	private int awayScore;
	private MatchStatus matchStatus;
	private TeamMatchStats homeStats;
	private TeamMatchStats awayStats;

	public Match() {
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public Team getHome() {
		return home;
	}

	public void setHome(Team home) {
		this.home = home;
	}

	public Team getAway() {
		return away;
	}

	public void setAway(Team away) {
		this.away = away;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}

	public MatchStatus getMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(MatchStatus matchStatus) {
		this.matchStatus = matchStatus;
	}

	public TeamMatchStats getHomeStats() {
		return homeStats;
	}

	public void setHomeStats(TeamMatchStats homeStats) {
		this.homeStats = homeStats;
	}

	public TeamMatchStats getAwayStats() {
		return awayStats;
	}

	public void setAwayStats(TeamMatchStats awayStats) {
		this.awayStats = awayStats;
	}

	private Match(Builder builder) {
		this.matchId = builder.matchId;
		this.home = builder.home;
		this.away = builder.away;
		this.homeScore = builder.homeScore;
		this.awayScore = builder.awayScore;
		this.matchStatus = builder.matchStatus;
		this.homeStats = builder.homeStats;
		this.awayStats = builder.awayStats;
	}

	public static final class Builder {

		private String matchId;
		private Team home;
		private Team away;
		private int homeScore;
		private int awayScore;
		private MatchStatus matchStatus;
		private TeamMatchStats homeStats;
		private TeamMatchStats awayStats;

		private Builder() {
		}

		public static Builder newBuilder() {
			return new Builder();
		}

		public Builder withMatchId(String val) {
			matchId = val;
			return this;
		}

		public Builder withHome(Team val) {
			home = val;
			return this;
		}

		public Builder withAway(Team val) {
			away = val;
			return this;
		}

		public Builder withHomeScore(int val) {
			homeScore = val;
			return this;
		}

		public Builder withAwayScore(int val) {
			awayScore = val;
			return this;
		}

		public Builder withMatchStatus(MatchStatus val) {
			matchStatus = val;
			return this;
		}

		public Builder withHomeStats(TeamMatchStats val) {
			homeStats = val;
			return this;
		}

		public Builder withAwayStats(TeamMatchStats val) {
			awayStats = val;
			return this;
		}

		public Match build() {
			return new Match(this);
		}

	}

	@Override
	public String toString() {
		return "Match{"
				+ "matchId='" + matchId + '\''
				+ ", home=" + home
				+ ", away=" + away
				+ ", homeScore=" + homeScore
				+ ", awayScore=" + awayScore
				+ ", matchStatus=" + matchStatus
				+ ", homeStats=" + homeStats
				+ ", awayStats=" + awayStats
				+ '}';
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		Match match = (Match) object;
		return homeScore == match.homeScore
				&& awayScore == match.awayScore
				&& Objects.equals(matchId, match.matchId)
				&& home == match.home
				&& away == match.away
				&& matchStatus == match.matchStatus
				&& Objects.equals(homeStats, match.homeStats)
				&& Objects.equals(awayStats, match.awayStats);
	}

	@Override
	public int hashCode() {
		return Objects.hash(matchId, home, away, homeScore, awayScore, matchStatus, homeStats, awayStats);
	}
}
