package lld.vendingMachine.states;

import lld.vendingMachine.Coin;
import lld.vendingMachine.Product;
import lld.vendingMachine.VendingMachine;

import java.text.MessageFormat;
import java.util.UUID;

public class HasMoneyState implements State{
    @Override
    public void addCoin(VendingMachine vm, Coin coin) {
        vm.setCurrentTransactionBalance(vm.getCurrentTransactionBalance()+coin.getValue());
        vm.setCurrentState(vm.getHasMoneyState());
    }

    @Override
    public void selectProduct(VendingMachine vm, UUID pdtId) throws Exception {
        Product pdt = vm.getInventory().getProduct(pdtId);
        if(pdt == null)
            throw new Exception("Product not Listed");
        else if(pdt.getPrice() > vm.getCurrentTransactionBalance())
            throw new Exception("Add more money for the product, add more "+ (pdt.getPrice()-vm.getCurrentTransactionBalance()));
        else{
            vm.setSelectedProduct(pdtId);
            vm.setCurrentState(vm.getDispensingState());
        }
    }

    @Override
    public void dispenseProduct(VendingMachine vm) throws Exception {
        throw new Exception("Can't dispense a product before selecting a product");
    }

    @Override
    public void cancel(VendingMachine vm) {
        if(vm.getCurrentTransactionBalance()>0){
            System.out.println(String.format("Refunded %d amount",vm.getCurrentTransactionBalance()));
            vm.setCurrentTransactionBalance(0L);
        }
        vm.setCurrentState(vm.getIdleState());
    }


}
