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
		setColors(Color.DARK_GRAY,Color.cyan,Color.yellow); // body,gun,radar
		
		/*final double battleWidth = getBattleFieldWidth();
		final double battleHeight = getBattleFieldHeight();*/
		
		addCustomEvent( new RadarTurnCompleteCondition(this));
		setAdjustRadarForGunTurn(true);
		setTurnRadarRight(360);
		
		
		// Robot main loop
		while(true)
		{
			// Replace the next 4 lines with any behavior you would like
			antiGravMove();
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
	
	void antiGravMove()
	{
   		double xforce = 0;
	    double yforce = 0;
	    double force;
	    double ang;
	    GravPoint p;
	    
	    //cycle through all the enemies.  If they are alive, they are repulsive.  Calculate the force on us
		for(int i = 0; i < enemyList.size(); i++)
		{
				p = enemyList.get(i).getGravPoint();
		        force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
		        //Find the bearing from the point to us
		        ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
		        //Add the components of this force to the total force in their respective directions
		        xforce += Math.sin(ang) * force;
		        yforce += Math.cos(ang) * force;
			
	    } //Could be convenient to add middel points for mele functions
	    
	    /**The following four lines add wall avoidance.  They will only affect us if the bot is close 
	    to the walls due to the force from the walls decreasing at a power 3.**/
	    xforce += 5000/Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 3);
	    xforce -= 5000/Math.pow(getRange(getX(), getY(), 0, getY()), 3);
	    yforce += 5000/Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 3);
	    yforce -= 5000/Math.pow(getRange(getX(), getY(), getX(), 0), 3);
	    
	    //Move in the direction of our resolved force.
	    goTo(getX()-xforce,getY()-yforce);
	}
	
	/**Move towards an x and y coordinate**/
	void goTo(double x, double y)
	{
	    double dist = 20; 
	    double angle = Math.toDegrees(absBearing(getX(),getY(),x,y));
	    double r = turnTo(angle);
	    setAhead(dist * r);
	}


	/**Turns the shortest angle possible to come to a heading, then returns the direction the
	the bot needs to move in.**/
	
	int turnTo(double angle)
	{
	    double ang;
    	int dir;
	    ang = Utils.normalRelativeAngle(getHeading() - angle);
	    if (ang > 90) {
	        ang -= 180;
	        dir = -1;
	    }
	    else if (ang < -90) {
	        ang += 180;
	        dir = -1;
	    }
	    else {
	        dir = 1;
	    }
	    setTurnLeft(ang);
	    return dir;
	}
	
	public double absBearing( double x1,double y1, double x2,double y2 )
	{
		double xo = x2-x1;
		double yo = y2-y1;
		double h = getRange( x1,y1, x2,y2 );
		if( xo > 0 && yo > 0 )
		{
			return Math.asin( xo / h );
		}
		if( xo > 0 && yo < 0 )
		{
			return Math.PI - Math.asin( xo / h );
		}
		if( xo < 0 && yo < 0 )
		{
			return Math.PI + Math.asin( -xo / h );
		}
		if( xo < 0 && yo > 0 )
		{
			return 2.0*Math.PI - Math.asin( -xo / h );
		}
		return 0;
	}
	
	public double getRange( double x1,double y1, double x2,double y2 )
	{
		double xo = x2-x1;
		double yo = y2-y1;
		double h = Math.sqrt( xo*xo + yo*yo );
		return h;	
	}
}
