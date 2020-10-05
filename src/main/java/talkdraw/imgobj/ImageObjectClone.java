package talkdraw.imgobj;

import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class ImageObjectClone extends ViewBoxClone{
    /** 圖片 */
    private volatile Image image;
    /** 圖片的標籤 */
    private volatile ArrayList<String> tags;   
    /** 規模(用來放大用) */
    private volatile double scale = 1.0; 
    /**<p>圖片旋轉角度</p> 
     * <p>其值範圍 {@code [0 ~ 360]}</p>*/
    private volatile int rotation = 0;
    /** 所屬圖層 */
    private volatile int belongLayerID;
    /** 座標 */
    private volatile double posX = 0, posY = 0;

    /** 圖片的長與高 */
    private volatile double width = 0, height = 0;

    /** 此物件在 {@code ImageList} 裡的位置 */
    private volatile int index;
    
    public ImageObjectClone( ImageObject imgObj ){
        super( imgObj );

        this.image          = copyImage( imgObj.getImage() );       //複製圖片
        this.tags           = new ArrayList<>( imgObj.getTags() );  //複製串列
        this.rotation       = imgObj.getRotation();                 //儲存旋轉
        this.posX           = imgObj.getX();                        //儲存 X 座標
        this.posY           = imgObj.getY();                        //儲存 Y 座標
        this.width          = imgObj.getImageWidth();               //儲存 寬 度
        this.height         = imgObj.getImageHeight();              //儲存 長 度

        if( !imgObj.getBelongLayer().equals( Layer.NONE ) ){
            this.belongLayerID  = imgObj.getBelongLayer().getID();      //儲存所屬圖層ID
            this.index          = imgObj.getBelongLayer().getImageList().indexOf( imgObj ); //取得此 ImageObject 在 Layer 裡的位置
        }
        else{
            this.belongLayerID  = 0;      //設定所屬圖層ID預設值
            this.index          = 0;      //設定串列位置預設值
        }
      
    }

    /** 取得目前旋轉的角度 @return 角度 {@code [Int]} */
    public int getRotation(){ return rotation; }
    /** 取得 X 座標  @return X 座標 {@code [Double]}*/
    public double getX(){ return posX; }
    /** 取得 Y 座標  @return Y 座標 {@code [Double]}*/
    public double getY(){ return posY; }
    /** 取得 寬度(Width) 座標  @return 寬度(Width) {@code [Double]}*/
    public double getWidth(){ return width; }
    /** 取得 長度(Height) 座標  @return 長度(Height) {@code [Double]}*/
    public double getHeight(){ return height; }
    /** 取得此圖片的 Image @return 圖片 Image {@code [Image]}*/
    public Image getImage(){ return image; }
    /** 取得此圖片的所有標籤 @return 取得標籤串列 {@code [ArrayList<String>]} */
    public ArrayList<String> getTags(){ return tags; }
    /** 回傳這個物件是屬於哪一個圖層ID {@link Layer} @return 圖層ID {@code [Int]} */
    public int getBelongLayerID(){ return belongLayerID; }
    /** 取得此物件在 {@code ImageList} 裡的位置 @return 位置 {@code [Int]} */
    public int getIndex(){ return index; }

    /** 複製圖片 {@code 複製} 
     *  @param img 欲複製的圖片
     *  @return 複製完的圖片 {@code [Image]}*/
    public static Image copyImage( Image img ){
        int w = (int)img.getWidth(), h = (int)img.getHeight();
        WritableImage wImage = new WritableImage( w , h );
        PixelReader reader = img.getPixelReader();
        PixelWriter writer = wImage.getPixelWriter();

        for(int i = 0; i < w; i++ ){
            for(int j = 0; j < h; j++){
                writer.setColor( i , j,  reader.getColor(i, j) );        
            }
        }
        return (Image)wImage;
    }
    
}