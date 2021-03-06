# ARSnova

ARSnova is a modern approach to Audience Response Systems (ARS). It is released under the GPLv3 license, and is offered as a Software as a Service free of charge. Head over to [arsnova.eu](https://arsnova.eu/) to see it in action.

![ARSnova](src/site/resources/showcase.png)

ARSnova consists of two projects: the mobile client and the server. This repository contains the server code. You will find the client at thm-projects/arsnova-mobile. However, you do not need to download both repositories in order to get started.

## Getting Started

This is the main repository. Almost all dependencies (including the mobile client) are managed for you by Maven.

### Download

If you have no intention in contributing, you might want to consider downloading one of our pre-built WAR archives. You will find them in our [Maven repository](https://maven.mni.thm.de/content/repositories/snapshots/de/thm/arsnova/arsnova-war/2.0.0-SNAPSHOT/), but please do note that we are not officially offering these archives.

### Building

ARSnova consists of two main projects: arsnova-war (this repository) and arsnova-mobile. By building arsnova-war, you will automatically download the mobile client. If you do not plan to work on the client, you won't need to build it separately.

Because all dependencies are handled by Maven, a complete build is done with:

	mvn install

*Note:* Java 1.7 JDK is required and must be installed prior to building ARSnova.

### Development

You need three things to get started developing ARSnova:

1. the configuration file,
2. a CouchDB database including several view documents,
3. and a development server.

We will cover all three in the following sections.

#### Configuration

You will need to do some configuration work upfront: add a new directory "arsnova" in `/etc`, and create a copy of [arsnova.properties.example](src/main/webapp/arsnova.properties.example) named `arsnova.properties` in this directory. Then change the settings to match your environment, e.g. you might want to change the URLs.

Also, don't forget to change all properties starting with `couchdb`, if you do not want to use our defaults. The properties are used in the next section.

*A note to Windows users*: our settings are based on Linux and Mac environments. We do not officially support Windows, so you have to do some extra steps. The property file's path is hard coded in [spring-main.xml](src/main/webapp/WEB-INF/spring/spring-main.xml) and in the "Setup Tool" (see next section). You want to change the paths to make them match your environment.

#### Database

We provide a Python script that will set up all database essentials. This "Setup Tool" is located at [thm-projects/setuptool](https://github.com/thm-projects/setuptool). Make sure you have configured your database credentials inside the ARSnova configuration file (see previous section): you will need to have the entries `couchdb.username` and `couchdb.password`.

To set up the database, run:

	python tool.py

This will create the database along with all required view documents. Note that this script requires Python 2 and will not run with Python 3.

#### Developer Mode

The easiest way to deploy ARSnova is via Jetty:

	mvn jetty:run -Dmobile.path=

This will work out of the box. ARSnova will be located at <http://localhost:8080/>.

If you're wondering what that `-Dmobile.path=` thing is doing, this is a special override property for Jetty. By default, this property points to a local version of ARSnova mobile located at `../arsnova-mobile` &mdash; relative to the location of this project. If you happen to have downloaded ARSnova mobile to this location, you may skip the override, and just use:

	mvn jetty:run

## Production Use

If you intend to use ARSnova in productive environments, you will have to do some additional configuration work.

### Server

In order to build up a full featured server installation containing ARSnova and CouchDB you have to install at least the following services:

 * Apache Tomcat 7.0.29 (or newer)
 * Apache Webserver 2.2 or newer with builtin modules `mod_proxy`, `mod_proxy_ajp` and `mod_proxy_http`
 * Apache CouchDB

Make sure all services are installed. Next step is to configure the Apache Webserver. Find the configuration file or create a new one for use with a virtual host. This depends on your needs. At least you should have a configuration containing these settings:

	<Location />
		ProxyPass ajp://127.0.0.1:8009/
		ProxyPassReverse ajp://127.0.0.1:8009/
	</Location>

All requests will be sent to your Apache Tomcat servlet container, using AJP running on port 8009.

To enable the required Apache Webserver modules simply type:

	# a2enmod proxy
	# a2enmod proxy_ajp
	# a2enmod proxy_http

The configuration is ready for development usage. Finally, you should (re)start all services. ARSnova is now listening on HTTP port 80.

### Session Persistence

Look for your Tomcat configuration directory and change the file "context.xml" to match this example:

	<Context>
		<Manager pathname="/path/to/tomcat/sessions/arsnova.ser"/>
	</Context>

This will enable session persistence across restarts as described [here](http://tomcat.apache.org/tomcat-7.0-doc/config/manager.html#Special_Features).

### HTTPS

To protect requests and responses you should use HTTPS and configure your Apache Webserver installation to redirect all traffic according to this
 [example](http://wiki.apache.org/httpd/RedirectSSL).

Finally you should (re)start all services. ARSnova is now listening on HTTP port 80 and 443.

### Securing Your Web Socket Connection

To provide SSL websocket encryption, you have to provide the servers SSL key and certificate in a Java keystore. The following steps will guide you through this process.

Use your webserver certificate, private key and certificate chain to create a PKCS12 keystore:

	openssl pkcs12 -export -in <servercert>.crt \
					-inkey <serverkey>.key \
	               	-out keystore.p12 -name 1 \
    	           	-certfile <your_cert_chain_file>

You will be asked for a password for your PKCS12 keystore. This password must be used for importing this keystore into your java keystore. The import can be done using this command:

	keytool -importkeystore \
    	    -deststorepass <your_java_keystore_password> \
    	    -destkeypass <your_java_keystore_password> \
    	    -destkeystore arsnova.jks \
        	-srckeystore keystore.p12 \
        	-srcstoretype PKCS12 \
        	-srcstorepass <your_pkcs12_keystore_password> \
        	-alias 1

Be sure to provide the correct certificate and key file names and to use the correct passwords for your keystore.

The last step is to find your ARSnova configuration file (see step "Configuration" above), setup the location of your Java keystore and its password.

	security.ssl=true
	security.keystore=<your keystore location>
	security.storepass=<your keystore password>

## Credits

ARSnova is powered by Technische Hochschule Mittelhessen - University of Applied Sciences.
