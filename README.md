## Maven Repository for GAE Integration Test Support

Example Gradle usage:
~~~~
repositories { 
  maven {
    url 'https://github.com/JFL110/gae-integration-test-support/raw/maven-repo'
  }
}

dependencies {
  compile group: 'org.jfl110', name: 'gae-integration-test-support', version:'1.0.0'
}
~~~~

Snapshot versions may change at any time.
