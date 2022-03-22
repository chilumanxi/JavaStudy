class localVarGcTest {

//    旨在证明临时变量表引用和GC回收的关系
//    当某个临时变量被引用时，GC不对其进行回收


    static int SIZE = 6 * 1024 * 1024;

    public void localVarGc1(){
        byte[] a = new byte[SIZE];  //byte数组被a引用，不可被回收
        System.gc();
    }

    public void localVarGc2(){
        byte[] a = new byte[SIZE];  //a引用完byte数组后又指向null，可被回收
        a = null;
        System.gc();
    }

    public void localVarGc3(){
        {
            byte[] a = new byte[SIZE];  //a虽然离开作用域，但是还是存在临时变量表中，并指向byte数组，所以不可被回收
        }
        System.gc();
    }

    public void localVarGc4(){
        {
            byte[] a = new byte[SIZE];  //a离开作用域，并且声明了变量c，使变量c复用了a的字，此时a被销毁，可被回收
        }
        int c = 10;
        System.gc();
    }

    public void localVarGc5(){          //调用了localVarGc1，在localVarGc1返回后，栈帧被销毁，所以byte数组失去引用，故可被回收
        localVarGc1();
        System.gc();
    }

    public static void main(String args[]){
        localVarGcTest test = new localVarGcTest();
//        test.localVarGc1();
//        test.localVarGc2();
//        test.localVarGc3();
//        test.localVarGc4();
        test.localVarGc5();
    }
}
