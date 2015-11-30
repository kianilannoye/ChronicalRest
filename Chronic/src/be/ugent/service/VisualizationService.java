package be.ugent.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.mongodb.util.JSON;

import be.ugent.Authentication;
import be.ugent.dao.HeadacheDao;
import be.ugent.dao.PatientDao;
import be.ugent.entitity.Headache;
import be.ugent.entitity.Pair;
import be.ugent.entitity.Patient;

@Path("/VisualizationService")
public class VisualizationService {

	@GET
	@Path("/sensors")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllSensors(@QueryParam("patientID") String patientID) {
		// TODO: better catch blocks and return states

		String context = "\"@context\": {" + "\"name\": \"http://example.org/name\","
				+ "\"semanticName\": {\"@id\": \"http://example.org/semanticName\",\"@type\": \"@id\"}," + "\"url\": \"http://example.org/url\""
				+ "}";

		String values = "\"name\":" + "\"Hoofdpijn Intensiteiten\" ,"
				+ "\"semanticName\": \"http://example.org/HeadacheIntensitySensor\"," + "\"url\": \""
				+ "headache_intensities\"";
		String output = "{" + context + "," + values + "}";

		Object compact;
		try {
			compact = JsonLdProcessor.expand(JSON.parse(output));
			System.out.println(compact + "");
			return Response.ok(JsonUtils.toPrettyString(compact)).build();
		} catch (JsonLdError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(422).build();
	}

	@GET
	@Path("/sensors/headache_intensities")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllHeadachesForAllPatients() {
		PatientDao patientDao = new PatientDao();
		Integer[] patientIDs = patientDao.getAllPatients();

		// Multimap<String, Pair> multiMap = ArrayListMultimap.create();

		// Create a context JSON map containing prefixes and definitions
		String context = "\"@context\": {" + "\"description\": \"http://example.org/description\","
				+ "\"discrete\": \"http://example.org/discrete\"," + "\"location\": \"http://example.org/location\","
				+ "\"semantics\": \"http://example.org/semantics\"," + "\"unit\": \"http://example.org/unit\","
				+ "\"value\": \"http://example.org/value\"" + "}";


		String values = "[";

		for (Integer integer : patientIDs) {
			// HeadacheDao headacheDao = new HeadacheDao();
			// for (Headache headache :
			// headacheDao.getAllHeadachesForPatient(integer)) {
			// for (Pair pair : headache.getIntensityValues()) {
			// multiMap.put(integer+"", h)
			// }
			// }

			
			Random rand = new Random();
			// \"description\": \"Test\"
			values += "{\"id\":" + "\"" + integer + "\"," + "\"description\":"
					+ "\"Hoofdpijn Intensiteiten\" ," + "\"discrete\": \"true\"," + "\"location\": \""
					+ patientDao.getPatienFromId(integer + "").getFullName() + "\","
					+ "\"semantics\": \"null\"," + "\"unit\": \"\"," + "\"value\": \"" + (0 + rand.nextInt(11)) + ""
					+ "\"},";
			
			;

		}
		values+="]";
		String output = "{" + context + "," + values + "}";

		Object compact;
		try {
			compact = JsonLdProcessor.expand(JSON.parse(output));
			System.out.println(compact + "");
			return Response.ok(JsonUtils.toPrettyString(compact)).build();
		} catch (JsonLdError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(422).build();

	}

}
