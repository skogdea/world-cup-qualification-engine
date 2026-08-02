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

	public Match() {
	}

	private Match(Builder builder) {
		this.matchId = builder.matchId;
		this.home = builder.home;
		this.away = builder.away;
		this.homeScore = builder.homeScore;
		this.awayScore = builder.awayScore;
		this.matchStatus = builder.matchStatus;
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

	public static final class Builder {

		private String matchId;
		private Team home;
		private Team away;
		private int homeScore;
		private int awayScore;
		private MatchStatus matchStatus;

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
				&& matchStatus == match.matchStatus;
	}

	@Override
	public int hashCode() {
		return Objects.hash(matchId, home, away, homeScore, awayScore, matchStatus);
	}
}
