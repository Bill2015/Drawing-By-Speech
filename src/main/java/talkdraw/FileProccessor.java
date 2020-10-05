package talkdraw;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import talkdraw.imgobj.ImageList;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.Layer;
import talkdraw.imgobj.LayerList;
import talkdraw.io.FXFileInputStream;
import talkdraw.io.FXFileOutputStream;
import talkdraw.misc.ConsoleColors;

import java.awt.image.BufferedImage;

public class FileProccessor{

    private String FILE_NAME = "unnamed";
    private String FILE_PATH = "./";
    private App APP;
    private Main MAIN;
    public FileProccessor(App app){
        this.APP = app;
        this.MAIN = app.MAIN;
    }
    public void saveAsFile(){
        Platform.runLater(() -> {
            try {
                Thread.sleep(5);
                //檔案路徑
                String direct = String.join("",FILE_PATH, "/", FILE_NAME, ".ghz");
                //將資料寫入到檔案裡(用來把資料寫到檔案裡的)
                FXFileOutputStream FXfos = new FXFileOutputStream( new File( direct ) );
                
                //先將畫布大小寫入  寬 -> 高
                MainDrawPane mainDraw = APP.DRAW_AREA;
                int cavansWidth = mainDraw.getDrawWidth(), cavansHeight = mainDraw.getDrawHeight();
                FXfos.writeInt( cavansWidth );
                FXfos.writeInt( cavansHeight );

                //將每一個 圖層(Layer) 取出並寫入，其寫入順序為     ID -> 名稱 -> 透明度 -> 畫布
                FXfos.writeInt( MAIN.getLayerList().size() );   //寫入有幾個圖層
                for( Layer layer : MAIN.getLayerList() ){
                    FXfos.writeLayer( layer );          //將圖層寫入
                }

                //將每一個 圖片物件(ImageObject) 取出並寫入
                FXfos.writeInt( MAIN.getUnModifyAllImageList().size() );    //寫入有幾個 ImageObejct
                for( ImageObject imgObj : MAIN.getUnModifyAllImageList() ){
                FXfos.writeImageObj( imgObj );       //將圖片物件寫入
                }
                FXfos.flush();
                FXfos.close();

                System.out.println("檔案寫入完成！");
            
            }
            catch( InterruptedException e ){ System.out.println("檔案寫入失敗！"); e.printStackTrace(); }
            catch( IOException e ){ System.out.println("檔案寫入失敗！"); e.printStackTrace();  }
        });
    }
    /**
     *  將存為專案 
     * @param filename 指定的檔案名稱
     * @param filepath 指定的路徑
     */
    public void saveAsFile(String filepath, String filename){
        //判斷是否有輸入檔名
        if(filename.length()>0){
            FILE_NAME = filename;
            FILE_PATH = filepath;
            saveAsFile();
        }else{
            System.out.println("請輸入檔名");
        }
    }
    //==============================================================================================================
    public void loadFile(){
        try{
            //檔案路徑
            String direct = String.join("",FILE_PATH, "/", FILE_NAME, ".ghz");
            //開啟檔案
            FXFileInputStream FXfis = new FXFileInputStream( new File( direct ) );

            LayerList layerList = new LayerList();                      //建立 Layer List 來存放
            HashMap<Integer, ImageList> imageListMap = new HashMap<>(); //儲存 ImageObject 的 List Map

            //畫布的寬高
            int cavansWidth  = FXfis.readInt();     //讀入畫布寬度
            int canvasHeight = FXfis.readInt();     //讀入畫布高度

            APP.DRAW_AREA.reSizeDrawArea( cavansWidth, canvasHeight );
            //=====================================================================
            System.out.println("\n\n圖層");
            //開始讀入圖層(Layer)
            int layerSize = FXfis.readInt();        //讀入圖層數量
            for(int i = 0; i < layerSize; i++){
                Layer layer = FXfis.readLayer();
                layer.setBelongLayerList( layerList );
                //加入至 LayerList
                layerList.add( layer );

                //先將 ImageMapList 給先建置出來
                imageListMap.put( layer.getID(), layer.getImageList() );
                layer.setImageList( imageListMap.get( layer.getID() ) );
            }
            //=====================================================================
            System.out.println("\n\n圖片物件");
            //開始讀入圖片物件(ImageObject)
            int objSize = FXfis.readInt();          //讀入圖片物件數量
            for(int i = 0; i < objSize; i++){
                ImageObject imgObj = FXfis.readImageObj()
                                            .setBelongLayer( layerList )
                                            .build();

                //加入至 Map List
                int key = imgObj.getBelongLayer().getID();
                imageListMap.get( key ).add( imgObj );
            }
            //=====================================================================
            System.err.println( "檔案已載入完成！" );
            FXfis.close();

            //最最最最最重要的部分 Loading 進入整個程式
            APP.MAIN.LoadingProject(layerList, imageListMap);
        }
        catch( IOException e){
            System.out.println("檔案讀入失敗！"); e.printStackTrace();
        }
    }
    /**
     *  將開啟專案 
     * @param filename 指定的檔案名稱
     * @param filepath 指定的路徑
     */
    public void loadFile(String filepath, String filename){
        //判斷是否有輸入檔名
        if(filename.length()>0){
            FILE_NAME = filename;
            FILE_PATH = filepath;
            loadFile();
        }else{
            System.out.println("請輸入檔名");
        }
    }
    //=======================================================================================================
    /** 將整個介面 圖層{@link Layer} 以及 圖片物件{@link ImageObject} 存成 PNG  */
    public void SaveAsPNG(){
        Platform.runLater(() -> {
            try {
                Thread.sleep(5);
                //檔案路徑
                String direct = String.join("",FILE_PATH, "/", FILE_NAME, ".png");
                File outputFile = new File( direct );

                //合成所有圖層以及圖片物件
                Image combinImage = combinationAllImage();
                BufferedImage bImage = SwingFXUtils.fromFXImage( combinImage, null);
        
                //輸出成檔案
                ImageIO.write(bImage, "png", outputFile);
            } 
            catch (IOException | InterruptedException e) { 
                System.out.println( ConsoleColors.ERROR + "匯出成 PNG 時發生問題！");
                e.printStackTrace();
            }
            catch (NoLayerException e){ System.out.println( e.getMessage() ); }
        });
    }
    /**
     *  將整個介面 圖層{@link Layer} 以及 圖片物件{@link ImageObject} 存成 PNG  
     * @param filename 指定的檔案名稱
     * @param filepath 指定的路徑
     */
    public void SaveAsPNG(String filepath, String filename){
        //判斷是否有輸入檔名
        if(filename.length()>0){
            FILE_NAME = filename;
            FILE_PATH = filepath;
            SaveAsPNG();
        }else{
            System.out.println("請輸入檔名");
        }
    }
    //=======================================================================================================
    /** 將整個介面 圖層{@link Layer} 以及 圖片物件{@link ImageObject} 存成 JPG  */
    public void SaveAsJPG(){
        try {
            //檔案路徑
            String direct = String.join("", FILE_NAME, ".jpeg");
            File outputFile = new File( direct );

            System.out.println( outputFile.getCanonicalFile() );

            //合成所有圖層以及圖片物件
            Image combinImage = combinationAllImage();
            BufferedImage bImage = SwingFXUtils.fromFXImage( combinImage, null);
    
            //輸出成檔案
            ImageIO.write(bImage, "jpeg", outputFile);
        } 
        catch (IOException e) { 
            System.out.println("匯出成 JPG 時發生問題！");
            e.printStackTrace();
        }
        catch (NoLayerException e){ System.out.println( e.getMessage() ); }
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       私有功能區(Private Function)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>整合整個專案所有圖片(ImageObject)與畫布(Layer)<p> 
     *  @return 回傳整合完的圖片 {@code [Image]} */
    private Image combinationAllImage() throws NoLayerException{
        //取得物件串列
        HashMap<Integer, ImageList> imageMap = MAIN.getImageListMap();
        LayerList layerList = MAIN.getLayerList();

        //圖層不可為空
        if( layerList.size() <= 0 )throw new NoLayerException("圖層不可為零！");

        MainDrawPane mainDraw = APP.DRAW_AREA;

        //建立主要用來整合的畫布
        Canvas combinCanvas = new Canvas( mainDraw.getDrawWidth(), mainDraw.getDrawHeight() );
        GraphicsContext gc = combinCanvas.getGraphicsContext2D();

        //取得圖層串列
        for (Layer layer : layerList) {
            //設定 圖層的透明度
            gc.setGlobalAlpha( layer.getAlpha() );
            gc.drawImage( cavansToImage( layer.getCanvas() ), 0, 0);
            //判斷此 Layer 是否有 ImageObject
            if( imageMap.get( layer.getID() ) != null ){
                //設定繪製 ImageObject 的透明度
                gc.setGlobalAlpha( 1.0 );
                //每個 ImageObject 繪製
                for( ImageObject imgObj : imageMap.get( layer.getID() ) ){
                    gc.drawImage( imgObj.getFinalImage() , imgObj.getX(), imgObj.getY() );
                }
            }
        }
        return cavansToImage( combinCanvas );
    }

    /** 將 {@link Cavans} 轉成 {@link Image} 
     *  @param canvas 欲轉換的畫布
     *  @return 回傳轉換完的圖片 {@code [Image]} */
    private Image cavansToImage(Canvas canvas){
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill( Color.TRANSPARENT );
        return canvas.snapshot( parameters , null );
    }
}

/** 當沒有圖層 {@link Layer} 時而產生的例外 */
class NoLayerException extends Exception{
    private static final long serialVersionUID = 1L;
    public NoLayerException(String msg) {
        super(msg);
    }
}