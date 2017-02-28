package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

public interface ParticleInitializer {

	void initParticle(Particle p, Random r);

}
