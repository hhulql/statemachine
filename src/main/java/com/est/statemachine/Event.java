package com.est.statemachine;

public class Event extends AbstractEvent {

	public Event(String name, String code) {
		super(name, code);
	}

	@Override
	public String toString() {
		return "Event:" + getName() + "," + getCode();
	}
}
