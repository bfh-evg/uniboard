/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import lu.uni.bft.interfaces.replica.BFTProtocolMessage;
import lu.uni.bft.interfaces.client.ClientRequest;
import lu.uni.bft.interfaces.replica.ReplicaMessagePublication;
import lu.uni.bft.interfaces.replica.ReplicatedService;
import lu.uni.bft.replica.util.IdentifiableQueueImpl;
import lu.uni.bft.replica.util.interfaces.ClientRequestQueue;
import lu.uni.bft.replica.util.interfaces.IdentifiableClientRequestQueue;
import lu.uni.bft.util.interfaces.Identity;

/**
 * The BFTReplicaCoreThread is the engine class that contains all the logic of 
 * the BFT agreement protocol. It assumes that all messages that it reads from 
 * the message buffers were previously checked for authenticity and integrity.
 * 
 * This class tries to provides a fair access to the service by implementing
 * a round robin schedule to access the messages contained in the clients and 
 * replicas message buffers.   
 * 
 * @author rui.joaquim@uni.lu
 */
public class BFTReplicaCoreThread implements Runnable{
    // Replica's state variables
    /** Replica's ID */
    protected final int ID;
    /** Flag to identify if this is the primary replica. */
    protected boolean isPrimary;
    /** Number of the current view. */
    protected long view;
    /** Current message sequence number in the view. */
    protected long sequenceNumber;
    
    
    // Other variables and constants
    
    /** The number of replica messages to be processed by client message */
    protected final int replicasMultiplier;
    /** The service replicated service. */
    protected final ReplicatedService service;
    /** The set of client request queues. */
    protected final ClientRequestQueue[] clients;
    /** The set of replica message queues. */
    protected final Queue<BFTProtocolMessage>[] replicas; 
    /** The service to send messages to other replicas. */
    protected final ReplicaMessagePublication replicaPublicationService;
    
    
    /**
     * Constructor
     * 
     * @param replicaID the ID number of the replica
     * 
     * @param service this object is responsible for the execution of the 
     *                agreed requests in the replicated service and also
     *                responsible to send results to the corresponding
     *                service clients.
     * 
     * @param clientRequestQueues is an array of queues containing the client 
     *                requests. 
     * 
     * @param replicaMessageQueues is an array of queues containing the replicas
     *                messages
     * 
     * @param replicaPublicationService is the object that is used to send 
     *                protocol messages to the other replicas. 
     * 
     * @param replicasMsgMultiplier The number of BFT protocol messages that 
     *                should be processed for each client request processed.
     * 
     * @throws IllegalArgumentException if at least one parameter is/or 
     *         contains null
     */
    public BFTReplicaCoreThread (int replicaID,
            ReplicatedService service, 
            IdentifiableClientRequestQueue[] clientRequestQueues, 
            IdentifiableQueueImpl<BFTProtocolMessage>[] replicaMessageQueues,
            ReplicaMessagePublication replicaPublicationService,
            int replicasMsgMultiplier)
            throws IllegalArgumentException {
        
        checkParameters(replicaID, service, clientRequestQueues, 
                replicaMessageQueues, replicaPublicationService);
        
               
        // No error detected: proceed with object initialization.
        this.replicasMultiplier = 
                (replicasMsgMultiplier < 1) ? 1 : replicasMsgMultiplier;
        this.ID = replicaID;
        this.service = service;
        this.replicaPublicationService = replicaPublicationService; 
        /* Create of a private copy of the cliant and replica queue arrays */
        this.clients = 
                Arrays.copyOf(clientRequestQueues, clientRequestQueues.length);
        this.replicas = 
                 Arrays.copyOf(replicaMessageQueues, replicaMessageQueues.length);
        
    }
    
    /**
     * This method checks the validity of the parameters received by the 
     * constructor. It checks for null elements and repetitions.
     *  
     * @param replicaID the replica ID.
     * @param service the replicated service.
     * @param clients the BFT protocol clients input queues.
     * @param replicas the BFT protocol replicas input queues.
     * @param replicaPublicationService the BFT protocol replica 
     *                                  publication service.
     * @throws IllegalArgumentException if an error is detected. 
     */
    private static void checkParameters(int replicaID,
            ReplicatedService service, 
            Identity[] clients, 
            Identity[] replicas,
            ReplicaMessagePublication replicaPublicationService)
            throws IllegalArgumentException{
    
        
        StringBuilder invalidParameter = new StringBuilder();
        
        // 1 - check nulls and repetitions in clients and replicas
        // 1.1 - check replicas
        Set<Integer> ids = new HashSet();
        ids.add(replicaID);
        verifyRepetionsAndNulls("Replica", invalidParameter, replicas, ids);
        
        // 1.2 - check clients
        ids.clear();
        verifyRepetionsAndNulls("Client", invalidParameter, clients, ids);
        
        
        // 2 - check other parameters for null values
        if(service == null){
            invalidParameter.append("service == null; ");
        }
        if(replicaPublicationService == null){
            invalidParameter.append("replicaPublicatioService == null; ");
        }
        
        // verify error ocurrence 
        if(invalidParameter.length() != 0){ 
            throw new IllegalArgumentException("Invalid parameters: " + 
                    invalidParameter.toString());
        }
    }

    
    /**
     * Verify the existence of ID repetitions or null elements in an array of
 Identitys.
     * 
     * @param elementType a description of the elements type.
     * @param errorMsg a stringBuilder to append possible error messages.
     * @param elements the array of elements to verify.
     * @param ids a set containing IDs that must not repeat. This method adds 
     *            to to the set the IDs of all not null elements in elements. 
     */
    private static void verifyRepetionsAndNulls(
            String elementType,
            StringBuilder errorMsg,
            Identity[] elements, 
            Set<Integer> ids) {
        
        for(Identity obj : elements){
            if(obj == null){
                errorMsg.append("Null value: ");
                errorMsg.append(elementType);
                errorMsg.append("; ");
            } else {
                if(! ids.add(obj.getID())){
                    errorMsg.append("repeated replica ID: ");
                    errorMsg.append(obj.getID());
                    errorMsg.append("; ");
                }
            }
        }
    }

    
    
    
    protected void init(){
        /*TODO*/
        
        /*replica state reset*/
        this.isPrimary = false;
        this.view = 0;
        this.sequenceNumber = 0;
        
    }
    
    
    @Override
    public void run() {
        // 1 - replica state initialization
        init();
        
        // 2 - BFT protocol
        while(true){
            // 2.1 - Process client requests if primary
            if(this.isPrimary){
                processClientMessages();
            }
            
            // 2.2 - Process BFT protocol messages from replicas
            for(int i=0; i<this.replicasMultiplier; i++){
                processReplicaMessages();           
            }
        }
    }

    
    /**
     * Method to process one round of incoming client requests. 
     * This method implements a round-robin processing policy to the requests
     * received from the different clients.
     */
    private void processClientMessages() {
        for(ClientRequestQueue client : this.clients){
            ClientRequest request = client.getNextRequest();
            if(request != null){
                processClientRequest(request);
            }
        }
    }

    /**
     * This method starts the BFT agreement protocol on a client request.
     * @param request the client request to be processed.
     */
    private void processClientRequest(ClientRequest request) {
        // 1 - add request to the in "process requests table" 
        
        // 2 - send pre-prepare message to all replicas
        // 2.1 - create pre-prepare message
        BFTProtocolMessage msg = null;
        
        // 2.2 - send message
        this.replicaPublicationService.sendAll(msg);
    }
    
    
    
    
    
    /**
     * Method to process the BFT protocol messages from the replicas.
     * This method implements a round-robin processing policy to the messages
     * received from the different replicas.
     */
    private void processReplicaMessages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    
    
}
