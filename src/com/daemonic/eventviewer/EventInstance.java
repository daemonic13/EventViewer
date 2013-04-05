package com.daemonic.eventviewer;

// right now this is just a dumb class
// kind of like a C struct

public class EventInstance {
	
	public String title; 
	public long start;
	public long end;
	public long eventID;
	public boolean allDay;
	
	public EventInstance() {
		
	}
	
	public void set(String iTitle,long iStart,long iEnd,long iEvent, boolean iAllDay){
		title = iTitle;
		start = iStart;
		end = iEnd;
		eventID = iEvent;
		allDay = iAllDay;
	}
};