package de.markusziller.alns.instancegenerator.random;

import cern.jet.random.Normal;
import cern.jet.random.engine.DRand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GaussDistribution implements IProbabilityDistribution {

    //	Normal normal;
//	Double lambda;
    private final Random randomGenerator;
    private Normal gaussian;


    public GaussDistribution(Double mean, Double deviance) {
//		this.lambda = lambda;
        this.gaussian = new Normal(mean, deviance, new DRand(new Date()));
        this.randomGenerator = new Random();
    }

//	@Override
//	public List<Qualification> getSome(List<Qualification> qualifications) {
//		
//		List<Qualification> temp = new ArrayList<>();
//		Integer size = qualifications.size();
//		Integer number = poisson.nextInt();
//		
//		for (int i = 0; i < number; i++) {
//			Integer nr = randomGenerator.nextInt(size);
//			Qualification q = qualifications.get(nr);
//			
//			while(temp.contains(q)){
//				nr = randomGenerator.nextInt(size);
//				q = qualifications.get(nr);
//			}
//			
//			temp.add(q);
//		}
//		
//		return temp;
//	}

    @Override
    public <T> List<T> getSubset(List<T> list) {
//		this.gaussian = new Poisson(lambda, new DRand(new Double(Math.random() * Integer.MAX_VALUE).intValue()));
        gaussian = new Normal(2.0, 3.0, new DRand(new Date()));
        List<T> temp = new ArrayList<>();
        Integer size = list.size();
        Integer number = gaussian.nextInt();

        for (int i = 0; i < number; i++) {
            Integer nr = randomGenerator.nextInt(size);
            T q = list.get(nr);

            while (temp.contains(q)) {
                nr = randomGenerator.nextInt(size);
                q = list.get(nr);
            }

            temp.add(q);
        }

        return temp;
    }

    @Override
    public Integer getQuantity() {
        this.gaussian = new Normal(2.0, 3.0, new DRand(new Date()));
        return gaussian.nextInt();
    }

    @Override
    public Integer getQuantity(Integer lowerBound, Integer upperBound) {
//		this.gaussian = new Normal(2.0, 3.0, new DRand());
        Integer generated = gaussian.nextInt();
        while (generated < lowerBound || generated > upperBound) {
            generated = gaussian.nextInt();
        }

        return generated;
    }

}
