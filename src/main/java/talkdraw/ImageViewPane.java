package talkdraw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import talkdraw.componet.MyLabel;
import talkdraw.componet.PagePane;
import talkdraw.dialog.AddImagePane;
import talkdraw.event.PagePaneFouseEvent;
import talkdraw.imgobj.ImageList;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.base.ViewBox;
import talkdraw.misc.ImageViewBuilder;

public class ImageViewPane extends BorderPane {
    /** 最大的寬 與 高 */
    public final int MAX_WIDTH = 210, MAX_HEIGHT = 1000;
    /** 圖片物件分頁 <blockquote><pre>
     *TabPage[0] = 顯示"所有物件"的分頁
     *TabPage[1] = 顯示"目前圖層物件"的分頁
     *TabPage[2] = 顯示"目前選取物件"的分頁
     *TabPage[3] = 顯示"網路選擇的物件"分頁
     *  </pre></blockquote>*/
    private ImageTab TabPage[] = new ImageTab[5];
    /** 顯示 {@link Tab} 的 {@link TabPane} */
    private TabPane TAB_PANE;

    /** <p>當使用者被指令或其他非自己切換時，暫存之前的 tempTab</p> 
     *  <p>比如在選擇網路圖片時，會跳至 {@code 網路Tab} 結束後就可以利用這個切回來</p>*/
    private ImageTab tempTab;

    private ImageView addIcon;
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;
    public ImageViewPane(App APP) {
        this.APP = APP;

        //初始化標題欄位
        StackPane titlePane = new StackPane( new MyLabel( "圖片物件", 18, Color.LIGHTGRAY, "-fx-font-weight: bold" ) );
        titlePane.setStyle("-fx-background-color: linear-gradient(to bottom, #131313, #2c2c2c);");
        titlePane.setAlignment( Pos.CENTER );
        titlePane.setMinHeight( 30 );

        addIcon = new ImageViewBuilder("/textures/addTag.png").reSize( 25, 25).align( Pos.CENTER_RIGHT ).build();
        addIcon.setOnMouseClicked( e -> {
            //System.out.println( "新增圖片 TODO......(ImageViewPane.java --- 50)" );
            new AddImagePane(APP.PRIMARY_STAGE, APP, Main.GET_MODE.WEB).show();
        } );

        //初始化 TabPane 以及 Tabs
        TAB_PANE = new TabPane();
        TabPage[0] = new ImageTab("所有");
        TabPage[1] = new ImageTab("目前");
        TabPage[2] = new ImageTab("選取");
        TabPage[3] = new ImageTab("網路");
        TabPage[4] = new ImageTab("本地");

        //先將暫存 TabPane 設為所有
        tempTab = TabPage[1];

        //監聽器：監聽當 Tab 分頁改變時所要做的動作
        TAB_PANE.getSelectionModel().selectedItemProperty().addListener( (observable, oldTab, newTab) -> {
            //當 Page Pane 改變時 Fire 給 Main 接收
            ImageTab nowTab = (ImageTab)newTab;
            Platform.runLater( () -> {
                //清除其他容器的 ViewBox 不然會跳出錯誤
                for( ImageTab tab : TabPage )tab.clearViewBox();
                //判斷目前使用者點選了哪一個 Tab
                switch ( TAB_PANE.getTabs().indexOf(newTab) ){
                    case 0:
                        updateImageViewBox( nowTab, -99);
                        break;
                    case 1:
                        updateImageViewBox( nowTab, APP.MAIN.getLayerList().getActiveLayer().getID() );
                        break;
                    case 2:
                        updatePickedImageViewBox( APP.MAIN.getPickedImageList() );
                        break;
                    case 3:
                        updateNetImageViewBox( nowTab );
                        break;
                    case 4:
                        updateLocalImageViewBox( nowTab );
                        break;
                    default:
                        break;
                } 
           

                titlePane.getChildren().remove( addIcon );
                //當只有在選擇 "全部" 以及 "個別圖層" 時的 TabPane 才會顯示 "增加圖片" 的圖案
                if( newTab == TabPage[0] || newTab == TabPage[1] ){
                    titlePane.getChildren().add( addIcon );
                }
            } );

            fireEvent( new PagePaneFouseEvent( PagePaneFouseEvent.CHANGE, nowTab.getPagePane() ) );

        } );
        

        //加入所有的 TabPage
        TAB_PANE.getTabs().addAll( TabPage );
        TAB_PANE.setSide( Side.RIGHT );
         
        //設定內容物
        setTop( titlePane );
        setCenter( TAB_PANE );
        
        //設定高度
        prefWidth( MAX_WIDTH );
        setPrefHeight( MAX_HEIGHT );
        setMaxHeight( MAX_HEIGHT );
        setHeight( MAX_HEIGHT );

        setMinHeight(0);
        setMinWidth(0);
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     顯示物件更新功能      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 在 Tab 裡放入圖片物件 
     *  @param tab 要放入的 Tab */
    private void updateNetImageViewBox(ImageTab tab){
        //清除其他容器的 ViewBox 不然會跳出錯誤
        for( ImageTab t : TabPage )t.clearViewBox();
        //App靜態呼叫：取得 Image 的圖片串列
        tab.addViewBox(  APP.MAIN.getNetConnection().getNetImageList() );
        tab.getPagePane().updateUI();;
    }

    /** 在 Tab 裡放入圖片物件 
     *  @param tab 要放入的 Tab */
    private void updateLocalImageViewBox(ImageTab tab){
        //清除其他容器的 ViewBox 不然會跳出錯誤
        for( ImageTab t : TabPage )t.clearViewBox();
        //App靜態呼叫：取得 Image 的圖片串列
        tab.addViewBox(  APP.MAIN.getLocalConnection().getLocalImageList() );
        tab.getPagePane().updateUI();;
    }
    //-----------------------------------------------------------------
    /** 更新目前使用者選出的圖片串列
     *  @param belongLayerID 物件所屬的 LayerID*/
    private void updatePickedImageViewBox(ArrayList<ImageObject> list){
        //清除其他容器的 ViewBox 不然會跳出錯誤
        for( ImageTab t : TabPage )t.clearViewBox();

        TabPage[2].addViewBox( list );
        TabPage[2].getPagePane().updateUI();
    }  
    //-----------------------------------------------------------------
    /** 更新普通的 Image View Box 
     *  @param tab 目標 Tab
     *  @param belongLayerID 物件所屬的 LayerID*/
    private void updateImageViewBox(ImageTab tab, int belongLayerID){
        //清除其他容器的 ViewBox 不然會跳出錯誤
        for( ImageTab t : TabPage )t.clearViewBox();

        //App靜態呼叫：取得 Image 的圖片串列
        if( belongLayerID != -99 ){
            ImageList list = APP.MAIN.getImageList( belongLayerID );
            for (int i = list.size() - 1; i >= 0; i--) {
                tab.addViewBox(  list.get(i) );
            }
        }
        else {
            ImageList list = APP.MAIN.getUnModifyAllImageList();
            Collections.reverse( list );
            tab.addViewBox( list );
        }
        tab.getPagePane().updateUI();
    }   

    //==============================================================================================
    /** <p>更新右側圖片欄位</p>
     *  <p>當有圖片物件被刪除或新增時，需要更新</p> */
    public void updateViewBox(){
        Platform.runLater( () -> {
            //清除其他容器的 ViewBox 不然會跳出錯誤
            for( ImageTab tab : TabPage )tab.clearViewBox();
            //判斷目前使用者點選了哪一個 Tab
            switch ( TAB_PANE.getSelectionModel().getSelectedIndex() ){
                case 0:
                    updateImageViewBox( TabPage[0], -99);
                    break;
                case 1:
                    updateImageViewBox( TabPage[1], APP.MAIN.getLayerList().getActiveLayer().getID() );
                    break;
                case 2:
                    updatePickedImageViewBox( APP.MAIN.getPickedImageList() );
                    break;
                case 3:
                    updateNetImageViewBox( TabPage[3] );
                    break;
                 case 4:
                    updateLocalImageViewBox( TabPage[4] );
                    break;
                default:
                    break;
            } 
        } );
    }
    /** <p>更新右側圖片欄位</p>
     *  <p>當有圖片物件被刪除或新增時，需要更新</p>
     *  <p>提供給 Main 的 ActiveLayer 監聽</p>
     *  <p>假沒有特殊需求請使用 <pre>public void updateViewBox()</pre> 來代替這個</p>
     *  @param layerID 要顯示的圖層 ID */
    public void updateViewBox(int layerID){
        //清除其他容器的 ViewBox 不然會跳出錯誤
       
        if( TAB_PANE.getSelectionModel().getSelectedIndex() == 1){
            updateImageViewBox( TabPage[1], layerID );
        }
    }

    /** 所有圖片物件 View TabPane */
    public final static int ALL = 0;
    /** 目前圖層圖片物件 View TabPane */
    public final static int CURRENT = 1;
    /** 目前選擇的圖片物件 View TabPane */
    public final static int SELECT = 2;
    /** 目前網路圖片物件 View TabPane */
    public final static int NETWORK = 3;
    /** 目前網路圖片物件 View TabPane */
    public final static int LOCAL = 4;
    /** 回到使用者之前的圖片物件 View TabPane */
    public final static int DEFAULT = 5;
    /** 切換 {@link TabPane} 的 View 
     * <blcokquote><pre>
     *public final static int ALL = 0;      //所有圖片物件
     *public final static int CURRENT = 1;  //目前圖層圖片物件
     *public final static int SELECT = 2;   //選擇的圖片物件
     *public final static int NETWORK = 3;  //網路圖片物件
     *public final static int LOCAL = 4;  //本地圖片物件
     *public final static int DEFAULT = 5;  //回到使用者之前的圖片物件
     * </pre></blockquote>
     *  @param index 欲切換的View */
    public void changeViewTabPane(int index){
        if( index != DEFAULT ){
            //當被切換時，就將暫存的 TabPane 存起來
            tempTab = (ImageTab)TAB_PANE.getSelectionModel().getSelectedItem();
            TAB_PANE.getSelectionModel().select( index );   //設定切換
        }
        else
            TAB_PANE.getSelectionModel().select( tempTab ); //設定回暫存
        updateViewBox();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      回傳區(Getter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得目前選擇的 {@link ImageTab} 
     *  @return 目前選擇的 {@link ImageTab} {@code [ImageTab]} */
    public ImageTab getSelectedTab(){ return (ImageTab)TAB_PANE.getSelectionModel().getSelectedItem(); }






    
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     加入在 TabPane 裡的物件類別      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>自訂義 Image Tab</p>  
     *  <p>解決了 Tab 在顯示中文字時會變成橫向的問題</p> */
    final class ImageTab extends Tab{
        /** 展示所有圖片物件的 Stack Pane */
        private PagePane pagePane;

        public ImageTab(String text){
            getStylesheets().add(getClass().getResource("/styles/tabPane.css").toExternalForm());

            //PagePane 初始化
            pagePane = new PagePane( text );
            pagePane.setStyle("-fx-background-color: #484848");

            //設定 Tab 的標題這樣做是為了防止文字變成橫向閱讀
            MyLabel label = new MyLabel( text, 14 );
            label.setWrapText( true) ;
            label.setPrefWidth( 1 );
            setGraphic( label );

            //依照字串的長度 給予 Tab 的寬度
            StringBuilder strBuilder = new StringBuilder();
            for( int i = text.length(); i-- >= 0 ;  )strBuilder.append("  ");
            setText( strBuilder.toString() );

            setContent( pagePane );

            //將分頁關閉功能關閉
            setClosable( false );
        }
        //---------------------------------------------------
        /** 把圖片框框加入至 Tab 裡 
         *  @param box 欲加的 ViewBox */
        public void addViewBox(ViewBox box){
            pagePane.getContentChildren().add( box );
        }
        //---------------------------------------------------
        /** 把圖片框框加入至 Tab 裡 
         *  @param box 欲加入的 box 串列 */
        public void addViewBox(Collection<? extends ViewBox> box){
            pagePane.getContentChildren().addAll( box );
        }
        //---------------------------------------------------
        /** 把所有的子元件清除，不清除會跳出子類別重複擁有不同父元件 */
        public void clearViewBox(){
            pagePane.getContentChildren().removeIf( node -> node instanceof ViewBox );
        }
        //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
        //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡   Gatter(回傳區)      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
        /** 取得 頁面 PagePane @return 頁面 {@code [PagePane]} */
        public PagePane getPagePane(){ return pagePane; }
    } 
}

