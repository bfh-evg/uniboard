/*
 * Copyright (c) 2014 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.clientlib;

/**
 * Enum listing the names of the alpha and beta attributes of UniBoard
 * @author Philémon von Bergen
 */
public enum UniBoardAttributesName {

    SECTION("section"),
    GROUP("group"),
    SIGNATURE("signature"),
    PUBLIC_KEY("publickey"),
    BOARD_SIGNATURE("boardSignature"),
    RANK("rank"),
    TIMESTAMP("timestamp");
    
    private final String name;

    private UniBoardAttributesName(String name) {
	this.name = name;
    }
    
    public String getName(){
	return name;
    }
}
