package org.rest.controller.foo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rest.common.event.EntityCreated;
import org.rest.common.event.SingleEntityRetrieved;
import org.rest.common.util.RestPreconditions;
import org.rest.model.Foo;
import org.rest.service.foo.IFooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author eugenp
 */
@Controller
final class FooController{
	
	@Autowired
	IFooService service;
	
	@Autowired
	ApplicationContext applicationContext;
	
	public FooController(){
		super();
	}
	
	// API
	
	@RequestMapping( value = "admin/foo",method = RequestMethod.GET )
	@ResponseBody
	public final List< Foo > getAll(){
		return this.service.getAll();
	}
	
	@RequestMapping( value = "admin/foo/{id}",method = RequestMethod.GET )
	@ResponseBody
	public final Foo get( @PathVariable( "id" ) final Long id, final HttpServletRequest request, final HttpServletResponse response ){
		final Foo entityById = RestPreconditions.checkNotNull( this.service.getById( id ) );
		
		this.applicationContext.publishEvent( new SingleEntityRetrieved( this, request, response ) );
		return entityById;
	}
	
	@RequestMapping( value = "admin/foo",method = RequestMethod.POST )
	@ResponseStatus( HttpStatus.CREATED )
	public final void create( @RequestBody final Foo entity, final HttpServletRequest request, final HttpServletResponse response ){
		RestPreconditions.checkNotNullFromRequest( entity );
		final Long idOfCreatedResource = this.service.create( entity );
		
		this.applicationContext.publishEvent( new EntityCreated( this, request, response, idOfCreatedResource ) );
	}
	
	@RequestMapping( value = "admin/foo",method = RequestMethod.PUT )
	@ResponseStatus( HttpStatus.OK )
	public final void update( @RequestBody final Foo entity ){
		RestPreconditions.checkNotNullFromRequest( entity );
		RestPreconditions.checkNotNull( this.service.getById( entity.getId() ) );
		this.service.update( entity );
	}
	
	@RequestMapping( value = "admin/foo/{id}",method = RequestMethod.DELETE )
	@ResponseStatus( HttpStatus.OK )
	public final void delete( @PathVariable( "id" ) final Long id ){
		this.service.deleteById( id );
	}
	
}
