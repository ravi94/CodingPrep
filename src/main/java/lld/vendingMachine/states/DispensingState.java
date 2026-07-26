package lld.vendingMachine.states;

import lld.vendingMachine.Coin;
import lld.vendingMachine.Product;
import lld.vendingMachine.VendingMachine;

import java.sql.SQLOutput;
import java.util.UUID;

public class DispensingState implements State{
    @Override
    public void addCoin(VendingMachine vm, Coin coin) throws Exception {
        throw new Exception("Can't add coin while dispensing a product");
    }

    @Override
    public void selectProduct(VendingMachine vm, UUID product) throws Exception {
        throw new Exception("Can't select product while dispensing a product");
    }

    @Override
    public void dispenseProduct(VendingMachine vm) throws Exception {
        UUID pdtId = vm.getSelectedProduct();
        if(pdtId == null  || !vm.getInventory().hasProduct(pdtId)){
            if(vm.getCurrentTransactionBalance()>0)
                vm.setCurrentState(vm.getHasMoneyState());
            else
                vm.setCurrentState(vm.getIdleState());

            throw new Exception("no product to dispense");
        }
        Product pdt = vm.getInventory().getProduct(pdtId);

        System.out.println(String.format("Dispensing product %s",pdt.getName()));
        long balanceLeft = vm.getCurrentTransactionBalance()-pdt.getPrice();

        //reduce the stock
        vm.getInventory().releaseProduct(pdtId);

        //add money to vendin machine
        vm.setVendingMachineBalance(vm.getVendingMachineBalance()+pdt.getPrice());

        //set pdt to null
        vm.setSelectedProduct(null);

        vm.setCurrentTransactionBalance(balanceLeft);

        if(balanceLeft>0)
            vm.setCurrentState(vm.getHasMoneyState());
        else
            vm.setCurrentState(vm.getIdleState());
    }

    @Override
    public void cancel(VendingMachine vm) throws Exception {
        throw new Exception("Can't cancel while dispensing a product");
    }
}


