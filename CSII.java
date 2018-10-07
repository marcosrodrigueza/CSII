package Informatica;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.util.*;

public class CSII extends AdvancedRobot 
{
	private Vector<EnemyBot> enemyList = new Vector<EnemyBot>(0);
	private Manager manager = new Manager();
	private int radarDirection = 1;
	private double firePow;
	
	//
	
	public void run()
	{
		// Initialization of the robot--------------------------------------------------
		setEventPriority("ScannedRobotEvent", 70); // done to make it uniterruptible from death events
		//To prevent accesing the vector at the same time;
		
		Color spec = new Color(220, 27, 124);
		setColors(Color.black,spec,Color.black); // body,gun,radar
		
		/*final double battleWidth = getBattleFieldWidth();
		final double battleHeight = getBattleFieldHeight();*/
		
		addCustomEvent( new RadarTurnCompleteCondition(this)); //Custom event
		setAdjustGunForRobotTurn(true); //gun  independent from robot
		setAdjustRadarForGunTurn(true); // radar independent from gun
		setAdjustRadarForRobotTurn(true);
		
		setTurnRadarRight(360);
		
		
		// Robot main loop
		while(true)
		{
			// Replace the next 4 lines with any behavior you would like
			/*if(!enemyList.isEmpty())
			{
				manager.searchTarget(getX(), getY());
				manager.perform(getX(), getY());
			}*/
			
			if(!enemyList.isEmpty())
			{
				firePow = 400/enemyList.get(0).getDistance();
				/*point = guessPosition( 4 , 6.253); 
			    gunOffset = getGunHeadingRadians() - (Math.PI/2 - Math.atan2(point.y - getY(), point.x - getX()));
			    
				setTurnGunLeftRadians(Utils.normalRelativeAngle(gunOffset));
				*/
				predict(firePow , 6.253);
				setFire(firePow);
			}
			antiGravMove();
			execute();
		}
	}

	/**
	 * onCustomEvent: Custom events handler
	 */
	public void onCustomEvent(CustomEvent e)
	{
		if(e.getCondition() instanceof RadarTurnCompleteCondition)
		{
			 sweep();
		}
				
	}
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e)
	{
		double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
		setTurnGunRightRadians(
		    robocode.util.Utils.normalRelativeAngle(absoluteBearing - 
		        getGunHeadingRadians()));
		
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
	public void onHitByBullet(HitByBulletEvent e)
	{
		// Replace the next line with any behavior you would like
		
	}
	public void onHitRobot(HitRobotEvent e)
	{
		// Replace the next line with any behavior you would like
		setFire(3);
		setBack(20);
		setTurnLeft(20);
		
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) 
	{
		// Replace the next line with any behavior you would like
		//setTurnRight(180);
		//ahead(150);
		
		
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
			if(getTime() - temp.lastAct() < 16)
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
			radarTurn = maxBearing + Math.signum(maxBearing)*16; //22.5 Correction factor

		setTurnRadarRight(radarTurn);
		radarDirection =(int) Math.signum(radarTurn);
	}
	
	void antiGravMove()
	{
   		double xforce = 0;
	    double yforce = 0;
	    double force;
	    double midpointstrength;
	    double ang;
	    GravPoint p;
	    //
	    double forceAtrac;
	    double forceRep;
	    double repPow;
	    double dist;
	    
	    //cycle through all the enemies.  If they are alive, they are repulsive.  Calculate the force on us
		for(int i = 0; i < enemyList.size(); i++)
		{
				p = enemyList.get(i).getGravPoint();
				dist = getRange(getX(),getY(),p.x,p.y);
				
		        forceAtrac = p.power/Math.pow(dist,2);
		        repPow = 0 - (p.power/2);
		        forceRep = repPow/Math.pow(getRange(getX(),getY(),p.x,p.y),1.5); //DEcreases to the 4th
		        force = forceAtrac - forceRep;
		        if(dist < 100)
		        {
		        	force = 0 - force;
		        }
		        
		        //Find the bearing from the point to us
		        ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
		        //Add the components of this force to the total force in their respective directions
		        xforce += Math.sin(ang) * force;
		        yforce += Math.cos(ang) * force;
			
	    } 
		//Could be convenient to add middle points for mele functions
		/*midpointstrength = -160;
		
		p = new GravPoint(getBattleFieldWidth()/2, getBattleFieldHeight()/2, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    midpointstrength = -1000;
	    
	    p = new GravPoint(0,0, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),4);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(0, getBattleFieldHeight(), midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),4);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(getBattleFieldWidth(), 0, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),4);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(getBattleFieldWidth(), getBattleFieldHeight(), midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    //---------------------------------------------------------------------------------------------
	    p = new GravPoint(getBattleFieldWidth()/2, 0, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(getBattleFieldWidth(), getBattleFieldHeight()/2, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(0, getBattleFieldHeight()/2, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(getBattleFieldWidth()/2, getBattleFieldHeight(), midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    */
	    /**The following four lines add wall avoidance.  They will only affect us if the bot is close 
	    to the walls due to the force from the walls decreasing at a power 3.**/
	    xforce += 5000/Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 3);
	    xforce -= 5000/Math.pow(getRange(getX(), getY(), 0, getY()), 3);
	    yforce += 5000/Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 3);
	    yforce -= 5000/Math.pow(getRange(getX(), getY(), getX(), 0), 3);
	    
	    //Move in the direction of our resolved force.
	    goTo(getX()-xforce,getY()-yforce);
	}
	
	
	public void predict(double firePower, double deltaAng) //in degrees
	{
		long time;
		long nextTime;
		double tarx = (enemyList.get(0)).getX();
		double tary = (enemyList.get(0)).getY();
		GravPoint p =  new GravPoint(tarx, tary, 1);
		
		deltaAng = Math.toRadians(deltaAng);
		
		for(int i = 0; i < 10; i++)
		{
			nextTime = (int)Math.round((getRange(getX(), getY(), p.x, p.y)/(20 - (3*firePower))));
			time = getTime() + nextTime;
			p = guessPosition(time, deltaAng);
		}
		
		/**Turn the gun to the correct angle**/
		double gunOffset = getGunHeadingRadians() - (Math.PI/2 - Math.atan2(p.y - getY(), p.x - getX()));
		setTurnGunLeftRadians(Utils.normalRelativeAngle(gunOffset));
	}
	
	private GravPoint guessPosition(long when, double changeHead)
	{
		double time = (enemyList.get(0)).lastAct();
		double tarx = (enemyList.get(0)).getX();
		double tary = (enemyList.get(0)).getY();
		double heading = Math.toRadians((enemyList.get(0)).getHeading()); //radians
		double speed = (enemyList.get(0)).getVelocity(); 
		double diff = when - time;
		double newX,newY;
		
		if(Math.abs(changeHead) > 0.0001)
		{
			double radius = speed/changeHead;
			double tothead = diff*changeHead; //----------------------------------------------------------
			
			newY = tary + (Math.sin(heading + tothead) * radius) - (Math.sin(heading) * radius);
			newX = tarx + (Math.cos(heading + tothead) * radius) - (Math.cos(heading) * radius);
		}
		else
		{
			newY = tary + Math.cos(heading) * speed * diff;
			newX = tarx + Math.cos(heading) * speed * diff;
		}
		
		GravPoint result = new GravPoint(newX, newY,1);
		return result;
	}
	/**Move towards an x and y coordinate**/
	void goTo(double x, double y)
	{
	    double dist = 20; 
	    double angle = Math.toDegrees(absBearing(getX(),getY(),x,y));
	    double r = turnTo(angle);
	    setAhead(dist * r);
	}
	
	void turnGunTo(GravPoint p)
	{
		double angle = Math.toDegrees(absBearing(getX(),getY(),p.x,p.y));
	    setTurnGunLeft(angle);
	}

	/**Turns the shortest angle possible to come to a heading, then returns the direction the
	the bot needs to move in.**/
	
	int turnTo(double angle)
	{
	    double ang;
    	int dir;
	    ang = Utils.normalRelativeAngleDegrees(getHeading() - angle);
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
