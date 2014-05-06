package newbies;
import robocode.*;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;

import CTFApi.CaptureTheFlagApi;
import robocode.util.Utils;

public class Bloodseeker extends CaptureTheFlagApi {
	double [][]a;
	final double PI = Math.PI;
	String[] myteam;
	Point2D enemyFlag;
	Point2D ownFlag;
	Rectangle2D homeBase;
	Rectangle2D enemyBase;
	boolean flagCaptured = false;
	boolean flagScanned = false;
	boolean baseScanned = false;
	final double FLAGFORCE=10;
	final double FLAGENTRYFORCE=5.8;
	final double OBSTACLEFORCE=-3500;
	final double WALLFORCE=-6000;
	int EnemyBaseX=0,EnemyBaseY=0; 
	int BaseX=0,BaseY=0;
	public void run() 
	{   
		a=new double[30][30];
		registerMe();
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		while(true) {
			UpdateBattlefieldState(getBattlefieldState()); 
			myteam = getTeammates();
			ownFlag=getOwnFlag();
			homeBase = getOwnBase();
			enemyBase = getEnemyBase();
			enemyFlag = getEnemyFlag();
			System.out.println("UpdateBattlefieldState done");
			turnRadarRight(360);			
			execute();						
		}
    }
	void antiGravMove() {
   		double xforce = 0;
	    double yforce = 0;
	    double force;
	    double ang;
	    Gravpoint p,q;
		
    	
	    //cycle through all the obstacles.  If they are alive, they are repulsive.  Calculate the force on us
		      for(int i=0;i<30;i++)
		      {
		    	  for(int j=0;j<30;j++)
		    	  {	 
		    			  if(a[i][j]!=0.0)
		    			  {
		    				  p = new Gravpoint(i*30,j*30,OBSTACLEFORCE);
		    				  force = p.power/Math.pow(getRange(getX(),getY(),p.x,p.y),2);
		    				  ang = absbearing(getX(), getY(), p.x, p.y); 
		    				  xforce += Math.sin(ang) * force;
		    				  yforce += Math.cos(ang) * force;
		    			  }
		    	  }
		      }
		      System.out.println("Obstacle ForceX :"+xforce+"\nObstacle ForceY :"+yforce);
	    /**The following four lines add wall avoidance.  They will only affect us if the bot is close 
	    to the walls due to the force from the walls decreasing at a power 3.**/
	    xforce += WALLFORCE/Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 2);
	    xforce -= WALLFORCE/Math.pow(getRange(getX(), getY(), 0, getY()), 2);
	    yforce += WALLFORCE/Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 2);
	    yforce -= WALLFORCE/Math.pow(getRange(getX(), getY(), getX(), 0), 2);
		      if(flagCaptured==false)
		      {
		    	  
		    	if(flagScanned==false)
		    	{   
		    		if(Math.abs(getX()-enemyFlag.getX())<70 && Math.abs(getY()-enemyFlag.getY())<70)
    				{
    					System.out.println("Flag Near");
    					q=new Gravpoint(enemyFlag.getX(),enemyFlag.getY(),FLAGFORCE);
    					force=q.power;
    					ang = absbearing(getX(), getY(), q.x, q.y); 
    					xforce += Math.sin(ang) * force;
    					yforce += Math.cos(ang) * force;
    					System.out.println("x force flag:"+Math.sin(ang) * force+"\ny force flag:"+Math.cos(ang) * force);
    				}
		    		else
		    		{
		    			System.out.println("Enemy flag not scanned");
		    			p=new Gravpoint(enemyFlag.getX(),enemyFlag.getY(),FLAGENTRYFORCE);
		    			force=p.power;
		    			ang=absbearing(getX(), getY(), p.x, p.y);
		    			xforce += Math.sin(ang) * force;
		    			yforce += Math.cos(ang) * force;
		    			System.out.println("Flag Forces\nXforce:"+Math.sin(ang) * force+"\nYforce:"+Math.cos(ang) * force);
		    		}
		    	}
		    	else
		    	{
		    		if(Math.abs(EnemyBaseX-getX())<40 && Math.abs(EnemyBaseY-getY())<40)
		    				{
		    					System.out.println("Flag Near");
		    					q=new Gravpoint(enemyFlag.getX(),enemyFlag.getY(),FLAGFORCE);
		    					force=q.power;
		    					ang = absbearing(getX(), getY(), q.x, q.y); 
		    					xforce += Math.sin(ang) * force;
		    					yforce += Math.cos(ang) * force;
		    					System.out.println("x force flag:"+Math.sin(ang) * force+"\ny force flag:"+Math.cos(ang) * force);
		    				}
		    		else
		    		{
		    			System.out.println("Enemy flag scanned");
		    			p=new Gravpoint(EnemyBaseX,EnemyBaseY,FLAGENTRYFORCE);
		    			force=p.power;
		    			ang = absbearing(getX(), getY(), p.x, p.y);
		    			xforce += Math.sin(ang) * force;
		    			yforce += Math.cos(ang) * force;
		    			System.out.println("x force Flag Entry:"+Math.sin(ang) * force+"\ny force Flag Entry:"+Math.cos(ang) * force);
		    		}
		    	}
		      }
	      else
	      {
	    	/*  if(baseScanned==false)
	    	  {*/
	    		  if(Math.abs(getX()-homeBase.getCenterX())<70&& Math.abs(getY()-homeBase.getCenterY())<70)
					{
						System.out.println("Base Near");
						q=new Gravpoint(homeBase.getCenterX(),homeBase.getCenterY(),FLAGFORCE);
						force=q.power;
						ang = absbearing(getX(), getY(), q.x, q.y); 
						xforce += Math.sin(ang) * force;
						yforce += Math.cos(ang) * force;
						System.out.println("x force base:"+Math.sin(ang) * force+"\ny force base:"+Math.cos(ang) * force);
					}
	    		  	else
	    		  	{
	    		  		System.out.println("Base not scanned");
	    		  		p=new Gravpoint(homeBase.getCenterX(), homeBase.getCenterY(),FLAGENTRYFORCE);
	    		  		force=p.power;
	    		  		ang = absbearing(getX(), getY(), p.x, p.y); 
	    		  		xforce += Math.sin(ang) * force;
	    		  		yforce += Math.cos(ang) * force;
	    		  		System.out.println("Base Forces\nXforce:"+Math.sin(ang) * force+"\nYforce:"+Math.cos(ang) * force);
	    		  	}
	    	  /*}
	    	 else
	    	  {
	    		  if(Math.abs(getX()-BaseX)<40&& Math.abs(getY()-BaseY)<40)
  					{
  						System.out.println("Base Near");
  						q=new Gravpoint(homeBase.getCenterX(),homeBase.getCenterY(),FLAGFORCE);
  						force=q.power;
  						ang = absbearing(getX(), getY(), q.x, q.y); 
  						xforce += Math.sin(ang) * force;
  						yforce += Math.cos(ang) * force;
  						System.out.println("x force base:"+Math.sin(ang) * force+"\ny force base:"+Math.cos(ang) * force);
  					}
	    		  	else
	    		  	{
	    		  		System.out.println("Base Scanned");
	    		  		p=new Gravpoint(BaseX,BaseY,FLAGENTRYFORCE);
	    		  		force=p.power;
	    		  		ang = absbearing(getX(), getY(), p.x, p.y);
	    		  		xforce += Math.sin(ang) * force;
	    		  		yforce += Math.cos(ang) * force;
	    		  		System.out.println("x force Base Entry:"+Math.sin(ang) * force+"\ny force Base Entry:"+Math.cos(ang) * force);
	    		  	}
	    	  }*/
	      }
		      System.out.println("Net X Force: "+xforce+"\nNet Y Force: "+yforce);
	    setTurnRightRadians(Utils.normalRelativeAngle(
				Math.atan2(xforce + 1/getX() - 1/(getBattleFieldWidth() - getX()), 
						   yforce + 1/getY() - 1/(getBattleFieldHeight() - getY()))
							- getHeadingRadians()) );
			setAhead(120 - Math.abs(getTurnRemaining()));
			setMaxVelocity( 350 / getTurnRemaining() );

	}
	public void onHitObject(HitObjectEvent event) {
		if (event.getType().equals("flag") && enemyFlag.distance(getX(), getY()) < 50 && flagCaptured != true)
		{
			System.out.println("Flag Captured");
			flagCaptured = true;
			setStop(true);
			if(EnemyBaseX!=0 && EnemyBaseY!=0){
			turnRightRadians(absbearing(getX(),getY(),EnemyBaseX,EnemyBaseY)-getHeadingRadians());
			ahead(70);
			}
			else{
				setTurnRadarRight(360);
				setBack(70);
			}
		}
	}
	
	public void onScannedObject(ScannedObjectEvent e) {
		double absBearing = Math.toRadians(e.getBearing()) + getHeadingRadians();

		if(e.getObjectType().equals("box"))
		{
			int x,y;
			x=(int) Math.floor(((getX()+e.getDistance()*Math.sin(absBearing))/30+.5));
			y=(int) Math.floor(((getY()+e.getDistance()*Math.cos(absBearing))/30+.5));
			a[x][y]=e.getDistance();
		}
		else if(e.getObjectType().equals("flag"))
		{
			int tempx=(int) (getX()+e.getDistance()*Math.sin(absBearing));
			int tempy=(int)(getY()+e.getDistance()*Math.cos(absBearing));
			if(enemyBase.contains(tempx,tempy))
			{
				flagScanned=true;
				double t1,t2,t3,t4,t;
				EnemyBaseX=(int)enemyFlag.getX();
				EnemyBaseY=(int)enemyFlag.getY();
				System.out.println("Flag Found");
				System.out.println("X :"+EnemyBaseX+"\nY :"+EnemyBaseY);
				t1=absbearing(enemyFlag.getX(),enemyFlag.getY(),enemyBase.getMaxX(),enemyBase.getMaxY());
				t2=absbearing(enemyFlag.getX(),enemyFlag.getY(),enemyBase.getMaxX(),enemyBase.getMinY());
				t3=absbearing(enemyFlag.getX(),enemyFlag.getY(),enemyBase.getMinX(),enemyBase.getMinY());
				t4=absbearing(enemyFlag.getX(),enemyFlag.getY(),enemyBase.getMinX(),enemyBase.getMaxY());
				t=absbearing(enemyFlag.getX(),enemyFlag.getY(),getX(),getY());
				if(t>0 && t<t1 || t>t4 && t<2*PI)
				{
					EnemyBaseY+=50;
				}
				else if(t<t2 && t>t1)
				{
					EnemyBaseX+=50;
				}
				else if(t>t2 && t<t3)
				{
					EnemyBaseY-=50;
				}
				else if(t>t3 && t< t4)
				{
					EnemyBaseX-=50;
				}
				System.out.println("Flag Entry");
				System.out.println("X :"+EnemyBaseX+"\nY :"+EnemyBaseY);
			}
			else if(homeBase.contains(tempx,tempy))
			{
				baseScanned= true;
				double t1,t2,t3,t4,t;
				BaseX=(int)homeBase.getCenterX();
				BaseY=(int)homeBase.getCenterY();
				System.out.println("Base Found");
				System.out.println("X :"+BaseX+"\nY :"+BaseY);
				t1=absbearing(homeBase.getCenterX(),homeBase.getCenterY(),homeBase.getMaxX(),homeBase.getMaxY());
				t2=absbearing(homeBase.getCenterX(),homeBase.getCenterY(),homeBase.getMaxX(),homeBase.getMinY());
				t3=absbearing(homeBase.getCenterX(),homeBase.getCenterY(),homeBase.getMinX(),homeBase.getMinY());
				t4=absbearing(homeBase.getCenterX(),homeBase.getCenterY(),homeBase.getMinX(),homeBase.getMaxY());
				t=absbearing(homeBase.getCenterX(),homeBase.getCenterY(),getX(),getY());
				if(t>0 && t<t1 || t>t4 && t<2*Math.PI)
				{
					BaseY+=50;
				}
				else if(t<t2 && t>t1)
				{
					BaseX+=50;
				}
				else if(t>t2 && t<t3)
				{
					BaseY-=50;
				}
				else if(t>t3 && t< t4)
				{
					BaseX-=50;
				}
				System.out.println("Base Entry");
				System.out.println("X :"+BaseX+"\nY :"+BaseY);
			}
		}
		antiGravMove();
	}
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		if(!isTeammate(e.getName()) &&  e.getDistance()<250){
			double bulletPower = Math.min(3.0,getEnergy());
			double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
			setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));
			fire(bulletPower);
		}

	}
	public void onHitObstacle(HitObstacleEvent e) {
		setStop(true);
		setBack(20);
		turnRight(30);
		setBack(20);
	}
	public void onHitWall(HitWallEvent e)
	{
		setStop(true);
		turnRight(30);
		setBack(50);
	}
	public void onHitRobot(HitRobotEvent e)
	{
		setBack(20);
	}
	public void onMessageReceived(MessageEvent e)
	{  String s,s1="";
	  	s=e.getMessage().toString();
        System.out.println(s);
        if(s.charAt(0)=='f')
        for(int i=1;i<s.length();i++)
        {
        	char c=s.charAt(i);
        	if(c==' ')
        	{
        	EnemyBaseX= Integer.parseInt(s1);
        	s1="";
        }
        	else if(c=='$'){
        		EnemyBaseY=Integer.parseInt(s1);
        		flagScanned=true;
        	}
        	else{
        		s1+=c;
        	}
        }
        else
        	for(int i=1;i<s.length();i++)
            {
            	char c=s.charAt(i);
            	if(c==' ')
            	{
            	BaseX= Integer.parseInt(s1);
            	s1="";
            }
            	else if(c=='$'){
            	BaseY=Integer.parseInt(s1);
            	baseScanned=true;	
            	}
            	else{
            		s1+=c;
            	}
            }
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
	
}


/**Holds the x, y, and strength info of a gravity point**/
class Gravpoint {
    public double x,y,power;
    public Gravpoint(double pX,double pY,double pPower) {
        x = pX;
        y = pY;
        power = pPower;
    }
}

