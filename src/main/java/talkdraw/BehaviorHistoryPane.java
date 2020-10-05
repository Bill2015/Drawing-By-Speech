package talkdraw;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import talkdraw.componet.PagePane;
import talkdraw.componet.PageableNode;

/** 歷史紀錄顯示欄位區塊 */
public class BehaviorHistoryPane extends PagePane{
    /** 設定初始大小 */
    public final int MAX_WIDTH = 2000, MAX_HEIGHT = 90;
    
    public BehaviorHistoryPane(){
        super( 20, 6 );
        
        getStyleClass().add( "filePane" );

        setStyle( "-fx-background-color: #484848;" );

        setPrefWidth(300);
        setMinWidth(0);
        setPrefWidth(MAX_WIDTH);
        //setWidth(120);

        setMinHeight( 0 );
        setPrefHeight( MAX_HEIGHT );
        Platform.runLater(()->{
            updateUI();
        });
    }

    /** 增加動作記錄文字細胞 
     *  @param cmdText 指令名稱
     *  @param actionText 動作名稱*/
    public void addActionText(String cmdText, String actionText){
        Platform.runLater( () -> {
            boolean mux = false;
            while( !mux ){
                mux = false;
                try {
                    Thread.sleep(5);
                    getContentChildren().add( new TextCell(cmdText, actionText) );
                    updateUI();
                    scrollToEnd();
                    mux = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );
    }
    /** 移除第一個 {@link TextCell} */
    public void removeFirstText(){
        getContentChildren().forEach( node -> {
            if( node instanceof PageableNode ){
                getContentChildren().remove( node );
                updateUI();
                return;
            }
        } );
    }
    /** 移除最後一個 {@link TextCell} */
    public void removeLastText(){
        ObservableList<Node> list = getContentChildren();
        for(int i = list.size() - 1; i >= 0; i-- ){
            if( list.get(i) instanceof PageableNode ){
                getContentChildren().remove( i );
                updateUI();
                return;
            }
        }
    }
    /** 清除所有 {@link TextCell} */
    public void clear(){
        getContentChildren().removeIf( node -> node instanceof PageableNode );
    }
    /** 顯示文字的 {@link PagePane} 細胞 */
    class TextCell extends HBox implements PageableNode{
        public TextCell(String cmdText, String actionText){
            //初始化圖片 Icon
            //ImageView icon = new ImageViewBuilder( String.join("", "/textures/file/", type, ".png") ).reSize( 18, 18).align( Pos.CENTER_LEFT ).build();
    
            //初始化指令文字 Cmd Text
            Text cmd = new Text( cmdText );
            cmd.setFill( Color.LIGHTGRAY );
            cmd.setFont( new Font("微軟正黑體", 13) );
    
            //初始化指令動作文字 Action Text
            Text act = new Text( "[" + actionText + "]" );
            act.setFill( Color.LIGHTGRAY );
            act.setFont( new Font("微軟正黑體", 13) );

            //初始化時間文字 Time Text
            String times = new SimpleDateFormat("HH:mm:ss").format( new Date() );
            Text time = new Text( "[" + times + "]" );
            time.setFill( Color.LIGHTGRAY );
            time.setFont( new Font("微軟正黑體", 13) );

            //設定 Style
            getStyleClass().add( "file-cell" );
            getChildren().addAll( cmd, act, time );
            StackPane.setAlignment( this, Pos.TOP_LEFT );
    
            //設定寬度
            setMaxHeight( 22 );
            setMaxWidth( 150 );
            setMinWidth( 150 );
            setPrefWidth( 150 );
        }
    
        
    }
}