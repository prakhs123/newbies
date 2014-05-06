package newbies;
import robocode.*;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;

import CTFApi.CaptureTheFlagApi;

public class flagseek extends CaptureTheFlagApi {
	int a[][]=new int[(int) 600/30][(int) 800/30],m,n;
	final double PI = Math.PI;
	public void run() 
	{
		registerMe();
		while(true)
		{
			
		}
		
		
    }
	void antiGravMove() {
   		double xforce = 0;
	    double yforce = 0;
	    double force;
	    double ang;
	    GravPoint p;
		Enemy en;
    	
	    //cycle through all the enemies.  If they are alive, they are repulsive.  Calculate the force on us
		      for(int i=0;i<m;i++)
		      {
		    	  for(int j=0;j<n;j++)
		    	  {	
		    		  if(a[i][j]==1)
		    		  {
		    			  p = new GravPoint(i*30,j*30,-1000);
		    			  force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
		    			  //Find the bearing from the point to us
		    			  ang = normaliseBearing(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
		    			  //Add the components of this force to the total force in their respective directions
		    			  xforce += Math.sin(ang) * force;
		    			  yforce += Math.cos(ang) * force;
		    		  }
		    		  else if(a[i][j]==2)
				      {
				    	  p = new GravPoint(i*30,j*30, 5000);
				    	  force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),1.5);
				    	  ang = normaliseBearing(Math.PI/2 - Math.atan2(getY() - p.y, getX() - p.x)); 
				    	  xforce += Math.sin(ang) * force;
				    	  yforce += Math.cos(ang) * force;
				      }
		    	  }
		      }
	   
	    /**The following four lines add wall avoidance.  They will only affect us if the bot is close 
	    to the walls due to the force from the walls decreasing at a power 3.**/
	    xforce += 5000/Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 3);
	    xforce -= 5000/Math.pow(getRange(getX(), getY(), 0, getY()), 3);
	    yforce += 5000/Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 3);
	    yforce -= 5000/Math.pow(getRange(getX(), getY(), getX(), 0), 3);
	    
	    //Move in the direction of our resolved force.
	    goTo(getX()-xforce,getY()-yforce);
	}
	
	
	
	public void onScannedObject(ScannedObjectEvent e) {
		double absBearing = Math.toRadians(e.getBearing()) + getHeadingRadians();

		if(e.getObjectType().equals("box"))
		{
			a[(int) (getX()+e.getDistance()*Math.sin(absBearing)/30)][(int) (getY()+e.getDistance()*Math.cos(absBearing))]=1;

		}
	}
	public void onMessageReceived(MessageEvent e)
	{
	  	Serializable s=e.getMessage();
	  	for(i=0;i<e.getMessage().length();i++)
	  	{
	  		
	  	}
	}
	void goTo(double x, double y) {
	    double dist = 20; 
	    double angle = Math.toDegrees(absbearing(getX(),getY(),x,y));
	    double r = turnTo(angle);
	    setAhead(dist * r);
	}
	int turnTo(double angle) {
	    double ang;
    	int dir;
	    ang = normaliseBearing(getHeading() - angle);
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
	public double absbearing( double x1,double y1, double x2,double y2 )
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
	double normaliseHeading(double ang) {
		if (ang > 2*PI)
			ang -= 2*PI;
		if (ang < 0)
			ang += 2*PI;
		return ang;
	}
	double normaliseBearing(double ang) {
		if (ang > PI)
			ang -= 2*PI;
		if (ang < -PI)
			ang += 2*PI;
		return ang;
	}
	
}
