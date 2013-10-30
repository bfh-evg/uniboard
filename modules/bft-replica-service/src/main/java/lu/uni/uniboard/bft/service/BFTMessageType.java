/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.uniboard.bft.service;

import java.io.Serializable;

/**
 *
 * @author rui.joaquim
 */
public class BFTMessageType implements Serializable{
    private static final long serialVersionUID = 1L;
    /*
     * Message Type Definitions
     */
    public static final byte CLIENT_REQUEST = 0x00;
    public static final byte REPLICA_PRE_PREPARE = 0x10;
    public static final byte REPLICA_PREPARE = 0x11;
    public static final byte REPLICA_COMMIT = 0x12;
    public static final byte REPLICA_REPLY = 0x13;
    
}
