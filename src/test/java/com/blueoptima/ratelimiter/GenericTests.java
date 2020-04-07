package com.blueoptima.ratelimiter;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GenericTests extends AbstractTest {

	@Override
	@Before
	public void setUp() {
		super.setUp();
	}

	/*
	 * For a random user for which no configuration is maintained, hence the defalt
	 * conf of 2 permits per minute.
	 */
	@Test
	public void genericTest_withRandomUser() throws Exception {
		String uri = "/generic/developers";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "sparsh").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "sparsh").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "sparsh").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);

	}

	/*
	 * User for which configuration is maintained in application.yml for number of
	 * permits as 5
	 * So, 6th request wiil throw 429 status code.
	 */
	@Test
	public void genericTest_withUser1_forDevelopersAPI() throws Exception {
		String uri = "/generic/developers";
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "user1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		int status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "user1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "user1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "user1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);
		
		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "user1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(200, status);

		mvcResult = mvc.perform(
				MockMvcRequestBuilders.get(uri).header("userid", "user1").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		status = mvcResult.getResponse().getStatus();
		assertEquals(429, status);

	}
}
