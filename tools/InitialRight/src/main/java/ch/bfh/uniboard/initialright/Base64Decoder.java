package ch.bfh.uniboard.initialright;

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

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 *
 * @author Philémon von Bergen
 */
public class Base64Decoder {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
	    
	    String encodedMessage = "eyJlbmNyeXB0ZWRWb3RlIjp7ImZpcnN0dmFsdWUiOiI5MDg1Njk3MDEwODg3OTIzMDM2MTc3NjMyMDg3NTM3NTUwNjY2MjQ1MTI4OTc0NzIxOTE3NDQzMzU1ODIwMDMzMDQyNjg4MzcwNjg4MzQ4NzM2MTMwNTY1MjA5Nzg1NTU3NjIzNjcwMjU3NDYzNDMwMTQyNDMwOTI1Njg4MzMyNzM0NTY1NjA4NDI4NjA4MTU4MDc3MzY0Mjk3ODYyNzA1NjQwODEwNTMxNDUwMTIzNzgwMjU1NDU2NjQwMDIzNDQ0NjQ0MjMyOTkwMzY3NDQ5MTU2MDU1MDIwMzk5MDMwNjg1ODUyMzQ3MTc0MzExMDYwNDgyMDg2MDAwMTQ4MjMzNjA2NjkyMDI3NDg1NTY4MDQ2MjAzNDM2MjM2OTY0OTk0NDI0Mjc4MTMwMzE3MTU2ODgzMyIsInNlY29uZHZhbHVlIjoiMTI0NTMwODE0Njk3ODkzMjM4Nzk5MjgyMDYyMjA3MjYwMzI0NDE1OTgyNjM0MDgzMTIzNjQ4MDQ1OTUyMDY1OTg2MDgwMzE4MDE3MDk5NjI4MjAwOTExNzUxNzc0NzQwODcyMDIzNjE1NTkzMTAyMzYyNDkyOTg1NzMwNjMzNDQ1NzkxOTA1NDE1NTQ3NjI1Nzk2NDc4MjYyMzI1ODM4MDQwNDg4ODEyNDYwOTg1NDQzMjM2NTQzMDA3ODM5NjE5OTc1NTk5MjI5MjY4NjMxNDk2ODkxMjcxODE5ODk3NzY2NzQxODMzOTEwOTQ0MDkzNjcxMzE1NDA2MDgzNzg1NjYxMTI3NTQ1MzYxNzI3MDE0Nzc0MTUwOTcxODU5NzE4NDUzMDE1MTg2MjgzODc0OTQ0OTc4In0sInByb29mIjp7ImNvbW1pdG1lbnQiOiIyMjM4NTI4NDc1MzQ0MDM0NjY3NDExODI0NzYzMjg0NjE3MDQwODMxODU3MjAyMDk4MjQxNzUwNTQ3MjY4MDIxMzczOTg2OTI5NzE4MDQwMjg2MTQzNjYwMDMzNzk5ODA1MjQ4MTg1MTg0MDU0NzM2NzgyMjAxMTEzNTQxNTIwOTc2NDY3MzQwMTY2MjM2OTcwOTc1MDA4MzIzMTgxNjQ4MTc3OTMwNDM4MzY2NzU0NzMzMjY4MDU4MjIwMzI2MjcwNjU1OTU3MzM5MjkwNTMzMjQwNzE5MTk3Njk0MTYxNjc0ODgyODA2OTY4MzQwMTkwNzEzMDIwMzM1MTY2MTYwMjUwOTMzNjUwMzgzNDA1ODE3MTkyMDQxNjgzMzYxMDIxNDQ4NTA3OTU1MzQ5NTAwMjI5NiIsInJlc3BvbnNlIjoiMTMwMTk1ODYwNDkwMDkxNDA5MTY3NjkyNjEyOTc5NjUzMDY5NTIxMDA4NDI4NTM5NTU5NDQwODEwODk0ODcwMzg5ODE0NTA1MTA0NjI1NzE5NTQ3MzkzMTYwMzc3NDg5MDQwODgyNTYzOTQyMjE5MDcyMjYwNzIxMTgzNTQxNTg1ODA3NzcwMDI2MzM1NTczNzA3NTQzMDEwNTYwMzUzNDgxOTI0MDU0ODUzNDUzNTk1MzY1NTQyNTcxNjY2NTA0NTMzNDcwNDczNTE0NjEwNDE0Mjg1NTgzNjQwNDA0MTM2NTQ2Mzg4NjEwODA4MTQwMTcyMDY4Njk0NjQxMzc5ODIwMzUxNTcyNDI2MTE0NzQwNzE3MjYwMDgwMjk4MDIzNzQwODM2NTcxOTkwMjQ5Mjc5NTIifX0=";
	    System.out.println(new String(Base64.decode(encodedMessage)));
	}
}