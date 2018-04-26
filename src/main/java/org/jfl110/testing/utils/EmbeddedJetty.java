package org.jfl110.testing.utils;

import java.net.URI;
import java.util.EnumSet;
import java.util.Optional;

import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.google.common.collect.ImmutableMap;
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
	public static class EmbeddedJettyBuilder {

		private final ImmutableMap.Builder<String, HttpServlet> servlets = ImmutableMap.builder();
		private Optional<ServletContextListener> contextListener = Optional.empty();
		private int port = 8080;
		private String resourceBasePath = "src/main/webapp";
		private String contextPath = "/";


		private EmbeddedJettyBuilder() {
		}


		/**
		 * Sets the port µnbcv0
		 */
		public EmbeddedJettyBuilder withPort(int port) {
			this.port = port;
			return this;
		}


		/**
		 * Sets the ServletContextListener
		 */
		public EmbeddedJettyBuilder withContextPath(String contextPath) {
			this.contextPath = contextPath;
			return this;
		}
		
		
		/**
		 * Add a servlet
		 */
		public EmbeddedJettyBuilder addServlet(String pathSpec, HttpServlet servlet) {
			servlets.put(pathSpec, servlet);
			return this;
		}
		
		
		/**
		 * Add a servlet that serves every path.
		 */
		public EmbeddedJettyBuilder addServletAll( HttpServlet servlet) {
			return addServlet("/.*", servlet);
		}


		/**
		 * Sets the ServletContextListener
		 */
		public EmbeddedJettyBuilder withContextListener(ServletContextListener contextListener) {
			this.contextListener = Optional.of(contextListener);
			return this;
		}


		/**
		 * Sets the path to be used for static resources and resources accessed via the
		 * ServletContext.
		 */
		public EmbeddedJettyBuilder withResourceBasePath(String resourceBasePath) {
			this.resourceBasePath = resourceBasePath;
			return this;
		}


		/**
		 * Builds the server.
		 */
		public EmbeddedJetty build() {
			return new EmbeddedJetty(port, resourceBasePath, contextListener, contextPath.startsWith("/") ? contextPath : ("/" + contextPath), servlets.build());
		}
	}

	private final ImmutableMap<String, HttpServlet> servlets;
	private final int port;
	private final String resourceBasePath;
	private final String contextPath;
	private final Optional<ServletContextListener> contextListener;

	private Server server;


	/**
	 * Start the EmbeddedJetty builder.
	 */
	public static EmbeddedJettyBuilder embeddedJetty() {
		return new EmbeddedJettyBuilder();
	}


	private EmbeddedJetty(int port, String resourceBasePath, Optional<ServletContextListener> contextListener, String contextPath, ImmutableMap<String, HttpServlet> servlets) {
		this.port = port;
		this.resourceBasePath = resourceBasePath;
		this.contextListener = contextListener;
		this.contextPath = contextPath;
		this.servlets = servlets;
	}


	/**
	 * Starts the server.
	 */
	public void start() throws Exception {

		server = new Server(port);
		
		ServletContextHandler context = new ServletContextHandler();
		context.setResourceBase(resourceBasePath);
		if(contextListener.isPresent()){
			context.addEventListener(contextListener.get());
			context.addFilter(GuiceFilter.class, "/*", EnumSet.of(javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.ASYNC));
		}
		
		servlets.forEach((path, servlet) -> {
			context.addServlet(new ServletHolder(servlet), path);
		});
		context.setContextPath(contextPath);
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase((resourceBasePath == null || resourceBasePath.startsWith("/") ? "." : "") + resourceBasePath);
		

		HandlerList handlerList = new HandlerList();
		handlerList.setHandlers(new Handler[] { resourceHandler, context });
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
	
	
	/**
	 * TODO
	 */
	public String getPath(String path) {
		return server.getURI() + path;
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
