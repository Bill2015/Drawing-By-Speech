package talkdraw.dialog;

import java.io.File;
import java.util.Stack;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import talkdraw.App;
import talkdraw.componet.ChineseTextField;
import talkdraw.componet.MyLabel;
import talkdraw.componet.PagePane;
import talkdraw.componet.PageableNode;
import talkdraw.misc.ImageViewBuilder;

public class FilePane extends Stage{
    /** 顯示 URL 的 {@link Label} */
    private Label pathLabel;
    /** 顯示 File List 的 {@link PagePane} */
    private PagePane pagePane;
    /** 顯示圖片預覽的 {@link ImageView}   */
    private ImageView imageView;
    /** 提供使用者輸入的 TextField */
    private ChineseTextField textField;
    /** 確認按鈕 */
    private MyLabel button;
    private String root = "./";
    /** <p>儲存檔案暫存的 {@link Stack}</p>
     *  <p>可以用來回到上一步的動作</p>*/
    private Stack<File> fileStack = new Stack<>();
    /** 方便呼叫功能 */
    private App APP;
    
    
    /** 可用的功能名稱 */
    public static enum FunctionName{
        另存為PNG,儲存專案,開啟專案,測試
    };
    public FilePane(String title, Window parentStage,FunctionName fName,App APP ){
        this.APP=APP;
        //永遠在最上層
        setAlwaysOnTop(true);
        initModality(Modality.WINDOW_MODAL);
        
        

        //初始化pathLabel
        pathLabel=new Label();
        pathLabel.setMinHeight(30);
        


        //初始化 pagePane
        pagePane = new PagePane("檔案", 30, 10);
        pagePane.getStyleClass().add( "filePane" );
        pagePane.setMinWidth( 200 );
        //pagePane.setMaxWidth( 200 );

        //設定返回上一層的 Arrow Button
        ImageView preArrow = new ImageViewBuilder("/textures/file/arrowUp.png").reSize(25, 25).align( Pos.CENTER_RIGHT ).build();
        Tooltip tip=new Tooltip("回上頁");
        tip.setShowDelay(new Duration(0));
        Tooltip.install(preArrow, tip);
        ((StackPane)pagePane.getTop()).getChildren().add( preArrow );
        preArrow.setOnMouseClicked( e -> {
            //假如上一步的 Stack 不為空就 Pop 出去
            if( !fileStack.empty() )
                exploreDirectory( fileStack.pop() );
        } );

        //設定根目錄
        File folder = new File( root );
        exploreDirectory( folder );

        //初始化物件
        textField = new ChineseTextField();
        textField.setMinWidth(400);
        imageView = new ImageViewBuilder().reSize(150, 150).build();

        //設定確認按鈕
        int fontSize=20;
        int btWidth=fontSize * (title.length() + 1);
        button=new MyLabel(title,fontSize,Pos.CENTER, true);
        button.setFont(new Font("Yu Gothic UI Semibold",fontSize));
        button.setTextFill( Color.BLACK );
        button.getStylesheets().add(getClass().getResource("/styles/toolPane.css").toExternalForm());
        button.getStyleClass().add("infoButton");
        button.setStyle("-fx-font-size: "+fontSize+";");
        button.setMinWidth(btWidth);
        button.setPrefWidth(btWidth);
        //按鈕按下後的動作
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            switch(fName){
                case 測試:
                break;
                case 另存為PNG:
                    APP.FILE_PROCCESSOR.SaveAsPNG(pathLabel.getText(),textField.getText());
                    new LoadingStage("生成 PNG 中...", "成功！", parentStage, LoadingStage.DEFAULT_TIME ).show();
                break;
                case 儲存專案:        
                    APP.FILE_PROCCESSOR.saveAsFile(pathLabel.getText(),textField.getText());
                    new LoadingStage("儲存中..", "成功！", APP.PRIMARY_STAGE, LoadingStage.DEFAULT_TIME ).show();
                break;
                case 開啟專案:
                    APP.FILE_PROCCESSOR.loadFile(pathLabel.getText(),textField.getText());
                    new LoadingStage("開啟中..", "成功！", parentStage, LoadingStage.DEFAULT_TIME ).show();
                break;
            }
            close();
        });
        

        BorderPane borderPane=new BorderPane();
        borderPane.getStylesheets().add( getClass().getResource("/styles/filePane.css").toExternalForm() );
        //設定細項
        //setMinHeight( 300 );
        //setMinWidth( 300 );
        borderPane.setTop( pathLabel );
        borderPane.setLeft( pagePane );
        borderPane.setCenter( imageView );
        borderPane.setBottom( new HBox( textField,new Label("  "), button));
        borderPane.setBackground( new Background( new BackgroundFill( Color.web("#484848"), null, null) ) );
        borderPane.setPadding(new Insets(5));

        
        setTitle(title);
        setMinHeight(500);
        setMinWidth(400+btWidth+30);
        setHeight(500);
        setWidth(400+btWidth+30);
        setScene( new Scene( borderPane ) );
        
        //鎖住父視窗
        initOwner(parentStage);
    }


    /** 設定要向下尋找的 File */
    private void exploreDirectory(File inFile){
        pagePane.getContentChildren().removeIf( node -> node instanceof PageableNode );

        //假如沒有檔案就結束
        if( inFile.listFiles() == null )return;

        for( File file : inFile.listFiles() ){
            //取得每個檔案的型態
            String type = getFileType(file);
            //判斷
            if( !type.equals("file") ){
                FileCell cell = new FileCell( type, file.getName() );

                //假如他是個資料夾就做監聽
                if( file.isDirectory() )
                    createFolderFileCellListener(cell, inFile, file.getName() );
                else
                    createImageFileCellListener(cell, inFile, file.getName());
                
                pagePane.getContentChildren().add( cell );
            }
        }
        pagePane.updateUI();
        //pathLabel.setText(inFile.getAbsolutePath());
        try{pathLabel.setText(inFile.getCanonicalPath());}catch(Exception e){e.printStackTrace();}
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      監聽區(Listener)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 監聽當按下的檔案是 {@code 資料夾(Folder)} 時 
     *  @param cell 監聽目標{@link FileCell}
     *  @param inFile 目前檔案
     *  @param nextFileName 下一個檔案名稱*/
    public void createFolderFileCellListener(FileCell cell, File inFile, String nextFileName){
        //匿名函式宣告：監聽是否按下了檔案資料夾
        cell.setOnMouseClicked( e -> {
            if( e.getButton() == MouseButton.PRIMARY && e.getClickCount() >= 2 ){
                //將目前的 檔案塞入至 Stack
                fileStack.push( inFile );

                //建立下一個檔案路徑
                String nextPath =  String.join("", inFile.getAbsolutePath(), "\\", nextFileName);
                exploreDirectory( new File( nextPath ) );
            }
        } );
    }
    //===============================================================================
    /** 監聽當按下的檔案是 {@code 圖片(Image)} 時 
     *  @param cell 監聽目標{@link FileCell}
     *  @param inFile 目前檔案
     *  @param nextFileName 下一個檔案名稱*/
    public void createImageFileCellListener(FileCell cell, File inFile, String nextFileName){
        //匿名函式宣告：監聽是否按下了檔案圖片
        cell.setOnMouseClicked( e -> {
            if( e.getButton() == MouseButton.PRIMARY ){
                String nextPath = String.join("", "file:", inFile.getAbsolutePath(), "\\", nextFileName);
                imageView.setImage( new Image( nextPath ) );
                //將檔案名稱顯示在輸入列
                textField.setText(nextFileName.split("[.]")[0]);
            }
        } );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡      功能區(Function)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>取得檔案型態</p> 
     *  <p>目前有判斷的是：{@code Folder}、{@code JPG}、{@code PNG}、{@code GHZ}</p>
     *  @param file 欲做判斷的檔案
     *  @return 回傳檔案型態字串 {@code [String]}*/
    private String getFileType(File file){
        try{
            //判斷是檔案 or 資料夾
            if( file.isDirectory() )return "folder";
            else{
           
                //取得最後一個 '.'
                String extend = file.getName().substring( file.getName().lastIndexOf(".") + 1 );  //取出子字串

                //判斷檔案類型
                if( extend.equalsIgnoreCase("png")  )return "pngImage";
                else if( extend.equalsIgnoreCase("jpg") )return "jpgImage";
                else if( extend.equalsIgnoreCase("ghz") )return "ghzProject";
                else return "file";
            }
        }
        catch( IndexOutOfBoundsException e){ 
            return "file"; 
        }
    }


    public ChineseTextField getInputTextField(){
        return textField;
    }


}

class FileCell extends StackPane implements PageableNode{
    private MyLabel label;
    public FileCell(String type, String text){
        //初始化圖片 Icon
        ImageView icon = new ImageViewBuilder( String.join("", "/textures/file/", type, ".png") ).reSize( 18, 18).align( Pos.CENTER_LEFT ).build();

        //初始化文字 FileName
        label = new MyLabel( text, 16, Color.LIGHTGRAY );
        label.setTextAlignment( TextAlignment.LEFT );
        label.setMaxWidth( 150 );
        label.setGraphic( icon );
        label.setGraphicTextGap( 10 );
        StackPane.setAlignment( label , Pos.CENTER );

        //設定 Style
        getStyleClass().add( "file-cell" );
        getChildren().addAll( icon, label );
        setAlignment( this, Pos.TOP_LEFT );

        //設定寬度
        setMaxHeight( 22 );
        setMaxWidth( 150 );
        setMinWidth( 150 );
        setPrefWidth( 150 );
    }

    
}
