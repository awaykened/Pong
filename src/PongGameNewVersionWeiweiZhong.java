import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;


/*
 * The following Pong program Extends the JComponent class and Implements the KeyListener interface.
 * When we extend a class, the properties of that class is inherited, that is now you can use all 
 * their methods.
 * 
 * An interface is different from a class in that it cannot be extended. However, the properties 
 * can still be inherited. In this program, we implement the KeyListener interface to detect 
 * key presses and releases.
 */

class Comp extends JComponent implements KeyListener{		
	static int paddleWidth = 10, paddleLength = 100, Border = 80;
	int length, height, tick;
	private final int fontSize = 30;
	private final Color UIColor = Color.LIGHT_GRAY;
	
	Paddle P1, P2; Ball ball; PowerUp PU;
	boolean gameOver, isPaused, p1Point, p2Point, gameStart, powerUp;
	int P1Score, P2Score;
	
	public Comp(int length, int height) {
		
		// this refers to the current object being instantiated
		this.length = length; this.height = height + Border; 
		setPreferredSize(new Dimension(length, height)); // set size of component (the pong game part of the window)
		System.setProperty("DARK_GREEN", "0x006600"); // this is how you define custom colors
        System.setProperty("LIGHT_GREEN", "0x00CC33"); 
        System.setProperty("DARK_RED", "0x990000");
        
		P1 = new Paddle(Border + height / 2 - paddleLength / 2 - 50, 10, true);
		P2 = new Paddle(Border + height / 2 - paddleLength / 2 - 50, length - paddleWidth - 10, true);
		ball = new Ball(Border + height / 2 - 60, length / 2 - 3);
		PU = new PowerUp();
		gameStart = false;
        
        
        setFocusable(true);	// you must have this for keylistener to work
		addKeyListener(this); // initialize keylistener
		
		/* this is a SWING timer, which allows you to execute 
		 * tasks after every constant delay. This timer is 
		 * what allows the game to run smoothly and continuously, 
		 * and is also what sets the speed of the game.
		 * 
		 * e.g. the following code will execute whatever is in 
		 * the function taskPerformer every second.
		 * 
		 * 	int delay = 1000; //milliseconds
		 * 	ActionListener taskPerformer = new ActionListener() {
		 * 		public void actionPerformed(ActionEvent evt) {
         *	 		/...Perform a task...
         * 		}
         *  };
         *  new Timer(delay, taskPerformer).start();
         *  
         *  The code here is cheating, taking a shortcut by using 
         *  the built in method repaint() which calls the function
         *  paintComponent (listed later).
		 */
		
		new Timer(2, e -> repaint()).start(); 
	}
	
	class Paddle {
		int r, c, speed, length = paddleLength; // variables to keep track of position
		boolean Player; // variable for AI
		
		public Paddle(int r, int c, boolean Player) {
			this.r = r; this.c = c; this.Player = Player;
		}
		public void move() {
			if (!Player) { // handle AI movement
				if ((ball.wild != 0 && tick % 100 == 0) || ball.wild == 0) {
					if (r + length /2 >= ball.r) 
						speed = -1;
					else 
						speed = 1;
				}
			}
			r += speed;
			// ^ move paddle in wanted direction 
			// speed is controlled through keyboard input (explained later)
			
			// make sure paddle doesn't go out of bounds
			r = Math.min(r, (int) (getHeight() - paddleLength));
			r = Math.max(r,  Border); // Border is the UI thing at the top
		}
		public boolean hit(Ball b) { //  detect if the paddle hits the ball
			if (b.dc < 0) return b.r >= r && b.r <= r + paddleLength && b.c == c + paddleWidth;
			if (b.dc > 0) return b.r >= r && b.r <= r + paddleLength && b.c == c;
			return false;
		}
		public void show(Graphics g) {
			
			// there are many draw functions, most are simple to search up 
			// and implement
			
			g.setColor(Color.BLACK);
			g.fillRect(c, r, paddleWidth, paddleLength);
		}
	}
	
	class Ball {
		// class for the game ball, pretty self explanatory
		final int radius = 7;
		int r, c, dr, dc, wild, invis; // position, direction, powerup variables
		int control;
		public Ball(int r, int c) {
			// position of current ball
			this.r = r; this.c = c;
			
			// initial direction of ball
			dr = (int) (Math.random() * 2) > 0 ? 1: -1;
			dc = (int) (Math.random() * 2) > 0 ? 1: -1;
		}
		public void move() {
			
			// handle powerups
			if (wild > 0) { 
				int chance = (int) (Math.random() * 10);
				if (chance == 0) 
					dr = (int) (Math.random() * 2) > 0 ? 1: -1;
				wild --;
			}
			if (invis > 0) {
				invis --;
			}
			
			// did the ball hit a powerup?
			if (PU.hit(this)) {
				if (PU.type == 1) wild = 3000;
				if (PU.type == 2) invis = 3000;
				if (PU.type == 3) control = dc < 0 ? 2:1;
				PU.type = 0;
			}
			
			// detect collision
			if (P1.hit(this) || P2.hit(this)) { // this keyword again! Remember, this refers the the current object that is calling the method
				if (P1.hit(this)) {
					if (P1.r + P1.length*2/5 >= ball.r) 
						ball.dr = -1;
					else if (P1.r + P1.length*3/5 >= ball.r) 
						ball.dr = 0;
					else 
						ball.dr = 1;
				} else {
					if (P2.r + P2.length*2/5 >= ball.r) 
						ball.dr = -1;
					else if (P2.r + P2.length*3/5 >= ball.r) 
						ball.dr = 0;
					else 
						ball.dr = 1;
				}
				dc *= -1;  control = 0;
			} else if (r - radius <= Border || r + radius > getHeight()) {
				dr *= -1;
			}
			
			//move the actual ball object
			r += dr; c += dc;
		}
		
		// we reset the ball after every point
		public void reset(int r, int c) {
			this.r = r; this.c = c;
			dr = (int) (Math.random() * 2) > 0 ? 1: -1;
			dc = (int) (Math.random() * 2) > 0 ? 1: -1;
			wild = 0; invis = 0; control = 0;
		}

		public void show(Graphics g) {
			
			// this is more complicated because of powerups
			// you can just do the first and last 
			// g.setcolor() ... line g.fillOval(.... 
			// for the normal ball w/o powerups
			
			g.setColor(Color.BLACK);
			if (wild > 0)
				g.setColor(Color.getColor("DARK_GREEN"));
			if (invis > 0)
				g.setColor(Color.BLUE);
			boolean show = false;
			show |= (invis == 0);
			for (int i = 0; i <= 80; i++) {
				if ((tick + i) % 500 == 0)
					show = true;
			}
			if (show) 
				g.fillOval(ball.c - ball.radius, ball.r - ball.radius, ball.radius * 2, ball.radius * 2);
		}
	}
	
	
	// Fun, optional include
	// be creative here, nothing much to learn
	
	class PowerUp {
		static final int pRad = 20;
		int r, c, type, timer; // 0 - none, 1 - wild, 2 - invis, 3 - control
		Color prim, sec;
		
		public PowerUp() {
			type = (int)(Math.random() * 3 + 1);
			r = (int) (Math.random() * (height/2 - 100) + pRad + Border + 200);
			c = (int) (Math.random() * (length/2 - 100) + pRad + 200);
			setColor(); timer = 0;
		}
		
		// generate the powerup
		public void gen() {
			type = (int)(Math.random() * 3 + 1);
			r = (int) (Math.random() * (height/2 - 100) + pRad + Border + 200);
			c = (int) (Math.random() * (length/2 - 100) + pRad + 200);
			setColor(); timer = 0; 
		}
		
		//helper function
		private void setColor() {
			if (type == 1) {
				prim = Color.getColor("LIGHT_GREEN");
				sec = Color.getColor("DARK_GREEN");
			} 
			if (type == 2) {
				prim = Color.BLUE;
				sec = Color.BLACK;
			}
			if (type == 3) {
				prim = Color.getColor("DARK_RED");
				sec = Color.RED;
			}
		}
		
		// checks ball collision with powerup
		public boolean hit(Ball b) {
			if (b.c + b.radius >= c - pRad && b.c - b.radius <= c + pRad && b.r + b.radius >= r - pRad && b.r - b.radius <= r + pRad) {
				return true;
			}
			return false;
		}
		public void show(Graphics g) {
			if (type == 0) return;
			g.setColor(prim);
			g.fillOval(c - pRad, r - pRad, pRad * 2, pRad * 2);
			g.setColor(sec);
			g.fillOval(c - pRad + 4, r - pRad + 4, pRad * 2 - 8, pRad * 2 - 8);
		}
		
	}
	
	public void paintComponent(Graphics g) {
		// paintComponent is called every tick through repaint (back up there when we explained Timer)
	
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Paint the background
		// g.fillRect(x, y, length, height) fills (x,y) -> (x + length, y + height)
		g.setColor(Color.BLACK); g.fillRect(0,  0, length, Border);
		g.setColor(Color.WHITE); g.fillRect(0, Border, length, height - Border);

		// main game execution
		if (!isPaused && gameStart) { tick ++; tick %= 5000;
			if ((PU.type == 0 && ball.wild == 0 && ball.invis == 0 && ball.control == 0)|| PU.timer == 5000) {
				PU.gen();
			}
			if (PU.type != 0) {
				PU.timer ++;
			}
			P1.move(); P2.move(); ball.move();	
		}
		
		// Check if ball has escaped
		if (ball.c <= 0 || ball.c >= length) {
			if (ball.c <= 0) P2Score ++;
			else 			 P1Score ++;
			ball.reset(Border + height / 2 - 60, length / 2 - 3); 
			gameStart = false; tick = 0;
			PU.gen();
		}
		
		// Paint the paddles/ball/PU
		ball.show(g); PU.show(g);
		P1.show(g); P2.show(g);
		
		// Paint the UI
		// google the drawing methods, there are many
		g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, fontSize)); 
		g.setColor(UIColor);
		g.drawString("PLAYER 1: " + P1Score, 50, 50);
		g.drawString("PLAYER 2: " + P2Score, length - 250 , 50);
		if (isPaused)
			g.drawString("PAUSED", length / 2 - 60, height / 2 - 50);
		else if (!gameStart)
			g.drawString("PRESS SPACE", length / 2 - 100, 50);
	}
	
	/* --------------------------IMPORTANT ------------------------ 
	 *  The reason why we implement KeyListener is to be able to override
	 *  the below functions. The code is pretty self explanatory: keyTyped 
	 *  we don't use, keyPressed is automatically called when a key press is detected,
	 *  keyReleased is automatically called when a key release is detected. 
	 *  Note that these functions are called WHENEVER you press/release a key, 
	 *  so there is no lag/need to press enter.
	 */
	
	public void keyTyped(KeyEvent e) {
		
	}
	public void keyPressed(KeyEvent e) {
		// a KeyEvent has a KeyCode, which maps to one of the keyboard keys
		// usually, these codes are KeyEvent.VK_(LETTER)
		// Here, keys up, down, w, s are used to determine the paddle direction (-1 = up, 1 = down)
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_UP) {  					P2.speed = -1; if (ball.control == 2) ball.dr = -1;}
		if (key == KeyEvent.VK_DOWN) { 					P2.speed = 1; if (ball.control == 2) ball.dr = 1;}
		if (key == KeyEvent.VK_W) {	 					P1.speed = -1; if (ball.control == 1) ball.dr = -1;}
		if (key == KeyEvent.VK_S) {	 					P1.speed = 1; if (ball.control == 1) ball.dr = 1;}
		if (key == KeyEvent.VK_P) 	 					isPaused = !isPaused;
		if (key == KeyEvent.VK_SPACE && !isPaused)		gameStart |= true;

	}
	public void	keyReleased(KeyEvent e) {
		// When (up/down/w/s) pressed, the paddle moves in a certain 
		// direction. Upon release, the paddle should stop moving in that
		// direction
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_W    && P1.speed == -1) 	P1.speed = 0; 
		if (key == KeyEvent.VK_S    && P1.speed == 1) 	P1.speed = 0; 
		if (key == KeyEvent.VK_UP   && P2.speed == -1) 	P2.speed = 0; 
		if (key == KeyEvent.VK_DOWN && P2.speed == 1) 	P2.speed = 0; 
	}
}

public class PongGameNewVersionWeiweiZhong {
	public static void main(String[] args)  {
		// JFrame allows us to create the window, and we can add components onto the window
		JFrame window = new JFrame(); 
		window.add(new Comp(800, 600));
		
		//makes sure all components starts >= their preferred size! 
		window.pack();
		
		// makes sure the window closes when you press close (lol)
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// sets the window ... to be visible
        window.setVisible(true);
	}
}
