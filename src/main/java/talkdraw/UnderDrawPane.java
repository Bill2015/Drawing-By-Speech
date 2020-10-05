package talkdraw;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import talkdraw.imgobj.LayerList;

public class UnderDrawPane extends Pane implements EventHandler<MouseEvent>{
    public int x = 0, y = 0;
	/** 調整畫布大小用的方塊 */
    public Pane ctrlArea[] = new Pane[3];
    /** 用來繪製出虛線的畫布 */
    private Canvas reSizeCanvas;
    private int drawWidth = 720, drawHeight = 480;
    /** 主要畫布區(基本上算是拿來當參照用的) */
    private final MainDrawPane MAIN_DRAW_PANE;
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;
    public UnderDrawPane( MainDrawPane mainDraw, App APP ){
        this.APP = APP;
        x = drawWidth;
        y = drawHeight;
        
        //設定最小大小
       // setMaxSize( drawWidth + 400, drawHeight + 400 );
        setStyle("-fx-background-color: lightgray");

        //主畫面保留
        MAIN_DRAW_PANE = mainDraw;
        //取得上畫布
        UpponDrawPane upponDraw = MAIN_DRAW_PANE.getUpponPane();
        upponDraw.relocate(5, 5);

        //-----------------------------------------------
        reSizeCanvas = new Canvas( drawWidth + 800, drawHeight + 400 );
        getChildren().add( reSizeCanvas );
        getChildren().add( upponDraw );

        //監聽移動的黑區塊
        for (int i = 0; i < 3; i++) {
            ctrlArea[i] = new Pane();
            ctrlArea[i].setMinSize(12, 12);
            ctrlArea[i].addEventFilter( MouseEvent.MOUSE_DRAGGED, this );
            ctrlArea[i].addEventFilter( MouseEvent.MOUSE_RELEASED, this );
			ctrlArea[i].setStyle("-fx-background-color: black");
			getChildren().add( ctrlArea[i] );
        }
        //設定黑方塊座標
        ctrlArea[0].relocate(drawWidth + 6, drawHeight + 6);
		ctrlArea[1].relocate(drawWidth + 6, (int) (drawHeight / 2));
        ctrlArea[2].relocate((int) (drawWidth / 2), drawHeight + 6);

    }
    //========================================================================================
    //===========================     滑鼠動作區塊    ========================================
    @Override
    public void handle(MouseEvent e) {
        //當滑鼠拖曳時
        if( e.getEventType() ==  MouseEvent.MOUSE_DRAGGED ){
            mouseDragging( e );
            //當被按下時把背景的 reSizeCaven 設置到最前面，來印出虛線
            reSizeCanvas.toFront();
        }
        //---------------------------------------------------------
        //當滑鼠放開時
        else if( e.getEventType().equals( MouseEvent.MOUSE_RELEASED ) ){
            //App靜態呼叫：取得最大的 寬 高 以免超出
            if( x > App.MAX_DRAW_WIDTH || x < 10 || y > App.MAX_DRAW_HEIGHT || y < 10){
                x = drawWidth;
                y = drawHeight;
            }
            mouseReleased();
            //重新設定底層 (為了可以讓畫布卷軸動態增加)
            reSize(x, y);
            //當放開時，再把 reSizeCaven 放到後面，以利 UpponDraw 監聽自己的事件
            reSizeCanvas.toBack();
        }
    }
    //------------------------------------------------------------------------------
    /** 當滑鼠放開的時候 */
    private void mouseReleased() {
		drawWidth = x; drawHeight = y;
        //重新設定座標
        ctrlArea[0].relocate(drawWidth + 3, drawHeight + 3);
        ctrlArea[1].relocate(drawWidth + 3, (int) (drawHeight / 2) + 3);
        ctrlArea[2].relocate((int) (drawWidth / 2) + 3, drawHeight + 3);
        //清空底層畫布
        reSizeCanvas.getGraphicsContext2D().clearRect(-10, -10, drawHeight + 720, drawWidth + 640);
    }
    //------------------------------------------------------------------------------
    /** @return 取得紅點 Mouse 位置 {@code X}, {@code Y}  */
    public int[] getDraggedPos(){ return new int[]{x, y}; }
    //------------------------------------------------------------------------------
    /** 當滑鼠在拖曳時 */
    private void mouseDragging(MouseEvent e){
        int mx = (int)e.getX(), my = (int)e.getY();
        if (e.getSource() == ctrlArea[0]) {
            x = mx + drawWidth;
            y = my + drawHeight;
        } else if (e.getSource() == ctrlArea[1]) {
            x = mx + drawWidth;
            y = drawHeight;
        } else if (e.getSource() == ctrlArea[2]) {
            x = drawWidth;
            y = my + drawHeight;
        }
        paint( reSizeCanvas.getGraphicsContext2D() );
    }
    //==================================================================================================
    //==============================        底層布大小設定      =========================================
    /** 重新設定 Cavens 與 Pane 大小 
     *  @param newWidth 新的寬度
     *  @param newHeight 新的高度*/
    public void reSize(int newWidth, int newHeight) {
        //App靜態呼叫：取得最大的 寬 高 以免超出
        if( newWidth > App.MAX_DRAW_WIDTH || newWidth < 10 || newHeight > App.MAX_DRAW_HEIGHT || newHeight < 10)
            return;
        //新值賦予
        x = drawWidth = newWidth; y = drawHeight = newHeight;
        //重新設定座標
        ctrlArea[0].relocate(drawWidth + 3, drawHeight + 3);
        ctrlArea[1].relocate(drawWidth + 3, (int) (drawHeight / 2) + 3);
        ctrlArea[2].relocate((int) (drawWidth >> 1) + 3, drawHeight + 3);

        //重新設定底層 (為了可以讓畫布卷軸動態增加)
        int extWidth  = (drawWidth <= 600) ? 1920 : drawWidth + 880;
        int extHeight = (drawHeight <= 480) ? 1080 : drawHeight + 480;
        setMinSize( extWidth, extHeight );
        setPrefSize( extWidth, extHeight);
        reSizeCanvas.setWidth( extWidth );
        reSizeCanvas.setHeight( extHeight );

        //重新設定 UpponDrawCanvas 得高度
        MAIN_DRAW_PANE.getUpponPane().reSize(drawWidth, drawHeight);

        //更新右側欄位的 ViewBox
        upadateLayerViewBox();
    }
    /** 更新右側欄位的 ViewBox */
    public void upadateLayerViewBox(){
        //-------------------------------------------------------
        //App靜態呼叫：取得 LayerList 串列
        LayerList layers = APP.MAIN.getLayerList();

        Platform.runLater( () -> {
            //App靜態呼叫：重新設定右側的 Image View 大小
            layers.forEach( layer -> layer.updateViewBox(drawWidth, drawHeight) );

            //App靜態呼叫：
            layers.getActiveLayer().updateImageView();
        } );
    }
    //===========================================================================
    //===========================   繪圖功能區塊    ==============================
    /** 印出虛線 */
    private void paint(GraphicsContext g) {
        g.clearRect(-10, -10, x + 720, y + 640);
        g.setStroke( Color.BLACK );
        g.setLineDashes( 2.0 );
        // 設定 g2d 的筆刷類型
        g.strokeRect(4, 4, x + 1, y + 1);
    }
    //===========================================================================
    //========================   回傳功能區塊(Getter)    ==========================
    /** 取得繪圖區的寬度 
     *  @return 寬度 {@code [Int]}*/
    public int getDrawWidth(){ return drawWidth; }
    /** 取得繪圖區的高度 
     *  @return 高度 {@code [Int]}*/
    public int getDrawHeight(){ return drawHeight; }
    //===========================================================================
    //=============================   雜項    ===================================
    
    

}
