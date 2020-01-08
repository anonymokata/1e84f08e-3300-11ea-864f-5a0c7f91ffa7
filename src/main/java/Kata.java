import Controller.Checkout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.applet.Main;
@SpringBootApplication
public class Kata {



    public static void main(String[] args) {
        Checkout checkout = new Checkout();

        checkout.createInventoryItem("Tomato Soup", "Unit", 1.50);
        checkout.createInventoryItem("Chicken Soup", "Unit", 1.00);
        checkout.createInventoryItem("Ground Beef", "Weighted", 3.00);
        checkout.createDiscount("Ground Beef", "XforyBeef", 100, 4, "XForY", "Percentage");
        //checkout.createDiscount("Chicken Soup", "ChickenSoupBXGY", 1.00, 1, "BXGY", "Currency");
        checkout.scanAnItemAtCheckout("Tomato Soup");
        checkout.scanAnItemAtCheckout("Chicken Soup");
        checkout.scanAnItemAtCheckout("Ground Beef", 10);
        double total = checkout.scanAnItemAtCheckout("Chicken Soup");

        System.out.println(total);
    }
}
