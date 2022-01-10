package Utils;

import java.util.Random;
import java.util.UUID;

public class RandomUtils {
    public static String randomString(){
        return UUID.randomUUID().toString();
    }

    public static int randomInt(int min, int max){
        if(min > max){
            int temp = min;
            min = max;
            max = temp;
        } return new Random().nextInt(max - min) + min;
    }
}
