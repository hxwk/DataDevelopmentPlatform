package com.dfssi.elasticsearch.utils.log4j2;

public class ELM {
	
	public static FluidMapMessage msg() {
		return new FluidMapMessage();
	}
	
	public static FluidMapMessage msg(String msg) {
		return (new FluidMapMessage()).add("message",msg);
	}
	

}