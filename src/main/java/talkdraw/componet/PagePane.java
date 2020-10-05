package talkdraw.componet;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import talkdraw.animation.FadeTransitionBuilder;
import talkdraw.animation.ScaleTransitionBuilder;


/**<p>具有頁數功能的 Page Pane 繼承了 {@link  BorderPane}</p>
 * <p>提供 {@code Scroll Bar} 使用滑鼠滾輪就會出現 </p>
 * <p>提供 {@code Paged Button} 使用滑鼠滾點擊即可翻頁</p>
 * <p>註：當新增內容物時要記得呼叫 {@link #updateUI()} 來更新物件</p>
 * <p>註：目前裡面的物件只能放 {@code 實作過} {@link PageableNode}<p> 
 * <p>=================================================</p>
 * <p>將要展示在 PagePane 上的 Node 實作此 {@link PageableNode}</p> 
 * <p>因為是為了能區別出他們在 PagePane 中的差別</p>
 * <p>註：這非常的重要，不實作就不會動了</p>
 * <p> @see {@link PageableNode}*/
public class PagePane extends BorderPane{
    /** 目前頁面位置 */
    private int nowPage = 1;
    /** 設定目前每頁顯示的最大數量 */
    private int showCounts = 2;
    /** 顯示目前頁數的 Label */
    private MyLabel pageLabel;
    /** 顯示 ViewBox 的中心 StackPane */
    private StackPane CENT_PANE;
    /** 顯示 Page 頁數 的 StackPane */
    private StackPane PAGE_PANE;

    //---------------------------------------------
    /** Scroll Bar 的滾輪位移比率 */
    private double scrollRate = 0.0;
    /** Scroll Bar 的底部方塊 */
    private Rectangle progressRect;
    /** Scroll Bar 的中心方塊 */
    private Rectangle centRect;

    //---------------------------------------------
    /** 底下的 Page Arrow Pane 的高度 */
    private final int PAGE_PANE_HEIGHT = 60;
    /** 初始化 Scroll Bar 的長度 */
    private final int INITAIL_SCROLL_HEIGHT = 350;
    /** 標題的高度 */
    private final int TITLE_HEIGHT = 30;
    /** 標題的高度 */
    private int TOP_GAP = 20;
    /** {@code ViewBox} 的大小 */
    private int VIEWBOX_GAP;
    /** 復原旗標 */
    private boolean NoAnimationOnShow = false;
    //---------------------------------------------
    /** 建構子 */
    public PagePane(){
        CENT_PANE = new StackPane();
        PAGE_PANE = new StackPane();
        
        initialTurnPagePane();
        initialScoller();

        setCenter( CENT_PANE );
        setBottom( PAGE_PANE );
        
        this.VIEWBOX_GAP = 95;      //設置預設為 95
    }
    /** <p>建構子</p>
     *  <p>{@code VIEWBOX_GAP} 預設 95, {@code showCounts} 預設 2</p>
     *  @param title 標題*/
    public PagePane( String title ){
        this();
        
        //標題
        MyLabel label = new MyLabel( title, 18, Color.LIGHTGRAY, "-fx-font-weight: bold" );
        StackPane.setAlignment( label, Pos.CENTER );
        StackPane titlePane = new StackPane( label );
        titlePane.setPrefHeight( TITLE_HEIGHT );
        titlePane.setMinHeight( TITLE_HEIGHT );
        titlePane.setMaxHeight( TITLE_HEIGHT );
        titlePane.setStyle("-fx-background-color: linear-gradient(to bottom, #131313, #2c2c2c);");
        setTop( titlePane );
    }
    /** 建構子 
     *  @param title 標題
     *  @param nodeGag 每個節點之間的空隙
     *  @param showCounts 每列最大可顯示數*/
    public PagePane( String title, int nodeGag, int showCounts ){
        this(title);
        this.VIEWBOX_GAP = nodeGag;
        this.showCounts = showCounts;
    }
    /** 建構子 (目前只給歷史紀錄Pane 來使用) (請不要使用此建構子)
     *  @param nodeGag 每個節點之間的空隙
     *  @param showCounts 每列最大可顯示數*/
    public PagePane( int nodeGag, int showCounts ){
        this();
        this.VIEWBOX_GAP = nodeGag;
        this.showCounts = showCounts;
        this.TOP_GAP = 0;
        this.NoAnimationOnShow = true;
        MyLabel label = new MyLabel( "        歷史紀錄", 15, Color.LIGHTGRAY );
        StackPane.setAlignment( label, Pos.CENTER_LEFT );
        StackPane titlePane = new StackPane( label );
        titlePane.setPrefHeight( 20 );
        titlePane.setMinHeight( 20 );
        titlePane.setMaxHeight( 20 );
        titlePane.setStyle("-fx-background-color: linear-gradient(to bottom, #131313, #2c2c2c);");
        setTop( titlePane );
        setBottom( null );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    初始化      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 建立翻頁箭頭 
     *  @param path 圖片檔案路徑
     *  @param transX X 偏移量
     *  @param pos 父節點對其位置
     *  @return 回傳 {@code [ImageView]}*/
    private ImageView createArrow(String path, int transX, Pos pos, int pageType){
        //初始化 ImageView
        ImageView view = new ImageView( getClass().getResource( path ).toExternalForm() ){{
            setFitHeight( 20 );
            setFitWidth( 40 );
            setTranslateX( transX );
            setTranslateY( -10 );
            getStyleClass().add("pageIcon");
        }};

        //設定滑鼠圖標
        view.setCursor( Cursor.HAND );

        //設定縮放動畫
        ScaleTransition clickScale = new ScaleTransitionBuilder( 0.25, view).setRange( 0.9, 1.2).build();
        ScaleTransition scaleIn = new ScaleTransitionBuilder( 0.25, view).setRange(1.0, 1.2).build();
        ScaleTransition scaleOut = new ScaleTransitionBuilder( 0.25, view).setRange(1.2, 1.0).setDelay( 1 ).build();

        //匿名函式宣告： 建立按鈕放大與縮小的監聽
        view.addEventHandler( MouseEvent.ANY, e -> {
            //當滑鼠進入時
            if( e.getEventType() == MouseEvent.MOUSE_MOVED ){
                scaleOut.stop();
                if( view.getScaleX() < 1.2 ){
                    scaleIn.play();
                }
                scaleOut.play();
            }
            //當滑鼠點擊後放開
            else if( e.getEventType() == MouseEvent.MOUSE_RELEASED ){
                if( e.getButton() == MouseButton.PRIMARY ){
                    System.out.println( turnPage( pageType ) );

                    //彈跳動畫
                    clickScale.play();
                }
            }
        });

        StackPane.setAlignment( view , pos );
        return view;
    }
    //---------------------------------------------------
    /** 初始化放置翻頁箭頭的動畫 */
    private void initialTurnPagePane(){
        //顯示目前頁數 Label
        pageLabel = new MyLabel( "0  1  2", 18 );
        pageLabel.setTextFill( Color.LIGHTGRAY );
        pageLabel.setTranslateY( -13 );
        pageLabel.setTranslateX( -10 );
        Rectangle clip = new Rectangle(0, 0, 20, 20);
        clip.setTranslateX( 12 );
        pageLabel.setClip( clip );
        pageLabel.setOnMouseClicked( e -> {
            turnPage( PAGE_LAST );
        } );
        StackPane.setAlignment( pageLabel, Pos.CENTER );

        //建立箭頭物件
        ImageView arrowL = createArrow("/textures/pageLeft.png", 20, Pos.BOTTOM_LEFT, PAGE_PREVIOUS);
        ImageView arrowR = createArrow("/textures/pageRight.png", -20, Pos.BOTTOM_RIGHT, PAGE_NEXT);

        //用於儲存左右箭頭的 Stack Pane
        PAGE_PANE = new StackPane( arrowL, arrowR, pageLabel );
        PAGE_PANE.setOpacity( 0.25 );
        PAGE_PANE.setMaxHeight( PAGE_PANE_HEIGHT );
        PAGE_PANE.setMinHeight( PAGE_PANE_HEIGHT );
        PAGE_PANE.setPrefHeight( PAGE_PANE_HEIGHT );
    
        //建構淡出入動畫
        FadeTransition fadeIn  = new FadeTransitionBuilder( 0.35, PAGE_PANE ).setRange( 0.25, 1.0 ).build();
        FadeTransition fadeOut = new FadeTransitionBuilder( 0.35, PAGE_PANE ).setRange( 1.0, 0.25 ).setDelay( 1 ).build();
        
        //匿名函式宣告：建立當滑鼠移進出時淡入與淡出
        PAGE_PANE.addEventHandler( MouseEvent.ANY , e -> {
            Platform.runLater(()->{
                fadeOut.stop();
                if( PAGE_PANE.getOpacity() < 1.0 ){
                    fadeIn.play();
                }
                
                //設定淡出入動畫
                Node node = e.getPickResult().getIntersectedNode();
                if( node != null && (!(node.equals( PAGE_PANE ) || (node instanceof ImageView))) ){
                    //規模 arrowL
                    ScaleTransition scale1 = new ScaleTransition( Duration.seconds( 0.35 ) , arrowL );
                    scale1.setToX( 1.0 );scale1.setToY( 1.0 );scale1.play();
                    //規模 arrowR
                    ScaleTransition scale2 = new ScaleTransition( Duration.seconds( 0.35 ) , arrowR );
                    scale2.setToX( 1.0 );scale2.setToY( 1.0 );scale2.play();
                }
    
                fadeOut.play();
            });
        });

    }
    //-----------------------------------------------------------------------------
    /** 初始化 ScollerBar */
    private void initialScoller(){
        //整體的進度條
        progressRect = new Rectangle( 10, INITAIL_SCROLL_HEIGHT, Color.LIGHTGRAY );
        progressRect.setArcHeight( 15 );
        progressRect.setArcWidth( 15 );
        StackPane.setAlignment( progressRect, Pos.TOP_RIGHT );

        //目前滑到的位置
        centRect = new Rectangle( 10, 20, Color.GRAY );
        centRect.setArcHeight( 15 );
        centRect.setArcWidth( 15 );
        StackPane.setAlignment( centRect, Pos.TOP_RIGHT );

        //存放以上兩者個 StackPane
        StackPane pane = new StackPane( progressRect, centRect );
        pane.setMaxWidth( 12 );
        pane.setTranslateX(-5);
        pane.setOpacity( 0.0 );
        StackPane.setAlignment(pane, Pos.TOP_RIGHT);
        
        //匿名函式宣告：當 PagePane 的大小更動時，更新 UI
        heightProperty().addListener( (obser, newVal, oldVal) -> {
            Platform.runLater( () ->{
                updateUI();
            });
        } );


        //淡入與淡出動畫
        FadeTransition fadeIn = new FadeTransitionBuilder( 0.35, pane).fadeIn();
        FadeTransition fadeOut = new FadeTransitionBuilder( 0.35, pane ).setDelay( 1 ).fadeOut();

        //匿名函式宣告：當滑鼠在 Scrolling 時，更新 ViewBox
        CENT_PANE.setOnScroll( e -> {

            //淡入與淡出
            fadeOut.stop();
            if( pane.getOpacity() < 1.0 ){
                fadeIn.play();
            }

            //滑鼠偏移輛
            double scroPos = centRect.getTranslateY() + -scrollRate * e.getTextDeltaY();

            //設定滾軸中間的偏移輛
            setCentRectPos( scroPos );

            //設定當滾動卷軸時 頁碼會自動翻頁
            int page = (int)(scroPos * scrollRate / (showCounts * VIEWBOX_GAP)) + 1;
            if( page != nowPage ){
                int inPage = page > nowPage ? 1 : -1;            
                pageNumberAnimation( (nowPage += inPage), inPage, 0.35  ).play();;
            }

            scrollingViewBox( (scroPos * scrollRate) );

            fadeOut.play();
        });

        CENT_PANE.getChildren().add( pane );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    Scoll Bar 功能      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>當使用者在 scrolling 時</p> 
     *  <p>就會呼叫此功能，用來展示動畫效果以及 Node 出現與消失</p>
     *  @param offset 每個物件的垂直偏移量 */
    private void scrollingViewBox( double offset ){
        int k = 0;
        //只取得子物件為 Node
        for( Node node : CENT_PANE.getChildren().filtered( node -> node instanceof PageableNode ) ){
            //計算 Node 偏差值
            double transY = ((TOP_GAP + (VIEWBOX_GAP * k++ )) - offset);
            node.setTranslateY( transY );
            //判斷顯示範圍
            if( transY >= -30 && transY <= (showCounts * VIEWBOX_GAP) ){
                //假如目前隱藏的就做動畫出現
                if( !node.isVisible() ){
                    //這裡建立序列動畫，當第一個動畫完成後，才會呼叫下一個
                    getScollerAnimation( node, false ).play();
                }
            }
            else {
                //假如目前出現的就做動畫消失
                if( node.isVisible() ){
                    getScollerAnimation( node, true ).play(); 
                }
            }
        }
    }
    //--------------------------------------------------------------------------------------
    /** <p>更新 Scroll Bar</p>
     *  <p>更新他的長度、比例、等等參數</p>
     *  <p>{@code 注：必須在有物件 ViewBox 呼叫才會更新成功} */
    public void updateScrollingBar(){
        if( getParent() == null )return;
        //取得　PagePane 的父框架大小
        double scrollHeight = ( CENT_PANE.getLayoutBounds().getHeight() - TITLE_HEIGHT ) ;
        progressRect.setHeight( scrollHeight );

        //計算 Scoller 比例  [總共要顯示的長度] / [目前 PagePane 長度]
        scrollRate = (double)(( getViewBoxSize() * VIEWBOX_GAP )  ) / scrollHeight;

        //邊界檢查，判斷中心 Scoller 有沒有 超過底圖 或 小於0
        double centHeight = ( ( scrollHeight - VIEWBOX_GAP ) / scrollRate);
        if( centHeight <= 0 || centHeight >= scrollHeight )centHeight = scrollHeight;
        //更改 Scoller 中心的方塊大小
        centRect.setHeight( centHeight );

        CENT_PANE.setClip( new Rectangle(CENT_PANE.getLayoutBounds().getWidth(),CENT_PANE.getLayoutBounds().getHeight()) );
    
    }

    /** 設定中間 ScollerBar 的中心位置 */
    private void setCentRectPos( double scroPos ){
        //判斷是否超出範圍
        if( scroPos < 0 )scroPos = 0;
        else if( (scroPos + centRect.getHeight()) > progressRect.getHeight() )scroPos = ( progressRect.getHeight() - centRect.getHeight() );
        //設定 Scoller 偏移量
        centRect.setTranslateY( scroPos );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    Page功能      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 分頁往前翻一頁 */
    public static int PAGE_PREVIOUS     = -1;
    /** 分頁往後翻一頁 */
    public static int PAGE_NEXT         = 1;
    /** 分頁跳至第一頁 */
    public static int PAGE_FIRST        = -50;
    /** 分頁跳至最後頁 */
    public static int PAGE_LAST         = 50; 
    /** <p>翻頁的函式</p>
     *  <p>跟 {@link #jumpPage(int)} 有些差別</p>
     *  <p>這個函數是負責 {@code 增加} 與 {@code 減少} 頁數</p>
     *  <p>{@code PAGE_PREVIOUS} = 上一頁</p>
     *  <p>{@code PAGE_NEXT} = 下一頁</p>
     *  <p>{@code PAGE_FIRST} = 第一頁</p>
     *  <p>{@code PAGE_LAST} = 最後一頁</p>
     *  @param inPage 要 {@code 增加} 與 {@code 減少} 的頁數
     *  @return 回傳訊息字串 {@code [String]}*/
    public String turnPage( int inPage ){

        //目前頁數暫存
        int tempNowPage = nowPage;
        int times = Math.abs(inPage);
    
        if( inPage == PAGE_FIRST )inPage = -1;
        else if( inPage == PAGE_LAST )inPage = 1;

        //總共幾頁
        int lastPage = (int)Math.ceil((double)getViewBoxSize() / showCounts);

        SequentialTransition sequen = new SequentialTransition();
        for( int k = 0; ((nowPage + inPage) <= lastPage && (nowPage + inPage) >= 1) && k < times; k++ ){
            nowPage += inPage;  
            sequen.getChildren().add( pageNumberAnimation( nowPage, inPage, (double)Math.abs(times - 35) / 100  ) );
        }
        sequen.play();

        //假如改變後的 nowPage 與原本的不一樣 就不要重新跑動畫
        if( nowPage == tempNowPage ){
            return (tempNowPage == 1) ? "以達到第一頁" : "以達到最後一頁";
        }
        else updateViewBox();

        //當翻頁的時候設定 滾軸中間的偏移量
        double scroPos = VIEWBOX_GAP * showCounts * (nowPage - 1) / scrollRate;
        setCentRectPos( scroPos );

        //回傳訊息
        return String.join("", "顯示第 ", Integer.toString( nowPage ), " 頁");
    }
    //--------------------------------------------------------------------------------------
    /** <p>跳至哪一個頁面</p>
     *  <p>跟 {@link #turnPage(int)} 有些差別</p>
     *  @param targetPage 欲跳的頁面 
     *  @return 回傳訊息字串 {@code [String]}*/
    public String jumpPage(int targetPage){

        //目前頁數暫存
        int tempNowPage = nowPage;
        //總共幾頁
        int lastPage = (int)Math.ceil((double)getViewBoxSize() / showCounts);
        //次數
        int times = Math.abs( targetPage - nowPage );
        //判斷遞增 or 遞減
        int inPage = targetPage > nowPage ? 1 : -1;

        SequentialTransition sequen = new SequentialTransition();
        for( int k = 0; ((nowPage + inPage) <= lastPage && (nowPage + inPage) >= 1) && k < times; k++ ){
            nowPage += inPage;  
        
            sequen.getChildren().add( pageNumberAnimation( nowPage, inPage, (double)Math.abs(times - 35) / 100  ) );
        }
        sequen.play();
        
        //判斷更改過後是否還是同一個頁面
        if( nowPage == tempNowPage ){
            return String.join("", "已經在第 ", Integer.toString( nowPage ), " 頁");
        }
        else updateViewBox();

        //當翻頁的時候設定 滾軸中間的偏移量
        double scroPos = VIEWBOX_GAP * showCounts * (nowPage - 1) / scrollRate;
        setCentRectPos( scroPos );

        return String.join("", "顯示第 ", Integer.toString( nowPage ), " 頁");
    }
    //--------------------------------------------------------------------------------------
    /** <p>當翻頁時，把目前顯示的 {@code Node} 做更新</p>
     *  <p>同時也在這邊做動畫的初始化設定</p>*/
    public void updateViewBox(){
        // 起始  [顯示 Node 位置]  與  [截止 Node 位置]
        int startK = ((nowPage - 1) * showCounts), endK = (startK + showCounts), k = 0, offset = 0;

        //建立序列動畫 Class
        SequentialTransition sequentialTransition = new SequentialTransition();

        //只取得 繼承了 Node 的物件
        for( Node node : CENT_PANE.getChildren().filtered( node -> node instanceof PageableNode ) ){
            //判斷目前已經有幾個 Node 元件
            if( startK <= k && k < endK ){
                //假如不要動畫就直接顯示
                if( NoAnimationOnShow == false ){
                    //這裡建立序列動畫，當第一個動畫完成後，才會呼叫下一個
                    sequentialTransition.getChildren().add( 0, getTurnAnimation( node, offset ) );
                }
                else{
                    node.setVisible( true );
                    node.setTranslateX( 20 );
                    node.setTranslateY( 5 + (VIEWBOX_GAP * offset ) );
                }
                offset += 1;
            }
            else node.setVisible( false );
            k += 1;
        }
        sequentialTransition.play();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    Animation(動畫)      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得 Page Turn 動畫 
     * @param node 要設定的動畫物件
     * @param offset 第幾個要顯示的元素
     * @return 回傳平行處理動畫 {@code [ParallelTransition]}*/
    private ParallelTransition getTurnAnimation(Node node, int offset){
        node.setVisible( true );node.setTranslateY( -100 );node.setTranslateX( 20 );

        //移動動畫
        TranslateTransition moveTransition = new TranslateTransition( Duration.seconds(0.2) , node );
        moveTransition.setToY( 20 + (VIEWBOX_GAP * offset ) );

        //淡入動畫
        FadeTransition fadeTransition = new FadeTransitionBuilder( 0.2, node ).fadeIn();

        //淡出動畫
        ScaleTransition scaleTransition = new ScaleTransitionBuilder( 0.2, node ).setRange( 0.2, 1.0).build();

        return new ParallelTransition( moveTransition, fadeTransition, scaleTransition );
    }
    //--------------------------------------------------------------------------------------
    /** 取得 Scroller 動畫 
     * @param node 要設定的動畫物件
     * @param isVanish 是否要消失
     * @return 回傳平行處理動畫 {@code [ParallelTransition]}*/
    private ParallelTransition getScollerAnimation(Node node, boolean isVanish){
        //位移動畫 由左<->右
        TranslateTransition moveTransition = new TranslateTransition( Duration.seconds(0.25), node );
        FadeTransitionBuilder fadeTransitionBuilder = new FadeTransitionBuilder( 0.25, node );
        //設定淡出與淡入
        FadeTransition fadeTransition = !isVanish ? fadeTransitionBuilder.fadeIn() : fadeTransitionBuilder.fadeOut();

        //假如現在要出現時
        if( !isVanish ){
            moveTransition.setToX( 20 );
            node.setTranslateX( -200 );
            node.setVisible( true );
        }
        //假如現在要消失時
        else{
            moveTransition.setToX( -200 );
            moveTransition.setOnFinished(e -> {
                node.setVisible( false );
            });
        }

        return new ParallelTransition( moveTransition, fadeTransition ); 
    }

    /** 數字翻轉動畫 
     *  @param cuPage 目前頁碼
     *  @param inPage 判斷上翻 or 下翻*/
    private ParallelTransition pageNumberAnimation( int cuPage, int inPage, double time ){
        //設定數字動畫
        TranslateTransition clilpMove = new TranslateTransition( Duration.seconds( time ), pageLabel.getClip() );
        clilpMove.setToX(  16 + (inPage > 0 ?  15  : -15) );
        TranslateTransition labelMove = new TranslateTransition( Duration.seconds( time ), pageLabel );
        labelMove.setToX( -10 + (inPage > 0 ? -15  :  15) );        

        ParallelTransition para = new ParallelTransition( clilpMove, labelMove );
        para.setOnFinished( e -> {
            pageLabel.setText( String.format("%3d%3d%3d", cuPage - 1, cuPage, cuPage + 1) );
            pageLabel.setTranslateX( -10 );
            pageLabel.getClip().setTranslateX( 16 );
        });

        //開始數字動畫
        return para;

    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    misc(雜項)      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得這個 PagePane 總共有幾個 
     *  @return ViewBox 數量 {@code [Int]}*/
    private int getViewBoxSize(){
        return CENT_PANE.getChildren().filtered( node -> node instanceof PageableNode ).size();
    }
    //--------------------------------------------------------------------------------------
    /** 更新所有的值，當有物件新增進來時或移除時都需要更新 */
    public void updateUI(){
        updateScrollingBar();
        updateViewBox();
    }
    //--------------------------------------------------------------------------------------
    /** 能取得 CENT_PANE 的 Children 值
     *  @return  modifiable list of children. */
    public ObservableList<Node> getContentChildren(){
        return CENT_PANE.getChildren();
    }
    /** 翻至最後一頁 */
    public void scrollToEnd(){
        //滑鼠偏移輛
        double scroPos =  progressRect.getHeight() - centRect.getHeight() ;

        //設定滾軸中間的偏移輛
        setCentRectPos( scroPos );

        //更新位置
        scrollingViewBox( (scroPos ) * scrollRate );
    }
    //--------------------------------------------------------------------------------------
    /** <p>不要使用此函式用來新增內容物</p>
     *  <p>注：請使用 {@link #getContentChildren()} 來加入物件</p>
     *  @return  modifiable list of children.*/
    @Deprecated @Override public ObservableList<Node> getChildren(){
        return super.getChildren();
    }
}