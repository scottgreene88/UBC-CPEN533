import java.util.*;
import java.util.concurrent.TimeUnit;

public class HelloWorld {


    public static void main(String[] args)
    {

        for ( Integer i = 0; i <= 50; i++  )
        {
            System.out.print("Hello World!" +  i.toString());
            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }


    }
}
