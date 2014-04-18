package com.est;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.est.statemachine.Command;
import com.est.statemachine.CommandChannel;
import com.est.statemachine.Controller;
import com.est.statemachine.Event;
import com.est.statemachine.State;
import com.est.statemachine.StateMachine;

public class AppXmlStyle {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		SAXReader reader = new SAXReader();
		Document doc = reader.read(getFile("controller.xml"));
		Element root = doc.getRootElement();
		
		List<Element> events = root.elements("event");
		List<Element> commands = root.elements("command");
		List<Element> states = root.elements("state");
		Element resetEventEl = root.element("resetEvent");
		
		Map<String, Event> eventMap = parseEvents(events);
		Map<String, Command> cmdMap = parseCommands(commands);
		Map<String, State> stateMap = parseStates(states, eventMap, cmdMap);
		
		State start = stateMap.get(root.attributeValue("start"));
				
		StateMachine sm = new StateMachine(start);
		sm.addResetEvents(parseResetEvent(eventMap, resetEventEl));
		
		CommandChannel commandChannel = new CommandChannel();
		Controller controller = new Controller(sm, start, commandChannel);
		controller.handle("D1CL");
		controller.handle("L1ON");
		controller.handle("D2OP");
		controller.handle("PNCL");
		controller.handle("D1OP");
	}
	
	public static Event parseResetEvent(Map<String, Event> eventMap, Element resetEventEl) {
		String name = resetEventEl.attributeValue("name");
		return eventMap.get(name);
	}
	
	public static Map<String, Event> parseEvents(List<Element> events) {
		Map<String, Event> map = new HashMap<String, Event>();
		for (Element el : events) {
			String name = el.attributeValue("name");
			String code = el.attributeValue("code");
			Event et = new Event(name, code);
			map.put(name, et);
		}
		
		return map;
	}
	
	public static Map<String, Command> parseCommands(List<Element> commands) {
		Map<String, Command> map = new HashMap<String, Command>();
		for (Element el : commands) {
			String name = el.attributeValue("name");
			String code = el.attributeValue("code");
			Command et = new Command(name, code);
			map.put(name, et);
		}
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, State> parseStates(List<Element> states, 
			Map<String, Event> eventMap, Map<String, Command> cmdMap) {
		Map<String, State> stateMap = new HashMap<String, State>();
		for (Element el : states) {
			String name = el.attributeValue("name");
			State st = new State(name);
			stateMap.put(name, st);
		}
		
		for (Element el : states) {
			String stateName = el.attributeValue("name");
			State st = stateMap.get(stateName);
			
			List<Element> subs = el.elements();
			for (Element s : subs) {
				if ("transition".equals(s.getName())) {
					String eventName = s.attributeValue("event");
					String targetName = s.attributeValue("target");
					
					st.addTransition(eventMap.get(eventName), stateMap.get(targetName));
				}
				
				if ("action".equals(s.getName())) {
					String commandName = s.attributeValue("command");
					st.addAction(cmdMap.get(commandName));
				}
			}
		}
		
		return stateMap;
	}
	
	public static InputStream getFile(String name) {
		return AppXmlStyle.class.getResourceAsStream(name);
	}

}
