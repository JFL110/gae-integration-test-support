package org.jfl110.testing.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.junit.ClassRule;
import org.junit.Test;

public class TestIntegrationTesting {

	@ClassRule
	public static final DatastoreRule	datastoreRule	= new DatastoreRule();
	@ClassRule
	public static final EmbeddedJetty	server				= EmbeddedJetty.embeddedJetty()
																										.withContextListener(new TestingApp())
																										.withResourceBasePath("/src/test/").build();

	private final Client							client				= ClientBuilder.newClient();

	@Test
	public void testPlainTextGet() {
		String response = client
				.target(server.getBaseUri())
				.path("/read-text")
				.request(MediaType.TEXT_HTML).get()
				.readEntity(String.class);

		assertNotNull(response);
		assertEquals("text-response", response);
	}

	@Test
	public void testGetStaticFile() {
		String response = client.target(server.getBaseUri())
				.path("/static-file.txt")
				.request(MediaType.TEXT_HTML).get()
				.readEntity(String.class);

		assertNotNull(response);
		assertEquals("Some static file text", response);
	}

	@Test
	public void testDatastoreWriteRead() {
		String writeResponse = client.target(server.getBaseUri())
				.path("/insert-entities")
				.request(MediaType.TEXT_HTML)
				.post(Entity.text(""))
				.readEntity(String.class);

		assertNotNull(writeResponse);
		assertEquals("entities-saved", writeResponse);

		ClientEntityListResponse readResponse = client.target(server.getBaseUri())
				.path("/read-entities")
				.request(MediaType.APPLICATION_JSON).get()
				.readEntity(ClientEntityListResponse.class);

		assertNotNull(readResponse);
		assertNotNull(readResponse.entities);
		assertEquals(3, readResponse.entities.size());

		assertEquals("entity-one", readResponse.entities.get(0).name);
		assertEquals("entity-two", readResponse.entities.get(1).name);
		assertEquals("entity-three", readResponse.entities.get(2).name);
	}


	static class ClientEntityListResponse {
		public List<ClientEntity> entities;
	}


	static class ClientEntity {
		public long		id;
		public String	name;
	}
}