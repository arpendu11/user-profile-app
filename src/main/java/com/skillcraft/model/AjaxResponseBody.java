package com.skillcraft.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class AjaxResponseBody {

	private String msg;
	private AjaxResults results;
}
