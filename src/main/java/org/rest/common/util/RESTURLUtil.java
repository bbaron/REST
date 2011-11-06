package org.rest.common.util;

public final class RESTURLUtil{
	
	private RESTURLUtil(){
		throw new AssertionError();
	}
	
	//

	public static String createLinkHeader( final String uri, final String rel ){
		return "<" + uri + ">; rel=\"" + rel + "\"";
	}
	
}
