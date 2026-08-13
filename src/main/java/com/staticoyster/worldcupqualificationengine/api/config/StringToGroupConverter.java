package com.staticoyster.worldcupqualificationengine.api.config;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.staticoyster.worldcupqualificationengine.domain.enums.Group;

/** Path/query {@link Group} binding: {@code f} and {@code F} both resolve to {@link Group#F}. */
@Component
public class StringToGroupConverter implements Converter<String, Group> {

	@Override
	public Group convert(String source) {
		return Group.valueOf(source.trim().toUpperCase(Locale.ROOT));
	}

}
