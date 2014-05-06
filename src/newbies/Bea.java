package newbies;
import robocode.*;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;

import CTFApi.CaptureTheFlagApi;
public class Bea extends CaptureTheFlagApi {
	public void run()
	{
		while(true)
		{
			ahead(20);
			
		}
	}
	public void onHitObstacle(HitObstacleEvent e)
	{
		System.out.println(e.getBearing());
	}
	

}
