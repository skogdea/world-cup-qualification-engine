package com.staticoyster.worldcupqualificationengine.api.config;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;

/**
 * Path/query {@link Team} binding by enum name or FIFA code:
 * {@code ir_iran} / {@code IR_IRAN} / {@code IRN} → {@link Team#IR_IRAN}.
 */
@Component
public class StringToTeamConverter implements Converter<String, Team> {

	@Override
	public Team convert(String source) {
		String normalized = source.trim().toUpperCase(Locale.ROOT);
		try {
			return Team.valueOf(normalized);
		}
		catch (IllegalArgumentException ignored) {
			return Team.fromCode(normalized);
		}
	}

}
