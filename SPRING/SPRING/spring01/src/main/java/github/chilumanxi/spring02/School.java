package github.chilumanxi.spring02;

import github.chilumanxi.aop.ISchool;
import github.chilumanxi.spring01.Student;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@Data
public class School implements ISchool {

    // Resource
    @Autowired(required = true) //primary
            Klass class1;

    @Resource(name = "student100")
    Student student100;

    @Override
    public void ding(){

        System.out.println("Class1 have " + this.class1.getStudents().size() + " students and one is " + this.student100);

    }

}
