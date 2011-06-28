package org.pelagios.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.pelagios.graph.exception.DatasetNotFoundException;

@Provider
public class DatasetNotFoundExceptionMapper implements ExceptionMapper<DatasetNotFoundException> {

	public Response toResponse(DatasetNotFoundException e) {
		return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
	}
	
}