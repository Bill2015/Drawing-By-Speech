package talkdraw.imgobj;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import talkdraw.componet.ClickableImageView;
import talkdraw.componet.menuitem.LockedMenuItem;
import talkdraw.componet.menuitem.TagFieldMenuItem;
import talkdraw.componet.menuitem.TextSliderMenuItem;
import talkdraw.componet.menuitem.TextSliderMenuItemBuilder;
import talkdraw.event.ImageEvent;
import talkdraw.imgobj.base.ViewBox;
import talkdraw.misc.ImageViewBuilder;

/** 程式圖片最主要的 Class */
public class ImageObject extends ViewBox{
    /** 沒有任何用途的 {@link ImageObject} */
    public static ImageObject NONE = new ImageObject("NONE");
    /** ID 計數器 ，用來設定 ImageObject 的 ID*/
    private static int ID_COUNTER = 0; 
    //============================================================================
    /** <p>顯示在畫布的圖片 </p>加入到中間畫布的 Image View<b>{@code (此為主要更改的)}</b> */
    private ClickableImageView mainImgView;
    /** 圖片的標籤 */
    private ArrayList<String> tags;   
    /** 規模(用來放大用) */
    private double scale = 1.0; 
    /**<p>圖片旋轉角度</p> 
     * <p>其值範圍 {@code [0 ~ 360]}</p>*/
    private int rotation = 0;
    /** 所屬圖層 */
    private Layer belongLayer;
    /** 座標 */
    private double posX = 0, posY = 0;
    /** 寬度 */
    private double imageWidth = 0;
    /** 長度 */
    private double imageHeight = 0;
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
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       建構子區      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>一般用建構子</p> 
     *  <p>沒有{@code 標籤(Tag)}、{@code 圖片(Image)}、{@code 所屬圖層(belongLayer)}預設 -99</p>
     *  @param name 圖片名稱*/
    public ImageObject( String name ){
        super(ID_COUNTER, name);
        this.mainImgView    = new ClickableImageView( this );   //顯示在畫布的 ImageView
        this.tags           = new ArrayList<String>();          //標間串列
        initialIcon();
        ID_COUNTER += 1;
    }
    /**<p>一般用建構子</p>
     * <p>沒有{@code 標籤(Tag)}</p>
    *  @param belongLayer 所屬圖層ID
    *  @param name 圖片名稱
    *  @param image  圖片*/
    public ImageObject(String name, Image image){
        super( ID_COUNTER, name, image);
        this.mainImgView    = new ClickableImageView( image, this );//顯示在畫布的 ImageView
        this.tags           = new ArrayList<String>();              //標間串列
        imageWidth = getPixelWidth();
        imageHeight = getPixelHeight();
        initialIcon();
        ID_COUNTER += 1;
    }
    /**<p>一般用建構子 </p>
     * <p>{@code 所屬圖層(belongLayer)}預設 null</p>
    *  @param name 圖片名稱
    *  @param image  圖片
    *  @param tags 標籤*/
    public ImageObject(String name, Image image, ArrayList<String> tags){
        this( name, image);
        this.tags = tags;
    }
    /**<p>一般用建構子</p>
     * <p>沒有{@code 標籤(Tag)}</p>
    *  @param belongLayer 所屬圖層
    *  @param name 圖片名稱
    *  @param image  圖片*/
    public ImageObject(Layer belongLayer, String name, Image image){
        this(name, image);
        this.belongLayer = belongLayer;
    }
    /**<p>一般用建構子</p>
     * <p>都包含的建構子</p>
    *  @param belongLayer 所屬圖層
    *  @param name 圖片名稱
    *  @param image  圖片
    *  @param tags 標籤*/
    public ImageObject(Layer belongLayer, String name, Image image, ArrayList<String> tags){
        this(belongLayer, name, image);
        this.tags = tags;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       初始區(Initializer)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>初始化刪除圖式</p> 
     *  <p>有 {@code 垃圾桶圖示}、{@code 眼睛圖示}*/
    private void initialIcon(){
        //初始化刪除的 Icon 【EventFired】
        ImageView delete = new ImageViewBuilder( TRASH_BIN_ICON ).reSize( 15, 15)/* .align( Pos.CENTER ) */.build();
        //監聽器： Main 皆有負責做接收
        delete.setOnMouseClicked( e -> {
            ImageObjectClone clone = new ImageObjectClone( this ); //先建立複製品

            //建立 FireEvent 好讓外面的使用者可以監聽，目前是只有 Main 有在監聽
            Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.DELETE, this, clone ) );
        } );
        //getChildren().add( delete );
        
        //---------------------------------
        //初始化顯示以及隱藏圖示
        Image show[] = new Image[]{
            new Image( String.join("-", VISIBLE_ICON, "on.png") ),
            new Image( String.join("-", VISIBLE_ICON, "off.png") )
        };
        ImageView visible = new ImageViewBuilder( show[0] ).reSize( 15, 15)/* .align( Pos.CENTER ) */.build();
        //監聽器：當點擊眼睛時讓圖片隱藏 or 顯示
        visible.setOnMouseClicked( e -> {
            mainImgView.setVisible( !mainImgView.isVisible() );
            visible.setImage( mainImgView.isVisible() ? show[0] : show[1] );
        } );
        //getChildren().add( visible );

        //初始化刪除的 Icon 【EventFired】
        ImageView first = new ImageViewBuilder( FIRST_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        first.setOnMouseClicked( e -> {
            setViewOrder( ImageObject.ORDER_TOP );
        } );
        //初始化刪除的 Icon 【EventFired】
        ImageView last = new ImageViewBuilder( LAST_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        last.setOnMouseClicked( e -> {
            setViewOrder( ImageObject.ORDER_BOTTOM );
        } );
        
        //初始化刪除的 Icon 【EventFired】
        ImageView up = new ImageViewBuilder( UP_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        up.setOnMouseClicked( e -> {
            int val = setViewOrder( ImageObject.ORDER_UP );
            System.out.println((val == 0) ? "向上一層" : "以達到頂" );
        } );
        //初始化刪除的 Icon 【EventFired】
        ImageView down = new ImageViewBuilder( DOWN_ICON ).reSize( 15, 15).build();
        //監聽器： Main 皆有負責做接收
        down.setOnMouseClicked( e -> {
            int val = setViewOrder( ImageObject.ORDER_DOWN );
            System.out.println((val == 0) ? "向下一層" : "以達到底" );
        } );
        

        GridPane iconsPane = new GridPane();
        iconsPane.addRow(0, first, visible  );
        iconsPane.addRow(1, up );
        iconsPane.addRow(2, down, delete  );
        iconsPane.addRow(3, last  );
        iconsPane.setAlignment(Pos.TOP_RIGHT);
        iconsPane.setVgap(0);
        iconsPane.setHgap(20);
        getChildren().add(iconsPane);
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       功能區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡

    //--------------------------------------------------
    /** 判斷是否有包含 Tag 
     * @param str 要判斷的 Tag */
    public boolean containTag( String str ){
        return tags.contains( str ) ? true : false;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       回傳區(Getter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得此圖片的所有標籤 @return 取得標籤串列 {@code [ArrayList<String>]} */
    public ArrayList<String> getTags(){ return tags; }
    //------------------------------------------------------------------------------------------------
    /** 回傳這個物件是屬於哪一個圖層 {@link Layer} @return 圖層 {@code [Layer]} */
    public Layer getBelongLayer(){  return belongLayer; }
    //------------------------------------------------------------------------------------------------
    /** 取得目前旋轉的角度 @return 角度 {@code [Int]} */
    public int getRotation(){ return rotation; }
    //------------------------------------------------------------------------------------------------
    /** 取得目前縮放的規模 @return 規模 {@code [Double]}*/
    public double getScale(){  return (double)scale; }
    //------------------------------------------------------------------------------------------------
    /** 取得圖片寬度像素大小 @return 寬度 {@code [Int]} */
    public int getPixelWidth(){ return (int)imageView.getImage().getWidth(); }
    //------------------------------------------------------------------------------------------------
    /** 取得圖片高度像素大小 @return 高度 {@code [Int]} */
    public int getPixelHeight(){ return (int)imageView.getImage().getHeight(); }
    //------------------------------------------------------------------------------------------------
    /** 取得 <b>{@code 添加在中間畫布區}</b> 的 {@link ClickableImageView} 的 {@link ImageView} @return {@code [ImageView]}*/
    public ImageView getMainImgView(){ return mainImgView.getImageView(); }
    //------------------------------------------------------------------------------------------------
    /** 取得 <b>{@code 添加在中間畫布區}</b> 的 ClickableImageView @return {@code [ClickableImageView]}*/
    public ClickableImageView getClickableImageView(){ return mainImgView; }
    //------------------------------------------------------------------------------------------------
    /** 取得 X 座標  @return X 座標 {@code [Int]}*/
    public double getX(){ return posX; }
    //------------------------------------------------------------------------------------------------
    /** 取得 Y 座標  @return Y 座標 {@code [Int]}*/
    public double getY(){ return posY; }
    //------------------------------------------------------------------------------------------------
    /** 取得 {@link Layer} 圖層透明度  @return 透明度 {@code [doubleㄋ]}*/
    public double getLayerAlpha(){ return belongLayer.getAlpha(); }
    /** 取得 寬度(Width) 座標  @return 寬度(Width) {@code [Double]}*/
    public double getImageWidth(){ return imageWidth; }
    /** 取得 長度(Height) 座標  @return 長度(Height) {@code [Double]}*/
    public double getImageHeight(){ return imageHeight; }
    //------------------------------------------------------------------------------------------------
    /** <p>回傳最後的圖片結果</p>
     *  <p>目前只使用在匯出成 PNG 與 JPG 時會使用到</p>
     *  @return 回傳由 {@code MainImageView} 轉換成的 {@code [Image]} */
    public Image getFinalImage(){
        SnapshotParameters params = new SnapshotParameters();
        params.setFill( Color.TRANSPARENT );
        return mainImgView.snapshot( params, null );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       設定區(Setter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定座標 【EventFired】
     * @param x 欲設定的 {@code X 座標}
     * @param y 欲設定的 {@code Y 座標}*/
    public void setLocation(double x, double y ){ 
        ImageObjectClone clone = new ImageObjectClone( this ); //先建立複製品

        this.posX = x; 
        this.posY = y; 

        //當 "座標" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.MOVING , this, clone) );
    }
    //--------------------------------------------------
    /** <p>圖片旋轉 【EventFired】</p>
     *  <p>註：有邊界檢查</p>
     * @param inDegree 欲旋轉的角度*/
    public void setRotation(int inDegree){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品


        setRotationNoEvent( inDegree );
        //設定當 "旋轉角度" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.ROTATION , rotation + " 度", this, clone) );
    }
    //--------------------------------------------------
    /** 設定所屬圖層 {@link Layer}
     *  @param belongLayer 圖層 */
    public void setBelongLayer(Layer belongLayer){
        this.belongLayer = belongLayer;
    }
    //≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡
    /** 更改所有的 Tags
     *  @param newTags 新的字串 */
    public void setTag( ArrayList<String> newTags ){
        this.tags = newTags;
    }
    //--------------------------------------------------
    /** 更改指定的 Tag 【EventFired】
     *  @param index 欲修改的 Tag 標籤
     *  @param newTag 新的字串 */
    public void setTag(int index, String newTag){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品

        tags.remove( index );
        tags.add( index, newTag );

        //當 "標籤" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.TAG_UPDATE , this, clone) );
    }
    //--------------------------------------------------
    /** 刪除指定的 Tag 【EventFired】
     *  @param index 欲刪除的 Tag 標籤位置
     *  @return 回傳刪除的 Tag {@code [String]}*/
    public String removeTag( int index ){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品

        //當 "標籤" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.TAG_DELETE , this, clone) );

        return tags.remove( index );
    }
    //--------------------------------------------------
    /** 刪除指定的 Tag 【EventFired】
     *  @param text 欲刪除的 text 字串
     *  @return 回傳刪除的 Tag {@code [String]}*/
    public boolean removeTag( String text ){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品

        //當 "標籤" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.TAG_DELETE , this, clone) );

        return tags.remove( text );
    }
    //--------------------------------------------------
    /** 新增標籤  【EventFired】
     *  @param str 欲增加的標籤
     *  @return false 假如標籤已存在or其他問題，true 成功加入 */
    public void addTag(String str){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品

        tags.add( str );

        //當 "標籤" 增加的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.TAG_ADD , this, clone) );
    }
    //≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡
    /** 由網路的圖片物件將圖片、標籤設定過來 【EventFired】
     *  @param inObj 網路取得的圖片物件*/
    public void setImageData(ImageObject inObj){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品

        tags = inObj.getTags();
        setImage( inObj.getImage() );

        //當 "圖片資訊" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.INFO_UPDATE , this, clone) );
    }
    //≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡
    /** 向上對其 */
    public final static int ALIGN_TOP = 0;
    /** 向下對齊 */
    public final static int ALIGN_DOWN = 1;
    /** 向左對齊 */
    public final static int ALIGN_LEFT = 2;
    /** 向右對齊 */
    public final static int ALIGN_RIGHT = 3;
    /** 置中 */
    public final static int ALIGN_CENTER = 4;
    /** 圖片對齊 
     *  @param type 圖片對其方式*/
    public void setAlign( int type ){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品
        double w = belongLayer.getCanvas().getWidth(), h = belongLayer.getCanvas().getHeight();
        switch(type){
            case ALIGN_TOP:
                posY = 0;
                break;
            case ALIGN_DOWN:
                posY = h - getPixelHeight();
                break;
            case ALIGN_LEFT:
                posX = 0;
                break;
            case ALIGN_RIGHT:
                posX = w - getPixelWidth();
                break;
            case ALIGN_CENTER:
                posY = (h - getPixelHeight() ) / 2;
                posX = (w - getPixelWidth()  ) / 2;
                break;
            default:return;
        }
        mainImgView.setTranslateX( posX );
        mainImgView.setTranslateY( posY );

        //當 "圖片對齊" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.ALIGN , this, clone) );
    }
    //≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡
    /** 向上移動 */
    public final static int MOVE_UP = 0;
    /** 向下移動 */
    public final static int MOVE_DOWN = 1;
    /** 向左移動 */
    public final static int MOVE_LEFT = 2;
    /** 向右移動 */
    public final static int MOVE_RIGHT = 3;
    /** 圖片移動距離
     *  @param dirct 移動方向
     *  @param pixel 移動 n 像素點 */
    public void move(int dirct, int pixel){

        //先建立複製品
        ImageObjectClone clone = new ImageObjectClone( this );  

        //取得父畫部的長]與[寬]
        double w = belongLayer.getCanvas().getWidth(), h = belongLayer.getCanvas().getHeight();
        switch(dirct){
            //向上
            case MOVE_UP:
                posY -= pixel;
                if( posY + getPixelHeight() < 0 )posY = -getPixelHeight();
                break;
            //向下
            case MOVE_DOWN:
                posY += pixel;
                if( posY > h )posY = h;
                break;
            //向左
            case MOVE_LEFT:
                posX -= pixel;
                if( posX + getPixelWidth() < 0 )posX = 0;
                break;
            //向右
            case MOVE_RIGHT:
                posX += pixel;
                if( posX > w )posX = w;
                break;
            default:break;
        }
        mainImgView.setTranslateX( posX );
        mainImgView.setTranslateY( posY );

        //當 "圖片移動" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.MOVING , this, clone) );
    }
    //≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡
    public static final int ORDER_TOP = 0;
    public static final int ORDER_BOTTOM = 1;
    public static final int ORDER_UP = 2;
    public static final int ORDER_DOWN = 3;
    /** 設定圖片的前後順序 
     *  @param type 動作類別
     *  @return 是否已達到底or頂 {@code 1 = 已到頂} | {@code  = 正常} | {@code -1 = 以到底} */
    public int setViewOrder(int type){
        //先建立複製品
        ImageObjectClone clone = new ImageObjectClone( this );  
        int value = 0;
        switch (type) {
            case ORDER_TOP:
                belongLayer.getImageList().setViewTop(this);
                break;
            case ORDER_BOTTOM:
                belongLayer.getImageList().setViewBottom(this);
                break;
            case ORDER_UP:
                if( !belongLayer.getImageList().setViewUp(this) )
                    value = 1;
                break;
            case ORDER_DOWN:
                if( !belongLayer.getImageList().setViewDown(this) )
                    value = -1;
                break;
            default:
                break;
        }

        if( value == 0 )
            //當 "圖片移動" 發生改變的時候，Fire 出一個Event
            Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.VIEW_ORDER , this, clone) );
        
        return value;
    }
    //≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡=≡
    /**設定圖片的寬度以及長度 
    *  @param width 寬度
    *  @param height 長度*/
    public void setImageSize( double width, double height ){
        //先建立複製品
        ImageObjectClone clone = new ImageObjectClone( this ); 
          
        if (width <= 0) width = this.imageWidth*(height/this.imageHeight);
        if (height <= 0) height = this.imageHeight*(width/this.imageWidth);
        this.imageWidth = width;
        this.imageHeight = height;
        setImageSizeNoEvent(width, height);

        //當 "圖片大小" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.SIZE , this, clone) );
   }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        覆寫區(Overriding)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    @Override public void setImage(Image img) {
        mainImgView.setImage( img );

        imageView.setImage( img );
        double w = img.getWidth(), h = img.getHeight();
        double scale = ((double)IMAGE_VIEW_BOX_SIZE / ((w > h) ? w : h));
        imageView.setFitWidth( (int)(w * scale) );
        imageView.setFitHeight( (int)(h * scale) );
    }
    //----------------------------------------------------------------------------------------
    /** {@inheritDoc} 【EventFired】 */
    @Override public void setAlpha(int inOpacity) {

        //先建立複製品
        ImageObjectClone clone = new ImageObjectClone( this );  

        setAlphaNoEvent( inOpacity );

        //設定當 "透明度" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.OPACITY , alpha + "%", this, clone) );
    }
    //----------------------------------------------------------------------------------------
    /** {@inheritDoc}  【EventFired】 */
    @Override public void filp(short type) {

        //先建立複製品
        ImageObjectClone clone = new ImageObjectClone( this );  

        filpNoEvent( type );

        //當 "翻轉" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.FILP , type == 0 ? "水平翻轉" : "垂直翻轉", this, clone) );
    }
    //----------------------------------------------------------------------------------------
    /** {@inheritDoc}  【EventFired】 */
    @Override public void rename(String name){
        ImageObjectClone clone = new ImageObjectClone( this );  //先建立複製品

        if( name.length() > 10 ){
            //throw new Exception("名稱長度超過 " + MAX_NAME_LENGHT + " 個字元！" );
        }
        else if( name.matches("[^a-zA-Z_0-9]*") ){
            //throw new Exception("名稱只能有數字以及英文！" );
        }
        //設定當 "名子" 發生改變的時候，Fire 出一個Event
        //fireEvent( new ImageEvent( ImageEvent.RENAME , name, this, this) );
        this.name = name;
        nameLabel.setText( name );


        //設定當 "名子" 發生改變的時候，Fire 出一個Event
        Event.fireEvent( belongLayer, new ImageEvent( ImageEvent.RENAME , name, this, clone) );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       復原動作區(Undo)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    @Override public void updateThis(){
        //設定 Label 名稱
        nameLabel.setText( name ); 

        //設定座標
        mainImgView.setTranslateX( posX );
        mainImgView.setTranslateY( posY );

        //設定旋轉
        setRotationNoEvent( rotation );

        //更新圖片大小
        setImageSizeNoEvent(imageWidth, imageHeight);

        //更新透明度
        setAlphaNoEvent( alpha );
    }
    //----------------------------------------------------------------------
    /** 將複製的 {@link ImageObjectClone} 回存至這個物件 
     *  @param clone 複製品*/
    public void restore( ImageObjectClone clone ){
        this.name           = clone.getName();
        this.alpha          = clone.getAlpha();
        this.posX           = clone.getX();
        this.posY           = clone.getY();
        this.tags           = clone.getTags();
        this.rotation       = clone.getRotation();
        this.imageWidth     = clone.getWidth();
        this.imageHeight    = clone.getHeight();

        imageView.setImage( clone.getImage() );
        mainImgView.setImage( clone.getImage() );

        //設定翻轉，不等於就翻轉
        if( filpHorizon  != clone.getFilp()[0] ) filpNoEvent( FILP_HORIZONTAL );
        if( filpVertical != clone.getFilp()[1] ) filpNoEvent( FILP_VERTICAL );

        updateThis();
        belongLayer.updateImageView();
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        資訊列(InfoList)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 建立當滑鼠右鍵點擊圖片時，跳出的詳細訊息 */
    @Override
    public void showInfoList( Node viewNode ){

        //設定彈出視窗的 Class
        infoMenu = new ContextMenu(){
            { getStyleClass().add("infoMenu"); }
        };
        //-------------------------------------------------
        //旋轉的 TextSlider
        TextSliderMenuItem rotationSider =  new TextSliderMenuItemBuilder("旋轉：").setRange(0, 360).setValue( rotation ).build();
        rotationSider.setTextFieldHandler( () -> setRotation( (int)rotationSider.getValue() ) );
        //設定當使用 Slider 時不要讓它頻繁更新值
        rotationSider.setSliderHander( () -> {
            int value = rotationSider.getValue();
            imageView.setRotate( value );
            mainImgView.setRotate( value );
        } );
        //-------------------------------------------------
        //透明度的 TextSlider
        TextSliderMenuItem alphaSilder = new TextSliderMenuItemBuilder( "透明：").setRange( 0, 100 ).setValue( alpha ).build();
        alphaSilder.setTextFieldHandler( () -> setAlpha( (int)alphaSilder.getValue() ) );
        //設定當使用 Slider 時不要讓它頻繁更新值
        alphaSilder.setSliderHander( () -> {   
            double value = (double)alphaSilder.getValue() / 100;
            mainImgView.setAlpha( value * ((double)belongLayer.getAlpha() / 100) );
            imageView.setOpacity( value * ((double)belongLayer.getAlpha() / 100) );
        });

        //-------------------------------------------------
        //寬度的 SliderBar
        TextSliderMenuItem widthSilder = new TextSliderMenuItemBuilder( "寬度：").setRange( 10, 600 ).setValue( (int)getClickableImageView().getWidth() ).build();
        TextSliderMenuItem heighSilder = new TextSliderMenuItemBuilder( "長度：").setRange( 10, 600 ).setValue( (int)getClickableImageView().getHeight() ).build();
        
        widthSilder.setTextFieldHandler( () -> setImageSize( widthSilder.getValue(), heighSilder.getValue() ) );
        //設定當使用 Slider 時不要讓它頻繁更新值
        widthSilder.setSliderHander( () -> setImageSizeNoEvent( widthSilder.getValue(), heighSilder.getValue() ) );
        
        heighSilder.setTextFieldHandler( () -> setImageSize( widthSilder.getValue(), heighSilder.getValue() ) );
        //設定當使用 Slider 時不要讓它頻繁更新值
        heighSilder.setSliderHander( () -> setImageSizeNoEvent( widthSilder.getValue(), heighSilder.getValue() ) );

        MenuItem baseItem[] = getInfoMenuItems();
        //-------------------------------------------------
        //建立資訊清單
        MenuItem items[] = new MenuItem[]{
            baseItem[0],
            alphaSilder,
            rotationSider,
            baseItem[1],
            widthSilder,
            heighSilder,
            new LockedMenuItem( "座標 ：", "(", Integer.toString( (int)posX ), ",", Integer.toString( (int)posY ), ")" ),
            new LockedMenuItem( "像素 ：", Integer.toString( getPixelWidth() ), " x ", Integer.toString( getPixelHeight() ) ),
            new LockedMenuItem( "標籤 ："){
                {
                    setContent( new ImageViewBuilder( "textures/addTag.png" ).reSize(18, 18).build() );
                    //匿名函式宣告：監聽是否按下新增 Tag Image View
                    getGraphic().setOnMouseReleased( e -> {
                        //初始化 tagItem
                        TagFieldMenuItem tagItem = new TagFieldMenuItem( "    -" );
                        tagItem.setContentText( "Null" );

                        //設定 tag
                        final int index = tags.size();
                        addTag( "Null" );
                        //當使用者設定 Tag 時
                        tagItem.setSetTagHandler( () -> setTag( index, tagItem.getInputText() ) );
                        //當使用者刪除 Tag 時
                        tagItem.setDeleteTagHandler( () -> {
                            removeTag( index );
                            infoMenu.hide();
                            showInfoList( viewNode );
                        });

                        infoMenu.getItems().add( tagItem );
                    } );
                }
            }
        };
        
        //加入至 ContextMenu
        ObservableList<MenuItem> itemList = infoMenu.getItems();    
        itemList.addAll( items );

        //建立標籤清單
        int tagIndex = 0;
        for( String tag : tags ){
            TagFieldMenuItem menuItem = new TagFieldMenuItem( "    -" );
            menuItem.setContentText( tag );
            final int index = tagIndex;

            //當使用者設定 Tag 時
            menuItem.setSetTagHandler( () -> setTag( index, menuItem.getInputText() ) );
            //當使用者刪除 Tag 時
            menuItem.setDeleteTagHandler( () -> {
                removeTag( index );
                infoMenu.hide();
                showInfoList( viewNode );
            });
            itemList.add( menuItem );
            tagIndex += 1;
        }


        //顯示 ContextMenu
        infoMenu.show(viewNode, Side.BOTTOM, 65, -5);

        //播放動畫
        playInfoMenuAnimation( itemList );

    }








    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        雜項(Misc)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>提供 {@code 不會觸發事件(Event)的 setRotation()}</p> 
     *  <p>假如要觸發事件請用 {@link #setRotation()} </p>
     *  @param type 垂直 or 水平 */
    final private void setRotationNoEvent( int inDegree ){
        rotation = ((inDegree <= 360) && (inDegree >= 0)) ? inDegree : ((inDegree > 720) ? 720 : 0);
        imageView.setRotate( inDegree );
        mainImgView.setRotate( inDegree );
    }
    /** <p>提供 {@code 不會觸發事件(Event)的 Filp()}</p> 
     *  <p>假如要觸發事件請用 {@link #filp()} </p>
     *  @param type 垂直 or 水平 */
    final private void filpNoEvent( short type ){
        double val = 0;
        //判斷垂直 　or　水平
        switch (type) {
            case FILP_HORIZONTAL:
                val = (filpHorizon = !filpHorizon) ? -1 : 1;
                imageView.setScaleX( val );
                mainImgView.setScaleX( val );
                break;
            case FILP_VERTICAL:
                val = (filpVertical = !filpVertical) ? -1 : 1;
                imageView.setScaleY( val  );
                mainImgView.setScaleY( val  );
            default:
                break;
        }
    }
    /**<p>提供 {@code 不會觸發事件(Event)的 setImageSize()}</p>
     * <p>假如要觸發事件請用 {@link #setImageSize()} </p>
     * 設定圖片的寬度以及長度 
     *  @param width 寬度
     *  @param height 長度*/
    final private void setImageSizeNoEvent( double width, double height ){
        
        imageView.setFitWidth( width );
        imageView.setFitHeight( height );

        mainImgView.reSize( width, height );

        updateSize(width, height);
    }
    //=======================================================================================
    /** <p>提供 {@code 不會觸發事件(Event)的 setAlpha()}</p> 
     *  <p>假如要觸發事件請用 {@link #setAlpha()} </p>
     *  @param inOpacity 欲設定的透明度 */
    final private void setAlphaNoEvent( int inOpacity ){
        this.alpha = ((inOpacity >= 0 && inOpacity <= 100) ? inOpacity : (inOpacity > 100 ? 100 : 0) );
        
        double setOpac = ((double)alpha / 100) * ((double)belongLayer.getAlpha() / 100);
        //當圖層(Layer)的後透明度發生改變時，自己也是隨著比例而改動透明度
        mainImgView.setAlpha( setOpac );
        imageView.setOpacity( setOpac );
    }
    
    //=======================================================================================
    /** <p>{@code 警告！ Warning！}</p>
     *  <p>提供 {@code 沒有 Event} 的圖片翻轉 </p> 
     *  <p>專提供給 Layer 來使子圖片物件翻轉用的 </p>
     *  <p>沒有特殊需求請不要使用</p>
     *  設定圖片翻轉
     *  @param type 垂直 or 水平 */
    @Deprecated protected void filpByLayer( short type ){
        if( type == FILP_HORIZONTAL ){
            double val = (filpHorizon = !filpHorizon) ? -1 : 1;
            imageView.setScaleX( val );
            mainImgView.setScaleX( val );
            //圖層寬度
            double layerW = (belongLayer.getCanvas().getWidth() );
            //圖片寬度
            double imgW = getImage().getWidth() ;
            //圖片移動
            posX = ( posX <= (layerW / 2) ? (layerW - posX - imgW) : (posX + imgW) - layerW ) ;
            mainImgView.setTranslateX( posX  );
        }
        else{
            double val = (filpVertical = !filpVertical) ? -1 : 1;
            imageView.setScaleY( val  );
            mainImgView.setScaleY( val  );
            //圖層寬度
            double layerH = (belongLayer.getCanvas().getHeight() );
            //圖片寬度
            double imgH = getImage().getHeight() ;
            //圖片移動
            posY = ( posY <= (layerH / 2) ? (layerH - posY - imgH) : (posY + imgH) - layerH ) ;
            mainImgView.setTranslateY( posY  );
        }

        //更新右側 ViewBox
        belongLayer.updateImageView();
    }
}