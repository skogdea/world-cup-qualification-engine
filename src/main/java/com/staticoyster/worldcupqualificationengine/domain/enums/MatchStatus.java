package com.staticoyster.worldcupqualificationengine.domain.enums;

/**
 * Domain match lifecycle. {@link #PAST} carries FIFA live {@code MatchStatus} code {@code 0}.
 */
public enum MatchStatus {

	SCHEDULED(null),
	LIVE(null),
	PAST(0);

	private final Integer fifaCode;

	MatchStatus(Integer fifaCode) {
		this.fifaCode = fifaCode;
	}

	/**
	 * FIFA live/calendar {@code MatchStatus} integer, or {@code null} when not mapped.
	 */
	public Integer getFifaCode() {
		return fifaCode;
	}

	public boolean matchesFifaCode(int fifaMatchStatus) {
		return fifaCode != null && fifaCode == fifaMatchStatus;
	}

}
