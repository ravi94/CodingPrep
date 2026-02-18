package machineCoding.shoppingKart.services;

import machineCoding.shoppingKart.entities.Coupon;

import java.util.HashMap;
import java.util.Map;

public class CouponService {
    Map<String , Coupon> coupons;

    public CouponService() {
        this.coupons = new HashMap<>();
    }

    public void addCoupon(Coupon coupon) {
        coupons.put(coupon.getCode(), coupon);
        System.out.println("Added coupon: " + coupon.getCode() +
                " - " + coupon.getDiscountStrategy().getDescription());
    }

    public Coupon getCoupon(String code) {
        return coupons.get(code);
    }

    public boolean validateCoupon(String code) {
        Coupon coupon = coupons.get(code);
        return coupon != null && coupon.isActive();
    }

    public void listActiveCoupons() {
        System.out.println("\n========== AVAILABLE COUPONS ==========");
        coupons.values().stream()
                .filter(Coupon::isActive)
                .forEach(coupon -> System.out.println(
                        coupon.getCode() + ": " +
                                coupon.getDiscountStrategy().getDescription()
                ));
        System.out.println("=======================================\n");
    }

}
