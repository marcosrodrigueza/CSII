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
	
	//
	
	public void run()
	{
		// Initialization of the robot--------------------------------------------------
		setEventPriority("ScannedRobotEvent", 70); // done to make it uniterruptible from death events
		//To prevent accesing the vector at the same time;
		
		setColors(Color.black,Color.black,Color.magenta); // body,gun,radar
		
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
			setFire(Math.min(getEnergy(), 3.0));
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
		setTurnRight(180);
		ahead(150);
		
		
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
			
	    } 
		//Could be convenient to add middle points for mele functions
		midpointstrength = -1000;
		
	    p = new GravPoint(getBattleFieldWidth()/2, getBattleFieldHeight()/2, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(0,0, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(0, getBattleFieldHeight(), midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(getBattleFieldWidth(), 0, midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
	    p = new GravPoint(getBattleFieldWidth()/2, getBattleFieldHeight(), midpointstrength);
	    force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
	    ang = Utils.normalRelativeAngle(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
	    xforce += Math.sin(ang) * force;
	    yforce += Math.cos(ang) * force;
	    
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
	    
	    /**The following four lines add wall avoidance.  They will only affect us if the bot is close 
	    to the walls due to the force from the walls decreasing at a power 3.**/
	    /*xforce += 5000/Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 3);
	    xforce -= 5000/Math.pow(getRange(getX(), getY(), 0, getY()), 3);
	    yforce += 5000/Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 3);
	    yforce -= 5000/Math.pow(getRange(getX(), getY(), getX(), 0), 3);
	    */
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
	
	public class Manager 
	{
		private int target;
		private boolean countCirc;
		public double prevHeading;
		
		
		public Manager()
		{
			target = -1;
			countCirc = false;
			prevHeading = 0;

		}
		
		public void searchTarget(double x, double y)
		{
	
		   Iterator<EnemyBot> it = enemyList.iterator();
		   double curMin = 9999.0;
		   int enemyindx = 0;
		   
		   while (it.hasNext()) 
		   {
		     EnemyBot tempBot = (EnemyBot) it.next();
		     double tpDist = getRange(x,y, tempBot.getX(), tempBot.getY());
		     // check for dead 
		     if ((  tempBot.getEnergy() >= 0 && (tpDist < curMin))) {
		        curMin   = tpDist;
		        enemyindx = enemyList.indexOf(tempBot);
		     } // of if
		   } // of while
		   
		   	target = enemyindx;
		     System.out.println("next target is " + (enemyList.get(target)).getName());
		   
		   }
		
		
		public void perform(double myx, double myy)
		{
			if (target == -1)
				System.out.println("No enemies detected");
			else
			{
				if((enemyList.get(target)).getName() == "sample.Crazy")
				{
					this.crazyStrategy(myx, myy);
				}
				else if((enemyList.get(target)).getName() == "sample.PaintingRobot")
				{
					this.paintingStrategy(myx, myy);
				}
				else if((enemyList.get(target)).getName() == "sample.SpinBot" || 
						(enemyList.get(target)).getName() == "SpinBot")
				{
					System.out.println("go for spinbot");
					this.spinStrategy(myx,myy);
				}
				else
				{
					this.strategy(myx,myy);
				}
			} //end else
		} //end perform
		
		private void crazyStrategy(double currX, double currY)
		{
			double firepow = 1;
			double gravstrenght = 500;
			double tarx, tary,tarhead_deg, tarvel;
			
			(enemyList.get(target)).setPower(gravstrenght);
			tarx = (enemyList.get(target)).getX();
			tary = (enemyList.get(target)).getY();
			tarhead_deg = (enemyList.get(target)).getHeading();
			tarvel = (enemyList.get(target)).getVelocity();
			
			//Intercept intercept = new Intercept();
			//intercept.calculate(currX, currY, tarx, tary, tarhead_deg, tarvel, firepow, /*Angvel*/ 0);
			
		}
		
		private void paintingStrategy(double currX, double currY)
		{
			double firepow = 2;
			double gravstrenght = -50;
			double tarx, tary,tarhead_deg, tarvel;
			
			(enemyList.get(target)).setPower(gravstrenght);
			tarx = (enemyList.get(target)).getX();
			tary = (enemyList.get(target)).getY();
			tarhead_deg = (enemyList.get(target)).getHeading();
			tarvel = (enemyList.get(target)).getVelocity();
			
			//Intercept intercept = new Intercept();
		//	intercept.calculate(currX, currY, tarx, tary, tarhead_deg, tarvel, firepow, /*Angvel*/ 0);
			
		}
		
		private void spinStrategy(double currX, double currY)
		{
			if(countCirc == false)
			{
				prevHeading = (enemyList.get(target)).getHeading();
				countCirc = true; // We never clear the flag since we will have values later on
			}
			
			else if(countCirc == true)
			{
				System.out.println("LETS predict");
				double firepow = 2;
				double gravstrenght = 500;
				double tarx, tary,tarhead_deg, tarvel;
				double absoluteBearing = getHeading() + (enemyList.get(target)).getBearing();
				
				(enemyList.get(target)).setPower(gravstrenght);
				tarx = (enemyList.get(target)).getX();
				tary = (enemyList.get(target)).getY();
				tarhead_deg = (enemyList.get(target)).getHeading();
				tarvel = (enemyList.get(target)).getVelocity();
				
				double enemyHeadingChange = tarhead_deg - prevHeading;
				prevHeading = tarhead_deg;
				double deltaTime = 0;
				double battleFieldHeight = getBattleFieldHeight(), 
				       battleFieldWidth = getBattleFieldWidth();
				double predictedX = tarx, predictedY = tary;
				//
				//
				while((++deltaTime) * (20.0 - 3.0 * firepow) < getRange(currX, currY, predictedX, predictedY))
				{
					predictedX += Math.sin(tarhead_deg) * tarvel;
					predictedY += Math.cos(tarhead_deg) * tarvel;
					tarhead_deg += enemyHeadingChange;
					if(	predictedX < 18.0 
							|| predictedY < 18.0
							|| predictedX > battleFieldWidth - 18.0
							|| predictedY > battleFieldHeight - 18.0)
					{
					 
							predictedX = Math.min(Math.max(18.0, predictedX), 
							    battleFieldWidth - 18.0);	
							predictedY = Math.min(Math.max(18.0, predictedY), 
							    battleFieldHeight - 18.0);
							break;
					}
				}
					
					double theta = Utils.normalAbsoluteAngleDegrees(Math.atan2
							(predictedX - getX(), predictedY - getY()));
						 
						setTurnRadarRight(Utils.normalRelativeAngleDegrees(
						    absoluteBearing - getRadarHeading()));
						setTurnGunRight(Utils.normalRelativeAngleDegrees(
						    theta - getGunHeading()));
						
						setFire(3);
				
			}
			
			
		//	CircularIntercept intercept = new CircularIntercept();
		//	intercept.calculate(currX, currY, tarx, tary, tarhead, tarvel, firepow, /*Angvel*/ 0);
			
		}
		
		private void strategy(double currX, double currY)
		{
			double firepow = 1;
			double gravstrenght = 500;
			double tarx, tary,tarhead_deg, tarvel;
			
			(enemyList.get(target)).setPower(gravstrenght);
			tarx = (enemyList.get(target)).getX();
			tary = (enemyList.get(target)).getY();
			tarhead_deg = (enemyList.get(target)).getHeading();
			tarvel = (enemyList.get(target)).getVelocity();
			
			//Intercept intercept = new Intercept();
			//intercept.calculate(currX, currY, tarx, tary, tarhead_deg, tarvel, firepow, /*Angvel*/ 0);
			
		}
	}
}
