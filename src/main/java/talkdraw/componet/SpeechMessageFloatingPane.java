package talkdraw.componet;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import talkdraw.misc.ImageViewBuilder;

/** <p>繼承了 {@link StackPane} </p>
 *  <p>顯示目前程式狀態的 Floating {@code StackPane}</p> 
 *  <p>基本上就是顯示圖片個基本指令</p>*/
public class SpeechMessageFloatingPane extends StackPane {
    
    private String nowStr;
    /** 顯示指令名稱的 {@link Text} */
    private Text msgText;
    /** 顯示 "等待" 的 {@link Text}  */
    private Text dateText;
    /** 點點的 Counter */
    private int dot;
    /** 最多顯示幾個點點 */
    private final int MAX_DOT = 3;
    /** Icon */
    private ImageView icon;
    /** 語音輸入的狀態 Delay 計數 */
    private int statusDelayCount = 0;
    /** 語音輸入的狀態 Delay 計數 */
    private final int MAX_STATUS_DELAY = 6;
    public SpeechMessageFloatingPane(){
        msgText = new Text("");
        msgText.setFill( Color.WHITE );
        msgText.setFont( new Font("微軟正黑體", 14) );
        msgText.setStyle("-fx-font-weight: bold");

        dateText = new Text("");
        dateText.setFill( Color.LIGHTGREEN );
        dateText.setFont( new Font("微軟正黑體", 12) );
        dateText.setStyle("-fx-font-weight: bold");
        dateText.setTranslateY( 1 );

        icon = new ImageViewBuilder("/textures/speechStatus.png").reSize(16, 16).build();
        icon.setTranslateY(0);

        HBox hBox = new HBox( 8, new Text("    "), icon, msgText, dateText );
        setStyle( "-fx-background-color: transparent" );
        StackPane.setAlignment( hBox, null );
        getChildren().add( hBox );
        setBorder( new Border( new BorderStroke( 
                                            Color.DARKGRAY, 
                                            BorderStrokeStyle.SOLID, 
                                            new CornerRadii(20), 
                                            new BorderWidths(2) ) ) );

        setPrefHeight(18);
        setMinHeight(18);
        setHeight(18);

        setPrefWidth(300);
        setMinWidth(300);
        setWidth(300);
     
        setOpacity(0.8);
        setManaged(false);

        setTranslateX( 100 );
        setTranslateY( 600 );

        changeToSpeak();
    }
    //====================================================================================
    /** 設定顯示的訊息 {@code Text} 
     *  @param text 指令名稱*/
    public void changeMessage(String text){
        msgText.setText( text );
        dateText.setText( "         時間：" + new SimpleDateFormat("HH:mm:ss").format( new Date() ) );
        nowStr = text;
        statusDelayCount = 0;
    }

    /** 設定顯示的訊息 {@code Text} 
     *  @param text 指令名稱*/
    public void changeToSpeak(){
        msgText.setText( "請說話" );
        dateText.setText( "" );
        nowStr = "請說話";
    }

    //====================================================================================
    /** 顯示 請說話 的 "....." 動畫*/
    public void animation(){
        if( nowStr.equals( "請說話" ) ){
            String temp = "請說話";
            for(int i = 0; i < dot; i++){
                temp = temp.concat(". ");
            }
                
            final String ftemp = temp;
            Platform.runLater(() -> {
                boolean b = false;
                while (!b) {
                    b = false;
                    try {
                        Thread.sleep(5);
                        msgText.setText( ftemp );
                        b = true;
                    } catch (Exception e) {}
                }
            });
            dot = ((dot + 1) % (MAX_DOT + 1));

        }
        else{
            if( ++statusDelayCount >= MAX_STATUS_DELAY ){
                statusDelayCount = 0;
                changeToSpeak();
            }
        }
    }
    //====================================================================================
    /** 設定圖示顯示 */
    public void setIconVisible(boolean value){
        icon.setVisible(value);
    }
}