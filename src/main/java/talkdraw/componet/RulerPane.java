package talkdraw.componet;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;

public final class RulerPane extends StackPane{
	/** @apiNote 0 = 水平、 1 = 垂直 */
    private final short TYPE;
    /** 紅線位置 */
    private Rectangle redLineRect;
    /** 用來存放尺規的區塊 */
    private Rectangle ruleRect;
    /** 設定水平 or 垂直 */
    public final static short HORIZON = 0, VERTICAL = 1; 
    /** 水平與垂直的寬度 (最大 SIZE ) */
    public final short MAX_WIDTH = 2080 + 400, MAX_HEIGHT = 1440 + 400;
  	/** 尺規建構子
	 * @param type HORIZON = 水平、VERTICAL = 垂直 */
	public RulerPane(short type){
        TYPE = type; 
        //用來存放尺規畫出來的結果
        WritableImage RULE_IMAGE; 
        //用來把尺規畫出來的畫布
        Canvas ruleCanvas;
        //判斷水平 or 垂直進而改變 長 寬
        int width =  ( ( TYPE == HORIZON ) ? MAX_WIDTH : 20 );
        int height = ( ( TYPE == HORIZON ) ? 20 : MAX_HEIGHT);
        //設定元件的 長 寬
        setPrefSize( width, height );
        ruleRect    = new Rectangle( width, height );
        ruleCanvas  = new Canvas( width, height );
        RULE_IMAGE  = new WritableImage( width, height );
        if( TYPE == 0 ){
            setMinHeight(20);
            redLineRect = new Rectangle( 2, 20 );
        }
        else {
            setMinWidth(20);
            redLineRect = new Rectangle( 20, 2 );
        }
        //-------------------------------------
        ruleRect.setManaged(false);
        redLineRect.setStroke( Color.RED );
        redLineRect.setFill( Color.RED );
        //-------------------------------------
        //將尺規畫在畫布上
        paintRuler( ruleCanvas.getGraphicsContext2D() );
        //把畫布上畫出的尺規擷取下來
        ruleCanvas.snapshot(null, RULE_IMAGE);
        //把尺規圖片設定成 Pane 背景圖片
        ruleRect.setFill( new ImagePattern( RULE_IMAGE )  );
        //把 RulePane 增加至 StackPane
        getChildren().add( ruleRect );
        setAlignment(ruleRect, Pos.TOP_LEFT);
        //-------------------------------------
        //用來遮住交叉點的 方形方塊
        Rectangle cover = coverRectange();
        getChildren().add( cover );
        setAlignment(cover, Pos.TOP_LEFT);
        //-------------------------------------
        //把紅標新增進去
        getChildren().add( redLineRect );
        setAlignment(redLineRect, Pos.TOP_LEFT);
        //設定背景為青色
        setStyle("-fx-background-color: #3b3b3b");
        setClip(new Rectangle(width, height));
    }
    /** 重新配置座標 紅標 
     *  @param x X 的位置
     *  @param y Y 的位置
     *  @param offset 因為 {@code ScollBar} 的偏移而改變*/
    final public void repaint( int x, int y, double offset){
        if( TYPE == HORIZON )redLineRect.setTranslateX( x - offset );
        else                 redLineRect.setTranslateY( y - offset );
    }
    /** 重新配置座標 尺規
    *  @param offset 因為 {@code ScollBar} 的偏移而改變*/
    final public void repaint( double offset ){
        if( TYPE == HORIZON )ruleRect.setTranslateX( -offset );
        else                 ruleRect.setTranslateY( -offset );
    }
    /** 印出尺規的刻度 */
	final private void paintRuler( GraphicsContext g ){
        //設定畫直線時角度是弧形
        g.setLineCap( StrokeLineCap.ROUND );
        g.setLineJoin( StrokeLineJoin.ROUND );
        g.setFont( new Font("微軟正黑體", 10) );
        g.setFill( Color.web("#3b3b3b") );
        g.setStroke( Color.LIGHTGRAY );
		//判斷是要垂直畫還是水平畫
        if( TYPE == 0 ){		//水平
            g.fillRect(0, 0, MAX_WIDTH, 20);
			for(int x = 25; x < (int)MAX_WIDTH; x += 1){	//間隔一些10座標畫一條線
                if( ((x - 25) % 100) == 0 ){				//每100就畫一條長的 + 數字
					g.strokeLine(x, 0, x, 20);
					g.strokeText((x - 25) + "", x + 5, 17);
				}
				else if( ((x - 25) % 50) == 0 )
					g.strokeLine(x, 0, x, 7);
				else if( ((x - 25) % 10) == 0 )
					g.strokeLine(x, 0, x, 5);
            }
		}
        else{			//垂直
            g.fillRect(0, 0, 20, MAX_HEIGHT);
			for(int y = 5; y < (int)MAX_HEIGHT; y += 1){ //間隔一些10座標畫一條線
				if( ((y - 5) % 100) == 0 ){			 	 //每100就畫一條長的 + 數字
					g.strokeLine(0, y, 20, y);
					String num = Integer.toString(y - 5);
					for( int i = 0; i < num.length(); i++)
						g.strokeText( num.charAt(i) + "", 11, (y + 13) + i * 11);
				}
				else if( ((y - 5) % 50) == 0 )
					g.strokeLine(0, y, 7, y);
				else if( ((y - 5) % 10) == 0 )
					g.strokeLine(0, y, 5, y);
            }
        }
        g.stroke();
	}
    /** 新增用來遮蓋的方塊 (避免尺規移動時，看起來不美觀) 
     *  @return 回傳方塊*/
    final private Rectangle coverRectange(){
        Rectangle cover;
        if( TYPE == HORIZON ){
            cover = new Rectangle(23, 20);
        }
        else {
            cover = new Rectangle(20, 28);
            cover.setTranslateY(-28);
        }
        cover.setStroke( Color.web("#3b3b3b") );
        cover.setFill(   Color.web("#3b3b3b") );
        return cover;
    }
}