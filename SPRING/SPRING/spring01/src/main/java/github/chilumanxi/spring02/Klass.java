package github.chilumanxi.spring02;

import github.chilumanxi.spring01.Student;
import lombok.Data;

import java.util.List;

@Data
public class Klass {

    List<Student> students;

    public void dong() {
        System.out.println(this.getStudents());
    }
}
