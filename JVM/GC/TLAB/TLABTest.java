public class TLABTest {
    public static void alloc(){
        byte[] b = new byte[2];
        b[0] = 1;
    }

    public static void main(String args[]){
        long b = System.currentTimeMillis();
        for(int i = 0; i < 10000000; i ++){
            alloc();
        }
        long e = System.currentTimeMillis();
        System.out.println(e - b);
    }
}

//-XX:+UseTLAB -Xcomp -XX:-BackgroundCompilation -XX:-DoEscapeAnalysis -server 耗时83毫秒
//-XX:-UseTLAB -Xcomp -XX:-BackgroundCompilation -XX:-DoEscapeAnalysis -server 耗时163毫秒


