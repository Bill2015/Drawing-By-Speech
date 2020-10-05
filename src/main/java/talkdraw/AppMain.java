package talkdraw;

public class AppMain {
    public static void main(String[] args) {
        try{
            App.main(args);
        }catch(Exception ex){
            System.out.println("在AppMain的Exception");
            ex.printStackTrace();
        }catch(Error err){
            System.out.println("在AppMain的Error");
            err.printStackTrace();
        }
    }
}