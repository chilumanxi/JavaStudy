public class HeapAlloc {
    static int MBSIZE = 1 * 1024 * 1024;

    private  void getMemory(){
        System.out.print("maxMemory=");
        System.out.println(Runtime.getRuntime().maxMemory() + " bytes");
        System.out.print("free mem=");
        System.out.println(Runtime.getRuntime().freeMemory() + " bytes");
        System.out.print("total mem=");
        System.out.println(Runtime.getRuntime().totalMemory() + " bytes");
    }


    public static void main(String args[]){
        HeapAlloc test = new HeapAlloc();

        test.getMemory();

        byte[] arr = new byte[MBSIZE];
        System.out.println("分配了1MB空间给数组");

        test.getMemory();

        arr = new byte[4 * MBSIZE];
        System.out.println("分配了4MB空间给数组");

        test.getMemory();



    }
}


