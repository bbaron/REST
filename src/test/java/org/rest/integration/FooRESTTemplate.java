package org.rest.integration;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import java.io.IOException;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.rest.constants.HttpConstants;
import org.rest.integration.http.HTTPLinkHeaderUtils;
import org.rest.integration.security.SecurityComponent;
import org.rest.model.Foo;
import org.rest.util.json.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.jayway.restassured.response.Response;

/**
 * Template for the consumption of the REST API <br>
 */
@Component
public final class FooRESTTemplate{
	
	@Autowired
	ExamplePaths paths;
	
	@Autowired
	SecurityComponent securityComponent;
	
	public FooRESTTemplate(){
		super();
	}
	
	//
	public final String createResource(){
		return this.createResource( new Foo( randomAlphabetic( 6 ) ) );
	}
	public final String createResource( final Foo resource ){
		Preconditions.checkNotNull( resource );
		
		final String cookie = this.securityComponent.authenticateAsAdmin();
		
		final Response response = this.securityComponent.givenAuthenticated( cookie ).contentType( HttpConstants.MIME_JSON ).body( resource ).post( this.paths.getFooURL() );
		
		return HTTPLinkHeaderUtils.extractSingleURI( response.getHeader( "Link" ) );
	}
	
	public final < T >T getResourceViaJson( final String uriOfResource, final Class< T > clazz, final String cookie ) throws ClientProtocolException, IOException{
		final String resourceAsJson = this.getResourceAsJson( uriOfResource, cookie );
		return ConvertUtil.convertJsonToResource( resourceAsJson, clazz );
	}
	public final String getResourceAsJson( final String uriOfResource, final String cookie ){
		return this.securityComponent.givenAuthenticated( cookie ).header( HttpHeaders.ACCEPT, HttpConstants.MIME_JSON ).get( uriOfResource ).asString();
	}
	
}
