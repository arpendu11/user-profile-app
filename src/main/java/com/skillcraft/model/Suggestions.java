package com.skillcraft.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Suggestions {

	private List<AjaxAutoComplete> suggestions = new ArrayList<>();	

}
