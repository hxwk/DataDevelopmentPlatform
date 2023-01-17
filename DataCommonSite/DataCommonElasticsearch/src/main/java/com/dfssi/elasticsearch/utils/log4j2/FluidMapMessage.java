package com.dfssi.elasticsearch.utils.log4j2;

import org.apache.logging.log4j.message.MapMessage;

public class FluidMapMessage extends MapMessage {

	private static final long serialVersionUID = 3885431241176011273L;
	
	
	public FluidMapMessage add(String key, String value) {
		this.put(key, value);
		return this;
	}
	
	public FluidMapMessage message(String value) {
		this.put("message", value);
		return this;
	}
	
	public FluidMapMessage msg(String value) {
		return this.message(value);
	}

}