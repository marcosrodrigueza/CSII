package Informatica;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.util.*;

public class CSII extends AdvancedRobot 
{
	private Vector<EnemyBot> enemyList = new Vector<EnemyBot>(0);
	private int radarDirection = 1;
	//
	
	public void run()
	{
		// Initialization of the robot--------------------------------------------------
		setColors(Color.DARK_GRAY,Color.cyan,Color.cyan); // body,gun,radar
		
		final double battleWidth = getBattleFieldWidth();
		final double battleHeight = getBattleFieldHeight();
		
		addCustomEvent( new RadarTurnCompleteCondition(this));
		setAdjustRadarForGunTurn(true);
		setTurnRadarRight(360);
		
		
		// Robot main loop
		while(true)
		{
			// Replace the next 4 lines with any behavior you would like
			execute();
		}
	}

	/**
	 * onCustomEvent: Custom events handler
	 */
	public void onCUstomEvent(CustomEvent e)
	{
		if(e.getCondition() instanceof RadarTurnCompleteCondition)
			sweep(); //we will change for sweep
				
	}
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e)
	{
		EnemyBot enemy =  new EnemyBot(e, getX(), getY(), getHeading());
		if(!enemyList.contains(enemy))
		{
			enemyList.addElement(enemy);
			System.out.println("Enemy added");
		}
		else //Contains it
		{
			
			for(int i = 0; i < enemyList.size(); i++)
			{
				if((enemyList.elementAt(i)).equals(enemy))
				{
						enemyList.get(i).printVals(); //DEBUG
						enemyList.get(i).updateEnemy(enemy); //It could be useful to add time actualization margin 
						//to reduce computational loads
						enemyList.get(i).printVals(); //DEBUG
						break;
				}
			}//end for
			
		} //end else
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
	
	/*Adapted from Eivind Bjarte Tjore sweep algorith from Secrets from the robocode master:RadarSweep*/
	private void sweep()
	{
		double maxBearingAbs = 0;
		double maxBearing = 0;
		int scanned = 0;
		Iterator<EnemyBot> it = enemyList.iterator();
		while(it.hasNext())
		{
			EnemyBot temp = (EnemyBot)it.next();
			if(getTime() + temp.lastAct() < 16)
			{
				double bearing = Utils.normalRelativeAngleDegrees(getHeading() + temp.getBearing() - getRadarHeading());
				if(Math.abs(bearing) > maxBearingAbs)
				{
					maxBearingAbs = Math.abs(bearing);
					maxBearing = bearing;
				}
				
				scanned++;
			}
		}
		
		double radarTurn = 180*radarDirection;
		if(scanned == getOthers())
			radarTurn = maxBearing + Math.signum(maxBearing)*22.5; //22.5 Correction factor
		setTurnRadarRight(radarTurn);
		radarDirection =(int) Math.signum(radarTurn);
	}
}
