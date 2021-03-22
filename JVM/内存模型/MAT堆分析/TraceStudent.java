import java.util.List;
import java.util.Vector;

public class TraceStudent {
    static List<WebPage> webPages = new Vector<WebPage>();
    public static void createWebPages(){
        for(int i = 0; i < 100; i ++){
            WebPage wp = new WebPage();
            wp.SetUrl("www." + Integer.toString(i) + ".com");
            wp.SetContent(Integer.toString(i));
            webPages.add(wp);
        }
    }

    public static void main(String args[]){
        createWebPages();
        Student st3 = new Student(3, "henry");
        Student st5 = new Student(5, "lily");
        Student st7 = new Student(7, "taotao");

        for(int i = 0; i < webPages.size(); i ++){
            if(i % st3.getId() == 0)
                st3.visit(webPages.get(i));
            if(i % st5.getId() == 0)
                st5.visit(webPages.get(i));
            if(i % st7.getId() == 0)
                st7.visit(webPages.get(i));
        }

        webPages.clear();
        System.gc();
    }
}

//使用如下参数
//-XX:+HeapDumpBeforeFullGC
//-XX:HeapDumpPath=D:/stu.hprof

//使用MAT分析stu.hprof