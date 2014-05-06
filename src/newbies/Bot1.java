package newbies;
import robocode.*;
import robocode.util.Utils;
import newbies.Gravpoint;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import CTFApi.CaptureTheFlagApi;

public class Bot1 extends CaptureTheFlagApi
{   int f=0;
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
	// Constants - Beware, changing these may affect code size!
	static final double	BASE_MOVEMENT 		= 180;	// How far to move on each leg of our journey
	static final double	GUN_FACTOR  		= 500;	// How close someone needs to be before we lock radar - also used in leading.
	static final double	BASE_TURN	   		= Math.PI/2;	// Currently 90 degrees for a box pattern
	static final double	BASE_CANNON_POWER 	= 20;	// How hard to shoot in general 
	
	// Globals
	static double 	movement;
	static double 	lastHeading;
	static String	lastTarget;
	static double	lastDistance;
	public void run() 
	{
		if(f==1)
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
		else{
		registerMe();
		// I had to make space for this...
		setColors(Color.white,Color.black,Color.green);
				
		// So we hit stationary targets.
		setAdjustGunForRobotTurn(true);

		// Set base movement huge so we get to edge of board.
		movement = Double.POSITIVE_INFINITY;
	
		// New target please
		onRobotDeath(null);

		// Use Infinite Radar lock technique to save space - no do while() loop.	
		while(true)
		{
			ownFlag=getOwnFlag();
			homeBase = getOwnBase();
			enemyBase = getEnemyBase();
			enemyFlag = getEnemyFlag();
			turnRadarRight(360);
			execute();
		}
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

	public void onHitWall(HitWallEvent e) 
	{
		if(f==1)
		{
			setStop(true);
			turnRight(30);
			setBack(50);
		
		}
		else{
		// If we haven't reduced the distance, do so now.
		if(Math.abs(movement) > BASE_MOVEMENT)
		{
			movement = BASE_MOVEMENT;
		}
		}
	}
		
	// When someone dies, reset our distance value so we don't get stuck aiming at a ghost
	public void onRobotDeath(RobotDeathEvent e) 
	{
		lastDistance = Double.POSITIVE_INFINITY;
	}
				
	// Do the majority of our bots code.
	public void onScannedRobot(ScannedRobotEvent e) 
	{
		if(f==1){
			if(!isTeammate(e.getName()) &&  e.getDistance()<250){
				double bulletPower = Math.min(3.0,getEnergy());
				double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
				setTurnGunRightRadians(robocode.util.Utils.normalRelativeAngle(absoluteBearing - getGunHeadingRadians()));
				fire(bulletPower);
			}
		}
		else{
		System.out.println(!isTeammate(e.getName()));
		if(!isTeammate(e.getName())){
			System.out.println("count:0");
			double	absoluteBearing = e.getDistance();// - 1 / e.getEnergy();

		// If we've reached the end of a leg, turn and move.
		if(getDistanceRemaining() == 0)
		{
			setAhead(movement = -movement);
			setTurnRightRadians(BASE_TURN);
		}
		
		// Lock onto a new target if this one is closer than our current one.
		if(lastDistance > absoluteBearing)
		{
			lastDistance = absoluteBearing;
			lastTarget = e.getName();
		}
		
		// Lock/Fire if gun is somewhat cool and we're pointed at our selected target.
		if(lastTarget == e.getName())
		{
			if(getGunHeat() < 1 && absoluteBearing < GUN_FACTOR)
			{
				// Fire if gun is cool and we're pointed at target
				if(getGunHeat() == getGunTurnRemaining())
				{
					// No room for getOthers clause
//					setFireBullet(getOthers() * getEnergy() * BASE_CANNON_POWER / absoluteBearing);
					setFireBullet(getEnergy() * BASE_CANNON_POWER / absoluteBearing);
					// Reset distance
					onRobotDeath(null);
				}
													
			// Let this var be equal the the absolute bearing now...
			// and set the radar.
				setTurnRadarLeft(getRadarTurnRemaining());
			}
		
			absoluteBearing = e.getBearingRadians() + getHeadingRadians();

			// Ok, a new gun.  Linear aim with reduced target leading as distance from us increases.  Works really well
			// as we don't spray the walls as much at long distances.
			setTurnGunRightRadians(Math.asin(Math.sin(absoluteBearing - getGunHeadingRadians() + 
				(1 - e.getDistance() / 500) * 
				Math.asin(e.getVelocity() / 11) * Math.sin(e.getHeadingRadians() - absoluteBearing) )));					
		}
		}
		}
	}
	public void onHitObstacle(HitObstacleEvent e) {
		setStop(true);
		setBack(20);
		turnRight(30);
		setBack(20);
	}
	
	
	public void onScannedObject(ScannedObjectEvent e){
		if(f==1)
		{
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
		else{
		double absBearing = Math.toRadians(e.getBearing()) + getHeadingRadians();
		int BaseX,BaseY,EnemyBaseX,EnemyBaseY;
		if(e.getObjectType().equals("flag"))
		{
			int tempx=(int) (getX()+e.getDistance()*Math.sin(absBearing));
			int tempy=(int)(getY()+e.getDistance()*Math.cos(absBearing));
			if(enemyBase.contains(tempx,tempy))
			{
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
				if(t>0 && t<t1 || t>t4 && t<2*Math.PI)
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
				try {
					broadcastMessage("f"+EnemyBaseX+" "+EnemyBaseY+"$");
				} catch (IOException e1) {

				}
			}
			else if(homeBase.contains(tempx,tempy))
			{
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
				try{
					broadcastMessage("b"+BaseX+" "+BaseY+"$");
					}
					catch(Exception e2){
					}
			}
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
	public void onHitRobot(HitRobotEvent e){
		setBack(20);
	}
	public void onMessageReceived(MessageEvent e)
	{f=1;
	}
	}
}
