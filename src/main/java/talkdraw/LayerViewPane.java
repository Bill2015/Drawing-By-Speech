package talkdraw;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import talkdraw.componet.PagePane;
import talkdraw.event.PagePaneFouseEvent;
import talkdraw.imgobj.LayerList;
import talkdraw.imgobj.base.ViewBox;
import talkdraw.misc.ImageViewBuilder;

/** 顯示 {@code 圖層} 的 Pane */
public class LayerViewPane extends StackPane{
    /** 最大的寬 與 高 */
    public final int MAX_WIDTH = 210, MAX_HEIGHT = 1000;
    /** 展示所有圖片物件的 Stack Pane */
    private PagePane pagePane;
    /** 將主程式參考進來，以方面更動其他物件 */
    private final App APP;
    public LayerViewPane(App APP) {
        this.APP = APP;
        this.pagePane = new PagePane( "圖層" );
        pagePane.setStyle("-fx-background-color: #484848");

        setStyle("-fx-background-color: #484848");

        
        ImageView addIcon = new ImageViewBuilder("/textures/addTag.png").reSize( 25, 25).align( Pos.TOP_RIGHT ).build();
        addIcon.setOnMouseClicked( e -> {
            System.out.println( "新增圖層" );
            APP.MAIN.newLayer("new"+APP.MAIN.getLayerList().size());
            APP.MAIN.upadateUI();
        } );

        

        getChildren().addAll( pagePane ,addIcon );

        setMinWidth( 0 );
        setPrefWidth( MAX_WIDTH );
        setMinHeight( 0 );
        setPrefHeight( MAX_HEIGHT );
        setMaxHeight( MAX_HEIGHT );

        //監聽器： 當滑鼠在這點擊時 Fire 給 Main 接收
        //這是提供 "暫存" 的 PagePane，可以用來指令翻頁
        addEventHandler( MouseEvent.MOUSE_PRESSED , click -> {
            fireEvent( new PagePaneFouseEvent( PagePaneFouseEvent.CHANGE, pagePane ) );
        });

    }
    /** 創建右側圖層預覽區塊 */
    public void updateViewBox(){
        ObservableList<Node> pList = pagePane.getContentChildren();
        //先移除新增
        pList.removeIf( node -> node instanceof ViewBox );
        LayerList temp = APP.MAIN.getLayerList();
        for(int i= temp.size()-1;i>=0;i--){
            pList.add(temp.get(i));    
        }
        //pagePane.getContentChildren().addAll( APP.MAIN.getLayerList() );
        pagePane.updateUI();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡    Gatter(回傳區)      ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得 頁面 PagePane @return 頁面 {@code [PagePane]} */
    public PagePane getPagePane(){ return pagePane; }
}
