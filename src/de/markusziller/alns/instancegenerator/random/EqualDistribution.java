package de.markusziller.alns.instancegenerator.random;

import java.util.List;
import java.util.Random;

public class EqualDistribution implements IProbabilityDistribution {

    @Override
    public <T> List<T> getSubset(List<T> list) {
        // TODO Implementieren
        return null;
    }

    @Override
    public Integer getQuantity() {
        Random random = new Random();
        return random.nextInt();
    }

    @Override
    public Integer getQuantity(Integer lowerBound, Integer upperBound) {


        Random random = new Random();
        Integer generated = random.nextInt(upperBound + 1);
        while (generated < lowerBound || generated > upperBound) {
            generated = random.nextInt(upperBound + 1);
        }

        return generated;
    }

}
