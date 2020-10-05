package talkdraw;

import java.io.IOException;
import java.util.Stack;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.vivoxalabs.customstage.CustomStage;
import lk.vivoxalabs.customstage.CustomStageBuilder;
import lk.vivoxalabs.customstage.tools.HorizontalPos;
import talkdraw.event.BehaviorEvent;
import talkdraw.event.ImageEvent;
import talkdraw.event.LayerEvent;
import talkdraw.imgobj.ImageList;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.ImageObjectClone;
import talkdraw.imgobj.Layer;
import talkdraw.imgobj.LayerClone;
import talkdraw.imgobj.ViewBoxClone;
import talkdraw.misc.ConsoleColors;
import talkdraw.command.base.CommandHashMap;
import talkdraw.componet.SpeechMessageFloatingPane;
import talkdraw.componet.StatusMessageFloatingPane;




public class App extends Application {
    /** 工具欄 */
    public ToolBar TOOL_BAR;
    /** 繪圖區 */
    public MainDrawPane DRAW_AREA;
    /** 詳細設定欄 */
    public DetailPane DETAIL_PANE;
    /** 程式上方選擇欄位 */
    public TitleMenuBar MENU_BAR;
    /** 檔案處裡 */
    public FileProccessor FILE_PROCCESSOR;
    /** 文字輸入欄位 */
    public TextZonePane TEXT_PANE;
    /** 圖層欄位 */
    public LayerViewPane LAYER_PANE;
    /** 圖片欄位 */
    public ImageViewPane IMAGE_PANE;
    /** 程式資訊回饋欄 */
    public InfoPane INFO_PANE;
    /** 管理圖片的Class */
    public Main MAIN;
    /** 主程式 Class 控制大部分個功能 */
    public Progression MAIN_PROGRESSION;
    /** 訊息視窗 */
    public StatusMessageFloatingPane STATUS_PANE;
    /** 動作復原視窗 */
    public BehaviorHistoryPane HISTORY_PANE;
    /** 主要與 Python 做溝通，負責接收語音與傳送訊息 */
    public PythonSocketServer SOCKET_SEVER;
    /** 返回用的堆疊 */
    public Stack<BehaviorEvent> HISTORY_UNDO = new Stack<>();
    /** 重做用的堆疊 */
    public Stack<BehaviorEvent> HISTORY_REDO = new Stack<>();

    
    /** <p>指令集</p> 
     *  <p>繼承了 {@link HushMap}</p>
     *  <p>由 Resourse 裡的 Command.yml 抓取指令集</p>*/
     private CommandHashMap commandHashSet;


    /** <p>主要的 Stage</p>
     *  <p>通常只用在開啟第二個視窗時需要鎖住</p>*/
    public CustomStage PRIMARY_STAGE;
    /** 畫布最大能畫的大小 */
    public final static int MAX_DRAW_WIDTH = 2080, MAX_DRAW_HEIGHT = 1440;

    public final static int INITIAL_DRAW_WIDTH = 720, INITIAL_DRAW_HEIGHT = 480;

    @Override
    public void start(Stage primaryStage) throws IOException {

        System.out.println( ConsoleColors.SEPERA_LINE );
        System.out.println( "\n\n" + ConsoleColors.INFO + "Starting Initial Program...." );

        // 初始化 指令集
        commandHashSet = new CommandHashMap(this);

        System.out.println(ConsoleColors.SEPERA_LINE);
        System.out.println(ConsoleColors.SUCCESS + "Initial Command Set Successfully！");
        /* ----------STAGE & SCENE---------- */
        BorderPane borderPane = new BorderPane();
        MAIN = new Main( this );
        DETAIL_PANE = new DetailPane();
        INFO_PANE = new InfoPane( this );
        DRAW_AREA = new MainDrawPane( this );
        TEXT_PANE = new TextZonePane( this );
        LAYER_PANE = new LayerViewPane( this );
        IMAGE_PANE = new ImageViewPane( this );
        TOOL_BAR = new ToolBar( this);
        MENU_BAR = new TitleMenuBar( this );
        FILE_PROCCESSOR = new FileProccessor( this );
        STATUS_PANE = new StatusMessageFloatingPane();
        HISTORY_PANE = new BehaviorHistoryPane();
        /* ---------- Function Class  ---------- */
        //建立與 Python 的 Socket 連線
        SOCKET_SEVER = new PythonSocketServer();
        //==========================================================
        //左側欄位 Pane 排版 
        BorderPane LEFT_PANE = new BorderPane();
        LEFT_PANE.setCenter( TOOL_BAR );

        //右側欄位 Pane 排版 
        VBox vBox = new VBox(LAYER_PANE, IMAGE_PANE);
        IMAGE_PANE.setPrefWidth( 220 );

        //底下底圖設定
        VBox BUTTOM_PANE = new VBox();
        BUTTOM_PANE.getChildren().add( new HBox(INFO_PANE, HISTORY_PANE )  );
        BUTTOM_PANE.getChildren().add( TEXT_PANE );
        //HISTORY_PANE.setPrefWidth( 300 );
        //將 Info Pane 與 Text Pane 放在 DrawArea 底下
        DRAW_AREA.setBottom( BUTTOM_PANE );

        borderPane.setCenter( DRAW_AREA );
        borderPane.setTop( DETAIL_PANE );
        borderPane.setRight( vBox );
        borderPane.setLeft( LEFT_PANE );
        
        // Import CSS 檔案 (讚喔)
        borderPane.getStylesheets().add(getClass().getResource("/styles/borderTitle.css").toExternalForm());
        borderPane.getStylesheets().add(getClass().getResource("/styles/tabPane.css").toExternalForm());
        borderPane.getStylesheets().add(getClass().getResource("/styles/toolPane.css").toExternalForm());
        borderPane.getStylesheets().add(getClass().getResource("/styles/infoMenuItem.css").toExternalForm());
        borderPane.getStylesheets().add(getClass().getResource("/styles/menuItemStyle.css").toExternalForm());
        
        //狀態浮動顯示列
        StackPane.setAlignment( STATUS_PANE, null );
        //語音訊息狀態顯示列
        StackPane stackPane = new StackPane( new BorderPane(borderPane, MENU_BAR, null, null, null), STATUS_PANE, SOCKET_SEVER.getFloatingPane() );
        stackPane.heightProperty().addListener( (obser, oldVal, newVal) -> {
            STATUS_PANE.setTranslateY( newVal.intValue() - 140 );

            SOCKET_SEVER.getFloatingPane().setTranslateY( newVal.intValue() - 32 );
        } );



        
        //主要程式 Command 程式執行區
        MAIN_PROGRESSION = new Progression( this );
        
        //使用一個名為 Custom Stage 的函式庫 Web： https://github.com/Oshan96/CustomStage/wiki
        PRIMARY_STAGE = new CustomStageBuilder()
            .setTitleColor("#FFFFFF")                   //設定標顏色
            .setWindowColor("rgba(66,66,66,0.4)")              //Color of the window (used alpha range to make transparent)
            .setIcon("/textures/icon7.png")
            .setWindowTitle("Draw To Paint", HorizontalPos.RIGHT, HorizontalPos.CENTER)
            .build();

        PRIMARY_STAGE.changeScene( stackPane );
        PRIMARY_STAGE.show();
  

        MAIN.initializer();

        System.out.println( ConsoleColors.SUCCESS + "Main Stage Initialize Successfully" );
        System.out.println( ConsoleColors.SEPERA_LINE );        
        System.out.println( ConsoleColors.INFO + "Starting Initial Program Threads...." );

        TOOL_BAR.toolsInitia(DRAW_AREA.getUpponPane().getPrevGC());
        TOOL_BAR.changTool("select");

  
        //==========================================================================

        //啟動 Main Progression Thread 的 Service
        Service<Void> mainService = new Service<Void>() {
            @Override
            protected Task<Void> createTask(){
                return MAIN_PROGRESSION;
            }
        };
        mainService.start();

        //==========================================================================
        //啟動 Socket Server Thread 的 Service
        Service<Void> socketService = new Service<Void>() {
            @Override
            protected Task<Void> createTask(){
                return SOCKET_SEVER;
            }
        };
        socketService.start();

        //==========================================================================
        //監聽是否都完成了
        mainService.setOnRunning( e -> {
            System.out.println( "\n" );
            System.out.println( ConsoleColors.SEPERA_LINE );
            System.out.println( ConsoleColors.SUCCESS + "The Program is Loading Fully" );
        } );

        //==========================================================================
        //當關閉我們的應用程式時，要執行的事
        Stage.getWindows().get(0).addEventFilter( ActionEvent.ACTION, e -> {
            
            if( e.getTarget().toString().contains("id=btnClose") ){
                //關閉 Google Browser Driver
                MAIN.getNetConnection().closeDriver();

                try{
                    //Send 給 Python Socket
                    SOCKET_SEVER.sendEnd();
                }
                catch( Exception exception ){
                    System.out.println( ConsoleColors.WARNING + "Socket Server Dose Not Active" );
                }
                //延遲 1 秒等待 Voice Socket Client 關閉
               // try{ Thread.sleep(1000); }catch(Exception exception){}
            }
        }); 

        //==========================================================================
        //復原與重作的事件監聽
        PRIMARY_STAGE.addEventHandler( BehaviorEvent.ANY , e -> {

            //把歷史紀錄增加至 History Pane
            HISTORY_PANE.addActionText( e.getActionName() , e.getEventType().getName() );

            if( HISTORY_UNDO.size() > 20 ){
                HISTORY_UNDO.remove( 0 );
                INFO_PANE.println( "暫存紀錄已滿！" );
            }
            try{
                HISTORY_REDO.clear();
                HISTORY_UNDO.push( e );
                
            }
            catch( Exception exception ){ System.out.println( ConsoleColors.ERROR + "爆炸！" ); }
            
        });

    }

    public boolean undo(){
        //假如歷史紀錄是空的就不要執行
        if( HISTORY_UNDO.isEmpty() ){
            INFO_PANE.println( "到達返回上限！" );
            return false;
        }

        Platform.runLater(() -> {
            boolean b = false;
            while (!b) {
                b = false;
                try {
                    Thread.sleep(5);
                    if( HISTORY_UNDO.isEmpty() )break;

                    BehaviorEvent event = HISTORY_UNDO.pop();

                    //History Pane移除最新的歷史紀錄 
                    HISTORY_PANE.removeLastText();

                    System.out.println("undo");

                    // 判斷是哪一個要進行複製 假如是 Layer 圖層
                    if (event.getClone() instanceof LayerClone) {
                        HISTORY_REDO.push(new BehaviorEvent(event.getEventType(), event.getActionName(),
                                event.getActionNode(), (ViewBoxClone) new LayerClone((Layer) event.getActionNode())));
                        // 取得複製品
                        LayerClone clone = (LayerClone) event.getClone();

                        if( event.getEventType() == LayerEvent.CLEAR ){
                            // 更新 Layer
                            MAIN.getLayerList().getLayerByID(clone.getID()).restore(clone);
                            //Main 裡的 HashMap 也要做更新
                            MAIN.getImageListMap().replace( clone.getID(), clone.getImageList() );
                            MAIN.upadateUI();
                        }
                        else if( event.getEventType() == LayerEvent.DELETE){
                            // 將圖層加回串列
                            MAIN.getLayerList().add((Layer)event.getActionNode());
                            // 更新 Layer
                            MAIN.getLayerList().getLayerByID(clone.getID()).restore(clone);
                            //Main 裡的 HashMap 也要做更新
                            //MAIN.getImageListMap().replace( clone.getID(), clone.getImageList() );
                            MAIN.getImageListMap().put( clone.getID(), clone.getImageList() );
                            MAIN.upadateUI();
                        }
                        else{
                            // 更新 Layer
                            MAIN.getLayerList().getLayerByID(clone.getID()).restore(clone);
                        }
                    }
                    // ---------------------------------------------------------------
                    // 假如是 ImageObject 圖層物件
                    else if (event.getClone() instanceof ImageObjectClone) {
                        //目前觸發此事件的 ImageObject
                        ImageObject actionNode = (ImageObject)event.getActionNode();
                        // 取得推入堆疊的複製品
                        // ImageEvent image = (ImageEvent)event;
                        HISTORY_REDO.push(
                                new BehaviorEvent(event.getEventType(), event.getActionName(), actionNode,
                                        (ViewBoxClone) new ImageObjectClone( actionNode )));

                        // 取得複製品
                        ImageObjectClone clone = (ImageObjectClone) event.getClone();

                        // 假如是刪除動作，要把它新增回來
                        if (event.getEventType() == ImageEvent.DELETE) {
                            MAIN.getImageList(clone.getBelongLayerID()).add(clone.getIndex(), actionNode);
                            MAIN.upadateUI();
                        }
                        //假如是新增圖片，則把它給移除
                        else if( event.getEventType() == ImageEvent.ADD ){
                            ((Layer)event.getTarget()).getImageList().remove( event.getActionNode() );
                            MAIN.upadateUI();
                        }
                        //假如是圖片順序，則把它給移回去
                        else if( event.getEventType() == ImageEvent.VIEW_ORDER ){
                            ImageList list = actionNode.getBelongLayer().getImageList();
                            list.restoreOrder( actionNode , clone.getIndex() );
                            MAIN.upadateUI();
                        }
                        // 假如只是普通的修改就直接將 ImageObject 給更新
                        else {
                            MAIN.getUnModifyAllImageList().getElementByID(clone.getID()).restore(clone);
                        }

                    }
                    b = true;
                } catch (Exception e) {}
            }
        });
        return true;
    }

    public boolean redo(){
        //假如歷史紀錄是空的就不要執行
        if( HISTORY_REDO.isEmpty() ){
            INFO_PANE.println( "到達重做上限！" );
            return false;
        }
        
        Platform.runLater(() -> {
            boolean b = false;
            while (!b) {
                b = false;
                try {
                    if( HISTORY_REDO.isEmpty() )break;
                    Thread.sleep(5);
                    BehaviorEvent event = HISTORY_REDO.pop();
                    System.out.println("redo");

                    // 判斷是哪一個要進行複製 假如是 Layer 圖層
                    if (event.getClone() instanceof LayerClone) {
                        HISTORY_UNDO.push(new BehaviorEvent(event.getEventType(), event.getActionName(),
                                event.getActionNode(), (ViewBoxClone) new LayerClone((Layer) event.getActionNode())));
                        // 取得複製品
                        LayerClone clone = (LayerClone) event.getClone();

                        //判斷目前的動作，而作相對應是處裡
                        if( event.getEventType() == LayerEvent.CLEAR ){
                            // 更新 Layer
                            MAIN.getLayerList().getLayerByID(clone.getID()).restore(clone);
                            //Main 裡的 HashMap 也要做更新
                            MAIN.getImageListMap().replace( clone.getID(), clone.getImageList() );
                            MAIN.upadateUI();
                        }                        //判斷目前的動作，而作相對應是處裡
                        else if( event.getEventType() == LayerEvent.DELETE ){
                            // 將圖層移除串列
                            MAIN.getLayerList().remove((Layer)event.getActionNode());
                            MAIN.getImageListMap().remove(((Layer)event.getActionNode()).getID());
                            //((Layer)event.getActionNode()).setImageList(new ImageList());
                            // 更新 Layer
                            //MAIN.getLayerList().getLayerByID(clone.getID()).restore(clone);
                            //Main 裡的 HashMap 也要做更新
                            //MAIN.getImageListMap().replace( clone.getID(), clone.getImageList() );
                            MAIN.upadateUI();
                        }
                        else{
                            // 更新 Layer
                            MAIN.getLayerList().getLayerByID(clone.getID()).restore(clone);
                        }

                    }
                    // ---------------------------------------------------------------
                    // 假如是 ImageObject 圖層物件
                    else if (event.getClone() instanceof ImageObjectClone) {
                        //目前觸發此事件的 ImageObject
                        ImageObject actionNode = (ImageObject)event.getActionNode();
                        // 取得推入堆疊的複製品
                        // ImageEvent image = (ImageEvent)event;
                        HISTORY_UNDO.push(
                                new BehaviorEvent(event.getEventType(), event.getActionName(), actionNode,
                                        (ViewBoxClone) new ImageObjectClone(actionNode)));

                        // 取得複製品
                        ImageObjectClone clone = (ImageObjectClone) event.getClone();

                        //假如是刪除動作，要把它刪回來
                        if (event.getEventType() == ImageEvent.DELETE) {
                            MAIN.removeImage(actionNode);
                            MAIN.upadateUI();
                        }
                        //假如是新增圖片，則把它給新增回來
                        else if( event.getEventType() == ImageEvent.ADD ){
                            MAIN.getImageList(clone.getBelongLayerID()).add(clone.getIndex(), actionNode);
                            MAIN.upadateUI();
                        }
                        //假如是圖片順序，則把它給移回去
                        else if( event.getEventType() == ImageEvent.VIEW_ORDER ){
                            ImageList list = actionNode.getBelongLayer().getImageList();
                            list.restoreOrder( actionNode , clone.getIndex() );
                            MAIN.upadateUI();
                        }
                        // 假如只是普通的修改就直接將 ImageObject 給更新
                        else {
                            MAIN.getUnModifyAllImageList().getElementByID(clone.getID()).restore(clone);
                        }

                    }
                    b = true;
                } catch (Exception e) {}
            }
        });
        return true;

        
    }

    public static void main(String[] args) {
        try{
            launch(args);
        }catch(Exception e){
            System.out.println("大爆炸!!");
            e.printStackTrace();
        }
    }

    //================================================
    /** 只輸出語音聲音與文字框訊息 */
    final public static int PRINT_BOTH = 0;
    /** 只輸出文字框訊息 */
    final public static int PRINT_ONLY_MESSAGE = 1;
    /** 只輸出語音聲音 */
    final public static int PRINT_ONLY_VOICE = 2;
    /** 在 {@code InfoPane} 上印出資訊 (用 {@code Boolean} 來控制是否要語音輸出)
     * @param msg 訊息
     * @param type 類型 {@code PRINT_BOTH} | {@code PRINT_ONLY_MESSAGE} | {@code PRINT_ONLY_VOICE}*/
    public void println(String msg, int type){
        switch ( type ) {
            case PRINT_ONLY_MESSAGE:
                INFO_PANE.println( msg );
                break;
            case PRINT_ONLY_VOICE:
                SOCKET_SEVER.sendToClient( msg );
                break;
            case PRINT_BOTH:
                INFO_PANE.println( msg );
                SOCKET_SEVER.sendToClient( msg );
                break;
            default: break;
        }
    }

    /** 取得指令集 {@link CommandHashMap}
     *  @return 指令集 {@code [CommandHashMap]}*/
    public CommandHashMap getCommandSet(){
        return commandHashSet;
    }

}


