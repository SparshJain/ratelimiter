package com.blueoptima.ratelimiter;

import static org.junit.Assert.assertEquals;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.blueoptima.ratelimiter.common.SpecificConfiguration;


/*
 * Here first we have to create a specific configuration for our user for a particular API.
 * 
 * eg.	for /developer -> user1 -> 10
 * 		for /organisation -> user2 -> 20
 * 		for /developer -> user1 -> 30
 * 		for /organisation -> user2 -> 40
 * 
 * 
 * TO RUN TEST AGAIN AND AGAIN, UUID IS USED RATHER THAN USER1 OR USER2. LIMITS REMAIN THE SAME AS ABOVE.
 * 
 * @author Sparsh Jain
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpecificTests extends AbstractTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	/*
	 * Setting limit of 10 for developer API for user1 and testing the same.
	 */
	@Test
	public void specificTest_withUser1_forDeveloperAPI() throws Exception {

		String uri = "/specificConfig";
		SpecificConfiguration specificConfiguration = new SpecificConfiguration();
		specificConfiguration.setControllerName("SpecificLimitController");
		specificConfiguration.setMethodName("developerAPI");
		specificConfiguration.setTimeUnit(TimeUnit.MINUTES.name());

		String userId = UUID.randomUUID().toString();
		
		specificConfiguration.setUserId(userId);
		specificConfiguration.setPermits(10);
		String inputJson = super.mapToJson(specificConfiguration);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		uri = "/specific/developers";

		// firing successful requests
		for (int i = 0; i < 11; i++) {
			mvcResult = mvc.perform(
					MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
					.andReturn();
			status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
			Thread.sleep(100);
		}

		// unsuccessful request test
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);

	}
	
	/*
	 * Setting limit of 20 for developer API for user2 and testing the same.
	 */
	@Test
	public void specificTest_withUser2_forDeveloperAPI() throws Exception {

		String uri = "/specificConfig";
		SpecificConfiguration specificConfiguration = new SpecificConfiguration();
		specificConfiguration.setControllerName("SpecificLimitController");
		specificConfiguration.setMethodName("developerAPI");
		specificConfiguration.setTimeUnit(TimeUnit.MINUTES.name());

		String userId = UUID.randomUUID().toString();
		
		specificConfiguration.setUserId(userId);
		specificConfiguration.setPermits(20);
		String inputJson = super.mapToJson(specificConfiguration);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		uri = "/specific/developers";

		// firing successful requests
		for (int i = 0; i < 21; i++) {
			mvcResult = mvc.perform(
					MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
					.andReturn();
			status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
			Thread.sleep(100);
		}

		// unsuccessful request test
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);

	}
	
	/*
	 * Setting limit of 30 for organisationAPI for user1 and testing the same.
	 */
	@Test
	public void specificTest_withUser1_forOrganisationAPI() throws Exception {

		String uri = "/specificConfig";
		SpecificConfiguration specificConfiguration = new SpecificConfiguration();
		specificConfiguration.setControllerName("SpecificLimitController");
		specificConfiguration.setMethodName("organisationAPI");
		specificConfiguration.setTimeUnit(TimeUnit.MINUTES.name());
		
		String userId = UUID.randomUUID().toString();
		
		specificConfiguration.setUserId(userId);
		specificConfiguration.setPermits(30);
		String inputJson = super.mapToJson(specificConfiguration);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		uri = "/specific/organisations";

		// firing successful requests
		for (int i = 0; i < 31; i++) {
			mvcResult = mvc.perform(
					MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
					.andReturn();
			status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
			Thread.sleep(100);
		}

		// unsuccessful request test
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);

	}
	
	/*
	 * Setting limit of 40 for organisationAPI for user2 and testing the same.
	 */
	@Test
	public void specificTest_withUser2_forOrganisationAPI() throws Exception {

		String uri = "/specificConfig";
		SpecificConfiguration specificConfiguration = new SpecificConfiguration();
		specificConfiguration.setControllerName("SpecificLimitController");
		specificConfiguration.setMethodName("organisationAPI");
		specificConfiguration.setTimeUnit(TimeUnit.MINUTES.name());

		String userId = UUID.randomUUID().toString();
		
		specificConfiguration.setUserId(userId);
		specificConfiguration.setPermits(40);
		String inputJson = super.mapToJson(specificConfiguration);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		uri = "/specific/organisations";

		// firing successful requests
		for (int i = 0; i < 41; i++) {
			mvcResult = mvc.perform(
					MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
					.andReturn();
			status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
			Thread.sleep(100);
		}

		// unsuccessful request test
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);

	}
	
	/*
	 * Setting limit of 20 for organisationAPI 
	 * and 20 for developerAPI
	 * for a random user and testing the same.
	 */
	@Test
	public void specificTest_withRandomUser_forBothAPIs() throws Exception {

		String uri = "/specificConfig";
		SpecificConfiguration specificConfiguration = new SpecificConfiguration();
		specificConfiguration.setControllerName("SpecificLimitController");
		specificConfiguration.setMethodName("organisationAPI");
		specificConfiguration.setTimeUnit(TimeUnit.MINUTES.name());

		String userId = UUID.randomUUID().toString();
		
		specificConfiguration.setUserId(userId);
		specificConfiguration.setPermits(20);
		String inputJson = super.mapToJson(specificConfiguration);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		
		specificConfiguration.setMethodName("developerAPI");
		specificConfiguration.setPermits(20);
		inputJson = super.mapToJson(specificConfiguration);
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		uri = "/specific/organisations";

		// firing successful requests
		for (int i = 0; i < 20; i++) {
			mvcResult = mvc.perform(
					MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
					.andReturn();
			status = mvcResult.getResponse().getStatus();
			assertEquals(200, status);
			Thread.sleep(100);
		}

		// unsuccessful request test
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);
		
		uri = "/specific/developers";

		// unsuccessful request test
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", userId).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);

	}
	
}
