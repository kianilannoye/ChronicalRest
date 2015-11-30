package be.ugent.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import be.ugent.Authentication;
import be.ugent.MongoDBSingleton;
import be.ugent.dao.DrugDao;
import be.ugent.dao.TriggerDao;
import be.ugent.entitity.Drug;
import be.ugent.entitity.Trigger;

@Path("/DBService")
public class DBConnectionService {
	
	Gson gson = new Gson();

	
	@GET
	@Path("/status")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getStatus(@HeaderParam("Authorization") String header) {
		
		try{
			if(!Authentication.isAuthorized(header)){
				return Response.status(403).build();
			}
			if(MongoDBSingleton.getInstance().isOnline("CHRONIC"))
				return Response.ok().build();
			else
				return Response.status(503).build();
		}catch(Exception e){
			return Response.status(503).build();
		}
		
		

	}

	
}