/*
 * Milena Mijuskovic
 * ICS4UE-01
 * Mr. Benum
 * Jan 6,2021
 * Class for handling the movements of a ball
 */

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import csta.ibm.pong.GameObject;

public class Ball extends GameObject{
        //fields
        private int dx = 1; //variable for change in x direction of ball
        private int dy = 1; //variable for change in y direction of ball
        private int bounces = 0; //collisions a ball makes
        private int speed = 1; //speed of a ball
        private File boink = new File("boink.wav"); //boink audio file
    	private Clip clip; //clip to play audio file
    	
        /**
         * Moves ball across screen depending on the values of dx and dy
         */
        public void act() {
                setX(getX()+dx);
                setY(getY()+dy); 
        }
        
        /**
         * Bounces the ball in the x direction
         * @param x
         */
        public void bounceXDirection(int x) {
        	playBoink();
            dx = speed*x; //changes direction of the ball depending on x
        }
        
        /**
         * Bounces the ball in the y direction
         * @param y
         */
        public void bounceYDirection(int y) {
        	dy = speed*y; //changes direction of the ball depending on y
        }
        
        /**
         * Plays the boink.wav audio file
         */
        private void playBoink(){
        	try {
				clip = AudioSystem.getClip();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	try {
				clip.open(AudioSystem.getAudioInputStream(boink));
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	clip.start();
        }
        
        /**
         * Increases the speed of the ball every 5 times it hits a paddle
         */
        public void speedUpBall() {
        	bounces++;
        	if(bounces%5==0) {
        		dx /= speed; //returns magnitude of dx and dy back to 1 before being multiplied by the new speed
            	dy /= speed;
        		speed++;
        		dx *= speed;
            	dy *= speed;
        	}
        }
        
        /**
         * Resets the speed of the ball
         */
        public void resetBallSpeed() {
        	//sets ball attributes back to their default values
        	bounces = 0;
        	speed = 1;
        	dx = 1;
        	dy = 1;
        }
        
        /**
         * Mutator method that sets the speed of the ball
         * @param s
         */
        public void setSpeed(int s)  {
        	speed = s;
        }
        
        /**
         * Accessor method that returns the speed of the ball
         * @return speed
         */
        public int getSpeed() {
        	return speed;
        }
        
        /**
         * Accessor method that returns the direction of the current ball in the X direction
         * @return dx/speed
         */
        public int getXDirection() {
        	return dx/speed; //makes sure to return only the direction, not the magnitude
        }
        
        /**
         * Accessor method that returns the direction of the current ball in the Y direction
         * @return dy/speed
         */
        public int getYDirection() {
        	return dy/speed; //makes sure to return only the direction, not the magnitude
        }
}

