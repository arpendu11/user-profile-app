package com.skillcraft.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class AjaxResults {

	private Long total;
	private List<AjaxResult> list;
}
