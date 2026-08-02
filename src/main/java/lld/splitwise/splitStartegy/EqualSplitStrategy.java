package lld.splitwise.splitStartegy;

import lld.splitwise.models.Split;
import lld.splitwise.models.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EqualSplitStrategy implements SplitStrategy{
    @Override
    public List<Split> computeSplits(BigDecimal total, List<User> participants, List<BigDecimal> ignored) {
        if (participants.isEmpty()) throw new IllegalArgumentException("No participants");
        int n = participants.size();
        BigDecimal share = total.divide(BigDecimal.valueOf(n), 2, RoundingMode.DOWN);
        List<Split> splits = new ArrayList<>(n);
        BigDecimal allocated = BigDecimal.ZERO;
        for (int i = 0; i < n - 1; i++) {
            splits.add(new Split(participants.get(i), share));
            allocated = allocated.add(share);
        }
        // Rounding remainder goes to the last participant so the splits sum EXACTLY to the total.
        splits.add(new Split(participants.get(n - 1), total.subtract(allocated)));
        return splits;
    }
}
