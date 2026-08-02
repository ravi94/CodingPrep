package lld.splitwise;

import lld.splitwise.models.*;
import lld.splitwise.splitStartegy.SplitStrategyFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ExpenseManager {
    SplitStrategyFactory splitStrategyFactory;
    Map<String , User> users;
    List<Expense> expenses;
    BalanceSheet balanceSheet;

    ExpenseManager(){
        splitStrategyFactory = new SplitStrategyFactory();
        this.users = new ConcurrentHashMap<>();
        this.expenses = new ArrayList<>();
        this.balanceSheet = new BalanceSheet();
    }

    public void addUser(User user){
        this.users.put(user.getId(),user);
    }

    public User getUser(String userId){
        User u = this.users.get(userId);
        if(u != null)
            return u;
        else
            throw new RuntimeException(String.format("user not found wit id %s !",userId));
    }

    public void addExpense(String desc ,BigDecimal amount, String paidByUser , List<String> participants ,List<BigDecimal> meta, SplitType splitType){
        User paidUser = getUser(paidByUser);
        List<User> participantUsers = participants.stream().map(this::getUser).toList();

        List<Split> splits = this.splitStrategyFactory.getStrategy(splitType)
                .computeSplits(amount,participantUsers,meta);

        Expense expense = new Expense(desc, UUID.randomUUID().toString(),
                amount,paidUser,splits, LocalDateTime.now());

        balanceSheet.apply(expense);
        expenses.add(expense);
    }

    public Map<String , BigDecimal> balancesForUser(String userId){
        getUser(userId); // for user validation
        Map<String , BigDecimal> view = new HashMap<>();
        for(User other: users.values()){
            if (other.getId().equals(userId)) continue;
            BigDecimal owing = balanceSheet.amountOwed(other.getId(),userId);
            BigDecimal owed = balanceSheet.amountOwed(userId,other.getId());

            BigDecimal net = owed.subtract(owing) ; // +ve means owed

            if(net.compareTo(BigDecimal.ZERO) !=0)
                view.put(other.getId(),net);
        }

        return view;
    }

    public List<Settlement> simplifyAll(){
        return new SettlementSimplifier().minimise(balanceSheet.netBalances());
    }
}
