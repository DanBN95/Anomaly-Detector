package test;
import java.util.Scanner;
import test.Commands.DefaultIO;

public class StandardIO implements DefaultIO {

    Scanner in,out;
    public StandardIO(){
        in=new Scanner(System.in);
    }
    @Override
    public String readText() {
        return in.nextLine();
    }

    @Override
    public void write(String text) {
        System.out.print(text);
    }

    @Override
    public float readVal() {
        return in.nextFloat();
    }

    @Override
    public void write(float val) {
        System.out.print(val);
    }
}
