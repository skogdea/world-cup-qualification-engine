package com.staticoyster.worldcupqualificationengine.domain.enums;

/**
 * Points awarded for a group-stage match result
 * (see <a href="https://www.fifa.com/en/tournaments/mens/worldcup/canadamexicousa2026/articles/groups-how-teams-qualify-tie-breakers">FIFA</a>).
 */
public enum MatchPoints {

	WIN(3),
	DRAW(1),
	LOSS(0);

	private final int value;

	MatchPoints(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
