import java.util.*;

public class ListTest {
    public static List<List<List<Integer>>> test = new ArrayList<>();

    public static void main(String args[]){
        List<Integer> a = new ArrayList<>();
        for(int i = 0; i < 5; i ++)
            a.add(i);
        List<List<Integer>> b = new ArrayList<>();
        for(int i = 0; i < 5; i ++)
            b.add(a);
        for(int i = 0; i < 5; i ++)
            test.add(b);

        test.forEach(x -> {
            x.forEach(y ->{
                y.forEach(z -> {
                    System.out.println(z);
                });
            });
        });
    }
}
