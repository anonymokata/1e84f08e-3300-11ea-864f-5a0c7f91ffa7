import Controller.Checkout;

public class Kata {

    public static void main(String[] args) {
        Checkout checkout = new Checkout();

        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createInventoryItem("Chicken Soup", "Unit", 1.00);
        checkout.createInventoryItem("Ground Beef", "Weighted", 3.00);
        checkout.createDiscount("Ground Beef", "XforyBeef", 50, 2, "XForY", "Percentage");
        checkout.createDiscount("Chicken Soup", "ChickenSoupBXGY", 1.00, 1, "BXGY", "Currency");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");
        checkout.deleteAnItemAtCheckout("Chicken Soup", 1);
        checkout.scanAnItemAtCheckout("Ground Beef", 2);

        System.out.println(checkout.getTotal());
    }
}
