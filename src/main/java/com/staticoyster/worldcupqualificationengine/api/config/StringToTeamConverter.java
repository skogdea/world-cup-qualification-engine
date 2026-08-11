package com.staticoyster.worldcupqualificationengine.api.config;

import com.staticoyster.worldcupqualificationengine.domain.enums.Team;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/** Path/query {@link Team} binding by FIFA code: {@code irn} / {@code IRN} → {@link Team#IR_IRAN}. */
@Component
public class StringToTeamConverter implements Converter<String, Team> {

	@Override
	public Team convert(String source) {
		return Team.fromCode(source);
	}

}
