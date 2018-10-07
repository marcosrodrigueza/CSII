package Informatica;

public class GravPoint
{
	public double x,y,power;
	
    public GravPoint(double pX,double pY,double pPower)
    {
        x = pX;
        y = pY;
        power = pPower;
    }
    
    public GravPoint(double pX,double pY)
    {
        x = pX;
        y = pY;
        power = 100; // Default value
    }
    public GravPoint()
    {
        x = 0;
        y = 0;
        power = 100; // Default value +100
    }
}
