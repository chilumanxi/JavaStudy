import java.util.List;
import java.util.Vector;

public class Student {
    private int id;
    private String name;
    private List<WebPage> history = new Vector<WebPage>();

    public Student(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public void visit(WebPage wp){
        this.history.add(wp);
    }
}
