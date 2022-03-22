public class OnStackTest {

    static int HUNDRED_MILLION = 100000000;

    public static class User{
        public int id = 0;
        public String name = "";
    }

    public static void alloc(){
        User u = new User();
        u.id = 1;
        u.name = "henry";
    }

    //在主函数中进行了1亿次alloc()调用来创建对象
    public static void main(String args[]) throws InterruptedException{
        long b = System.currentTimeMillis();
        for(int i = 0; i < HUNDRED_MILLION; i ++){
            alloc();
        }
        long e = System.currentTimeMillis();
        System.out.println("Cost Time: " + (e - b));
    }
}
