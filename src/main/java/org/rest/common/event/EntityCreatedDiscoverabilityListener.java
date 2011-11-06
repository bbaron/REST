package org.rest.common.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rest.common.util.RESTURLUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

@Component
final class EntityCreatedDiscoverabilityListener implements ApplicationListener< EntityCreated >{
	
	@Override
	public final void onApplicationEvent( final EntityCreated entityCreatedEvent ){
		Preconditions.checkNotNull( entityCreatedEvent );
		
		final HttpServletRequest request = entityCreatedEvent.getRequest();
		final HttpServletResponse response = entityCreatedEvent.getResponse();
		final long idOfNewEntity = entityCreatedEvent.getIdOfNewEntity();
		
		this.addLinkHeaderOnEntityCreation( request, response, idOfNewEntity );
	}
	
	final void addLinkHeaderOnEntityCreation( final HttpServletRequest request, final HttpServletResponse response, final long idOfNewEntity ){
		final StringBuffer requestURL = request.getRequestURL();
		
		final String linkHeaderValue = RESTURLUtil.createLinkHeader( requestURL + "/" + idOfNewEntity, "self" );
		
		response.addHeader( "Link", linkHeaderValue );
	}
	
}
