package lld.splitwise.splitStartegy;

import lld.splitwise.models.Split;
import lld.splitwise.models.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExactSplitStartegy implements SplitStrategy{
    @Override
    public List<Split> computeSplits(BigDecimal total, List<User> participants, List<BigDecimal> amounts) {
        if (participants.size() != amounts.size())
            throw new IllegalArgumentException("Need one amount per participant");
        BigDecimal sum = amounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sum.compareTo(total) != 0)
            throw new IllegalArgumentException("Exact splits sum to " + sum + " but total is " + total);
        List<Split> splits = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) splits.add(new Split(participants.get(i), amounts.get(i)));
        return splits;
    }
}
