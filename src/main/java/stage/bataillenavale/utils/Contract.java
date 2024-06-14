package stage.bataillenavale.utils;

public class Contract {
    private Contract(){
    }

    public static void checkCondition(boolean condition, String... message){
        if(!condition){
            if (message.length > 0){
                throw new AssertionError(message[0]);
            } else {
                throw new AssertionError();
            }
        }
    }
}
