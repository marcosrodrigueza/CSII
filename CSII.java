package Informatica;

import robocode.*;
import java.awt.Color;
import java.util.*;

public class CSII extends AdvancedRobot 
{
	private Vector<EnemyBot> enemyList;
	public void run()
	{
		// Initialization of the robot--------------------------------------------------
		setColors(Color.DARK_GRAY,Color.gray,Color.yellow); // body,gun,radar
		
		final double battleWidth = getBattleFieldWidth();
		final double battleHeight = getBattleFieldHeight();
		
		
		// Robot main loop
		while(true)
		{
			// Replace the next 4 lines with any behavior you would like
			ahead(100);
			turnGunRight(128);
			fire(5);
			turnRight(128);
			back(10);
			turnGunRight(128);
			turnRight(120);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e)
	{
		EnemyBot enemy =  new EnemyBot(e, getX(), getY(), getHeading());
		if(!enemyList/*vector object that we will include later*/.contains(enemy))
		{
			enemyList.add(enemy);
		}
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
		turnGunLeft(128);
		fire(5);
		turnLeft(128);
		back(10);
		turnGunLeft(128);
		turnLeft(120);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
		turnGunRight(128);
		turnRight(128);
		back(10);
		turnGunRight(128);
		turnRight(0);
	}
}
