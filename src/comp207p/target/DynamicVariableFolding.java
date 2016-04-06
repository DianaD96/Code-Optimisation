package comp207p.target;

public class DynamicVariableFolding {
    public int methodOne() { //works
        int a = 42;
        int b = (a + 764) * 3; //806*3=2418
        a = b - 67; //2351
        return b + 1234 - a; //3652-2351=1301
    }

    public boolean methodTwo() { //works?
        int x = 12345;
        int y = 54321;
        System.out.println(x < y);
        y = 0;
        return x > y;
    }

    public int methodThree() { //works
        int i = 0;
        int j = i + 3; //3
        i = j + 4; //7
        j = i + 5; //12
        return i * j; //84
    }
    
    public int methodFour(){ //works?
        int a = 534245;
        int b = a - 1234; //533011
        System.out.println((120298345 - a) * 38.435792873);
        for(int i = 0; i < 10; i++){
            System.out.println((b - a) * i);
        }
        a = 4;
        b = a + 2;
        return a * b;
    }
}