package com.est.statemachine;

public class Controller {
	private State currentState;
	private StateMachine machine;
	private CommandChannel commandChannel;
	
	
	public Controller(StateMachine machine,State currentState, 
			CommandChannel commandChannel) {
		super();
		this.currentState = currentState;
		this.machine = machine;
		this.commandChannel = commandChannel;
	}

	public CommandChannel getCommandChannel() {
		return commandChannel;
	}
	
	public void handle(String eventCode) {
		if (currentState.hasTransition(eventCode)) {
			transitionTo(currentState.targetState(eventCode));
		}
		else if (machine.isResetEvent(eventCode)) {
			System.out.println("reset machine.");
		}
	}
	
	private void transitionTo(State target) {
		System.out.println("transition from : " + currentState.getName() + " to :" + target.getName());
		
		currentState = target;
		currentState.executeActions(commandChannel);
	}
}
