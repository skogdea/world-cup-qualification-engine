package com.staticoyster.worldcupqualificationengine.api;

import com.staticoyster.worldcupqualificationengine.domain.dto.StandingDto;
import com.staticoyster.worldcupqualificationengine.domain.enums.Group;
import com.staticoyster.worldcupqualificationengine.service.GroupStageStandingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/standings")
public class StandingsController {

	private final GroupStageStandingsService groupStageStandingsService;

	public StandingsController(GroupStageStandingsService groupStageStandingsService) {
		this.groupStageStandingsService = groupStageStandingsService;
	}

	@GetMapping
	public Map<Group, List<StandingDto>> getAllGroupStandings() {
		return groupStageStandingsService.getAllGroupStandingsDtos();
	}

	@GetMapping("/groups/{group}")
	public List<StandingDto> getGroupStandings(@PathVariable("group") Group group) {
		return groupStageStandingsService.getGroupStandingsDtoInCurrentGroup(group);
	}

}
