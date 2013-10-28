package ch.bfh.uniboard.z.demo.app.client1;

import ch.bfh.uniboard.service.Service;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Enterprise Application Client main class.
 *
 */
public class Main {
    //@EJB
    //private static Service access;

    public static void main( String[] args ) throws Exception {
        System.out.println( "Starting Z Demo Application Client 1..." );

        Service svc = lookupService();
        //access.post(null);

        System.out.println( "Z Demo Application Client 1 finished." );
    }

    private static Service lookupService() throws Exception {
        Context ctx = new InitialContext();
        Service svc = (Service) ctx.lookup("java:global/z-demo/z-demo-access-1.0-SNAPSHOT/DemoAccessBean");
        return svc;
    }
}
