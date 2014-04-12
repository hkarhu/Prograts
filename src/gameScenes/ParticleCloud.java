package gameScenes;


import java.util.Vector;

class Particle {
	
	private float x, y, vx, vy;
	private int life;
	
	public Particle(float x, float y, float vx, float vy, int life) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.life = life;
	}
	
	public void advance(){
		x += vx;
		y += vy;
		life--;
	}

	public boolean isAlive() {
		return life > 0;
	}

	public float getX() {
		return x;
	}
	
	public float getY(){
		return y;
	}

	public int getLife() {
		return life;
	}
	
}

public class ParticleCloud {
	
	Vector<Particle> particles;
	
	private int numParticles = 0;
	
	public ParticleCloud() {
		particles = new Vector<Particle>();
	}
	
	public void advance(){
		for(int i=0; i < particles.size(); i++){
			Particle p = particles.get(i);
			p.advance();
			if(!p.isAlive()){
				particles.remove(i);
				addNewParticle();
			}
		}
	}
	
	public Vector<Particle> getParticles(){
		return particles;
	}

	public void setNumParticles(int numParticles) {
		this.numParticles = numParticles;
		if(numParticles < particles.size()){
			particles.setSize(numParticles);
		} else if(numParticles > particles.size()){
			for(int i=0; i < numParticles-particles.size(); i++){
				addNewParticle();
			}
			
		}
	}
	
	public void addNewParticle(){
		particles.add(new Particle(0, 0, (float)(1f-Math.random()*2)*0.01f, (float)(1-Math.random()*2)*0.01f, (int)(Math.random()*1000)));
	}

}
