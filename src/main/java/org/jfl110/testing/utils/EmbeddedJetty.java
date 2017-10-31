package org.jfl110.testing.utils;

import java.net.URI;
import java.util.EnumSet;

import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.inject.servlet.GuiceFilter;

/**
 * An embedded Jetty server.
 * 
 * @author JFL110
 */
public class EmbeddedJetty implements TestRule {
	
	/**
	 * Builder to construct the EmbeddedJetty server.
	 *
	 * @author JFL110
	 */
	public static class EmbeddedJettyBuilder{
		
		private ServletContextListener contextListener;
		private final int port = 8080;
		private final String resourceBasePath = "src/main/webapp";
		
		private EmbeddedJettyBuilder(){}
		
		public EmbeddedJettyBuilder withContextListener(ServletContextListener contextListener){
			this.contextListener = contextListener;
			return this;
		}
		
		public EmbeddedJetty build(){
			return new EmbeddedJetty(port,resourceBasePath,contextListener);
		}
		
	}

	private final int port;
	private final String resourceBasePath;
	private final ServletContextListener contextListener;

	private Server server;

	public static EmbeddedJettyBuilder embeddedJetty() {
		return new EmbeddedJettyBuilder();
	}

	private EmbeddedJetty(int port,String resourceBasePath,ServletContextListener contextListener) {
		
		if(contextListener == null){
			throw new IllegalArgumentException("No ServletContextListener specified");
		}
		
		this.port = port;
		this.resourceBasePath = resourceBasePath;
		this.contextListener = contextListener;
	}

	
	/**
	 * Starts the server.
	 */
	public void start() throws Exception {

		server = new Server(port);
		
		ServletContextHandler context = new ServletContextHandler();
		context.setResourceBase(resourceBasePath);       
		context.addEventListener(contextListener);
		context.addFilter(GuiceFilter.class, "/*",EnumSet.of(javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.ASYNC));
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase(".");
		
		HandlerList handlerList =  new HandlerList();
		handlerList.addHandler(context);
		handlerList.addHandler(resourceHandler);
		server.setHandler(handlerList);
		server.start();
	}

	
	/**
	 * Stops the server.
	 */
	public void stop() throws Exception {
		server.stop();
	}

	
	/**
	 * Gets the base URI to use for requests to this server.
	 */
	public URI getBaseUri() {
		return server.getURI();
	}

	
	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				try {
					start();
					base.evaluate();
				} finally {
					stop();
				}
			}
		};
	}
}
