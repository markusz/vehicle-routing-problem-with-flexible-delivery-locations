package de.markusziller.alns.instancegenerator.random;

import cern.jet.random.Poisson;
import cern.jet.random.engine.DRand;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PoissonDistribution implements IProbabilityDistribution {

    private final Poisson poisson;
    private final Double lambda;
    private final Random randomGenerator;


    public PoissonDistribution(Double lambda) {
        this.lambda = lambda;
//		this.poisson = new Poisson(lambda, new DRand(new Double(Math.random() * Integer.MAX_VALUE).intValue()));
        this.poisson = new Poisson(lambda, new DRand(new Date()));

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
//		this.poisson = new Poisson(lambda, new DRand(new Double(Math.random() * Integer.MAX_VALUE).intValue()));
//		poisson = new Poisson(lambda, new DRand(new Date()));
        List<T> temp = new ArrayList<>();
        Integer size = list.size();
        Integer number = Math.min(list.size(), poisson.nextInt());

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
//		this.poisson = new Poisson(lambda, new DRand(new Double(Math.random() * Integer.MAX_VALUE).intValue()));
        return poisson.nextInt();
    }

    @Override
    public Integer getQuantity(Integer lowerBound, Integer upperBound) {
//		this.poisson = new Poisson(lambda, new DRand(new Double(Math.random() * Integer.MAX_VALUE).intValue()));
        Integer generated = poisson.nextInt();
        while (generated < lowerBound || generated > upperBound) {
            generated = poisson.nextInt();
        }

        return generated;
    }

}
