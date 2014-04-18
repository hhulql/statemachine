package com.est.statemachine;

public class AbstractEvent {
	private String name;
	private String code;
	public AbstractEvent(String name, String code) {
		super();
		this.name = name;
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getName() {
		return name;
	}
}
