import Controller.Checkout;

public class Kata {


    public static void main(String[] args) {
        Checkout checkout = Checkout.getInstance();
        checkout.createInventoryItem("Tomato Soup", "Unit", 1.00);
        double total = checkout.scanAnItemAtCheckout("Tomato Soup");
        System.out.println(total);
    }
}
