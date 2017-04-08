package de.markusziller.alns.instancegenerator.random;

import java.util.List;

public interface IProbabilityDistribution {

    <T> List<T> getSubset(List<T> list);

    Integer getQuantity();

    Integer getQuantity(Integer lowerBound, Integer upperBound);
}
