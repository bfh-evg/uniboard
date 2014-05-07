package ch.bfh.uniboard.z.demo.app.client2;

import ch.bfh.z.demo.access.DemoAccessRemote;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Hello world!
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("Starting Z Demo Application Client 1...");

		DemoAccessRemote svc = lookupService();
		svc.post(new byte[1], null, null);

		System.out.println("Z Demo Application Client 1 finished.");
	}

	private static DemoAccessRemote lookupService() throws Exception {
		Properties props = System.getProperties();

		props.put("javax.net.ssl.trustStore", "/keystore.jks");
		props.put("javax.net.ssl.trustStorePassword", "changeit");

		Properties p = new Properties();
		p.put("org.omg.CORBA.ORBInitialHost", "ziu");
		p.put("org.omg.CORBA.ORBInitialPort", "3700");
		p.put(Context.SECURITY_PROTOCOL, "ssl");
		p.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
		p.put(Context.URL_PKG_PREFIXES, "com.sun.enterprise.naming");
		p.put(Context.STATE_FACTORIES, "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");

		Context ctx = new InitialContext(p);
		DemoAccessRemote svc = (DemoAccessRemote) ctx.lookup("java:global/z-demo/z-demo-access-1.0-SNAPSHOT/DemoAccessBean");
		return svc;
	}
}
