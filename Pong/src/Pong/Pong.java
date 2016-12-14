package Pong;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Pong extends JFrame implements KeyListener{

	private final static int WIDTH = 700, HEIGHT = 450;
	private PongPanel panel;
	
	public Pong(){
		setSize(WIDTH,HEIGHT);
		setTitle("Pong");
		setBackground(Color.WHITE);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel = new PongPanel(this);
		add(panel);
		addKeyListener(this);
	}
	
	public PongPanel getPanel() {
        return panel;
    }

	@Override
	public void keyPressed(KeyEvent e) {
		panel.player1.pressed(e.getKeyCode());
		panel.player2.pressed(e.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		panel.player1.released(e.getKeyCode());
		panel.player2.released(e.getKeyCode());
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[]  args){
		new Pong();
	}
}
