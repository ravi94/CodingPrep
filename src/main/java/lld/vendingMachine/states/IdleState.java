package lld.vendingMachine.states;

import lld.vendingMachine.Coin;
import lld.vendingMachine.Product;
import lld.vendingMachine.VendingMachine;

import java.util.UUID;

public class IdleState implements State{
    @Override
    public void addCoin(VendingMachine vm, Coin coin) {
        vm.setCurrentTransactionBalance(vm.getCurrentTransactionBalance()+coin.getValue());
        vm.setCurrentState(vm.getHasMoneyState());
    }

    @Override
    public void selectProduct(VendingMachine vm, UUID product) throws Exception {
        throw new Exception("Add money before selecting a product");
    }

    @Override
    public void dispenseProduct(VendingMachine vm) throws Exception {
        throw new Exception("Can't dispense a product before selecting a product");
    }

    @Override
    public void cancel(VendingMachine vm) {
    }
}
