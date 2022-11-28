/*
 * Milena Mijuskovic
 * ICS4UE-01
 * Mr.Benum
 * Jan 6,2021
 * Class for handling a pong game: how the ball interacts with the paddles, how points are accumulated and displayed,
 * and how the game ends with a winner displayed
 */

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import csta.ibm.pong.Game;

public class Pong extends Game {
        //fields
        private Ball ball; //ball
        private final int BALL_SIZE = 15; //size of the ball
        private Ball specialBall; //special ball
        private int ballType; //type of special ball
        private boolean specialBallOnScreen = false, ballUsed = false; //if there is a special ball on the screen, and if it has been used (hit by a paddle)
        private int ballUsedOn = 0; //if the ball hit the paddle, ballUsedOn indicates which paddle is going to receive the power up
        private Paddle p1, p2; //paddles
        private JLabel score1, score2; //score labels
        private int wHeight, wWidth; // window dimensions
        private int p1Height, p1Width; //dimensions for paddle 1
        private int p2Height, p2Width; //dimensions for paddle 2
        private static String winScore; //score needed to win a game
        private int p1Score, p2Score; //scores of players
        private int red = 255 , green = 0, blue = 0; //colours
        private File yay = new File("yay.wav"); //yay audio file
		private Clip clip; //clip to play audio file
    	
        /**
         * Code before the game begins, sets up objects on the screen
         */
        public void setup() {
                
                //Gets dimensions of window
                wHeight = getFieldHeight();
                wWidth = getFieldWidth();
                
                //Gets dimensions of paddles
                p1Height = wHeight/4;
                p1Width = wWidth/20;
                p2Height = wHeight/4;
                p2Width = wWidth/20;
                
                //Sets up ball to be on screen
                ball = new Ball();
                ball.setSize(BALL_SIZE, BALL_SIZE); //sets size
                ball.setX(wWidth/2);  //sets location
                ball.setY(wHeight/2);
                ball.setColor(Color.BLACK); //sets colour
                add(ball);
                
                //Sets up paddles to be on screen
                p1 = new Paddle();
                p1.setSize(p1Width, p1Height); //sets size
                p1.setY(3*wHeight/8); //sets location
                p1.setColor(Color.BLACK); //sets colour
                add(p1);
                p2 = new Paddle();
                p2.setSize(p2Width, p2Height); //sets size
                p2.setX(wWidth-p2Width); //sets location
                p2.setY(3*wHeight/8);
                p2.setColor(Color.BLACK); //sets colour
                add(p2);
                
                //sets both players to 0 points
                p1Score = 0;
                p2Score = 0;
                
                //sets up scores on screen
                score1 = new JLabel();
                score1.setFont(new Font("Comic Sans", Font.BOLD, 20)); //creates and sets font
                score1.setText("" + p1Score); //sets text
                score1.setForeground(Color.BLACK); //sets colour
                score1.setBounds(wWidth/2-15, 0, 30, 30); //sets size and location of the textbox
                add(score1);
                score2 = new JLabel();
                score2.setFont(new Font("Comic Sans", Font.BOLD, 20)); //creates and sets font
                score2.setText("" + p2Score); //sets text
                score2.setForeground(Color.BLACK); //sets colour
                score2.setBounds(wWidth/2+15, 0, 30, 30); //sets size and location of the textbox
                add(score2);
                
        }
        
        /**
         * Code that is repeated during the game, responsible for objects behaviour
         */
        @Override
        public void act() {
        		setDelay(5); //sets slight delay so that the game is not too fast
        		changeBackgroundColor(); //changes background colour
        		resize(); //checks if window has been resized and makes correct changes
        		
                // Handles user input, moves paddles based on which key is pressed and will not let paddle go out of frame
                if(ZKeyPressed()) {
                        if(p1.getY() > 0)
                                p1.moveUp();
                }
                else if(XKeyPressed()) {
                        if(p1.getY() <= wHeight-p1Height)
                                p1.moveDown();
                }
                if(NKeyPressed()) {
                        if(p2.getY() > 0)
                                p2.moveUp();
                }
                else if(MKeyPressed()) {
                        if(p2.getY() <= wHeight-p2Height)
                                p2.moveDown();
                }
                
                //Handle collisions with paddles
                if(ball.collides(p1)) {
                        if(ball.getX() + ball.getSpeed() < p1Width) { //catches the error if the ball hit the side of the paddle
                        	ball.bounceXDirection(-1); //ball keeps bouncing towards the left (negative X direction)
                        	if(ball.getY() <= p1.getY())
                        		ball.bounceYDirection(-1); //if hit top of the paddle, ball bounces up in the negative Y direction 
                        	else
                        		ball.bounceYDirection(1); //if hit bottom of the paddle, ball bounces down in the positive Y direction
                        }
                        else { //ball was hit normally on the paddle
                        	ball.bounceXDirection(1); //passes 1 because the ball needs to bounce back towards the right (positive X direction)
                        	specialBall(); //creates a possible special ball
                        	if(!ballUsed) 
                        		ballUsedOn = 2; //if the special ball was created and not used, this means it must be used on paddle 2 since this ball came from paddle 1
                        }
                }
                else if(ball.collides(p2)) {
                	if(ball.getX()+BALL_SIZE> p2.getX()+ball.getSpeed()) { //catches the error if the ball hit the side of the paddle in the same way it was handled if it collided with p1
                		ball.bounceXDirection(1); //unlike p1, the ball will keep bouncing towards the right (positive X direction)
                		if(ball.getY() <= p2.getY())
                			ball.bounceYDirection(-1);
                		else
                			ball.bounceYDirection(1);
                	}
                	else {
                        ball.bounceXDirection(-1); //passes -1 because ball needs to bounce back towards the left (negative X direction)
                        specialBall();
                        if(!ballUsed)
                        	ballUsedOn = 1; //if the special ball was created not used, this means it must be used on paddle 1 since this ball came from paddle 2
                	}
                }
                if(specialBallOnScreen && !ballUsed) { //if the special ball is on the screen and it hasn't been used
	                if(specialBall.collides(p1) && ballUsedOn == 1) { //if it collides with paddle 1 and the special ball is for paddle 1
	                	if(ballType == 1) //if the ball was green (1), paddle height is doubled
	                		p1Height *= 2;
	                	else if(ballType == 2) //if the ball was red (2), paddle height is halved
	                		p1Height /= 2;
	                	p1.setSize(p1Width, p1Height); //updates the size
	                	remove(specialBall); //remove special ball since it has now been used
	                	ballUsed = true;
	                	specialBallOnScreen = false; //ball is not on the screen anymore
	                }
	                else if(specialBall.collides(p2) && ballUsedOn == 2) { //if it collides with paddle 2 and the special ball is for paddle 2, the special ball is handled the same way as if it collided with paddle 1
	                	if(ballType == 1)
	                		p2Height *= 2;
	                	else if(ballType == 2)
	                		p2Height /= 2;
	                	p2.setSize(p2Width, p2Height);
	                	ballUsed = true;
	                	specialBallOnScreen = false;
	                }
                }
                
                //Handles speeding up paddles and balls per collision
                if(ball.collides(p1) || ball.collides(p2)) {
                	ball.speedUpBall();
                	p1.speedUpPaddles();
                	p2.speedUpPaddles();
                }
                
                //handles collisions with boundaries
                if(ball.getY()<=0)
                    ball.bounceYDirection(1); //passes 1 because the ball needs to bounce back downwards (positive y direction) 
                else if(ball.getY()+BALL_SIZE>=wHeight)
                    ball.bounceYDirection(-1); //passes -1 because the ball needs to bounce back upwards (negative y direction)
                if(specialBallOnScreen) {
                	if(specialBall.getY()<=0)
                        specialBall.bounceYDirection(1); //passes 1 because the ball needs to bounce back downwards (positive y direction) 
                    else if(specialBall.getY()+BALL_SIZE>=wHeight)
                        specialBall.bounceYDirection(-1); //passes -1 because the ball needs to bounce back upwards (negative y direction)
                }
                
                if(specialBallOnScreen) { //if the special ball leaves the left and right boundaries, it is removed
	                if(specialBall.getX() >= wWidth || specialBall.getX() <= 0) {
	                	remove(specialBall);
	                	specialBallOnScreen = false;
	                	ballUsed = false; //the ball was removed but ballUsed remains false as it wasn't used/ a paddle did not hit it
	                	ballUsedOn = 0;
	                }
                }
                
                //Handles if a round was won
                if(ball.getX() >= wWidth)
                	score(1, 0); //p1 gets a point while p2 gets nothing
                else if(ball.getX() <= 0)
                    score(0, 1); //p2 gets a point while p1 gets nothing
        }
        
        /**
         * Calculates and displays score on the screen
         * Displays the winner at the end of the game
         * @param p1Point
         * @param p2Point
         */
        private void score(int p1Point, int p2Point) {
        	//adds points to the scores
        	p1Score += p1Point; 
        	p2Score += p2Point;
        	
        	//updates score on screen
        	score1.setText("" + p1Score); 
        	score2.setText("" + p2Score);
        	
        	//handles if a player wins a game. If a player reaches the winning number of points, the winner is displayed with sound and the game ends
        	if(p1Score == Integer.parseInt(winScore)) {
        		playYay();
        		JOptionPane.showMessageDialog(null, "Player 1 wins!!!");
        		System.exit(0);
        	}
        	else if(p2Score == Integer.parseInt(winScore)) {
        		playYay();
        		JOptionPane.showMessageDialog(null, "Player 2 wins!!!");
        		System.exit(0);
        	}
        	
        	reset(); //calls round to reset since this round has ended
        	
        	//resets object speeds
        	ball.resetBallSpeed();
        	p1.resetPaddleSpeed();
        	p2.resetPaddleSpeed();
        }
        
        /**
         * Resets round back to default values
         */
        private void reset() {
        	//resets dimensions of paddles in case they have changed
        	p1Height = wHeight/4;
            p1Width = wWidth/20;
            p2Height = wHeight/4;
            p2Width = wWidth/20;
            
            //resets paddle sizes in case they have changed
            p1.setSize(p1Width, p1Height);
            p2.setSize(p2Width, p2Height);
            
            //resets locations of ball and paddles
        	ball.setX(wWidth/2);
        	ball.setY(wHeight/2);
        	p1.setY(3*wHeight/8);
            p2.setX(wWidth-p2Width);
            p2.setY(3*wHeight/8);
            
            //resets special ball attributes
            specialBallOnScreen = false;
            ballUsed = false;
            ballUsedOn = 0;
        }
        
        /**
         * Changes background colour in a rainbow pattern 
         */
        private void changeBackgroundColor() {
        	 if(red != 255 && green == 0)
        	 	red++;
        	 if(red == 255 && green != 255)
        		 green++;
        	 if(green == 255 && blue != 255) {
        		 red--;
        		 blue++;
        	 }
        	 if(blue==255 & green != 0)
        		 green--;
        	 if(red != 255 && green == 0)
        		 blue--;
        	 setBackground(new Color(red, green, blue)); //sets background to new RGB value       		 
        }
        
        /**
         * Checks if the user has resized the window, if so, the correct adjustments are made
         */
        private void resize() {
        	//gets new height and width
        	int newHeight = getFieldHeight();
        	int newWidth = getFieldWidth();
        	if(newHeight != wHeight || newWidth != wWidth) { //if a change has occurred
        		//the new dimensions become the window dimension fields
        		wHeight = newHeight;
        		wWidth = newWidth;
        		
        		//paddle dimensions are created using the new dimensions
        		p1Height = wHeight/4;
                p1Width = wWidth/20;
                p2Height = wHeight/4;
                p2Width = wWidth/20;
                
                //makes sure to double or halve the correct paddle
                if(ballUsedOn == 1 && ballUsed) {
                	if(ballType == 1)
                		p1Height *= 2;
                	else if(ballType == 2)
                		p1Height /= 2;
                }
                if(ballUsedOn == 2 && ballUsed) {
                	if(ballType == 1)
                		p2Height *= 2;
                	else if(ballType == 2)
                		p2Height /= 2;
                }
        		
        		p1.setSize(p1Width, p1Height); //sets new size
        		
                p2.setSize(p2Width, p2Height); //sets new size
                p2.setX(wWidth-p2Width); //sets new X location
                
                //makes sure that scores are centered in the window
                score1.setBounds(wWidth/2-15, 0, 30, 30);
                score2.setBounds(wWidth/2+15, 0, 30, 30);
            		
        	}
        	//makes sure to keep the paddles within the window
        	if(p1.getY() + p1Height > wHeight)
                p1.setY(wHeight-p1Height);
            if(p2.getY() + p2Height > wHeight)
            	p2.setY(wHeight-p2Height); 
            
            //makes sure to keep the ball(s) within the window
            if(ball.getY()+BALL_SIZE > wHeight)
            	ball.setY(wHeight-BALL_SIZE);
            if(specialBallOnScreen) {
            	if(specialBall.getY()+BALL_SIZE > wHeight) {
                	specialBall.setY(wHeight-BALL_SIZE);
                }
            }
        }
        
        /**
         * Creates a special ball a in 1 of 10 chance
         */
        private void specialBall() {
        	if(!specialBallOnScreen && !ballUsed) { //if there isn't a ball on the screen and a ball hasn't been used
	        	Random random = new Random();
	        	int num = random.nextInt(10)+1; //generates random number from 1 - 10
	        	if(num == 1) { //1 in 10 chance
	        		specialBall = new Ball(); //creates ball
	        		//sets its attributes
	        		specialBall.setSize(BALL_SIZE, BALL_SIZE); ///sets size
	        		//sets it at the same X and Y as the real ball
	        		specialBall.setX(ball.getX());
	        		specialBall.setY(ball.getY());
	        		specialBall.setSpeed(ball.getSpeed()+1); //makes the special ball 1 speed faster than the real ball
	        		//bounces it the same direction as the real ball
	        		specialBall.bounceXDirection(ball.getXDirection());
	        		specialBall.bounceYDirection(ball.getYDirection());
	        		ballType = random.nextInt(2)+1; //randomly chooses 1 or 2
	        		if(ballType == 1) {
	        			specialBall.setColor(Color.GREEN); //if 1, the ball doubles the paddle size
	        		}
	        		else if(ballType == 2) {
	        			specialBall.setColor(Color.RED); //if 2, the ball halves the paddle size
	        		}
	        		add(specialBall);
	        		specialBallOnScreen = true;
	        	}
        	}
        }
        
        /**
         * Plays the yay.wav audio file
         */
        private void playYay() {
        	try {
				clip = AudioSystem.getClip();
			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	try {
				clip.open(AudioSystem.getAudioInputStream(yay));
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
         * Checks if the value inputed by the user is a valid score
         * @param score
         * @return true or false
         */
        private static boolean isNotAValidScore(String score) {
        	if(score == null || score.length() == 0) //checks if there wasn't an input = not a valid input
        		return true;
        	for(int i = 0; i<score.length(); i++) { //checks if a char is not an integer = not a valid input
        		if(Character.isDigit(score.charAt(i)) == false)
        			return true;
        	}
        	if(Integer.parseInt(score)<1) //checks that the integer is less than 1 = not a valid input
        		return true;
        	return false; //otherwise the score inputed was a valid integer
        }
        
        /**
         * Gets the score input from the user and makes sure that the score entered is a valid positive integer
         */
        private static void getScore() {
        	
        	do { ///keeps entering score until the score is valid
            	winScore = JOptionPane.showInputDialog("Please enter the score needed to win a game. It must be a positive integer.");
            } while(isNotAValidScore(winScore));
        	
        	if(Integer.parseInt(winScore) > 10) { //asks user for confirmation if the score is over 10 points
            	while(Integer.parseInt(winScore) > 10) {
                	int confirmation = JOptionPane.showConfirmDialog(null, "You entered a score needed that was over 10 points. \n Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
                	if(confirmation == JOptionPane.YES_OPTION) { //if yes, game proceeds
                		JOptionPane.showMessageDialog(null, "Win " + winScore + " points to win the game!");
                		break;
                	}
                	else if(confirmation == JOptionPane.NO_OPTION) { //if no, score is asked again
                		do {
                        	winScore = JOptionPane.showInputDialog("Please enter the score needed to win a game. It must be a positive integer.");
                        } while(isNotAValidScore(winScore));
                	}
            	}
            }
            if(Integer.parseInt(winScore) <= 10)
            	JOptionPane.showMessageDialog(null, "Win " + winScore + " points to win the game!");
        }
        /**
         * main method of the game
         * @param args
         */
        public static void main(String[] args) {
                Pong p = new Pong(); //creates pong game
                p.setVisible(true);
                
                //shows instructions on screen
                JOptionPane.showMessageDialog(null,"Hello! Welcome to Pong.\nPong is a game where there are two paddles on each side of the screen and a ball bouncing around.\nYour job is to try to get the black ball to reach the other side of the screen and defend your side by deflecting the ball using the paddle.");  
                JOptionPane.showMessageDialog(null, "Watch out for the special balls, there is a max of 1 special ball that can be used per round! Every time you hit the black ball, there is a 1 in 10 chance that a special green or red ball will appear!\nHitting them is not mandatory, but they might help you out. A green ball doubles your paddle size, but a red ball halves your paddle size.");
                JOptionPane.showMessageDialog(null, "Player 1 is on the left side of the screen, move your paddle up using the Z key and down using the X key.\nPlayer 2 is on the right side of the screen, move your paddle up using the N key and down using the M key.");
                
                //gets number of points needed to win a game
                getScore();
                                
                p.initComponents();
                p.setResizable(true); // is able to resize window
                
        }
        
}
