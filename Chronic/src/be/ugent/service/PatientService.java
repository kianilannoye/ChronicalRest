package be.ugent.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import be.ugent.Authentication;
import be.ugent.TestClass;
import be.ugent.dao.PatientDao;
import be.ugent.entitity.Patient;
 
@Path("/PatientService")
public class PatientService {
	PatientDao patientDao = new PatientDao();

	
	@GET
	@Path("/patients")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUser(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName, @HeaderParam("Authorization") String header) {
		if(!Authentication.isAuthorized(header)){
			return Response.status(403).build();
		}
//		System.out.println("Patient opgevraagd met naam: " + firstName + " " + lastName);
		Patient retrieved = patientDao.getPatient(firstName, lastName);
		retrieved.setPassword("");
		return Response.ok(retrieved+"").build();
	}
	
	@GET
	@Path("/patients/id")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUser(@QueryParam("patientID") String patientID, @HeaderParam("Authorization") String header) {
		System.out.println("Patient opgevraagd met id: "+patientID);
		if(!Authentication.isAuthorized(header)){
			return Response.status(403).build();
		}
//		System.out.println("Patient opgevraagd met naam: " + firstName + " " + lastName);
		Patient retrieved = patientDao.getPatient(Integer.parseInt(patientID));
		retrieved.setPassword("");
		return Response.ok(retrieved+"").build();
	}
	
	
	
	@GET
	@Path("/advice")
	@Produces({ MediaType.TEXT_PLAIN })
	public Response getAdvice(@QueryParam("patientID") String patientID, @HeaderParam("Authorization") String header) {
		
		if(!Authentication.isAuthorized(header)){
			return Response.status(403).build();
		}
//		System.out.println("Patient opgevraagd met naam: " + firstName + " " + lastName);
		Patient retrieved = patientDao.getPatienFromId(patientID);
		
		return Response.ok().entity(retrieved.getAdvice()+"").build();

	}
	
	@GET
	@Path("/login")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response login(@HeaderParam("Authorization") String header) {
		if(!Authentication.isAuthorized(header)){
			return Response.status(403).build();
		}		
//		System.out.println("User ingelogd met email:"+patientDao.getPatientFromHeader(header));
		Patient retrieved = patientDao.getPatientFromHeader(header);
		retrieved.setPassword("");
		return Response.ok(retrieved+"").build();

	}
	
	@POST
	@Path("/patients/hello")
	@Consumes({MediaType.TEXT_PLAIN})
	public Response hello(String user){
		
		System.out.println("Hello "+user);
		return Response.ok("Hello "+user).build();
		
	}

	@PUT
	@Path("/patients")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addUser(String user){
		
		System.out.println("pat: "+user);
		System.out.println("Patient requested to add: "+user.toString());
		Gson gson = new Gson();
		Patient toAdd = gson.fromJson(user, Patient.class);
		if(toAdd.getPatientID()==0){
			PatientDao patientDao = new PatientDao();
			toAdd.setPatientID(patientDao.getNewId());
		}
		JSONObject userJSON = null;
		try {
			userJSON = new JSONObject(user);
			toAdd.setRelation(""+userJSON.get("relation"));
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return Response.status(417).build();
		}
//		System.out.println("Patient to add:"+toAdd);
		if(patientDao.storePatient(toAdd)){
			//return patient successfully created
			String message = "Beste,\n\nEr is een nieuwe patient die zich heeft geregistreerd met patientID "+toAdd.getPatientID()+".\n\nMet vriendelijke groet,\n\nDe paashaas";
			try {
				TestClass.generateAndSendEmail("Nieuwe patient geregistreerd",message);
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return Response.status(201).entity(patientDao.getPatienFromId(toAdd.getPatientID()+"")).build();
		}else{
			//return record was already in database, or was wrong format
			return Response.status(409).build();
		}
	}

	@POST
	@Path("/patients/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeUser(String user){
		
		System.out.println("Patient requested to change: "+user.toString());
		Gson gson = new Gson();
		Patient toAdd = gson.fromJson(user, Patient.class);
		if(toAdd.getPatientID()<=0){
			return Response.status(404).build();
		}
		
		JSONObject userJSON = null;
		try {
			userJSON = new JSONObject(user);
			toAdd.setRelation(""+userJSON.get("relation"));
			
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		System.out.println("Patient to add:"+toAdd);
		if(patientDao.updatePatient(toAdd)){
			//return patient successfully created
			return Response.status(202).entity(toAdd.getPatientID()).build();
		}else{
			//return record was already in database, or was wrong format
			return Response.status(409).build();
		}
	}
	
	@POST
	@Path("/patients/diagnose")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response diagnoseUser(String tupleID,@HeaderParam("Authorization") String header) {
		if(!Authentication.isAuthorized(header)){
			return Response.status(403).build();
		}		
		System.out.println("Patient requested to diagnose: "+tupleID);
		Gson gson = new Gson();
		Patient toAdd = null;
		try {
			JsonObject tuple = gson.fromJson(tupleID, JsonObject.class);
			toAdd = patientDao.getPatienFromId(""+Integer.parseInt(tuple.get("patientID").getAsString()));
			if(toAdd.getPatientID()<=0){
				return Response.status(404).build();
			}
			toAdd.setDiagnoseID(tuple.get("diagnoseID").getAsInt());
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(patientDao.updatePatient(toAdd)){
			//return patient successfully created
			return Response.status(202).entity(toAdd.getPatientID()).build();
		}else{
			//return record was already in database, or was wrong format
			return Response.status(409).build();
		}
	}
	
	
}
