package Informatica;
import robocode.*;

public class EnemyBot 
{
	  static final double fullTurn = 360.0;
	  
	  
	  private String name;
	  private double energy;
	  private double bearing;
	  private double distance;
	  private double heading;
	  private double velocity;
	  private long timeac;
	  private double x;
	  private double y;
	  //
	  
	  public EnemyBot(ScannedRobotEvent e, double myx, double myy, double myheading)
	  {
		  this.name = e.getName();
		  this.energy = e.getEnergy();
		  this.bearing = e.getBearing();
		  this.distance = e.getDistance();
		  this.heading = e.getHeading();
		  this.velocity = e.getVelocity();
		  this.timeac = e.getTime();
		  calcEnemyCordsXY(myx, myy, myheading);
	  }
	  
	  //Set and get methods
	  public String getName() 
	  {
	    return name;
	  }
	  public double getBearing()
	  {
	   return bearing;
	  }
	  public double getDistance()
	  {
	    return distance;
	  }
	  public double getHeading()
	  {
	    return heading;
	  }
	  public double getVelocity()
	  {
	    return velocity;
	  }
	  public double getEnergy()
	  {
	    return energy;
	  }
	  public long lastAct()
	  {
		  return timeac;
	  }
	  public double getX()
	  {
		  return x;
	  }
	  public double getY()
	  {
		  return y;
	  }
	  public void setName(String newname)
	  {
	  name = newname;
	  }
	  public void setDistance(double d)
	  {
	  distance = d;
	  }
	  public void setHeading(double h)
	  {
	    heading = h;
	  }
	  public void setVelocity(double v)
	  {
	    velocity = v;
	  }
	  public void setEnergy(double e)
	  {
	   energy = e;
	  }
	  public void setBearing(double b)
	  {
		 bearing = b;
	  }
	  
	  public void update(double d, double h, double v, double e, double b, long t, double myx, double myy, double myheading)
	  {
		  distance = d;
		  heading = h;
		  velocity = v;
		  energy = e;
		  bearing = b;
		  timeac = t;
		  calcEnemyCordsXY(myx, myy, myheading);
	  }
	  
	  public void update(ScannedRobotEvent e, double myx, double myy, double myheading)
	  {
		  distance = e.getDistance();
		  heading = e.getEnergy();
		  velocity = e.getVelocity();
		  energy = e.getEnergy();
		  bearing = e.getBearing(); 
		  timeac = e.getTime();
		  calcEnemyCordsXY(myx, myy, myheading);
	  }
	  
	  /*Based on Sing Li DuckBot XY algorithm*/
	  
	  private void calcEnemyCordsXY(double mx, double my, double heading) 
	  {

	        // remove the sign
	       double absoluteBearing = ( heading + this.getBearing()) % fullTurn;
	       double distanceWhenSighted = this.getDistance();
	       
	       absoluteBearing = Math.toRadians(absoluteBearing);   // convert to radians

	       this.x = mx + Math.sin(absoluteBearing) * distanceWhenSighted;
	       this.y = my + Math.cos(absoluteBearing) * distanceWhenSighted;

	       System.out.println( this.getName() + " is at " + x + ", " + y); //Visual Debugging

	  }
	  
	  public boolean equals(Object obj) //Done to be able to compare EnemyBot objets from the iterator in the vector that will collect enemies
	  {
		   if (obj instanceof EnemyBot)
		   {
		 //    System.out.println("in obj is " + ((Duck) obj).getName() + " me is " + duckInfo.getName());
		      if (((EnemyBot) obj).getName().equals(this.getName()) )
		        return true;
		   }
		//   System.out.println("equals called");
		   return false;
	 }

}


