package talkdraw;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import talkdraw.connection.LocalConnection;
import talkdraw.dialog.AddImagePane;
import talkdraw.dialog.FilePane;
import talkdraw.dialog.InfoStage;
import talkdraw.dialog.LoadingStage;
import talkdraw.dialog.SavetoObjPane;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.Layer;
import talkdraw.imgobj.base.ViewBox;
import talkdraw.misc.ImageViewBuilder;

public class TitleMenuBar extends HBox{

    private App APP;
    //private String mode = "一般模式";
    
    //右側
    private HBox rightBar = new HBox();
    //顯示模式的Label
    private Label normal    = new Label("一般模式", new ImageViewBuilder("/textures/normal.png").reSize(24, 24).build());
    private Label face      = new Label("人臉模式", new ImageViewBuilder("/textures/face.png").reSize(24, 24).build());
    private Label mix       = new Label("混合模式", new ImageViewBuilder( "/textures/mix.png" ).reSize(24, 24).build());

    public TitleMenuBar(App app){
        this.APP = app;
        
        getStylesheets().add(getClass().getResource("/styles/menuBar.css").toExternalForm());

        Menu iconMenu = createIconMenu();
        Menu fileMenu = createFileMenu(); 
        Menu editMenu = createActionMenu();
        Menu windowMenu = createWindowMenu();
        Menu infoMenu = createInfoMenu();
        Label modeLabel = createModeLabel();
        

        //左側
        MenuBar leftBar = new MenuBar();
        leftBar.getMenus().addAll( iconMenu, fileMenu, editMenu, windowMenu, infoMenu  );

        rightBar.getChildren().addAll(modeLabel);

        /** 用於將menubar分成左右兩側 */
        Region spacer = new Region();
        spacer.getStyleClass().add("menu-bar");
        setHgrow(spacer, Priority.SOMETIMES);

        getChildren().addAll(leftBar, spacer, rightBar);



    }
    //==========================================================
    /** 建立 Icon Menu */
    private Menu createIconMenu(){
        Menu iconMenu = new Menu( "" );
        ImageView view = new ImageView( "/textures/icon7.png" );
        view.setSmooth(true);
        view.setFitHeight( 24 );
        view.setFitWidth( 24 );
        iconMenu.setDisable( true );
        iconMenu.setGraphic( view );
        return iconMenu;
    }
    //==========================================================
    /** 建立 State Menu */
    private Label createModeLabel(){
        Label modeLabel;
        
        normal.setPrefSize(120, 30);
        normal.setAlignment(Pos.CENTER);
        normal.setId("normalMode");
        normal.setOnMouseClicked(e->{
            setMode(LocalConnection.USED_MODE.人臉);
        });
        
        
        face.setPrefSize(120, 30);
        face.setAlignment(Pos.CENTER);
        face.setId("faceMode");
        face.setOnMouseClicked( e -> {
            setMode(LocalConnection.USED_MODE.混合);
        } );
        
        mix.setPrefSize(120, 30);
        mix.setAlignment(Pos.CENTER);
        mix.setId("proMode");
        mix.setOnMouseClicked( e -> {
            setMode(LocalConnection.USED_MODE.一般);
        } );
  
        modeLabel=normal;

        return modeLabel;
    }
    //==========================================================
    /** 建立 File Menu */
    private Menu createFileMenu(){
        Menu fileMenu = new Menu( "檔案" );
/* 
        //新增新的專案 
        MenuItem newFile = new MenuItem( "新增檔案" );
        //快捷鍵
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newFile.setOnAction( e -> {
            APP.FILE_PROCCESSOR.SaveAsPNG();
            new LoadingStage("生成 PNG 中...", "成功！", APP.PRIMARY_STAGE ).show();
        } ); */

        //儲存專案 
       /* MenuItem saveFile = new MenuItem( "儲存檔案" );
        saveFile.setOnAction( e -> {
            //APP.FILE_PROCCESSOR.saveAsFile();
            //new LoadingStage("生成 PNG 中...", "成功！", APP.PRIMARY_STAGE ).show();
        } );*/

        //儲存為新的專案 
        MenuItem saveAsNewFile = new MenuItem( "儲存專案檔" );
        //快捷鍵
        saveAsNewFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        saveAsNewFile.setOnAction( e -> {
            new FilePane("儲存專案檔", APP.PRIMARY_STAGE , FilePane.FunctionName.儲存專案 ,APP).show();
        } );

        //儲存為新的專案 
        MenuItem loadFile = new MenuItem( "開啟專案檔" );
        //快捷鍵
        loadFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        loadFile.setOnAction( e -> {
            new FilePane("開啟專案檔", APP.PRIMARY_STAGE , FilePane.FunctionName.開啟專案 ,APP).show();
            //new LoadingStage("生成 PNG 中...", "成功！", APP.PRIMARY_STAGE ).show();
        } );

        //輸出成 PNG 
        MenuItem saveAsPNG = new MenuItem( "另存為 PNG" );
        //快捷鍵
        saveAsPNG.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        saveAsPNG.setOnAction( e -> {
            new FilePane("另存成PNG檔", APP.PRIMARY_STAGE , FilePane.FunctionName.另存為PNG,APP).show();
        } );

        //輸出成 PNG 
        MenuItem saveAsObj = new MenuItem( "另存為物件" );
        //快捷鍵
        //saveAsObj.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        saveAsObj.setOnAction( e -> {
            //new FilePane("另存成PNG檔", APP.PRIMARY_STAGE , FilePane.FunctionName.另存為PNG,APP).show();
            new SavetoObjPane(APP.PRIMARY_STAGE, APP).show();
        } );

        //另存為 JPG
        /* MenuItem saveAsJPG = new MenuItem( "另存為 JPG" );
        saveAsJPG.setOnAction( e -> {
            APP.FILE_PROCCESSOR.SaveAsJPG();
            new LoadingStage("生成 JPG 中...", "成功！", APP.PRIMARY_STAGE ).show();
        } ); */

        MenuItem test = new MenuItem( "測試動畫" );
        test.setOnAction( e -> {
            new LoadingStage("生成檔案中...", "成功！", APP.PRIMARY_STAGE, LoadingStage.FAST_TIME ).show();
        } );
        
        MenuItem testDialog = new MenuItem( "測試 File Dialog" );
        testDialog.setOnAction( e -> {

            new FilePane("測試", APP.PRIMARY_STAGE , FilePane.FunctionName.測試,APP).show();
        } );

        MenuItem testImageSize = new MenuItem( "測試 圖片大小" );
        testImageSize.setOnAction( e -> {
            APP.MAIN.getNowSelectedImageObject().setImageSize(100, 50);
            APP.MAIN.upadateUI();
        } );

        fileMenu.getItems().addAll( /* newFile, */ saveAsNewFile, loadFile, saveAsPNG, saveAsObj, /* saveAsJPG,  */ new SeparatorMenuItem(),test, testDialog, testImageSize );
        return fileMenu;
    }

    //==========================================================
    /** 建立 Icon Menu */
    private Menu createActionMenu(){
        Menu actionMenu = new Menu( "動作" );

        //[復原] 按鈕
        MenuItem undo = new MenuItem( "復原" );
        //快捷鍵
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        undo.setOnAction( e -> {
            APP.undo();
        } );
 
        //[重作] 按鈕
        MenuItem redo = new MenuItem( "重做" );
        //快捷鍵
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN));
        redo.setOnAction( e -> {
            APP.redo();
        } );
        
        // 新增 [網路] 圖片
        MenuItem addNetImg = new MenuItem("新增網路圖片");
        addNetImg.setOnAction(e -> {
            new AddImagePane(APP.PRIMARY_STAGE, APP, Main.GET_MODE.WEB).show();
        });

        // 新增 [本地] 圖片
        MenuItem addLocalImg = new MenuItem("新增本地圖片");
        addLocalImg.setOnAction(e -> {
            new AddImagePane(APP.PRIMARY_STAGE, APP, Main.GET_MODE.LOCAL).show();
        });

        //語音輸入功能
        CheckMenuItem voiceIn = new CheckMenuItem( "語音輸入" );
        //快捷鍵
        voiceIn.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN));
        //預設為打勾
        voiceIn.setSelected(true);
        voiceIn.setOnAction( e -> {
            voiceIn.setSelected(voiceIn.isSelected());
            APP.SOCKET_SEVER.setinputOpen( voiceIn.isSelected() );
            APP.STATUS_PANE.setIconVisible( voiceIn.isSelected() );
        } );

        //語音回饋功能
        CheckMenuItem voiceOut = new CheckMenuItem( "語音回饋" );
        //快捷鍵
        voiceOut.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));
        //預設為打勾
        voiceOut.setSelected( false );
        voiceOut.setOnAction( e -> {
            voiceOut.setSelected( voiceOut.isSelected() );
            APP.SOCKET_SEVER.setoutputOpen( voiceOut.isSelected() );
        } );
/* 
        //人臉按鈕 
        CheckMenuItem face = new CheckMenuItem( "人臉模式" );
        //一般按鈕 
        CheckMenuItem normal = new CheckMenuItem( "一般模式" );

        //預設為打勾
        normal.setSelected( true );
        normal.setOnAction( e -> {
            APP.MAIN.getLocalConnection().setMode(LocalConnection.USED_MODE.一般);
                
            rightBar.getChildren().clear();
            rightBar.getChildren().add(this.normal);
            //mode.setText("一般模式");
            normal.setSelected( true );
            face.setSelected( false );
        } );

        //預設為打勾
        face.setSelected( false );
        face.setOnAction( e -> {
            APP.MAIN.getLocalConnection().setMode(LocalConnection.USED_MODE.人臉 );
            
            
            rightBar.getChildren().clear();
            rightBar.getChildren().add(this.face);
            //mode.setText("人臉模式");
            face.setSelected( true );
            normal.setSelected( false );
        } ); */

        actionMenu.getItems().addAll( undo , redo, new SeparatorMenuItem(), addNetImg, addLocalImg, new SeparatorMenuItem(), voiceIn, voiceOut/* , new SeparatorMenuItem(), normal, face */ );
        return actionMenu;
    }
    //==========================================================
    /** 建立 Window Menu */
    private Menu createWindowMenu(){
        Menu windowMenu = new Menu( "視窗" );

        //開啟 [工具列] 按鈕
        CheckMenuItem tool = new CheckMenuItem( "工具列" );
        tool.setOnAction( e -> {
            tool.setSelected(tool.isSelected());
            APP.TOOL_BAR.setVisible(tool.isSelected());
            APP.TOOL_BAR.setMaxWidth(tool.isSelected()? APP.TOOL_BAR.MAX_WIDTH :0);
            
        } );
        //開啟 [工具屬性] 按鈕
        CheckMenuItem toolDetail = new CheckMenuItem( "工具屬性" );
        toolDetail.setOnAction( e -> {
            toolDetail.setSelected(toolDetail.isSelected());
            APP.DETAIL_PANE.setVisible(toolDetail.isSelected());
            APP.DETAIL_PANE.setMaxHeight(toolDetail.isSelected()? APP.DETAIL_PANE.MAX_HEIGHT :0);
            
        } );
        //開啟 [圖層列表] 按鈕
        CheckMenuItem layer = new CheckMenuItem( "圖層列表" );
        layer.setOnAction( e -> {
            layer.setSelected(layer.isSelected());
            APP.LAYER_PANE.setVisible(layer.isSelected());
            APP.LAYER_PANE.setMaxHeight(layer.isSelected()? APP.LAYER_PANE.MAX_HEIGHT :0);
            //APP.LAYER_PANE.setMinHeight(layer.isSelected()? APP.LAYER_PANE.MAX_HEIGHT :0);
            APP.LAYER_PANE.setMaxWidth(layer.isSelected()? APP.LAYER_PANE.MAX_WIDTH :0);
            //APP.LAYER_PANE.setMinWidth(layer.isSelected()? APP.LAYER_PANE.MAX_WIDTH :0);
        } );
        // 開啟 [物件列表] 按鈕
        CheckMenuItem object = new CheckMenuItem( "物件列表" );
        object.setOnAction( e -> {
            object.setSelected(object.isSelected());
            APP.IMAGE_PANE.setVisible(object.isSelected());
            APP.IMAGE_PANE.setMaxHeight(object.isSelected()? APP.IMAGE_PANE.MAX_HEIGHT :0);
            APP.IMAGE_PANE.setMaxWidth(object.isSelected()? APP.IMAGE_PANE.MAX_WIDTH :0);
        } );
        //開啟 [資訊回饋]按鈕
        CheckMenuItem info = new CheckMenuItem( "資訊回饋" );
        info.setOnAction( e -> {
            info.setSelected(info.isSelected());
            APP.INFO_PANE.setVisible(info.isSelected());
            APP.INFO_PANE.setMaxHeight(info.isSelected()? APP.INFO_PANE.MAX_HEIGHT :0);
            APP.INFO_PANE.setMaxWidth(info.isSelected()? APP.INFO_PANE.MAX_WIDTH :0);
            
            //APP.TEXT_PANE.setMaxHeight(com.isSelected()? APP.TEXT_PANE.MAX_HEIGHT+APP.INFO_PANE.MAX_HEIGHT :0);
        } );
        //開啟 [指令輸入列] 按鈕
        CheckMenuItem com = new CheckMenuItem( "指令輸入列" );
        com.setOnAction( e -> {
            com.setSelected(com.isSelected());
            APP.TEXT_PANE.setShow(com.isSelected());
            APP.TEXT_PANE.setMaxHeight(com.isSelected()? APP.TEXT_PANE.MAX_HEIGHT :0);
            APP.TEXT_PANE.setMaxWidth(com.isSelected()? APP.TEXT_PANE.MAX_WIDTH :0);

            
            //APP.INFO_PANE.setMaxHeight(info.isSelected()? APP.INFO_PANE.MAX_HEIGHT+APP.TEXT_PANE.MAX_HEIGHT :0);
        } );
        //開啟 [尺規] 按鈕
        CheckMenuItem ruler = new CheckMenuItem( "尺規" );
        ruler.setOnAction( e -> {
            ruler.setSelected(ruler.isSelected());
            APP.DRAW_AREA.getRulerPane()[0].setVisible(ruler.isSelected());
            APP.DRAW_AREA.getRulerPane()[1].setVisible(ruler.isSelected());
            
            APP.DRAW_AREA.getRulerPane()[0].setMaxHeight(ruler.isSelected()? 20:0);
            APP.DRAW_AREA.getRulerPane()[0].setMinHeight(ruler.isSelected()? 20:0);
            APP.DRAW_AREA.getRulerPane()[1].setMaxWidth(ruler.isSelected()? 20:0);
            APP.DRAW_AREA.getRulerPane()[1].setMinWidth(ruler.isSelected()? 20:0);
        } );
        //開啟 [歷史紀錄] 按鈕
        CheckMenuItem history = new CheckMenuItem( "歷史紀錄" );
        history.setOnAction( e -> {
            history.setSelected(history.isSelected());
            APP.HISTORY_PANE.setVisible(history.isSelected());
            APP.HISTORY_PANE.setMaxHeight(history.isSelected()? APP.HISTORY_PANE.MAX_HEIGHT :0);
            APP.HISTORY_PANE.setMaxWidth(history.isSelected()? APP.HISTORY_PANE.MAX_WIDTH :0);
        } );

        windowMenu.getItems().addAll(tool, toolDetail, ruler, layer, object, new SeparatorMenuItem(), info, history, com);

        windowMenu.getItems().forEach(item->{
            if(!(item instanceof SeparatorMenuItem)){
                //預設為打勾
                ((CheckMenuItem)item).setSelected(true);
            }
        });
        return windowMenu;
    }
    //==========================================================
    /** 建立 Icon Menu */
    private Menu createInfoMenu(){
        Menu infoMenu = new Menu( "關於" );

        //作者按鈕
        MenuItem author = new MenuItem( "作者資訊" );
        //快捷鍵
        author.setAccelerator(new KeyCodeCombination(KeyCode.F11));
        author.setOnAction( e -> {
            new InfoStage("作者資訊", "\n團隊名稱：GHZ\n團隊成員：學霸1號、學霸2號、yeee\n指導單位：某國立科大\n\n", APP.PRIMARY_STAGE).show();
        } );
        //復原按鈕
        MenuItem pro = new MenuItem( "程式資訊" );
        //快捷鍵
        pro.setAccelerator(new KeyCodeCombination(KeyCode.F12));
        pro.setOnAction( e -> {
            new InfoStage("程式資訊", "\n好喔\n反正就是可以畫畫的程式\n語音很好用喔\n\n", APP.PRIMARY_STAGE).show();
        } );

        infoMenu.getItems().addAll( author ,pro);
        return infoMenu;
    }

    
    //==========================================================
    /** 設定顯示的mode 
     *  @return 是否更新成功
    */
    public boolean setMode(LocalConnection.USED_MODE mode){
        try{
            Platform.runLater(()->{
                //更新本地圖庫位置
                APP.MAIN.getLocalConnection().setMode(mode);
                
                //清空右上角顯示
                rightBar.getChildren().clear();
                switch(mode){
                    case 一般:
                        //更新右上角顯示
                        rightBar.getChildren().add(normal);
                    break;
                    case 人臉:
                        //更新右上角顯示
                        rightBar.getChildren().add(face);
                        changeToFaceMode();
                    break;
                    case 混合:
                        //更新右上角顯示
                        rightBar.getChildren().add(mix);
                    break;
                }
            });
        }catch(Exception e){
            return false;
        }
        return true;
    }

    /** 更變為人臉模式時做的動作，預設一張隨機人臉 */
    private void changeToFaceMode(){
        Main main = APP.MAIN;
        //新增人臉圖層，並設其為動作圖層
        Layer layer = main.newLayer("Face" + main.getLayerList().size() ); 

        //計數用
        int i = 0;
        //相對座標用
        double centerX = 0, centerY = 0, gapH=0, gapW=0, startX=0, startY=0;
        //名稱
        String[] imgName = new String[]{"頭","左眉","右眉","左眼","右眼","嘴巴","鼻子","頭髮"};
        //絕對位置
        //int[][] location = new int[][]{{0,0},{100,30},{300,150},{220,150},{300,190},{220,190},{260,200},{240,270}};
        //中心相對位置
        double[][] centerlocation = new double[][]{{0,0},{1.1,-2},{-1.1,-2},{1.1,-1},{-1.1,-1},{0,2},{0,0},{0,-3}};
        //左上相對位置
        //int[][] location = new int[][]{{0,0},{0,0},{1,1},{220,150},{300,190},{220,190},{260,200},{240,270}};
        //寬高細節修正
        double[][] detail = new double[][]{{0,0},{2,0},{2,0},{1.5,0},{1.5,0},{2.5,0},{1.5,0},{5,0}};

            
        for(String keyword : imgName){
            //尋找圖片
            if( main.getLocalConnection().searchImages( keyword ) <= 0 ){
                System.out.println(keyword);
            }
            //main.getLocalConnection().getLocalImageList().size()
            //取得圖片
            ImageObject img;
            if((img = main.getLocalConnection().createImageObject()) == null ){
                System.out.println("ID 為 NULL");
                continue;
            }
            //設定圖片座標、大小
            if (keyword.equals("頭")) {
                //取得中心點
                centerX = img.getImageWidth() / 2;
                centerY = img.getImageHeight() / 3;
                //取得左上角
                startX = centerX- centerY/2;
                startY = centerY- centerY/2;
                //
                gapH=centerY/6;
                gapW=centerY/5;
                

                    
                //img.setLocation(location[i][0], location[i][1]);
                //img.setImageSize(-1, -1);
                //絕對位置
                img.setLocation(centerlocation[i][0], centerlocation[i][1]);
            }else if(keyword.equals("頭髮")){
                
                //相對位置
                img.setLocation(centerX+ gapW*centerlocation[i][0]-img.getImageWidth()/2, /* centerY+ gapH* centerlocation[i][1]-img.getImageHeight() */gapH/2);
                //img.setImageSize(-1, -1);
            }else{
                //大小修正
                img.setImageSize( gapW* detail[i][0], gapH* detail[i][1]);
                //相對位置
                img.setLocation(centerX+ gapW*centerlocation[i][0]-img.getImageWidth()/2, centerY+ gapH* centerlocation[i][1]-img.getImageHeight()/2);
                
            }

            //將圖片加入圖層
            img.setBelongLayer( layer ); 
            layer.getImageList().add( img );
            /* if (!main.addImageToLayer(Main.GET_MODE.LOCAL, null, location[i][0], location[i][1], ViewBox.FILP_NONE, 100, 0, -1, -1)) {
                System.out.println("ID 為 NULL");
            } */

            //計數增加
            i++;
        }
        //更新顯示
        main.upadateUI();
    }
}

