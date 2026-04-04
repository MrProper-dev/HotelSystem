import java.util.ArrayList;
import java.util.List;

import integration.RoomServiceIntegrationTest;
import unit.RoomServiceTest;
import utils.Test;

public class MainTest {

    private static List<Test> tests = new ArrayList<>();

    public static void main(String[] args) {
        addTests();
        for(Test test : tests){
            test.runAllTests();
        }
    }

    private static void addTests(){
        tests.add(new RoomServiceTest());
        tests.add(new RoomServiceIntegrationTest());
    }

}
