import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RoundPanel extends JPanel implements MouseListener{
	
	String Playername;
	int Playerlevel;
	int nhood;
	boolean isCompleted = false;
	Random random = new Random();
	
	static final int SCREEN_WIDTH = 1800;
	static final int SCREEN_HEIGHT = 750;
	static final int UNIT_SIZE = 50;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	final int x[] = new int [GAME_UNITS];
	final int y[] = new int [GAME_UNITS];
	int bubbleX = 1*UNIT_SIZE;
	int bubbleY = 1*UNIT_SIZE;
	int round;
	bub bubbles[];
	tempBub tempbubbles[];
	int i=0;
	int Score;
	
	RoundPanel(String name, int level, int R){
		Playername = name;
		Playerlevel = level;
		round = R;
		nhood = R;
		Score = Playerlevel + 4;
		bubbles = new bub[Playerlevel+4];
		tempbubbles = new tempBub[Playerlevel+4];
		for(int j=0;j<Playerlevel+4;j++) {
			bubbles[j] = new bub();
			tempbubbles[j] = new tempBub();
		}
		setBounds(50, 200, 1800, 750);
		setBackground(new Color(0x696969));
		addMouseListener(this);
		setVisible(true);
		drawBubbles(0, 0, (int)(SCREEN_WIDTH/UNIT_SIZE)-1, (int)(SCREEN_HEIGHT/UNIT_SIZE)-1);
		
		//Local Hopping
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				
				int ii=0;
				for(ii=0;ii<Playerlevel+4;ii++) {
					int nmx = (bubbles[ii].Xpos)/UNIT_SIZE - nhood;
					int nmy = (bubbles[ii].Ypos)/UNIT_SIZE - nhood;
					int NMX = (bubbles[ii].Xpos)/UNIT_SIZE + nhood;
					int NMY = (bubbles[ii].Ypos)/UNIT_SIZE + nhood;
					if(nmx < 0)
						nmx = 0;
					if(nmy < 0)
						nmy = 0;
					if(NMX > 35)
						NMX = 35;
					if(NMY > 14)
						NMY = 14;
					i = ii;
					removemouse();
					generate(nmx, nmy, NMX, NMY);
					addmouse();
				}
				
			}
			
		};
		timer.scheduleAtFixedRate(task, 0, 2000);
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void removemouse() {
		removeMouseListener(this);
	}
	public void addmouse() {
		this.addMouseListener(this);
	}
	
	public void draw(Graphics g) {
		
		/*for(int j=0;j<SCREEN_HEIGHT/UNIT_SIZE;j++)
			g.drawLine(0, j*UNIT_SIZE, SCREEN_WIDTH, j*UNIT_SIZE);
		for(int j=0;j<SCREEN_WIDTH/UNIT_SIZE;j++)
			g.drawLine(j*UNIT_SIZE, 0, j*UNIT_SIZE, SCREEN_HEIGHT);*/
		
		int l=Playerlevel + 4-1;
		g.setColor(new Color(0x87CEEB));
		for(;l>=0;l--) {
			if(bubbles[l].isBurst!=1) {
				g.fillOval(tempbubbles[l].Xpos, tempbubbles[l].Ypos, UNIT_SIZE, UNIT_SIZE);
			}
		}
		
	}
	
	public void paintpanelcol(Graphics g, int xx, int yy) {
		g.setColor(new Color(0x696969));
		g.fillOval(xx, yy, 50, 50);
	}
	
	public void drawBubbles(int mx, int my, int MX, int MY) {
		int tests = Playerlevel + 4;
		while(tests!=0) {
			gen(mx,my,MX,MY);
			i++;
			tests--;
		}
	}
	
	public void gen(int mx, int my, int MX, int MY) {
		bubbleX = ThreadLocalRandom.current().nextInt(mx, MX +1) * UNIT_SIZE;
		bubbleY = ThreadLocalRandom.current().nextInt(my, MY +1) * UNIT_SIZE;
		
		if(bubbles[i].isBurst != 1) {
			tempbubbles[i].changeloc(bubbleX, bubbleY);
			bubbles[i].changeloc(bubbleX, bubbleY);
			//System.out.println(bubbles[i].Xpos);
			//System.out.println(bubbles[i].Ypos);
		}
	}
	
	public void generate(int mx, int my, int MX, int MY) {
		bubbleX = ThreadLocalRandom.current().nextInt(mx, MX +1) * UNIT_SIZE;
		bubbleY = ThreadLocalRandom.current().nextInt(my, MY +1) * UNIT_SIZE;
		
		if(bubbles[i].isBurst != 1) {
			tempbubbles[i].changeloc(bubbleX, bubbleY);
			repaint();
			bubbles[i].changeloc(bubbleX, bubbleY);
			//System.out.println(bubbles[i].Xpos);
			//System.out.println(bubbles[i].Ypos);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
		int cx = e.getX();
		int cy = e.getY();
		cx = (int)(cx/UNIT_SIZE) * UNIT_SIZE;
		cy = (int)(cy/UNIT_SIZE) * UNIT_SIZE;
		boolean flag = false;
		for(int p=0;p<Playerlevel+4;p++) {
			if(cx == bubbles[p].Xpos) {
				if(cy == bubbles[p].Ypos) {
					
					paintpanelcol(getGraphics(), cx, cy);
					bubbles[p].changeloc(-1, -1);
					Score--;
					if(Score==0) {
						if(round==10) {
							backtomain();
							JOptionPane.showMessageDialog(null, "Congratulations! YOU WON " + Playername, "Bubble Burst Winner!", JOptionPane.INFORMATION_MESSAGE);
						}
						//System.out.println("You won this round!");
						else {
							backtomain();
							new RoundFrame(Playername, Playerlevel,round+1);
						}
					}
					
					flag = true;
					break;
				}
			}
		}
		if(flag==false) {
			System.out.println("Game Over bro!");
			backtomain();
			JOptionPane.showMessageDialog(null, "GAME OVER!", "Round: " + round +"GAME OVER!", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public void backtomain() {
		isCompleted = true;
		Window x = SwingUtilities.getWindowAncestor(this);
		x.dispose();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
