package be.ugent.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.gson.Gson;
import com.mongodb.util.JSON;

import be.ugent.Authentication;
import be.ugent.dao.DrugDao;
import be.ugent.entitity.Drug;

@Path("/DrugService")
public class DrugService {
	DrugDao drugDao = new DrugDao();
	Gson gson = new Gson();
	
	@GET
	@Path("/drugs/ld")
	public Response getAllLDDrugs(){
		// Open a valid json(-ld) input file
		InputStream inputStream;
		try {
			// Read the file into an Object (The type of this object will be a List, Map, String, Boolean,
			// Number or null depending on the root object in the file).
			Drug drug = drugDao.getDrug("Aspirine");
			System.out.println(gson.toJson(drug));
			
			Object jsonObject = gson.toJson(drug);
			// Create a context JSON map containing prefixes and definitions
			Map context = new HashMap();
			// Customise context...
			context.put("ex", "http://example.org/");
//			context.put("description", value)
			String graph = "\"@graph\": ["+
					"{ \"@id\":\""+drug.getDrugID()+"\","+
					"\"ex:description\":\""+drug.getDescription()+"\"}"+
							"]";
			
			System.out.println(graph);
			
			
			// Create an instance of JsonLdOptions with the standard JSON-LD options
			JsonLdOptions options = new JsonLdOptions();
			options.setUseNativeTypes(false);
			// Customise options...
			// Call whichever JSONLD function you want! (e.g. compact)
			System.out.println("{"+"\"@context\": { \"description\": \"http://example.org/description\"},\"description\": \"Test\"}");
			Object compact =JsonLdProcessor.expand(JSON.parse("{"+"\"@context\": { \"description\": \"http://example.org/description\"},\"description\": \"Test\"}")); 
//					JsonLdProcessor.expand(JSON.parse(graph), context, options);
			System.out.println(compact+"");
			// Print out the result (or don't, it's your call!)
//			System.out.println(JsonUtils.toPrettyString(compact));
			return Response.ok(JsonUtils.toPrettyString(compact)).build();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		} catch (JsonLdError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Response.serverError().build();
	}

	@GET
	@Path("/drugs")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllDrugs() {
		return Response.ok(drugDao.getAllDrugs()).build();
	}

	@PUT
	@Path("/drugs")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response addDrug(Drug drug, @HeaderParam("Authorization") String header) {
		System.out.println("header:" + header);
		if (!Authentication.isAuthorized(header)) {
			return Response.status(403).build();
		}
		Drug toAdd = drug;
		
		if (toAdd == null) {
			return Response.status(422).build();
		}

		System.out.println("Got request to add drug: " + gson.toJson(drug));

		toAdd.setDrugID(drugDao.getNewDrugID());

		System.out.println("Created drug: " + gson.toJson(toAdd));

		if (drugDao.addDrug(toAdd)) {
			// return drug successfully created
			return Response.status(201).entity(drugDao.getDrug(toAdd.getName())).build();
		} else {
			// return record was already in database, or was wrong format
			return Response.status(409).build();
		}
	}

	@POST
	@Path("/drugs/update")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response changeDrug(Drug drug, @HeaderParam("Authorization") String header) {
		if (!Authentication.isAuthorized(header)) {
			return Response.status(403).build();
		}

		Drug toAdd = drug;

		if (toAdd == null) {
			return Response.status(422).build();
		}
		System.out.println("Got request to update drug: " + gson.toJson(drug));
		// if it's a drug that is not yet submitted to the database
		if (drug.getDrugID() == -1) {
			int id = drugDao.getNewDrugID();
			drug.setDrugID(id);
			if (drugDao.addDrug(drug)) {
				return Response.status(201).entity(drugDao.getDrug(toAdd.getName())).build();
			} else {
				// drug given is already in database, but with wrong drugID
				return Response.status(409).build();
			}
		}

		System.out.println("Created drug: " + gson.toJson(toAdd));

		if (drugDao.changeDrug(toAdd)) {
			// return drug successfully created
			return Response.status(202).entity(drugDao.getDrug(toAdd.getName())).build();
		} else {
			// return record was already in database, or was wrong format
			return Response.status(409).build();
		}
	}

	@DELETE
	@Path("/drugs/delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response deleteDrug(Drug drug, @HeaderParam("Authorization") String header) {
		if (!Authentication.isAuthorized(header)) {
			return Response.status(403).build();
		}

		Drug toAdd = drug;

		if (toAdd == null) {
			return Response.status(422).build();
		}
		System.out.println("Got request to delete drug: " + gson.toJson(drug));
		// if it's a drug that is not yet submitted to the database
		if (drug.getDrugID() < 0) {
			// drug given is already in database, but with wrong drugID
			return Response.status(404).build();

		}

		if (drugDao.deleteDrug(toAdd)) {
			// return drug successfully deleted
			return Response.status(200).build();
		} else {
			// return record was already in database, or was wrong format
			return Response.status(404).build();
		}
	}

}
