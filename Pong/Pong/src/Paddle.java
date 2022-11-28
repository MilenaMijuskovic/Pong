/*
 * Milena Mijuskovic
 * ICS4UE-01
 * Mr. Benum
 * Jan 6,2021
 * Class for handling the movements of a paddle
 */

import java.awt.Color;
import csta.ibm.pong.GameObject;

public class Paddle extends GameObject {
	//fields
	private int dy = 1; //variable for change in y direction of paddle
	private int bounces = 1; //collisions with a ball
	
	/**
	 * Allows paddles to move
	 */
	public void act() {
		
	}
	
	/**
	 * Moves paddle upwards
	 */
	public void moveUp() {
		setY(getY()-dy);
	}
	
	/**
	 * Moves paddle downwards
	 */
	public void moveDown() {
		setY(getY()+dy);
	}
	
	/**
	 * Speeds up paddle every 5 times it hits a ball
	 */
	public void speedUpPaddles() {
		bounces++;
		if(bounces%5==0)
    		dy++;
	}
	
	/**
	 * Resets paddle speed 
	 */
	public void resetPaddleSpeed() {
		//sets paddle attributes back to their defaults values
		bounces = 1;
		dy = 1;
	}
	
}
