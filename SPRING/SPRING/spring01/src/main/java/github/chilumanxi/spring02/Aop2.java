package github.chilumanxi.spring02;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class Aop2 {
    @Pointcut(value="execution(* github.chilumanxi.*.Klass.*dong(..))")
    public void point(){

    }

    @Before(value="point()")
    public void before(){
        System.out.println("========>begin klass dong...");
    }

    @AfterReturning(value = "point()")
    public void after(){
        System.out.println("========>after klass dong...");
    }

    @Around("point()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("========>around begin klass dong");
        joinPoint.proceed();
        System.out.println("========>around after klass dong");

    }
}
