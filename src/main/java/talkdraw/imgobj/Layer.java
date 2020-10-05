package talkdraw.imgobj;

import java.util.HashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import talkdraw.componet.menuitem.TextSliderMenuItem;
import talkdraw.componet.menuitem.TextSliderMenuItemBuilder;
import talkdraw.event.LayerEvent;
import talkdraw.imgobj.base.ViewBox;
import talkdraw.misc.ImageViewBuilder;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.GridPane;

/**
 * <p>自訂義圖層</p>
 * <p>繼承了 {@link ViewBox}</p>
 * <p>與 ImageObject 不同的是沒有標籤以及其他功能</p>
 * @see ViewBox*/
public class Layer extends ViewBox {
    /** 創建空白的 {@link Layer}，可提供預先設定圖層 */
    public final static Layer NONE = new Layer(-1, "null");
    /** 目前圖層是否是鎖定的(不能更改的意思) */
    private boolean isLock = false;
    /* Layer 主要畫布區 */
    private Canvas canvas;
    /** 允許最長的名稱字元數 */
    public final short MAX_NAME_LENGHT = 10;
    /** 圖片顯示的區塊大小 */
    public final short IMAGE_VIEW_BOX_SIZE = 75;
    /** 所屬圖層串列 */
    public LayerList layerList;
    /** 所屬圖片物件串列 */
    private HashMap<Integer, ImageList> imageListMap;
    /** 垃圾桶圖片檔案路徑 */
    private final String TRASH_BIN_ICON = "/textures/trashBin.png";
    /** 設定顯示圖片路徑 */
    private final String VISIBLE_ICON = "/textures/visible"; 
    /** 至頂圖片檔案路徑 */
    private final String FIRST_ICON = "/textures/first.png";
    /** 至底顯示圖片路徑 */
    private final String LAST_ICON = "/textures/last.png"; 
    /** 上一層圖片檔案路徑 */
    private final String UP_ICON = "/textures/up.png";
    /** 下一層顯示圖片路徑 */
    private final String DOWN_ICON = "/textures/down.png"; 

    private ImageList imageList;
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        建構子(Constructor)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /**建構子
     * @param ID          編號 
     * @param name        名稱*/
    public Layer(int ID, String name) {
        super(ID, name);
        this.canvas         = new Canvas(720, 480);
        createListener();
        initialIcon();
    }
    /**建構子
     * @param ID          編號 
     * @param name        名稱
     * @param list        包含的圖片串列*/
    public Layer(int ID, String name, ImageList list, LayerList layerList, HashMap<Integer, ImageList> imageListMap) {
        this(ID, name);
        this.imageList = list;
        this.layerList = layerList;
        this.imageListMap = imageListMap;
    }
    /**建構子
     * @param ID          編號 
     * @param name        名稱
     * @param img         初始圖片*/
    public Layer(int ID, String name, Image img) {
        super(ID, name);
        this.canvas         = new Canvas( img.getWidth(), img.getHeight() );
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
        setImage( img );
        createListener();
        initialIcon();
    }
    /**建構子
     * @param ID          編號 
     * @param name        名稱
     * @param img         初始圖片
     * @param list        包含的圖片串列*/
    public Layer(int ID, String name, Image img, ImageList list,HashMap<Integer, ImageList> imageListMap) {
        this(ID, name, img);
        this.imageList = list;
        this.imageListMap = imageListMap;
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       初始區(Initializer)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>初始化刪除圖式</p> 
     *  <p>有 {@code 垃圾桶圖示}、{@code 眼睛圖示}*/
    private void initialIcon(){
        // Import CSS 檔案 (讚喔)
        

        //初始化刪除的 Icon 【EventFired】
        ImageView delete = new ImageViewBuilder( TRASH_BIN_ICON ).reSize( 15, 15).align( Pos.TOP_RIGHT ).build();
        //監聽器： Main 皆有負責做接收
        delete.setOnMouseClicked( e -> {
            //先建立複製品
            LayerClone clone = new LayerClone(this);

            layerList.remove(this);
            imageListMap.remove(this.ID);

            //建立 FireEvent 好讓外面的使用者可以監聽，目前是只有 Main 有在監聽
            fireEvent( new LayerEvent( LayerEvent.DELETE, this, clone ) );
        } );
        //getChildren().add( delete );

        //---------------------------------
        //初始化顯示以及隱藏圖示
        Image show[] = new Image[]{
            new Image( String.join("-", VISIBLE_ICON, "on.png") ),
            new Image( String.join("-", VISIBLE_ICON, "off.png") )
        };
        ImageView visible = new ImageViewBuilder( show[0] ).reSize( 15, 15).align( Pos.CENTER_RIGHT ).build();
        //監聽器：當點擊眼睛時讓圖片隱藏 or 顯示
        visible.setOnMouseClicked( e -> {
            canvas.setVisible( !canvas.isVisible() );
            imageList.forEach((item)->{item.getClickableImageView().setVisible(canvas.isVisible());});
            visible.setImage( canvas.isVisible() ? show[0] : show[1] );
        } );
        //getChildren().add( visible );

        //---------------------------------
        //初始化頂層的 Icon 【EventFired】
        ImageView first = new ImageViewBuilder( FIRST_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        first.setOnMouseClicked( e -> {
            setViewOrder( ImageObject.ORDER_TOP );
        } );
        //---------------------------------
        //初始化底層的 Icon 【EventFired】
        ImageView last = new ImageViewBuilder( LAST_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        last.setOnMouseClicked( e -> {
            setViewOrder( ImageObject.ORDER_BOTTOM );
        } );
        
        //---------------------------------
        //初始化向上的 Icon 【EventFired】
        ImageView up = new ImageViewBuilder( UP_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        up.setOnMouseClicked( e -> {
            int val = setViewOrder( ImageObject.ORDER_UP );
            System.out.println((val == 0) ? "向上一層" : "以達到頂" );
        } );
        //---------------------------------
        //初始化向下的 Icon 【EventFired】
        ImageView down = new ImageViewBuilder( DOWN_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        down.setOnMouseClicked( e -> {
            int val = setViewOrder( ImageObject.ORDER_DOWN );
            System.out.println((val == 0) ? "向下一層" : "以達到底" );
        } );


        //---------------------------------
        
        GridPane iconsPane = new GridPane();
        iconsPane.addRow(0, first, visible  );
        iconsPane.addRow(1, up );
        iconsPane.addRow(2, down, delete  );
        iconsPane.addRow(3, last  );
        iconsPane.setAlignment(Pos.TOP_RIGHT);
        iconsPane.setVgap(0);
        iconsPane.setHgap(20);
        iconsPane.getStylesheets().add(getClass().getResource("/styles/imageView.css").toExternalForm());
        getChildren().add(iconsPane);
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        功能區(Function)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定是否要鎖定圖層
     * @param img 讓圖層不能動作 */
    public void setLock(boolean lock) {
        isLock = lock;
    }
    /** 設定所屬的 {@link LayerList} 
     *  @param list 所屬的 LayerList*/
    public void setBelongLayerList( LayerList list ){
        this.layerList = list;
    }
    /** 清空畫布 包括 {@code 畫布}、{@code 圖片物件}*/
    public void clearAll(){
        //先建立複製品
        LayerClone clone = new LayerClone(this);
        
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        imageList = new ImageList();

        //設定當 "透明度" 發生改變的時候，Fire 出一個Event
        fireEvent( new LayerEvent( LayerEvent.CLEAR , this, clone) );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        更新區(Updater)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡

    /** 更新 Image View 的畫布框 
     *  @param newWidth 新的寬度
     *  @param newHeight 新的高度 */
    public void updateViewBox(int newWidth, int newHeight) {
        canvas.setWidth(newWidth);
        canvas.setHeight(newHeight);
        canvas.setClip( new Rectangle(newWidth, newHeight) );

        SnapshotParameters params = new SnapshotParameters();
        params.setFill( Color.TRANSPARENT  );
        imageView.setImage( canvas.snapshot(params, null) );

        double scale = ((double) IMAGE_VIEW_BOX_SIZE / ((newWidth > newHeight) ? newWidth : newHeight));

        imageView.setFitWidth( newWidth * scale );
        imageView.setFitHeight( newHeight * scale );

        imageView.setTranslateX( (double)(IMAGE_VIEW_BOX_SIZE - imageView.getFitWidth() + 5) / 2  );
        imageView.setTranslateY( (double)(IMAGE_VIEW_BOX_SIZE - imageView.getFitHeight() + 5) / 2  );
    }

    //===================================================================================
    /** 更新 Image View 的畫布框的 {@code (預覽圖)}*/
    public void updateImageView() {
        Platform.runLater( () -> {
            boolean b = false;
            while (!b) {
                b = false;
                try {
                    Thread.sleep(5);
                    //先將使用者畫筆畫出的圖片給轉成圖片匯出
                    SnapshotParameters params = new SnapshotParameters();
                    params.setFill( Color.TRANSPARENT );
                    Image drawImg = canvas.snapshot(params, null);

                    //繪製出圖片物件(ImageObject)
                    Canvas copyCanvas = new Canvas( canvas.getWidth() , canvas.getHeight() );
                    GraphicsContext gc = copyCanvas.getGraphicsContext2D();
                    gc.drawImage( drawImg, 0, 0);                                                        //繪制出使用者畫筆
                    gc.setGlobalAlpha( 1.0 );                                                            //設定要畫的圖片透明度
                    //所有物件繪製
                    imageList.forEach( obj -> {                          
                        gc.drawImage( obj.getClickableImageView().snapshot(params, null) , obj.getX(), obj.getY() );       //繪制出圖片
                    } );   //繪製出圖片物件
                    
                    //將以上兩者合併後，設置成 ViewBox 的預覽圖
                    imageView.setImage(copyCanvas.snapshot(params, null));
                    b = true;
                } catch (Exception e) { e.printStackTrace(); return; }
            }
        } );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        回傳區(Getter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得畫布，用來把加入至 Stack Pane @return 畫布 {@code [Cavnas]} */
    public Canvas getCanvas() {  return canvas; }
    /** 取得這個圖層所屬的 {@link ImageList} @return {@code [ImageList]} */
    public ImageList getImageList(){ return imageList; }
    /** 設定所包含的圖片串列 (通常只用在 {@code 檔案處裡} 與 {@code 新增圖層}) 
     *  @param imageList 欲設定的圖片串列 */
    public void setImageList( ImageList imageList ){ this.imageList = imageList; }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        監聽器(Listener)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 建立事件監聽器*/
    final private void createListener(){}
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        覆寫區(Overriding)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** {@inheritDoc} 【EventFired】
     *  @param inOpacity 欲設定的透明度 */
    @Override public void setAlpha(int inOpacity) {
        //先建立複製品
        LayerClone clone = new LayerClone( this );

        setAlphaNoEvent( inOpacity );

        //設定當 "透明度" 發生改變的時候，Fire 出一個Event
        fireEvent( new LayerEvent( LayerEvent.OPACITY , alpha + "%", this, clone) );
    }
    //===================================================================
    /** {@inheritDoc} 【EventFired】 
     *  @param type 垂直 or 水平 */
    @Override public void filp(short type) {
        //先建立複製品
        LayerClone clone = new LayerClone( this );

        filpNoEvent( type ); 

        //當 "翻轉" 發生改變的時候，Fire 出一個Event
        fireEvent( new LayerEvent( LayerEvent.FILP , type == 0 ? "水平翻轉" : "垂直翻轉", this, clone) );
    }
    //===================================================================
    /** {@inheritDoc}  【EventFired】 
     *  @param name 名稱 */
    @Override public void rename(String name){
        //先建立複製品
        LayerClone clone = new LayerClone( this );

        //-------------------------------------------
        if( name.length() > 10 ){
            //throw new Exception("名稱長度超過 " + MAX_NAME_LENGHT + " 個字元！" );
        }
        else if( name.matches("[^a-zA-Z_0-9]*") ){
            //throw new Exception("名稱只能有數字以及英文！" );
        }
        this.name = name;
        nameLabel.setText( name );
        //------------------------------------------

        //設定當 "名子" 發生改變的時候，Fire 出一個Event
        fireEvent( new LayerEvent( LayerEvent.RENAME , name, this, clone) );
    }
    //===================================================================
    /** @param img 欲設定的圖片 */
    @Override public void setImage(Image img) {
        imageView.setImage( img );
    }
    //===================================================================
    public static final int ORDER_TOP = 0;
    public static final int ORDER_BOTTOM = 1;
    public static final int ORDER_UP = 2;
    public static final int ORDER_DOWN = 3;
     /** 設定圖片的前後順序 
     *  @param type 動作類別
     *  @return 是否已達到底or頂 {@code 1 = 已到頂} | {@code  = 正常} | {@code -1 = 以到底} */
    public int setViewOrder(int type){
        //先建立複製品
        LayerClone clone = new LayerClone( this );  
        int value = 0;
        switch (type) {
            case ORDER_TOP:
                layerList.setViewTop(this);
                break;
            case ORDER_BOTTOM:
                layerList.setViewBottom(this);
                break;
            case ORDER_UP:
                if( !layerList.setViewUp(this) )
                    value = 1;
                break;
            case ORDER_DOWN:
                if( !layerList.setViewDown(this) )
                    value = -1;
                break;
            default:
                break;
        }

        if( value == 0 )
            //當 "圖片移動" 發生改變的時候，Fire 出一個Event
            fireEvent( new LayerEvent( LayerEvent.VIEW_ORDER , this, clone) );
        
        return value;
    }


    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        雜項(Misc)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 建立當滑鼠右鍵點擊圖片時，跳出的詳細訊息 */
    @Override public void showInfoList( Node viewNode ) {
        //設定彈出視窗的 Class
        infoMenu = new ContextMenu(){
            { getStyleClass().add("infoMenu"); }
        };

        //-------------------------------------------------
        //透明度的 TextSlider
        TextSliderMenuItem alphaSilder = new TextSliderMenuItemBuilder( "透明：").setRange( 0, 100 ).setValue( alpha ).build();
        //確定後執行的動作
        alphaSilder.setTextFieldHandler( () -> setAlpha( (int)alphaSilder.getValue() ) );
        //設定當使用 Slider 時不要讓它頻繁更新值
        alphaSilder.setSliderHander( () -> {   
            double value = (double)alphaSilder.getValue() / 100;
            imageView.setOpacity( value );
            canvas.setOpacity( value );

            //更新所有在於自己下方的 ImageObject 透明度
            imageList.forEach( imgObj -> {
                imgObj.getImageView().setOpacity( ((double)imgObj.getAlpha() / 100) * value );
                imgObj.getMainImgView().setOpacity( ((double)imgObj.getAlpha() / 100) * value );
            });
        });



        ObservableList<MenuItem> itemList = infoMenu.getItems();    

        //取的基礎 InFo Menu
        MenuItem baseItem[] = getInfoMenuItems();
        
        //建立資訊清單
        MenuItem items[] = new MenuItem[]{
            baseItem[0],
            alphaSilder,
            baseItem[1]
        };

        //加入至 ContextMenu
        itemList.addAll( items );

        //顯示 ContextMenu
        infoMenu.show(viewNode, Side.BOTTOM, 65, -5);

        //播放動畫
        playInfoMenuAnimation( itemList );

    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       復原動作區(Undo)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    @Override protected void updateThis(){
        //設定 Label 名稱
        nameLabel.setText( name ); 

        //更新透明度
        setAlphaNoEvent( alpha );
        
    }
    //===================================================================================
    /** 將複製的 {@link LayerClone} 回存至這個物件 
     *  @param clone 複製品*/
    public void restore( LayerClone clone ){
        
        LayerClone.reDrawCanvans( canvas, clone.getImage() );
        this.name           = clone.getName();
        this.alpha          = clone.getAlpha();
        this.imageList      = clone.getImageList();

        //設定翻轉，不等於就翻轉
        if( filpHorizon  != clone.getFilp()[0] ) filpNoEvent( FILP_HORIZONTAL );
        if( filpVertical != clone.getFilp()[1] ) filpNoEvent( FILP_VERTICAL );

        updateThis();
        updateImageView();
    }







    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        無事件觸發區(No Event Function)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>提供 {@code 不會觸發事件(Event)的 Filp()}</p> 
     *  <p>假如要觸發事件請用 {@link #filp()} </p>
     *  @param type 垂直 or 水平 */
    final private void filpNoEvent( short type ){
        //取得畫筆
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //半段要往哪個方向翻轉
        SnapshotParameters params = new SnapshotParameters();
        params.setFill( Color.TRANSPARENT );
        params.setTransform( (type == FILP_HORIZONTAL) ? Transform.scale( -1, 1 ) : Transform.scale( 1, -1 ) );
        //取得讀取像素器
        PixelReader pixelReader = canvas.snapshot( params, null).getPixelReader();
        //清空原本畫布
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        //重畫每一個像素，以免失真
        for(int i = 0; i < (int)canvas.getWidth(); i++ ){
            for(int j = 0; j < (int)canvas.getHeight(); j++){
                gc.getPixelWriter().setColor( i, j,  pixelReader.getColor(i, j) );
            }
        }

        //設定翻轉 boolean
        if(      (type == FILP_HORIZONTAL) )filpHorizon  = !filpHorizon;
        else if( (type == FILP_VERTICAL)   )filpVertical = !filpVertical;

        //翻轉所屬的 圖片物件串列
        imageList.forEach( obj -> obj.filpByLayer(type) );

        //更新右側 LayerViewBox
        updateImageView();
    }
    //=======================================================================================
    /** <p>提供 {@code 不會觸發事件(Event)的 setAlpha()}</p> 
     *  <p>假如要觸發事件請用 {@link #setAlpha()} </p>
     *  @param inOpacity 欲設定的透明度 */
    final private void setAlphaNoEvent( int inOpacity ){
        alpha = ((inOpacity >= 0 && inOpacity <= 100) ? inOpacity : (inOpacity > 100 ? 100 : 0) );
        
        final double opacDouble = (double)alpha / 100;
        canvas.setOpacity( opacDouble );
        imageView.setOpacity( opacDouble );

        //更新所有在於自己下方的 ImageObject 透明度
        imageList.forEach( imgObj -> {
            imgObj.getImageView().setOpacity( ((double)imgObj.getAlpha() / 100) * opacDouble );
            imgObj.getMainImgView().setOpacity( ((double)imgObj.getAlpha() / 100) * opacDouble );
        });
    }
}


