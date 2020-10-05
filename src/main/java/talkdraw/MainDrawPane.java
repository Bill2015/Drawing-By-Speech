package talkdraw;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import talkdraw.componet.ClickableImageView;
import talkdraw.componet.RulerPane;
import talkdraw.componet.ScrollSwingPane;

/** <p>主要畫布區 </p>
 *  <p>包含了 {@link UnderDrawPane} 與 {@link UpponDrawPane}</p>
 *  <p>附註 1： {@link UnderDrawPane} 是顯示底圖的，就是在更改畫布大小時所用的虛線</p>
 *  <p>附註 2： {@link UpponDrawPane} 繪圖的區域 </p>
 *  @see UnderDrawPane
 *  @see UpponDrawPane*/
public class MainDrawPane extends BorderPane{
    /** 水平 與 垂直 直尺 */
    private final RulerPane Rule[] = new RulerPane[2];
    /** 卷軸面板 {@code Pane} 用來包住 塗鴉區 */
    private final ScrollSwingPane scrollPane;
    /** 中間主要畫布 */
    private final UnderDrawPane underDraw;
    /** 上層畫布區 */
    private final UpponDrawPane upponDraw;
    /** 把 Detail Pane 傳進來做參照 */
    //private final DetailPane detailPane; 
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;
    public MainDrawPane(App APP){
        this.APP = APP;
        //兩個尺規
        Rule[0] = new RulerPane( RulerPane.HORIZON );
        Rule[1] = new RulerPane( RulerPane.VERTICAL  );
        setTop( Rule[0] );
        setLeft( Rule[1] );

        upponDraw = new UpponDrawPane( APP );
        underDraw = new UnderDrawPane( this, APP );
        
        scrollPane = new ScrollSwingPane( underDraw );
        //---------------------------------------------------------------------------------
        //監聽器： 監聽水平方向的 Scoll Bar
        scrollPane.hvalueProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
                //水平(因為 Scroll Bar 的值介於 [0,1] 所以要做換算)
                Rule[0].repaint( scrollPane.getHoffset() );
                //垂直
                Rule[1].repaint( scrollPane.getVoffset() );              
            }
        }); 

        //監聽器： 監聽垂直方向的 Scoll Bar
        scrollPane.vvalueProperty().addListener( new ChangeListener<Number>() {
            @Override
            public void changed( ObservableValue<? extends Number> observable, Number oldValue, Number newValue){
                //水平(因為 Scroll Bar 的值介於 [0,1] 所以要做換算)
                Rule[0].repaint( scrollPane.getHoffset() );
                //垂直
                Rule[1].repaint( scrollPane.getVoffset() );              
            }
        }); 

        setCenter( scrollPane );
        //-------------------------------------------------------------------------
        //監聽器： 假如滑鼠在移動時，重繪直尺刻度
        underDraw.setOnMouseMoved(e ->{
            int x = (int)e.getX() + 25, y = (int)e.getY();
            //水平                 
            Rule[0].repaint( x, y, scrollPane.getHoffset() ); 
             //垂直
            Rule[1].repaint( x, y, scrollPane.getVoffset() );
        });
        //監聽器： 假如滑鼠在移動時，重繪直尺刻度
        underDraw.setOnMouseDragged(e ->{
            int x = (int)e.getX() + 25, y = (int)e.getY();
            //水平                 
            Rule[0].repaint( x, y, scrollPane.getHoffset() ); 
            //垂直
            Rule[1].repaint( x, y, scrollPane.getVoffset() );
        });
        //setStyle("-fx-background-color: #008000");
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡          功能區(Function)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    public void reSizeDrawArea(int newWidth, int newHeight){
        Platform.runLater( () -> {
            upponDraw.reSize( newWidth, newHeight );
            underDraw.reSize( newWidth, newHeight );
        });
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡          回傳區(Getter)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** @return 取得 {@code 下層畫布} */
    public UnderDrawPane getUnderPane(){ return underDraw; }
    /** @return 取得 {@code 上層畫布} */
    public UpponDrawPane getUpponPane(){ return upponDraw; }
    /** 取得繪圖區的寬度 
     *  @return 寬度 {@code [Int]}*/
    public int getDrawWidth(){ return underDraw.getDrawWidth(); }
    /** 取得繪圖區的高度 
     *  @return 高度 {@code [Int]}*/
    public int getDrawHeight(){ return underDraw.getDrawHeight(); }
    
    /** 取得尺規 @return 尺規 {@code [RulerPane[]]} */
    public RulerPane[] getRulerPane(){ return Rule; }

     /** 取得目前選擇的圖片 {@code Property} 的 {@link ClickableImageView} 
     *  @return {@code [SimpleObjectProperty<ClickableImageView>]}*/
    public SimpleObjectProperty<ClickableImageView> getSelectImageProperty(){ return upponDraw.getSelectImageProperty(); }
  
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡          設定區(Setter)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定目前選取的圖片
     *  @param newImg 欲設定的新圖片*/
    public void setNowSelectImage( ClickableImageView newImg ){
        upponDraw.setNowSelectImage( newImg );
    }
}

