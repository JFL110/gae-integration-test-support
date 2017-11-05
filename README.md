## GAE Integration Test Support

[![Build Status](https://travis-ci.org/JFL110/gae-integration-test-support.svg?branch=master)](https://travis-ci.org/JFL110/gae-integration-test-support) [![Coverage Status](https://coveralls.io/repos/github/JFL110/gae-integration-test-support/badge.svg?branch=master)](https://coveralls.io/github/JFL110/gae-integration-test-support?branch=master)[![A weather badge for the home of this repo](https://grrbadge.com/img/location-weather/London_UK.svg)](https://github.com/JFL110/grrbadge)

Testing utilities for java web apps that use:
- Guice servlet
- Google App Engine Datastore

Provides facilities to:
- Launch an embedded Jetty server which is configured via a ServletContextListener.
- Launch a testing datastore service with multithreading support

An example usage is provided in the [tests](https://github.com/JFL110/gae-integration-test-support/blob/master/src/test/java/org/jfl110/testing/utils/TestIntegrationTesting.java).
