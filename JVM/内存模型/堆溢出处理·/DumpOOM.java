import java.util.Vector;

public class DumpOOM {

    static int ALLOC_TIMES = 25;
    static int MBSIZE = 1 * 1024 * 1024;

//    申请25MB的空间，当执行参数-Xmx20m时，会出现堆溢出错误
    public static void main(String args[]) {
        Vector v = new Vector();
        for(int i = 0; i < ALLOC_TIMES; i ++){
            v.add(new byte[MBSIZE]);
        }
    }

//    执行参数    -Xmx20m -Xms5m "-XX:OnOutOfMemoryError=D:\printStack.bat %p" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\a.dump
//    printStack.bat内容为 C:\Progra~1\Java\jdk1.8.0_152\bin\jstack -F %1 > D:\a.txt
//    a.txt存储的是线程转存信息
}
