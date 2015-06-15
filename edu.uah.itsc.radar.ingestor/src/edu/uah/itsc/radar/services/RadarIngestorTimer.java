package edu.uah.itsc.radar.services;

public class RadarIngestorTimer extends Thread {

	int time;
	
	public RadarIngestorTimer(int time){
		this.time = time;
	}
	
	@Override
	public void run(){
		
		try {
			sleep(this.time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
