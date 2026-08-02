package lld.splitwise.splitStartegy;

import lld.splitwise.models.SplitType;

import java.util.HashMap;
import java.util.Map;

final public class SplitStrategyFactory {
    final static Map<SplitType, SplitStrategy> REGISTRY = Map.of(
            SplitType.EQUAL,new EqualSplitStrategy(),
            SplitType.EXACT,new ExactSplitStartegy()
    ) ;



    public SplitStrategy getStrategy(SplitType splitType){
        if (REGISTRY.get(splitType) != null)
            return REGISTRY.get(splitType);
        else
            throw new RuntimeException("Strategy not found !");
    }
}
