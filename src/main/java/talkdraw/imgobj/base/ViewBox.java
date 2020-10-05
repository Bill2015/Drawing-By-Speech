package talkdraw.imgobj.base;

import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import talkdraw.App;
import talkdraw.componet.menuitem.ButtonMenuItem;
import talkdraw.componet.menuitem.LockedMenuItem;
import talkdraw.componet.menuitem.TextFieldMenuItem;
import talkdraw.componet.menuitem.TextFieldMenuItemBuilder;
import talkdraw.componet.PageableNode;
import talkdraw.componet.MyLabel;

/** <p>{@link Layer} 與 {@link ImageObject} 的母親</p>
 *  <p>因為這兩者需要顯示右側框框，所以就讓他們繼承這個</p>
 *  <p>注意：不要把他拿來宣告，沒有用的喔</p> 
 *  @see Layerd
 *  @see ImageObject*/
public abstract class ViewBox extends StackPane implements PageableNode{
    /** 物件名稱 */
    protected String name;
    /** 名稱 Label，會顯示出來 */
    protected MyLabel nameLabel;
    /** 編號 Label，不會顯示出來，只有被選擇到時 */
    protected MyLabel IDLabel;
    /** 顯示與儲存圖片用的，很重要 */
    protected ImageView imageView;
    /** <p>不透明度</p> 
     *  <p>其值範圍 {@code [0 ~ 100]}</p>*/
    protected int alpha = 100;
    /** {@code 水平} 與 {@code 垂直} 翻轉 */
    protected boolean filpHorizon = false, filpVertical = false;
    /** 顯示資訊的 {@link ContextMenu } */
    protected ContextMenu infoMenu;
    /** 圖層的 ID */
    protected final int ID;
    /** 圖片顯示的區塊大小 */
    protected final short IMAGE_VIEW_BOX_SIZE = 75;
    /** 允許最長的名稱字元數 */
    protected final short MAX_NAME_LENGHT = 10;
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    建構子區      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 建構子 -- 創建透明背景的圖層 
     *  @param ID 編號
     *  @param name 名稱*/
    public ViewBox(int ID, String name){
        this.ID             = ID;                               //物件編號
        this.name           = name;
        this.imageView      = new ImageView();
        this.infoMenu       = new ContextMenu();
        initializer( App.INITIAL_DRAW_WIDTH, App.INITIAL_DRAW_HEIGHT );
        createListener();
    }
    //-------------------------------------------------------------
    /** 建構子 -- 具有圖片的圖層or圖片 
     *  @param ID 編號
     *  @param name 名稱
     *  @param img 圖片 */
    public ViewBox(int ID, String name, Image img ){
        this.ID             = ID;
        this.name           = name;
        this.imageView      = new ImageView(img);
        this.infoMenu       = new ContextMenu();
        initializer( (int)img.getWidth(), (int)img.getHeight() );
        createListener();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    (Initializer)初始化區      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 更新右側 {@code ViewBox} Size 
     *  @param width 寬度
     *  @param height 長度*/
    final protected void updateSize(double width, double height){
        //設定縮圖邊常比例
        double w = width, h = height;
        double scale = ((double)IMAGE_VIEW_BOX_SIZE / ((w > h) ? w : h));
        imageView.setFitWidth( (int)(w * scale) );
        imageView.setFitHeight( (int)(h * scale) );
    } 
    /** 初始化 */
    final private void initializer(int width, int height){
        updateSize( width, height );

        //設定顯示名稱的 Label
        nameLabel = new MyLabel( name, 18, Pos.BOTTOM_RIGHT);
        nameLabel.setTextFill( Color.LIGHTGRAY );
        getChildren().add( nameLabel );
        setAlignment( nameLabel , Pos.BOTTOM_RIGHT );

        //設定 ID(選擇編號) 的 Label
        IDLabel = new MyLabel( "", 18, Pos.BOTTOM_RIGHT);
        IDLabel.setTranslateY( -20 );
        IDLabel.setTextFill( Color.LIGHTGRAY );
        getChildren().add( IDLabel );
        setAlignment( IDLabel , Pos.BOTTOM_RIGHT );

        //設定 Image View 的底圖
        StackPane pane = new StackPane();
        pane.setCursor( Cursor.HAND );
        pane.setManaged( false );
        pane.getChildren().add( imageView );
        setAlignment( imageView , Pos.TOP_LEFT );
        pane.resizeRelocate(5, 5, IMAGE_VIEW_BOX_SIZE + 5, IMAGE_VIEW_BOX_SIZE + 5);
        pane.setStyle("-fx-border-color: gray;-fx-border-style: solid;-fx-border-width: 1;-fx-background-color: gray");
        getChildren().add( pane );
        setAlignment( pane , Pos.CENTER );

        setManaged(false);
        resize(150, 90);
        setStyle("-fx-border-color: blue;-fx-border-style: solid;-fx-border-width: 2;");
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        (function)功能區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>更新自己的值，目前只有在 {@code 復原or重作} 會用到</p> */
    protected abstract void updateThis();
     /** <p>設定顯示編號的文字(用於使用者選擇編號)</p> 
      *  @param ID 編號(A0 ... A9)*/
    public void setIDLabelText( String ID ){
        IDLabel.setText( ID );
        IDLabel.setVisible( true );
    }
    /** <p>設定隱藏編號的文字(用於使用者選擇編號)</p> */
    public void hideIDLabel(){
        IDLabel.setVisible( false );
    }
    /** 隱藏資訊欄 {@link ContextMenu} */
    public void hideInfoMenu(){
        Platform.runLater(() -> infoMenu.hide() );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Getter)回傳區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得 Image View 的 Image @return 圖片 Image {@code [Image]}*/
    public Image getImage(){ return imageView.getImage(); }
    /** 取得 Image View @return 圖片 ImageView {@code [ImageView]}*/
    public ImageView getImageView(){ return imageView; }
    /** 取得 Image 的 Width @return 寬度 Width {@code [double]}*/
    public double getImageWidth(){ return imageView.getImage().getWidth(); }
    /** 取得 Image 的 Height @return 寬度 Height {@code [double]}*/
    public double getImageHeight(){ return imageView.getImage().getWidth(); }
    /** 取得 ID  @return 編號 ID {@code [int]}*/
    public final int getID(){ return ID; }
    /** 取得 Name  @return 名稱 ID {@code [String]}*/
    public final String getName(){ return name; }
    /** 取得 Opacity 其值{@code 範圍 [0 ~ 100]}  @return 不透明度 Opacity {@code [float]}*/
    public final int getAlpha(){ return alpha; }
    /** 取得 圖片翻轉  @return {@code [0] 水平翻轉} | {@code [1] 垂直翻轉} {@code [boolean[]]}*/
    public final boolean[] getFilp(){ return new boolean[]{ filpHorizon, filpVertical }; }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        監聽器(Listener)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 建立事件監聽器*/
    final private void createListener(){
        //當滑鼠按下時，跳出 Info List
        addEventHandler( MouseEvent.MOUSE_CLICKED, e -> {
            if( e.getButton() == MouseButton.SECONDARY ){
                infoMenu.hide();
                showInfoList( this );
            }
        });
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      (misc)雜項       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得基礎 Info List 
     *  @return 回傳 {@code [MenuItem]}*/
    final protected MenuItem[] getInfoMenuItems(){

        //重新命名的 MenuItem
        TextFieldMenuItem renameItem = new TextFieldMenuItemBuilder( "名稱：" ).setText( name ).build();
        renameItem.setHandler( () -> rename( renameItem.getContentText() ) );

        //-------------------------------------------------
        //水平 or 垂直翻轉的 ButtonMenuItem
        Button horizenBtn = new Button( "水平" );
        horizenBtn.setOnAction( e -> filp( FILP_HORIZONTAL ) );     //設定按下去的動作
        Button verticaBtn = new Button( "垂直" );
        verticaBtn.setOnAction( e ->  filp( FILP_VERTICAL ) );      //設定按下去的動作
        ButtonMenuItem filpMenuItem = new ButtonMenuItem("翻轉：", horizenBtn, verticaBtn );

        //建立資訊清單
        MenuItem item[] = new MenuItem[]{
            renameItem, filpMenuItem
        };

        return item;
    }
    /** 播放當 InfoMenuItem Show 出來時的動畫 */
    final protected void playInfoMenuAnimation( ObservableList<MenuItem> itemList ){
        //播放動畫
        SequentialTransition Sequen = new SequentialTransition();
        for(MenuItem item : itemList){
            TranslateTransition transition = new TranslateTransition( Duration.seconds(0.1), ((LockedMenuItem)item).getContent());
            transition.setFromX( 300 );
            transition.setToX( 0 );
            Sequen.getChildren().add( transition );
        }
        Sequen.play();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      (abstrat)抽象函式       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 當右鍵點擊時，會跳出的訊息提示欄 */
    public abstract void showInfoList( Node viewNode );
    
    //----------------------------------------------------------------------
    /** 設定圖片透明度 {@code 範圍 [0 ~ 100]}
     *  @param inOpacity 欲設定的透明度 */
    public abstract void setAlpha(int inOpacity);
    //----------------------------------------------------------------------
    /** 不翻轉 */  public final static short FILP_NONE = -1;
    /** 水平翻轉 */public final static short FILP_HORIZONTAL = 0;
    /** 垂直翻轉 */public final static short FILP_VERTICAL = 1;
    /** 設定圖片翻轉
     *  @param type 垂直 or 水平 */
    public abstract void filp(short type);
    //----------------------------------------------------------------------
    /** 設定 Image View 的圖片 
     *  @param img 欲設定的圖片 */
    public abstract void setImage(Image img);
    //----------------------------------------------------------------------
    /** <p>重新命名圖層 </p>
     *  目前設定的限制名稱長度不可超過 10 以及只能英文與數字
     *  @param name 名稱 */
    public abstract void rename(String name);
}


