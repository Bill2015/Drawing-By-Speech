package talkdraw.componet;

import javafx.application.Platform;
import javafx.geometry.Pos;
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
public class StatusMessageFloatingPane extends StackPane {
    
    /** 顯示指令名稱的 {@link Text} */
    private Text cmdText;
    /** 顯示指令狀態名稱的 {@link Text} */
    private Text statusText;
    /** 顯示 "... 的 {@link Text} */
    private Text endText;
    /** 顯示 "等待" 的 {@link Text}  */
    private Text watText;
    /** 點點的 Counter */
    private int dot;
    /** 最多顯示幾個點點 */
    private final int MAX_DOT = 6;
    /** Icon */
    private ImageView icon;
    public StatusMessageFloatingPane(){
        cmdText = new Text("");
        cmdText.setFill( Color.BLUE );
        cmdText.setFont( new Font("微軟正黑體", 22) );
        cmdText.setStyle( "-fx-stroke: darkblue;-fx-stroke-width: 1px;" );

        statusText = new Text("");
        statusText.setFill( Color.BLUE );
        statusText.setFont( new Font("微軟正黑體", 20) );
        statusText.setStyle( "-fx-stroke: darkblue;-fx-stroke-width: 1px;" );

        watText = new Text("等待");
        watText.setFill( Color.DARKBLUE );
        watText.setFont( new Font("微軟正黑體", 20) );
        watText.setStyle( "-fx-stroke: black;-fx-stroke-width: 1px;" );

        endText = new Text("中");
        endText.setFill( Color.DARKBLUE );
        endText.setFont( new Font("微軟正黑體", 20) );
        endText.setStyle( "-fx-stroke: black;-fx-stroke-width: 1px;" );

        icon = new ImageViewBuilder("/textures/status.png").reSize(20, 20).build();
        icon.setTranslateY(5);
        HBox hBox = new HBox( 8, new Text("    "), icon, cmdText, watText, statusText, endText );

        setStyle( "-fx-background-color: transparent" );
        StackPane.setAlignment( hBox, Pos.CENTER_LEFT );
        getChildren().add( hBox );
        setBorder( new Border( new BorderStroke( 
                                            Color.DARKGRAY, 
                                            BorderStrokeStyle.SOLID, 
                                            new CornerRadii(20), 
                                            new BorderWidths(2) ) ) );

        setPrefHeight(25);
        setMinHeight(25);
        setHeight(25);

        setPrefWidth(500);
        setMinWidth(500);
        setWidth(500);
     
        setOpacity(0.5);
        setManaged(false);

        setTranslateX( 120 );
        setTranslateY( 600 );

        changeBothText("", "指令");
    }
    //====================================================================================
    /** 設定兩者 {@code Text} 
     *  @param cmdText 指令名稱
     *  @param statusText 指令狀態名稱*/
    public void changeBothText(String cmdText, String statusText){
        if( cmdText == "" )
            this.cmdText.setText( "" );
        else
            this.cmdText.setText( String.join("", "<", cmdText, ">")  );
        this.statusText.setText( String.join("", "[", statusText, "]")  );

        this.cmdText.setVisible( true );
        this.watText.setVisible( true );
    }
    //====================================================================================
    /** 設定指令名稱 {@code Text} 
     * @param text 指令名稱*/
    public void changeCommandText( String text ){
        cmdText.setText( String.join("", "【", text, "】")  );
        this.cmdText.setVisible( true );
        this.watText.setVisible( true );
    }
    //====================================================================================
    /** 設定指令名稱 {@code Text} 
     * @param text 指令名稱*/
    public void changeOnlyStatusText( String text ){
        watText.setVisible( false );
        cmdText.setVisible( false );
        statusText.setText( String.join("", "【", text, "】")  );
    }
    //====================================================================================
    /** 設定指令 "狀態" 名稱 {@code Text} 
     * @param text 指令狀態名稱 */
    public void changeStatusText( String text ){
        statusText.setText( String.join("", "[", text, "]")  );
        this.cmdText.setVisible( true );
        this.watText.setVisible( true );
    }
    //====================================================================================
    /** 顯示 等待中 的 "....." 動畫*/
    public void animation(){
        String temp = "中";
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
                    endText.setText( ftemp );
                    b = true;
                } catch (Exception e) {}
            }
        });
        dot = ((dot + 1) % (MAX_DOT + 1));
    }
    //====================================================================================
    /** 設定圖示顯示 */
    public void setIconVisible(boolean value){
        icon.setVisible(value);
    }
}