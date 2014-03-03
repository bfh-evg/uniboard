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

import ch.bfh.uniboard.service.Component;
import ch.bfh.uniboard.service.Message;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.Response;
import ch.bfh.uniboard.service.Result;
import ch.bfh.uniboard.service.Service;
import ch.bfh.uniboard.service.UniBoardException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless
@Local(value = Service.class)
public class Component1Bean extends Component implements Service {

    @EJB(beanName = "TerminatingComponentBean")
    private Service successor;

    // References to local worker beans simulating parallel activities.
    // These could also be remote beans, however.
    @EJB
    private WorkerBean1 worker1;
    @EJB
    private WorkerBean2 worker2;
    @EJB
    private WorkerBean3 worker3;
    @EJB
    private WorkerBean4 worker4;

    private final List<Future<String>> futures = new ArrayList<>();

    @Override
    protected Service getSuccessor() {
        return this.successor;
    }

    @Override
    protected Result afterGet(Result result) throws UniBoardException {
        System.out.println("afterGet() called.");
        return result;
    }

    @Override
    protected Query beforeGet(Query query) throws UniBoardException {
        System.out.println("beforeGet() called.");
        return query;
    }

    @Override
    protected Response afterPost(Response response) throws UniBoardException {
        System.out.println("afterPost() called.");
        return response;
    }

    @Override
    protected Message beforePost(Message message) throws UniBoardException {
        System.out.println("beforePost() called.");

        // Simulating the parallel processing of four activities...
        // Call each worker in turn and memorize the Future object.
        futures.add(worker1.doWork("Starting w1..."));
        futures.add(worker2.doWork("Starting w2..."));
        futures.add(worker3.doWork("Starting w3..."));
        futures.add(worker4.doWork("Starting w4..."));

        // Each of the worker is running. ...
        long expectedMaxDuration = 1400; // in milliseconds
        long remainingMilliSecondsToWait = expectedMaxDuration;
        long timeAtBeginning = new Date().getTime();
        int i = 0; // Not important, for nicer logger output only
        for (Future<String> f : futures) {
            i++;
            if (remainingMilliSecondsToWait > 0) {
                try {
                    String w = f.get(remainingMilliSecondsToWait, TimeUnit.MILLISECONDS);
                    Logger.getLogger(Component1Bean.class.getName()).log(Level.INFO,
                        "Got answer in get(TIME), worker: {0}", i);
                    // Do something with answer 'w'. Then adjust 'remainingMilliSecondsToWait'
                    // and wait for remaining workers.
                    long timeNow = new Date().getTime();
                    remainingMilliSecondsToWait = expectedMaxDuration - (timeNow - timeAtBeginning);
                    Logger.getLogger(Component1Bean.class.getName()).log(Level.INFO,
                        "Remaining time in milli seconds: {0}", remainingMilliSecondsToWait);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Component1Bean.class.getName()).log(Level.SEVERE,
                        "Worker {0} interrupted", i);
                } catch (ExecutionException ex) {
                    Logger.getLogger(Component1Bean.class.getName()).log(Level.SEVERE,
                        "Worker {0} threw exception", i);
                } catch (TimeoutException ex) {
                    Logger.getLogger(Component1Bean.class.getName()).
                        log(Level.SEVERE, "Worker {0} did not finish in time", i);
                    // However, the remaining other workers should have done their tasks if good behaving.
                    remainingMilliSecondsToWait = 0;
                }
            } else {
                if (f.isDone()) {
                    String w;
                    try {
                        w = f.get();
                        Logger.getLogger(Component1Bean.class.getName()).log(Level.INFO,
                            "Got answer in get(), worker: {0}", i);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Component1Bean.class.getName()).log(Level.SEVERE,
                            "Worker {0} interrupted", i);
                    } catch (ExecutionException ex) {
                        Logger.getLogger(Component1Bean.class.getName()).
                            log(Level.SEVERE, "Worker {0} threw exception", i);
                    }
                } else {
                    Logger.getLogger(Component1Bean.class.getName()).
                        log(Level.SEVERE, "Worker {0} did not finish in time", i);
                }
            }
        }

        return message;
    }
}
