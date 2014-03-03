/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniVote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.z.ejb.component1;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless
@LocalBean
public class WorkerBean4 {
    @Resource
    private SessionContext ctx;

    @Asynchronous
    public Future<String> doWork(String what) {
        // Do some work...
        try {
            Logger.getLogger(WorkerBean4.class.getName()).log(Level.INFO, "Worker4 working ...");
            Thread.sleep((int) (Math.random() * 2000)); // 0 <= value < 2000
            Logger.getLogger(WorkerBean4.class.getName()).log(Level.INFO, "Worker4 done.");
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkerBean4.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Check if client is still interested in the answer...
        if (ctx.wasCancelCalled()) {
            // clean up
            return new AsyncResult<>("'Cancel' for '" + what + "' called by client");
        } else {
            // Do more work...
        }
        return new AsyncResult<>("Worker4 for '" + what + "'");
    }
}
