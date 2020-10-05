package talkdraw.dialog;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import talkdraw.animation.FadeTransitionBuilder;
import talkdraw.componet.MyLabel;
import talkdraw.misc.Int;

/** <p>建立 Loading 效果的視窗，繼承了 {@link Stage}</p> 
 *  <p>總之就是個三個圈圈轉轉轉，然後像動畫一樣播出來</p>*/
public class LoadingStage extends Stage{

    public static final int FAST_TIME = 10;
    public static final int DEFAULT_TIME = 50;
    /** 建構子 
     *  @param title 中間顯示的文字 
     *  @param endTitle 完成時中間顯示的文字*/
    public LoadingStage( String title, String endTitle, int time ){

        //永遠在最上層
        setAlwaysOnTop(true);
        initStyle( StageStyle.UNDECORATED );
        initModality(Modality.WINDOW_MODAL);

        //設定三個圈圈
        Circle c1 = createCircle( 100, Color.web("#00a8f3") );
        Circle c2 = createCircle( 70, Color.web("#3f48cc") );
        Circle c3 = createCircle( 40, Color.web("#00a8f3") );
        
        //設定中間的文字
        MyLabel label = new MyLabel( "", 25, Pos.CENTER, true );
        label.setTextFill( Color.BLACK );

        //建立底層的 Stack Pane
        StackPane pane = new StackPane();
        pane.setAlignment( Pos.CENTER );
        pane.getChildren().addAll( c1, c2, c3, label );
        pane.setStyle( "-fx-background-color: transparent" );

        //建立動畫
        ParallelTransition rotation = new ParallelTransition( 
            getRotationAnimation(c1, 600.0, 10),
            getRotationAnimation(c2, 500.0, 18),
            getRotationAnimation(c3, 400.0, 26)
        );

        //當文字跑完時，就把他淡出
        FadeTransition fadeOut = new FadeTransitionBuilder(1.0, pane).setStart( 0.75 ).fadeOut();
        fadeOut.setOnFinished( e -> {
            close();
            rotation.stop();
        } );

        //設置動態文字
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.setLength( title.length() );
        
        final int range = time;
        //建立 Int 封裝，以好在 Lambda 含是裡做加減
        Int i = new Int(0);
        //動畫每動一毫秒做把字串打亂
        rotation.currentTimeProperty().addListener( (obser, oldVal, newVal) -> {
            int millis = (int)(newVal.toSeconds() * 100);
            //判斷文字是否設定完了
            if( i.val() < title.length()){
                //判斷是否要設定文字了
                if((int)(millis %  range) <= 2 ){
                    strBuilder.setCharAt(i.val(), title.charAt(i.val()));
                    i.add();
                } 
                else{
                    //建立文字亂碼
                    for(int k = i.val(); k < title.length(); k++ )
                        strBuilder.setCharAt( k, ((char)(Math.random() * 94 + 32)) );
                    label.setText( strBuilder.toString() );
                }  
            }
            //文字設定完後，呼叫淡出動畫
            else {
                label.setText( endTitle );
                fadeOut.play();
            }
        } );

        rotation.play();

        initStyle( StageStyle.TRANSPARENT );
        setScene( new Scene( pane ){ { setFill( Color.TRANSPARENT ); }} );
    }
    /** 建構子 
     *  @param title 中間顯示的文字 
     *  @param endTitle 完成時中間顯示的文字
     *  @param parentStage 父 {@link Stage}，當這個視窗還沒關掉時會鎖住父視窗
     *  @param time 值行時間 */
    public LoadingStage( String title, String endTitle, Window parentStage, int time ){
        this(title, endTitle, time);
        initOwner( parentStage );
    }

    /** 創建圓形 {@link Circle} 物件 
     *  @param radius 半徑
     *  @param color 顏色
     *  @return 回傳只有邊框的圓形 {@code [Circle]} */
    private Circle createCircle( int radius, Color color ){
        Circle c = new Circle( radius, Color.TRANSPARENT );
        c.setStrokeWidth( 10 );
        c.setStroke( color );
        c.getStrokeDashArray().addAll( (double)radius*2, 1000.0 );
        

        //設定燈光效果
        //Light.Spot light = new Light.Spot(0, 0, 100.0, 1.0, Color.WHITE); 

        //c.setEffect( new Lighting( light ) );
        c.setSmooth( true );
        return c;
    }

    /** 建立圓形 {@link Circle} 旋轉動畫 
     *  @param c 欲設定的圓形物件
     *  @param angle 總共旋轉角度
     *  @param duration 旋轉時間
     *  @return 回傳平行動畫 {@code [ParallelTransition]} */
    private ParallelTransition getRotationAnimation( Circle c, double angle, double duration ){
        RotateTransition rotate = new RotateTransition( Duration.seconds( duration ) , c );
        rotate.setFromAngle( 0.0 );
        rotate.setByAngle( angle );
        rotate.setRate( 3 );
        rotate.setAutoReverse( true );
        rotate.setCycleCount( 2 );

        FadeTransition fade = new FadeTransitionBuilder( (duration / 10) , c ).setStart( 0.0 ).fadeIn();

        return new ParallelTransition( rotate, fade );
    }
}
