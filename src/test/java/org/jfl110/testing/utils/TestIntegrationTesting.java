package org.jfl110.testing.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jfl110.quickstart.EmbeddedJetty;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

public class TestIntegrationTesting {
	
	@ClassRule
	public static final DatastoreRule datastoreRule = new DatastoreRule();
	public static final EmbeddedJetty server = EmbeddedJetty.embeddedJetty().withContextListener(new TestingApp()).build();
	
	private final Client client = ClientBuilder.newClient();

	@BeforeClass
	public static void beforeClass() throws Exception {
		server.start();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		server.stop();
	}
	
	@Test
	public void testPlainTextGet(){
		String response = 
				client.target(server.getBaseUri())
				.path("/read-text")
				.request(MediaType.TEXT_HTML)
				.get()
				.readEntity(String.class);
		
		assertNotNull(response);
		assertEquals("text-response",response);
	}
	
	
	@Test 
	public void testDatastoreWriteRead(){
		String writeResponse = 
				client.target(server.getBaseUri())
				.path("/insert-entities")
				.request(MediaType.TEXT_HTML)
				.post(Entity.text(""))
				.readEntity(String.class);
		
		assertNotNull(writeResponse);
		assertEquals("entities-saved",writeResponse);
		
		ClientEntityListResponse readResponse = 
				client.target(server.getBaseUri())
				.path("/read-entities")
				.request(MediaType.APPLICATION_JSON)
				.get()
				.readEntity(ClientEntityListResponse.class);

		assertNotNull(readResponse);
		assertNotNull(readResponse.entities);
		assertEquals(3,readResponse.entities.size());
		
		assertEquals("entity-one",readResponse.entities.get(0).name);
		assertEquals("entity-two",readResponse.entities.get(1).name);
		assertEquals("entity-three",readResponse.entities.get(2).name);
	}
	
	
	static class ClientEntityListResponse{
		public List<ClientEntity> entities;
	}
	
	
	static class ClientEntity{
		public long id;
		public String name;
	}
	
}
