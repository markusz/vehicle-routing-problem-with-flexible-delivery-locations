package de.markusziller.alns.instancegenerator.random;

import java.util.List;

public interface IProbabilityDistribution {

    public <T> List<T> getSubset(List<T> list);

    public Integer getQuantity();

    public Integer getQuantity(Integer lowerBound, Integer upperBound);
}
