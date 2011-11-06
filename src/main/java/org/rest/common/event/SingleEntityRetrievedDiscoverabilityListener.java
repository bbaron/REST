package org.rest.common.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rest.common.util.RESTURLUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component
final class SingleEntityRetrievedDiscoverabilityListener implements ApplicationListener< SingleEntityRetrieved >{
	
	@Override
	public final void onApplicationEvent( final SingleEntityRetrieved entityRetrievedEvent ){
		Preconditions.checkNotNull( entityRetrievedEvent );
		
		final HttpServletRequest request = entityRetrievedEvent.getRequest();
		final HttpServletResponse response = entityRetrievedEvent.getResponse();
		
		this.addLinkHeaderOnSingleEntityRetrieval( request, response );
	}
	
	final void addLinkHeaderOnSingleEntityRetrieval( final HttpServletRequest request, final HttpServletResponse response ){
		final StringBuffer requestURL = request.getRequestURL();
		final int positionOfLastSlash = requestURL.lastIndexOf( "/" );
		final String uriForEntityCreation = requestURL.substring( 0, positionOfLastSlash );
		
		final String linkHeaderValue = RESTURLUtil.createLinkHeader( uriForEntityCreation, "collection" );
		response.addHeader( "Link", linkHeaderValue );
	}
	
	

}
