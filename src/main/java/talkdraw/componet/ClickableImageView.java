package talkdraw.componet;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import talkdraw.event.CanvasMouseEvent;
import talkdraw.imgobj.ImageObject;

/** <p>提供可以點擊與拖曳的 {@link ImageView}</p>
 *  <p>繼承了 {@link StackPane}</p>
 *  <p>基本上都是由 {@code UpponDrawPane} 發送過來的滑鼠事件</p>*/
public class ClickableImageView extends StackPane{
    private ImageView imageView;
    /** 顯示在 {@link ImageView} 外的外框 */
    private Rectangle border;
    /** 顯示在 {@link ImageView} 四個角落的方塊 */
    private EdgeRectangle rects[];

    /** <p>當滑鼠點下時，在圖片上的相對位置</p>
     *  <p>以防止拖曳時造曹的偏移</p>*/
    private Point2D dragPos;

    /** 此物件屬於的 {@link ImageObejct} */
    private ImageObject belongImgObj;

    /** <p>判斷是否有移動過了</p> 
     *  <p>有移動過才需要更新 {@link ImageObject}</p>*/
    private boolean isMoved = false;

    private boolean isSelected = false;
    
    /** Allocates a new ImageView object
     *  <p>建構子</p>
     *  @param belongImgObj 所屬的 {@link ImageObject}*/
    public ClickableImageView( ImageObject belongImgObj ){
        this.belongImgObj = belongImgObj;
        initializer( );
        createListener();
    }
    /**Allocates a new ImageView object with image loaded from the specified
     * URL.<p>
     * The {@code new ImageView(url)} has the same effect as
     * {@code new ImageView(new Image(url))}.</p>
     * @param url the string representing the URL from which to load the image
     * @param belongImgObj 所屬的 {@link ImageObject}
     * @throws NullPointerException if URL is null
     * @throws IllegalArgumentException if URL is invalid or unsupported*/
    public ClickableImageView( String url, ImageObject targetNode ){
        this.belongImgObj = targetNode;

        imageView = new ImageView( url );
        initializer( );
        createListener();
    }

    /** Allocates a new ImageView object using the given image.
     *  @param image Image that this ImageView uses
     *  @param belongImgObj 所屬的 {@link ImageObject}*/
    public ClickableImageView( Image img, ImageObject targetNode  ){
        this.belongImgObj = targetNode;

        imageView = new ImageView( img );
        initializer( );
        createListener();
    }

    private void initializer( ){
        if( belongImgObj.getImage() == null )return;
        //取得 ImageView 的座標與邊常
        double x = belongImgObj.getX(), y = belongImgObj.getY(), w = imageView.getImage().getWidth(), h = imageView.getImage().getHeight();
        
        //建立 ImageView 的外框
        this.border = new Rectangle( x, y, w, h );
        border.setFill( Color.TRANSPARENT );
        border.setStroke( Color.GREEN );
        border.setManaged(false);
        border.setStrokeWidth( 3 );
        
        //建立 ImageVuew 四個角的方塊
        rects = new EdgeRectangle[]{
            new EdgeRectangle( 0, 0 ),
            new EdgeRectangle( w, 0 ),
            new EdgeRectangle( w, h ),
            new EdgeRectangle( 0, h )
        };

        //加入至 StackPane
        getChildren().add( border );
        getChildren().addAll( rects );
        getChildren().add( imageView );

        setBorderVisible( false );
    }
    /** 創建滑鼠監聽器 */
    private void createListener() {
        //監聽器： 監聽滑鼠動作
        addEventHandler( CanvasMouseEvent.ANY , e -> {
            MouseEvent mouseEvent = e.getMouseEvent();

            //判斷是否是滑鼠左鍵
            if( mouseEvent.getButton() != MouseButton.PRIMARY && mouseEvent.getButton() != MouseButton.SECONDARY ){
                e.consume();
                return;
            }

            //當右鍵點擊畫布上的圖片時，顯示圖片的資訊
            if( mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && mouseEvent.getButton() == MouseButton.SECONDARY ){
                if( isSelected ){
                    belongImgObj.showInfoList( this );
                }
                e.consume();
                return;
            }

            if( mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED ){
                //假如目前被選擇，並且 DragPos != Null 就執行拖曳動作
                if( dragPos != null && isSelected ){
                    onMouseDragged( e );
                }
            }
            else if( mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED ){
                //是否有被選擇
                isSelected = e.isOnView();
                if( isSelected ){
                    onMousePressed( e );
                }
                else{
                    setBorderVisible( false );
                }
            }
            else if( mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED ){
                if( isSelected ){
                    onMouseReleased();
                }
            }
            e.consume();
        });


    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     滑鼠事件(MouseEvent)      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 當滑鼠在拖曳時做的動作 
     *  @param mouseEvent 滑鼠事件 */
    private void onMouseDragged( CanvasMouseEvent e ){
        border.setStroke( Color.BLUEVIOLET );
        for( Rectangle r : rects )r.setVisible( false );
        //有移動過了
        isMoved = true;     

        //計算座標差
        double dx = e.getX() + dragPos.getX(), dy = e.getY() + dragPos.getY();

        //設定圖片座標
        setTranslateX( dx );
        setTranslateY( dy );
    }
    /** 當滑鼠放開時做的動作  */
    private void onMouseReleased(){
        border.setStroke( Color.GREEN );
        border.setVisible( true );
        for( Rectangle r : rects )r.setVisible( true );

        if( isMoved == true ){
            //設定 ImageObject 座標
            belongImgObj.setLocation( getTranslateX(), getTranslateY() );
            isMoved = false;
        }
    }
    /** 當滑鼠放開時做的動作 
     *  @param mouseEvent 滑鼠事件 */
    private void onMousePressed( CanvasMouseEvent mouseEvent ){
        //將滑鼠點擊的座標轉換成 Point2D
        Point2D point = new Point2D( mouseEvent.getX(), mouseEvent.getY() );

        //取得滑鼠點擊時與目前的圖片的座標差，這樣當在拖曳時才不會異位
        dragPos = new Point2D( getTranslateX() - point.getX() , getTranslateY() - point.getY());
        setBorderVisible( true );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡

    /** 設定被選擇了 
     *  @param flag {@code true = 被選擇} | {@code false = 沒有被選擇}
    */
    public void setSelected( boolean flag  ){
        isSelected = flag;
        setBorderVisible( flag );
    }
    /** 設定 圖片{@link Image}
     *  @param image */
    public void setImage( Image image ){
        imageView.setImage( image );
        reSizeBorder();
    }

    /** 取得這個的 {@link ImageView} 
     *  @return {@code [ImageView]}*/
    public ImageView getImageView(){
        return imageView;
    }

    /** 取得所屬的 {@link ImageObject} @return 圖片物件 {@code [ImageObject]}*/
    public ImageObject getBelongImageObject(){ return belongImgObj; }
    
    /** 設定透明度 {@code 範圍 [0.0 ~ 1.0]}
     *  @param opacity 透明度*/
    public void setAlpha(double opacity){
        imageView.setOpacity( opacity );
    }

    /** <p>以覆寫，能回傳 {@link ImageView} 的 {@code snapshot}</p>
     *  <p>而不會有邊框跑出來</p>
     * {@inheritDoc} */
    @Override public WritableImage snapshot(SnapshotParameters params, WritableImage image){
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.millis(100), ae -> setBorderVisible( isSelected ) ));
        timeline.play(); 
        setBorderVisible( false );
        return super.snapshot(params, image);
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定是否要顯示邊框  
     *  @param val 邊框 {@code true = 顯示} | {@code false 不顯示}*/
    private void setBorderVisible(boolean val){
        border.setVisible( val );
        for( Rectangle r : rects )r.setVisible( val );
    }
    
    public void reSize( double width, double height ){
        imageView.setFitWidth( width );
        imageView.setFitHeight( height );
        reSizeBorder();
    }

    /** <p>重新設定邊界</p> 
     *  <p>通常是在設定圖片時才會用到</p>*/
    private void reSizeBorder(){
        double w = imageView.getFitWidth(), h = imageView.getFitHeight();
        border.setWidth( w );
        border.setHeight( h );

        rects[0].relocate( 0, 0 );
        rects[1].relocate( w, 0 );
        rects[2].relocate( w, h );
        rects[3].relocate( 0, h );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>四個角落的方塊 Class</p>
     *  <p>繼承了 {@link Rectangle} */
    class EdgeRectangle extends Rectangle{
        /** 邊框大小 */
        private final int SIDE = 15;
        /** 建構子 
         *  @param oW 水平偏移量
         *  @param oH 垂直偏移量*/
        public EdgeRectangle( double oW, double oH ){
            super(0 + oW - 7.5, 0 + oH - 7.5, 15, 15 );
            setFill( Color.BLANCHEDALMOND ) ;
            setStroke( Color.GREEN );
            setManaged(false);
        }
        @Override
        public void relocate(double oW, double oH){
            double dx = oW - (SIDE / 2);
            double dy = oH - (SIDE / 2);
            super.relocate(dx, dy);
        }
    }
}