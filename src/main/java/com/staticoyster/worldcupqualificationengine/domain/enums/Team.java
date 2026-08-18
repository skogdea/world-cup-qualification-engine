package com.staticoyster.worldcupqualificationengine.domain.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum Team {

	MEXICO("MEX", "Mexico", Group.A),
	SOUTH_AFRICA("RSA", "South Africa", Group.A),
	KOREA_REPUBLIC("KOR", "Korea Republic", Group.A),
	CZECHIA("CZE", "Czechia", Group.A),

	CANADA("CAN", "Canada", Group.B),
	BOSNIA_AND_HERZEGOVINA("BIH", "Bosnia and Herzegovina", Group.B),
	QATAR("QAT", "Qatar", Group.B),
	SWITZERLAND("SUI", "Switzerland", Group.B),

	BRAZIL("BRA", "Brazil", Group.C),
	MOROCCO("MAR", "Morocco", Group.C),
	HAITI("HAI", "Haiti", Group.C),
	SCOTLAND("SCO", "Scotland", Group.C),

	USA("USA", "USA", Group.D),
	PARAGUAY("PAR", "Paraguay", Group.D),
	AUSTRALIA("AUS", "Australia", Group.D),
	TURKIYE("TUR", "Türkiye", Group.D),

	GERMANY("GER", "Germany", Group.E),
	CURACAO("CUW", "Curaçao", Group.E),
	COTE_DIVOIRE("CIV", "Côte d'Ivoire", Group.E),
	ECUADOR("ECU", "Ecuador", Group.E),

	NETHERLANDS("NED", "Netherlands", Group.F),
	JAPAN("JPN", "Japan", Group.F),
	SWEDEN("SWE", "Sweden", Group.F),
	TUNISIA("TUN", "Tunisia", Group.F),

	BELGIUM("BEL", "Belgium", Group.G),
	EGYPT("EGY", "Egypt", Group.G),
	IR_IRAN("IRN", "IR Iran", Group.G),
	NEW_ZEALAND("NZL", "New Zealand", Group.G),

	SPAIN("ESP", "Spain", Group.H),
	CABO_VERDE("CPV", "Cabo Verde", Group.H),
	SAUDI_ARABIA("KSA", "Saudi Arabia", Group.H),
	URUGUAY("URU", "Uruguay", Group.H),

	FRANCE("FRA", "France", Group.I),
	SENEGAL("SEN", "Senegal", Group.I),
	IRAQ("IRQ", "Iraq", Group.I),
	NORWAY("NOR", "Norway", Group.I),

	ARGENTINA("ARG", "Argentina", Group.J),
	ALGERIA("ALG", "Algeria", Group.J),
	AUSTRIA("AUT", "Austria", Group.J),
	JORDAN("JOR", "Jordan", Group.J),

	PORTUGAL("POR", "Portugal", Group.K),
	CONGO_DR("COD", "Congo DR", Group.K),
	UZBEKISTAN("UZB", "Uzbekistan", Group.K),
	COLOMBIA("COL", "Colombia", Group.K),

	ENGLAND("ENG", "England", Group.L),
	CROATIA("CRO", "Croatia", Group.L),
	GHANA("GHA", "Ghana", Group.L),
	PANAMA("PAN", "Panama", Group.L);

	private static final Map<String, Team> BY_CODE;

	static {
		Map<String, Team> byCode = new HashMap<>();
		for (Team team : values()) {
			byCode.put(team.code, team);
		}
		BY_CODE = Collections.unmodifiableMap(byCode);
	}

	private final String code;
	private final String name;
	private final Group group;

	Team(String code, String name, Group group) {
		this.code = code;
		this.name = name;
		this.group = group;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public Group getGroup() {
		return group;
	}

	/** Resolves a FIFA 3-letter code (case-insensitive), e.g. {@code IRN} → {@link #IR_IRAN}. */
	public static Team fromCode(String code) {
		if (code == null || code.isBlank()) {
			throw new IllegalArgumentException("FIFA team code is required");
		}
		Team team = BY_CODE.get(code.trim().toUpperCase(Locale.ROOT));
		if (team == null) {
			throw new IllegalArgumentException("Unknown FIFA team code: " + code);
		}
		return team;
	}

}
