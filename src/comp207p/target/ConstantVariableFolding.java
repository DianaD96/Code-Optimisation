package comp207p.target;

public class ConstantVariableFolding
{
    public int methodOne(){ //works
        int a = 62;
        int b = (a + 764)*3; //826*3=2478
        int c = a + 10; //72
        return b + 1234 - a; //2060-a = 1998
    }

    public double methodTwo(){ //works
        double i = 0.67;
        int j = 1;
        return i + j;
    }

    public boolean methodThree(){
        int x = 12345;
        int y = 54321;
        return x > y;
    }

    public boolean methodFour(){
        long x = 4835783423L;
        long y = 400000;
        long z = x + y; // 4836183423
        return x > y;
    }

}