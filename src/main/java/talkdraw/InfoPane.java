package talkdraw;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import talkdraw.componet.MyLabel;

public class InfoPane extends BorderPane{
    /** 設定初始大小 */
    public final int MAX_WIDTH = 2000, MAX_HEIGHT = 90;
    /** 標題初始大小 */
    private final int TITLE_HEIGHT = 20;
    /** 顯示資訊的 TextArea */
    private VBox infoArea;
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;
    public InfoPane(App APP) {
        this.APP = APP;
        
        //標題
        MyLabel label = new MyLabel( "        訊息回饋欄", 18, Color.LIGHTGRAY, "-fx-font-weight: bold" );
        StackPane.setAlignment( label, Pos.CENTER_LEFT );
        StackPane titlePane = new StackPane( label );
        titlePane.setPrefHeight( TITLE_HEIGHT );
        titlePane.setMinHeight( TITLE_HEIGHT );
        titlePane.setMaxHeight( TITLE_HEIGHT );
        titlePane.setStyle("-fx-background-color: linear-gradient(to bottom, #131313, #2c2c2c);");
        setTop( titlePane );

        //文字顯示區域
        infoArea = new VBox();
        infoArea.setStyle("-fx-background-color: #484848;");
        infoArea.setPadding( new Insets(1, 5, 1, 5) );
        infoArea.setPrefHeight( MAX_HEIGHT );
        infoArea.setId("outputText");
        infoArea.getStyleClass().setAll("null");
        infoArea.setBorder(new Border(new BorderStroke(Color.BLACK, Color.web("#2c2c2c"), Color.BLACK, Color.BLACK,
                                BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
                                        CornerRadii.EMPTY, new BorderWidths(5), Insets.EMPTY)));
        setCenter( infoArea );

        setMinHeight( 0 );
        setPrefHeight( MAX_HEIGHT );
        setMinWidth( 0 );
        setPrefWidth(MAX_WIDTH);
    }
    /** 印出文字 
     *  @param text 文字 */
    public void println(String text){
        Platform.runLater(( ) -> {
            ObservableList<Node> list = infoArea.getChildren();
            int size = list.size();
            if( size >= 3 ){
                list.remove(0);
                ((Text)list.get( list.size() - 1 )).setFill( Color.WHITE );
                list.add( new MyText( text, Color.GOLD ) );
            }
            else if( size >= 1 ){
                ((Text)list.get( list.size() - 1 )).setFill( Color.WHITE );
                list.add( new MyText( text, Color.GOLD ) );
            }
            else list.add( new MyText( text, Color.GOLD ) );
        });
    }
    /** 自我定義的文字輸入框 */
    class MyText extends Text{
        MyText( String text ){
            super(text);
            setFill( Color.WHITE );
            setFont( new Font("微軟正黑體", 12) );
        }
        MyText( String text, Color color ){
            this(text);
            setFill( color );
            setFont( new Font("微軟正黑體", 12) );
        }
    }
}