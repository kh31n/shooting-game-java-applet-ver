package shooting_game_applet_ver;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;

public class Shooting extends Applet implements Runnable, KeyListener {
	private final int SPEED = 8;
	private final int ENEMY_SPEED = 8;
	private final int BULLET_X = 29;
	private final int BULLET_Y = 7;
	private final int BULLET_SPEED = 10;
	private final int ENEMY_X = 33;
	private final int ENEMY_Y = 40;
	private Player player;
	private Enemy enemy;
	private Hit bullet_hit;
	private Hit enemy_hit;
	private Bullet bullet;
	private Thread gameThread;
	private boolean left,right,up,down;
	private boolean shot;
	private LinkedList<Enemy> enemy_task;
	private LinkedList<Bullet> bullet_task;
	private Iterator<Enemy> enemy_iter;
	private Iterator<Bullet> bullet_iter;
	private boolean enemy_wait;
	private int enemy_cnt;
	
	public void init(){
		setFocusable(true);
		setBackground(Color.black);
		setForeground(Color.white);
		
		create_player();
		
		bullet_task = new LinkedList<Bullet>();
		bullet_iter = bullet_task.iterator();
		
		enemy_wait = false;
		enemy_task = new LinkedList<Enemy>();
		enemy_cnt = 0;
		enemy_iter = enemy_task.iterator();
		
		bullet_hit = new Hit();
		
		bullet_hit.left = 0;
		bullet_hit.right = BULLET_X;
		bullet_hit.top = 0;
		bullet_hit.bottom = BULLET_Y;
		
		enemy_hit = new Hit();
		
		enemy_hit.left = 0;
		enemy_hit.right = ENEMY_X;
		enemy_hit.top = 0;
		enemy_hit.bottom = ENEMY_Y;
		
		addKeyListener(this);
	}
	
	public void start(){
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public void stop(){
		gameThread = null;
	}
	
	public void run(){
		while(gameThread == Thread.currentThread()){
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			player_move_control();
			
			if(shot){
				create_bullet();
			}
			
			bullet_move();
			
			enemy_move_pattern();
			
			if(is_collision()){
				enemy_iter.remove();
				bullet_iter.remove();
			}
			
			repaint();
		}
	}
	
	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_SPACE:
			shot = true;
			break;
		}
	}
	
	public void keyReleased(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_SPACE:
			shot = false;
			break;
		}
	}
	
	public void keyTyped(KeyEvent e){
		
	}
	
	public void paint(Graphics g){
		g.drawImage(player.player_img, player.player_x, player.player_y, this);
		
		bullet_iter = bullet_task.iterator();
		
		while(bullet_iter.hasNext()){
			Bullet b = bullet_iter.next();
			g.drawImage(b.bullet_img, b.bullet_x, b.bullet_y, this);
		}
		
		enemy_iter = enemy_task.iterator();
		
		while(enemy_iter.hasNext()){
			Enemy e = enemy_iter.next();
			g.drawImage(e.enemy_img, e.enemy_x, e.enemy_y, this);
		}
		
		g.dispose();
	}
	
	public void create_enemy(int enemy_x, int enemy_y){
		enemy = new Enemy();
		enemy.enemy_x = enemy_x;
		enemy.enemy_y = enemy_y;
		enemy.enemy_img = getImage(getDocumentBase(), "./shooting_game_applet_ver/enemy1_1.png");
		enemy_task.add(enemy);
	}
	
	public void player_move_control(){
		if(left)
			player.player_x -= SPEED;
		else if(right)
			player.player_x += SPEED;
		else if(up)
			player.player_y -= SPEED;
		else if(down)
			player.player_y += SPEED;
	}
	
	public void bullet_move(){
		bullet_iter = bullet_task.iterator();
		
		while(bullet_iter.hasNext()){
			Bullet b = bullet_iter.next();
			
			if(b.bullet_x > 640){
				bullet_iter.remove();
			}else{
				b.bullet_x += BULLET_SPEED;
			}
		}
	}
	
	public void create_player(){
		player = new Player();
		player.player_x = 100;
		player.player_y = 200;
		player.player_img = getImage(getDocumentBase(), "./shooting_game_applet_ver/player1_jiki.png");
	}
	
	public void create_bullet(){
		bullet = new Bullet();
		bullet.bullet_x = player.player_x + 56;
		bullet.bullet_y = player.player_y + 8;
		bullet.bullet_img = getImage(getDocumentBase(), "./shooting_game_applet_ver/player1_shot.png");
		bullet_task.add(bullet);
	}
	
	public void enemy_move_pattern(){
		if(!enemy_wait){
			create_enemy(650, 300);
			enemy_wait = true;
		}
		
		if(enemy_cnt >= 15){
			enemy_wait = false;
			enemy_cnt = 0;
		}
		
		if(enemy_wait){
			enemy_cnt++;
		}
		
		enemy_iter = enemy_task.iterator();
				
		while(enemy_iter.hasNext()){
			Enemy e = enemy_iter.next();
			if(e.enemy_x < 0){
				enemy_iter.remove();
			}else{
				e.enemy_x -= ENEMY_SPEED;
			}
		}
	}
	
	public boolean is_collision(){
		enemy_iter = enemy_task.iterator();
		
		while(enemy_iter.hasNext()){
			Enemy e = enemy_iter.next();
			
			int l0 = e.enemy_x + enemy_hit.left;
			int r0 = e.enemy_x + enemy_hit.right;
			int t0 = e.enemy_y + enemy_hit.top;
			int b0 = e.enemy_y + enemy_hit.bottom;
			
			bullet_iter = bullet_task.iterator();
			
			while(bullet_iter.hasNext()){
				Bullet b = bullet_iter.next();
			
				int l1 = b.bullet_x + bullet_hit.left;
				int r1 = b.bullet_x + bullet_hit.right;
				int t1 = b.bullet_y + bullet_hit.top;
				int b1 = b.bullet_y + bullet_hit.bottom;
				
				if(l0 < r1 && l1 < r0 && t0 < b1 && t1 < b0){
					return true;
				}
			}
			
		}
		
		return false;
	}
}