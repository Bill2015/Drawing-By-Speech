package talkdraw;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import talkdraw.imgobj.LayerList;
import talkdraw.tools.Tool;
import talkdraw.componet.PagePane;
import talkdraw.connection.LocalConnection;
import talkdraw.connection.NetConnection;
import talkdraw.event.BehaviorEvent;
import talkdraw.event.ImageEvent;
import talkdraw.event.LayerEvent;
import talkdraw.event.PagePaneFouseEvent;
import talkdraw.imgobj.ImageList;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.ImageObjectClone;
import talkdraw.imgobj.Layer;

public class Main{
    /** 處理圖層的 Class 內含有 LayersList */
    private LayerList layerList = new LayerList();

    /** 圖片物件串列 */
    private HashMap<Integer, ImageList> imageListMap = new HashMap<>();

    /** 使用者目前選取出的所有圖片雜湊表 */
    private HashMap<String, ImageObject> pickedImageMap = new HashMap<>();

    /** 取得網路圖片的物件 */
    private NetConnection netConnection;

    /** 取得本地圖片的物件 */
    private LocalConnection localConnection;

    /** <p>暫存用的 ImageObject</p> 
     *  <p>原因是當使用者一開始輸入某個動作的時候</p>
     *  <p>會需要一個圖片物件做操作，而當在操作{@code 第二次}時</p>
     *  <p>就可以不用再次輸入圖片名稱，直接拿這個去執行</p>
     *  <p>{@code 註：這個的變體是}<blockquote><pre>private ArrayList<ImageObject> selectedList = new ArrayList<>();</pre>
     *      用來儲存多個使用者選出的圖片，目前 "未使用"</blockquote>*/
    private volatile ImageObject selectedImgObject = ImageObject.NONE;

    /** <p>用來儲存使用者目前操作的是哪一個 {@link PagePane}</p> 
     *  <p>可以執行翻頁、跳頁等等 {@link PagePane} 的操作</p>*/
    private volatile PagePane nowPagePane;

    /** 使用者所選出的圖片 List */
    @Deprecated private ArrayList<ImageObject> selectedList = new ArrayList<>();

    /** 將主程式參考進來，以方面更動其他物件 */
    private App APP;

    /** 要使用的模式 */
    public static enum GET_MODE{
        /** 網路 */
        WEB, 
        /** 本地 */
        LOCAL
    }

    public Main(App APP) {
        this.APP = APP;

        //網頁物件初始化
        netConnection = new NetConnection();
        //本地物件初始化
        localConnection = new LocalConnection();

        newLayer("A0");
        newLayer("A1");
        newLayer("A2");

        Layer layer = layerList.getActiveLayer();
        ImageList imageList = layer.getImageList();


        imageList.add( new ImageObject(layer, "cat", new Image("testingimg/hibiki.png")  ) );
        //imageList.add( new ImageObject(layer, "貓", new Image("一般/貓.png")  ) );
        /*imageList.add( new ImageObject(belongLayerID, "name3", new Image("testingimg/hibiki.png")  ) );
        imageList.add( new ImageObject(belongLayerID, "name4", new Image("testingimg/hibiki2.png")  ) );
        imageList.add( new ImageObject(belongLayerID, "name5", new Image("testingimg/hibiki.png")  ) );
        imageList.add( new ImageObject(belongLayerID, "name6", new Image("testingimg/hibiki2.png")  ) );
        imageList.add( new ImageObject(belongLayerID, "name7", new Image("testingimg/hibiki.png")  ) );
        imageList.add( new ImageObject(belongLayerID, "name8", new Image("testingimg/hibiki2.png")  ) );*/
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      新增區(Adder)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>新增圖層</p> 
     *  @param name 畫布名稱*/
    public final Layer newLayer(String name){
        int newID = layerList.generateID();  
        
        //新增新的畫布，並接收回傳
        Layer newLayer = new Layer(newID, name, new ImageList(), layerList ,imageListMap);
        layerList.add( newLayer  );
        layerList.getActiveLayerProperty().set( newLayer );

        imageListMap.put( newID, newLayer.getImageList() );
        return newLayer;
    }

    public final void newImage(String name){

    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      刪除區(Remover)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 刪除要刪去的圖片 
     *  @param obj 要刪去的 {@link ImageObject} 物件
     *  @return {@code true = 刪除成功} | {@code false = 刪除失敗} */
    public final boolean removeImage(ImageObject obj){
        //App靜態呼叫：取得在主畫布的上的子元件，已用於刪除 ImageView
        ObservableList<Node> drawAreaList = APP.DRAW_AREA.getUpponPane().getChildren();
        //迭代取得 List
        for(ImageList list : imageListMap.values() ){
            //先判斷能不能刪除在 DrawArea 的 ImageView，在判斷能不能刪除在 list 裡的物件
            drawAreaList.remove( obj.getMainImgView() );
            if( list.remove( obj ) ){
                return true;
            }
        }
        return false;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     網路圖片相關功能    ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //---------------------------------------------------------------------------
    /** 加入圖片至畫布 
     * <p>當 {@code id} 為 "一個" 時是指定編號 {@code [A0]}、{@code [A1]}，為 "零個" 時事隨機取一張圖片
     *  @param id 可以輸入 {@code 一個} or {@code 零個} 
     *  @param mode 網路或本地
     *  @return {@code true = 加入成功} | {@code false = 加入失敗}*/
    final public boolean addImageToLayer(GET_MODE mode ,String ... id){
        ImageObject image = null;
        switch(mode){
            case WEB:
                image = netConnection.createImageObject( id );
            break;
            case LOCAL:
                image = localConnection.createImageObject( id );
            break;
        }

        //沒有找到圖片
        if( image == null )return false;

        //設定圖片所屬的圖層(Layer) 並且加入至串列
        Layer layer = getLayerList().getActiveLayer();
        layer.getImageList().add( image );

        ImageObjectClone clone = new ImageObjectClone( image );

        //當新增完後，設定此圖片為目前選擇的
        setNowSelectedImageObject( image );

        layer.fireEvent( new ImageEvent( ImageEvent.ADD , image, clone) );
        
        return true;
    }
    //---------------------------------------------------------------------------
    /** 加入圖片至畫布 
     * <p>當 {@code id} 為 "一個" 時是指定編號 {@code [A0]}、{@code [A1]} ，為 "零個" 時事隨機取一張圖片
     *  @param mode 網路或本地
     *  @param id 可以輸入 {@code Null = 隨機}
     *  @param x 座標 X
     *  @param y 座標 Y
     *  @param filpType 翻轉類型
     *  @param opacity 透明度
     *  @param rotation 旋轉度數
     *  @param width 圖片寬度
     *  @param height 圖片高度
     *  @return {@code true = 加入成功} | {@code false = 加入失敗}*/
    final public boolean addImageToLayer(GET_MODE mode, String id, double x, double y, short filpType, int opacity, int rotation, double width, double height){
        ImageObject image = null;
        switch(mode){
            case WEB:
                image = netConnection.createImageObject( id, x, y, filpType, opacity, rotation, width, height);
            break;
            case LOCAL:
                image = localConnection.createImageObject( id, x, y, filpType, opacity, rotation, width, height );
            break;
        }

        //沒有找到圖片
        if( image == null )return false;

        //設定圖片所屬的圖層(Layer) 並且加入至串列
        Layer layer = getLayerList().getActiveLayer();
        image.setBelongLayer( layer );
        layer.getImageList().add( image );

        ImageObjectClone clone = new ImageObjectClone( image );

        //當新增完後，設定此圖片為目前選擇的
        setNowSelectedImageObject( image );

        layer.fireEvent( new ImageEvent( ImageEvent.ADD , image, clone) );
        
        return true;
    }
    //---------------------------------------------------------------------------
    /** 切換圖片
     * <p>當 {@code id} 為 "一個" 時是指定編號，為 "零個" 時事隨機取一張圖片
     *  @param id 可以輸入 {@code 一個} 
     *  @param mode 網路或本地
     *  @return {@code true = 加入成功} | {@code false = 加入失敗}*/
    final public boolean changeImageToLayer(ImageObject imgObj, String id ,GET_MODE mode ){
        ImageObject temp = null;
        switch(mode){
            case WEB:
                temp = netConnection.createImageObject( id );
                break;
            case LOCAL:
                temp = localConnection.createImageObject( id );
                break;
        }

        ImageObject obj = temp;

        //沒有找到圖片
        if( obj == null )return false;
        
        //栓鎖器
        Platform.runLater(() -> {
            boolean b = false;
            while (!b) {
                b = false;
                try {
                    Thread.sleep(5);
                    //將網路取得的圖片更新至 imgObj
                    imgObj.setImageData( obj );
                    b = true;
                }
                catch(Exception e){}
            }
        });

        return true;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      回傳區(Getter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得圖層串列 @return 圖層串列 {@code [LayerList<? extends ArrayList>]} */
    public LayerList getLayerList(){ return layerList; }
    
    //---------------------------------------------------------------------------
    /** 取得圖片物件串列雜湊圖 @return 圖片物件串列 {@code [ImageList<? extends ArrayList>]} */
    public HashMap<Integer, ImageList> getImageListMap(){ return imageListMap; }

    //---------------------------------------------------------------------------
    /** 取得目前選出的圖片物件串列 @return 圖片物件串列 {@code  ArrayList<ImageObject>} */
    public ArrayList<ImageObject> getPickedImageList(){ return  new ArrayList<ImageObject>( pickedImageMap.values() ); }

    /** 取得目前選出的圖片物件雜湊圖 @return 圖片物件串列 {@code HashMap<String, ImageObject>} */
    public HashMap<String, ImageObject> getPickedImageMap(){ return pickedImageMap; }
    //---------------------------------------------------------------------------
    /** 取得圖片物件串列 
     *  @param belongLayerID 所屬圖層 ID
     *  @return 圖片物件串列 {@code [ImageList<? extends ArrayList>]} */
    public ImageList getImageList(int belongLayerID){ 
        return imageListMap.containsKey( belongLayerID ) ? imageListMap.get(belongLayerID) : new ImageList(); 
    } 
    //---------------------------------------------------------------------------
    /** <p>取得 {@code 所有圖層物件串列} </p>
     *  <p>註：這個串列不能拿來 {@code 新增} 與 {@code 修改} </p>
     * @return 所有圖層串列 {@code [ImageList]} */
    public final ImageList getUnModifyAllImageList(){  
        ImageList temp = new ImageList();
        for( ImageList list : imageListMap.values() )temp.addAll( list );   
        return temp;
    }
    //---------------------------------------------------------------------------
    /** 取得目前暫存的圖片 
     *  <p>如有疑問請參照：{@link #selectedImgObject}<p>
     *  @return 目前操做的圖片 {@code [ImageObject]} */
    public final ImageObject getNowSelectedImageObject( ){ 
        return selectedImgObject != null ? selectedImgObject : ImageObject.NONE ; 
    }
    //---------------------------------------------------------------------------
    /** 取得目前是否有選擇圖片
     *  @return {@code true = 有} | {@code false = 沒有} {@code [Boolean]} */
    public final boolean isNowHaveSelected(){ return (selectedImgObject != null && !selectedImgObject.equals( ImageObject.NONE )); }
    //---------------------------------------------------------------------------
    /** 取得目前操作得 {@link PagePane}
     *  <p>如有疑問請參照：{@link #nowPagePane}<p>
     *  @return 目前操做的{@link PagePane} {@code [PagePane]} */
    public final PagePane getNowPagePane( ){ return nowPagePane; }

    //---------------------------------------------------------------------------
    /** 回傳 {@link NetConnect} 網頁連線 
     *  @return 回傳網頁連線 {@code [NetConnection]}*/
    public final NetConnection getNetConnection(){ return netConnection; }
    //---------------------------------------------------------------------------
    /** 回傳 {@link NetConnect} 本地連線 
     *  @return 回傳本地連線 {@code [NetConnection]}*/
    public final LocalConnection getLocalConnection(){ return localConnection; }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    // ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      設定區(Setter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定目前暫存的圖片 
     *  <p>如有疑問請參照：{@link #selectedImgObject}<p>
     *  @param imgObj 欲設定的圖片*/
    public final void setNowSelectedImageObject( ImageObject imgObj ){  
        Platform.runLater(()->{

            if( selectedImgObject != ImageObject.NONE/*  && !imgObj.equals( selectedImgObject ) */ )
            //將舊的選取圖片框框隱藏
            selectedImgObject.getClickableImageView().setSelected( false );
        
            APP.DRAW_AREA.setNowSelectImage( imgObj.getClickableImageView() );
        });

    }
    /** 設定目前使用者選出的圖片雜湊圖 
     *  @param imgObj 欲設定的以選出圖片雜湊圖*/
    public final void setNowPickedImageObjectMap( HashMap<String, ImageObject> pickedImgMap ){  
        this.pickedImageMap = pickedImgMap;
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     專案檔相關   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>目前只用來做 Loading 整個 Project </p> 
     *  <p>目前使用的函式有 {@link FileProccessor#loadFile()}</p>
     *  @param layerList 圖層串列
     *  @param imageListMap 圖片物件串列地圖*/
    public void LoadingProject(LayerList layerList, HashMap<Integer, ImageList> imageListMap){
        //更新資料 Main 裡的物件資料
        this.layerList = layerList;
        this.imageListMap = imageListMap;
        this.layerList.changeActiveLayer( layerList.get(0) );
        this.selectedImgObject = ImageObject.NONE;

        //清除歷史紀錄堆疊
        APP.HISTORY_UNDO.clear();
        APP.HISTORY_REDO.clear();
        APP.HISTORY_PANE.clear();

        upadateUI();
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     UI 功能區   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>更新所有 UI 介面</p>
     *  <p>在於一些 UI 的變化時，需要呼叫</p>*/
    public void upadateUI(){
        Platform.runLater( () -> {
            //更新右側圖片 ViewBox 欄位
            APP.IMAGE_PANE.updateViewBox();
            
            //更新右側圖層 ViewBox 欄位
            APP.LAYER_PANE.updateViewBox();

            //更新下畫布
            APP.DRAW_AREA.getUnderPane().upadateLayerViewBox();

            //更新中間畫布區
            APP.DRAW_AREA.getUpponPane().updateDrawArea();
        });
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     PagePane 監聽器   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    private void createPagePaneListener(){
        //監聽器： 監聽在 Layer View Pane 的 Page Pane
        APP.LAYER_PANE.addEventHandler( PagePaneFouseEvent.CHANGE , e -> {
            nowPagePane = e.getNowPagePane();
        });
        
        //監聽器： 監聽在 Image View Pane 的 Page Pane
        APP.IMAGE_PANE.addEventHandler( PagePaneFouseEvent.CHANGE , e -> {
            nowPagePane = e.getNowPagePane();;
        });

        Platform.runLater( () -> {
            //初始化 nowPagePane TODO: 未知的BUG解決方法
            //其 BUG 為一開始的 Layer 名子改變時不會馬上改變 
            nowPagePane = APP.LAYER_PANE.getPagePane();
            nowPagePane.jumpPage( 2 );
            //layerList.remove( layerList.size() - 1 );
            nowPagePane.jumpPage( 1 );
        } );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡     UI更新 監聽器   ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    private void createUIListener(){
        //監聽器：當 有做任何動作時，更新目前選擇的 Layer 的 View Box
        APP.PRIMARY_STAGE.addEventHandler( BehaviorEvent.ANY , e -> {

            //假如是圖片動作
            if( e.getActionNode() instanceof ImageObject ){

                if( e.getEventType() == ImageEvent.DELETE ){
                    //刪除在 Main 裡的 ImageObjectList
                    removeImage( (ImageObject)e.getActionNode() );
                    //更新 所有的 的 ViewBox
                    upadateUI();;
                }
                else if( e.getEventType() == ImageEvent.ADD ){
                    //更新 所有的 的 ViewBox
                    upadateUI();
                }
                else if( e.getEventType() == ImageEvent.VIEW_ORDER ){
                    //更新 所有的 的 ViewBox
                    upadateUI();;
                }
                else{
                    ImageObject obj = (ImageObject)e.getActionNode();
                    obj.getBelongLayer().updateImageView();
                }


            }
            //假如是圖層動作
            else if( e.getActionNode() instanceof Layer ){

                if( e.getEventType() == LayerEvent.CLEAR  ){
                    //更新 所有的 的 ViewBox
                    upadateUI();
                }
                else if( e.getEventType() == LayerEvent.DELETE  ){
                    //更新 所有的 的 ViewBox
                    upadateUI();
                }
                else if( e.getEventType() == LayerEvent.VIEW_ORDER ){
                    //更新 所有的 的 ViewBox
                    upadateUI();;
                }
                else{
                    layerList.getActiveLayer().updateImageView();
                }
            }
            //假如是畫筆動作
            else if(e.getActionNode() instanceof Tool){
                layerList.getActiveLayer().updateImageView();
            }
        });

        //----------------------------------------------------------
        //監聽器： 監聽當目前選擇的畫布改變時，就更新右側欄位
        layerList.getActiveLayerProperty().addListener( (obser, oldVal, newVal) -> {
            APP.IMAGE_PANE.updateViewBox( newVal.getID() );
        });

        //----------------------------------------------------------
        //監聽器： 監聽當使用者點選圖片時，會切換目前選擇的圖片 Property (selectedImgObject)
        APP.DRAW_AREA.getSelectImageProperty().addListener( (obser, oldVal, newVal) -> {
            selectedImgObject = newVal.getBelongImageObject();

            if( selectedImgObject != ImageObject.NONE )
                //將新的圖片框框顯示
                selectedImgObject.getClickableImageView().setSelected( true );
        } );
    
    }
    

    public void initializer(){
        upadateUI();
        //建立 PagePane 發生改變時的監聽器
        createPagePaneListener();

        //建立 當變動時更新 UI 的監聽器
        createUIListener();
    }

}
