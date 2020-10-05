package talkdraw;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import talkdraw.componet.ClickableImageView;
import talkdraw.event.CanvasMouseEvent;
import talkdraw.imgobj.ImageList;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.Layer;
import talkdraw.tools.SelectTool;
import talkdraw.tools.Tool;

/** 主要繪圖區！ 
 *  所有的繪圖工作都在這裡執行 */
public class UpponDrawPane extends StackPane implements EventHandler<MouseEvent>{
    private int width = App.INITIAL_DRAW_WIDTH, height = App.INITIAL_DRAW_HEIGHT;
    /** <p>用來畫出目前大略操作 (Preview Canvas)</p> */
    private Canvas preViewCanvas;
    /** 畫出預覽用的畫筆 {@code prevViewCanvas} */
    private GraphicsContext preGC;
    /** 背景底圖的方形 */
    private Rectangle background;
    //=============================================================
    private SimpleObjectProperty<ClickableImageView> selectImgProperty;
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;
    public UpponDrawPane( App APP ){
        //主畫面傳遞
        this.APP = APP;

        background = new Rectangle( width, height, Color.WHITE );
        getChildren().add( background );

        //初始化開始
        preViewCanvas = new Canvas( width, height );
        preGC = preViewCanvas.getGraphicsContext2D();
        preGC.setStroke( Color.BLACK );
        setPrefSize( width, height );
        addEventHandler( MouseEvent.ANY, this);
        setStyle("-fx-background-color: white");
        setClip( new Rectangle( width, height ) );
        getStylesheets().add(getClass().getResource("/styles/seletImage.css").toExternalForm());

        //初始化選取框框
        selectImgProperty = new SimpleObjectProperty<>( );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        繪圖區監聽(Drawing Listener)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    @Override
    public void handle( MouseEvent e ) {
        //App靜態呼叫：
        Layer activeLayer = APP.MAIN.getLayerList().getActiveLayer();
        //判斷是否有選到工具
        if (APP.TOOL_BAR.getNowTool() != null) {
            //設定畫筆要畫的圖層
            APP.TOOL_BAR.getNowTool().setMainGC(  activeLayer );
        }

        //預覽畫布清除
        preGC.clearRect(0, 0, width, height);

        paint( e ); //<-- 繪圖判斷寫在面

        //當滑鼠放開時，更新右側圖層縮圖 
        if( e.getEventType() == MouseEvent.MOUSE_RELEASED ){

        }

        //=======================================================================================
        //判斷是否為選擇工具，才可以選擇圖片
        Platform.runLater(() -> {
            mouseSelectImage( e );
        });
  
    }
    //--------------------------------------------------------------------
    /** <p>進行繪圖處裡，像是清除與印出主要圖層 </p>
     *  <p>在做繪圖處裡時，要幫我看一下有沒有 RAM 會爆掉的情況發生</p>
     *  <p>就是隨便畫個幾筆，看看 RAM 會不會居高不下 </p>*/
    private void paint( MouseEvent e ){
        //取得事件動作
        EventType<? extends MouseEvent> event = e.getEventType();
        //判斷是否有選到工具
        Tool nowTool = APP.TOOL_BAR.getNowTool();
        if ( nowTool != null) {
            if( event == MouseEvent.MOUSE_DRAGGED){         nowTool.onMouseDragging( e );   }
            else if( event == MouseEvent.MOUSE_PRESSED){    nowTool.onMouseClick( e );      }
            else if( event == MouseEvent.MOUSE_MOVED){      nowTool.onMouseMove( e );       }
            else if( event == MouseEvent.MOUSE_RELEASED){   nowTool.onMouseRelease( e );    }
        }
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        設定大小函式(reSize Function)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 重新設定 Cavens 與 Pane 大小 
     *  @param newWidth 新的寬度
     *  @param newHeight 新的高度*/
    public void reSize(int newWidth, int newHeight){
        //設定新的值
        width   = newWidth;
        height  = newHeight;
        setPrefSize( width, height );
        preViewCanvas.setWidth( newWidth );
        preViewCanvas.setHeight( newHeight );
        preViewCanvas.setClip( new Rectangle( width, height ) );
        setClip( new Rectangle( width, height ) );
        background.setWidth( width );
        background.setHeight( height );
        APP.TOOL_BAR.SELECT_TOOL.setDrawAreaBox(width, height);
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡         中間圖層部分(Mid DrawArea)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>更新中間所有物件</p> 
     *  <p>會把所有關於 {@link Cavans} 與 {@link ImageView} 全部移除重新新增 </p>
     *  <p>基本上只有 在開啟專案檔時會使用到 {@link Main#LoadingProject(talkdraw.imgobj.LayerList, java.util.HashMap)}</p>*/
    public void updateDrawArea(){
        //先將 [畫布] 以及 [ImageView] 清空
        getChildren().removeIf( node -> node instanceof Canvas || node instanceof ClickableImageView );

        setAlignment( Pos.TOP_LEFT );
        setManaged( false );
        //App靜態呼叫： 把畫布加入至中間圖層
        for ( Layer layer : APP.MAIN.getLayerList()) {
            getChildren().add( layer.getCanvas() );
            
            //圖片物件更新
            ImageList imageList = layer.getImageList();
            for( ImageObject imgObj : imageList ){
                if( imgObj != null ){
                    ClickableImageView view = imgObj.getClickableImageView();
                    view.setTranslateX( imgObj.getX() );
                    view.setTranslateY( imgObj.getY() );
                    getChildren().add( view );
                }
            }
            layer.updateImageView();
        }
        //再把 preViewCanvas 加入進去
        getChildren().add( preViewCanvas );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡          回傳區(Getter)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得預覽畫筆 
     *  @return 回傳預覽畫筆 {@code [GraphicContext]} */
    public GraphicsContext getPrevGC(){ return preGC; }


    /** 取得目前選擇的圖片 {@code Property} 的 {@link ClickableImageView} 
     *  @return 回傳 ClickableImageViewProperty {@code [SimpleObjectProperty<ClickableImageView>]}*/
    public SimpleObjectProperty<ClickableImageView> getSelectImageProperty(){
        return selectImgProperty;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡          回傳區(Setter)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定目前選取的圖片
     *  @param newImg 欲設定的新圖片*/
    public void setNowSelectImage( ClickableImageView newImg ){
        //Platform.runLater( () -> {
            selectImgProperty.set( newImg );;
        //} );
    }
//※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡         滑鼠在畫布上的功能(Mouse On the Draw Area)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡

    /** 滑鼠在處理圖片時所做的事情 
     *  @param e 滑鼠事件 */
    public void mouseSelectImage( MouseEvent e ){
        
        //假如是移動事件就不要執行
        if( e.getEventType() == MouseEvent.MOUSE_MOVED || !(APP.TOOL_BAR.getNowTool() instanceof SelectTool) )
            return;


        //當滑鼠按下時還會做判斷
        if( e.getEventType() == MouseEvent.MOUSE_PRESSED ){
            //紀錄是否第一個
            boolean first = true;
            ObservableList<Node> list = getChildren().filtered( child -> (child instanceof ClickableImageView) );
            //反向搜尋
            for( int k = list.size() - 1; k >= 0; k-- ){
                Node node = list.get( k );
                //判斷滑鼠座標是否在物件內，並且是第一個
                if( node.getBoundsInParent().contains( e.getX(), e.getY() ) && first ){
                    ClickableImageView nodeView =  (ClickableImageView)node;
                    int dx = (int)(e.getX() - nodeView.getTranslateX());
                    int dy = (int)(e.getY() - nodeView.getTranslateY());
                    //System.out.println( nodeView.getImageView().getImage().getPixelReader().getArgb(dx, dy) );
                    SnapshotParameters snap = new SnapshotParameters();
                    snap.setFill( Color.TRANSPARENT );
                    try{
                        if( nodeView.getImageView().snapshot(snap, null).getPixelReader().getArgb(dx, dy) != 0  ){
                            //FireEvent 至這個 Node(ClickableImageView)
                            Event.fireEvent( node, new CanvasMouseEvent( e, true) );
                            selectImgProperty.set( (ClickableImageView)nodeView );
                            first = false;  //第一個出現了
                        }
                        else{
                            //FireEvent 至這個 Node(ClickableImageView)
                            Event.fireEvent( node, new CanvasMouseEvent( e, false) );
                        }
                    }
                    catch( IndexOutOfBoundsException exception  ){
                        Event.fireEvent( node, new CanvasMouseEvent( e, false) );
                    }
                }
                else{
                    //FireEvent 至這個 Node(ClickableImageView)
                    Event.fireEvent( node, new CanvasMouseEvent( e, false) );
                }
            }
        }
        //假如目前有選擇的 Image 並且是在 滑鼠放開與拖曳時就 FireEvent 至這個 Node(ClickableImageView)
        else if( selectImgProperty.get() != null && (e.getEventType() == MouseEvent.MOUSE_RELEASED || e.getEventType() == MouseEvent.MOUSE_DRAGGED) ){
            boolean inBound = selectImgProperty.get().getBoundsInParent().contains( e.getX(), e.getY() );
            //判斷是否還在 TempImage 的邊界裡
            if( inBound ){
                Event.fireEvent( selectImgProperty.get() , new CanvasMouseEvent( e, inBound ) );
            }
            //設定為 None ImageObject
            else selectImgProperty.set( ImageObject.NONE.getClickableImageView() );
        }
    }

}
