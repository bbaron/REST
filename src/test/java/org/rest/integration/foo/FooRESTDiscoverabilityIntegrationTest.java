package org.rest.integration.foo;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.matchers.JUnitMatchers.containsString;

import org.apache.http.HttpHeaders;
import org.hamcrest.core.AnyOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rest.constants.HttpConstants;
import org.rest.integration.ExamplePaths;
import org.rest.integration.FooRESTTemplate;
import org.rest.integration.http.HTTPLinkHeaderUtils;
import org.rest.integration.security.SecurityComponent;
import org.rest.model.Foo;
import org.rest.spring.root.ApplicationConfig;
import org.rest.spring.root.PersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = { ApplicationConfig.class, PersistenceConfig.class },loader = AnnotationConfigContextLoader.class )
public class FooRESTDiscoverabilityIntegrationTest{
	@Autowired
	ExamplePaths paths;
	
	@Autowired
	FooRESTTemplate restTemplate;
	
	@Autowired
	SecurityComponent securityComponent;
	
	private String cookie;
	
	// fixtures
	
	@Before
	public final void before(){
		this.cookie = this.securityComponent.authenticateAsAdmin();
	}
	
	// tests
	
	// GET
	
	@Test
	public final void whenResourceIsRetrieved_thenURIToCreateANewResourceIsDiscoverable(){
		// Given
		final String uriOfNewlyCreatedResource = this.restTemplate.createResource();
		
		// When
		final Response response = this.givenAuthenticated().get( uriOfNewlyCreatedResource );
		
		// Then
		final String linkHeader = response.getHeader( "Link" );
		final String uriForResourceCreation = HTTPLinkHeaderUtils.extractSingleURI( linkHeader );
		final Response secondCreationResponse = this.givenAuthenticated().contentType( HttpConstants.MIME_JSON ).body( new Foo( randomAlphabetic( 6 ) ) ).post( uriForResourceCreation );
		assertThat( secondCreationResponse.getStatusCode(), is( 201 ) );
	}
	
	@Test
	public final void whenResourceIsRetrieved_thenURIToGetAllResourcesIsDiscoverable(){
		// Given
		final String uriOfExistingResource = this.restTemplate.createResource();
		
		// When
		final Response getResponse = this.givenAuthenticated().get( uriOfExistingResource );
		
		// Then
		final String uriToAllResources = HTTPLinkHeaderUtils.extractURIByRel( getResponse.getHeader( "Link" ), "collection" );
		
		final Response getAllResponse = this.givenAuthenticated().get( uriToAllResources );
		assertThat( getAllResponse.getStatusCode(), is( 200 ) );
	}
	
	// GET (all)
	
	// POST
	
	@SuppressWarnings( "unchecked" )
	@Test
	public final void whenInvalidPOSTIsSentToValidURIOfResource_thenAllowHeaderListsTheAllowedActions(){
		// Given
		final String uriOfNewlyExistingResource = this.restTemplate.createResource();
		
		// When
		final Response res = this.givenAuthenticated().post( uriOfNewlyExistingResource );
		
		// Then
		final String allowHeader = res.getHeader( HttpHeaders.ALLOW );
		assertThat( allowHeader, AnyOf.<String> anyOf( containsString( "GET" ), containsString( "PUT" ), containsString( "DELETE" ) ) );
	}
	
	@Test
	public final void whenAResourceIsCreated_thenTheResponseContainsTheLinkHeader(){
		// When
		final Response response = this.givenAuthenticated().contentType( HttpConstants.MIME_JSON ).body( new Foo( randomAlphabetic( 6 ) ) ).post( this.paths.getFooURL() );
		
		// Then
		assertNotNull( response.getHeader( "Link" ) );
	}
	@Test
	public final void whenResourceIsCreated_thenURIToTheNewlyCreatedResourceIsDiscoverable(){
		// When
		final Foo unpersistedResource = new Foo( randomAlphabetic( 6 ) );
		final Response createResp = this.givenAuthenticated().contentType( HttpConstants.MIME_JSON ).body( unpersistedResource ).post( this.paths.getFooURL() );
		final String uriOfNewlyCreatedResource = HTTPLinkHeaderUtils.extractSingleURI( createResp.getHeader( "Link" ) );
		
		// Then
		final Response response = this.givenAuthenticated().header( HttpHeaders.ACCEPT, HttpConstants.MIME_JSON ).get( uriOfNewlyCreatedResource );
		
		final Foo resourceFromServer = response.body().as( Foo.class );
		Assert.assertEquals( unpersistedResource, resourceFromServer );
	}
	
	// PUT
	
	// DELETE
	
	// util
	
	private final RequestSpecification givenAuthenticated(){
		return this.securityComponent.givenAuthenticated( this.cookie );
	}
	
}
