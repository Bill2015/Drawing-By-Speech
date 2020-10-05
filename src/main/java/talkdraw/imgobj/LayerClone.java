package talkdraw.imgobj;

import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class LayerClone extends ViewBoxClone{
    /** 儲存圖片用的，很重要 */
    protected volatile Image image;
    /** 儲存底下的圖片物件串列 */
    protected volatile ImageList imageList;

    public LayerClone( Layer layer ){
        super( layer );
        this.image      = copyImage( layer.getCanvas() );
        this.imageList  = layer.getImageList();
    }

    /** 取得圖片，基本上是畫布轉出來的圖片 
     *  @return 圖片 {@code [Image]}*/
    public Image getImage(){
        return image;
    }
    /** 取得這個圖層原本所屬的 {@link ImageList} @return {@code [ImageList]} */
    public ImageList getImageList(){ 
        return imageList; 
    }

    /** 畫布轉圖片 {@code 複製} 
     *  @param canvas 欲轉換得畫布
     *  @return 轉換完的圖片 {@code [Image]}*/
    public static Image copyImage( Canvas canvas ) {
        //生成快照圖
        SnapshotParameters param = new SnapshotParameters();
        param.setFill( Color.TRANSPARENT );
        Image image = canvas.snapshot( param , null );

        //取得高度
        int height = (int)canvas.getHeight();
        int width  = (int)canvas.getWidth();
        PixelReader pixelReader = image.getPixelReader();
        WritableImage writableImage = new WritableImage( width, height );
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        //複製並劃出
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                pixelWriter.setColor(x, y, pixelReader.getColor(x, y) );
            }
        }
        return (Image)writableImage;
    }


    /** 重新繪製畫布 
     *  @param cavnas 欲重新繪製的畫布
     *  @param img 欲畫上的圖片 */
    public static void reDrawCanvans( Canvas canvas, Image img ){
        PixelWriter gc = canvas.getGraphicsContext2D().getPixelWriter();
        int height = (int)canvas.getHeight();
        int width  = (int)canvas.getWidth();
        PixelReader pixelReader = img.getPixelReader();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                gc.setColor(x, y, pixelReader.getColor(x, y) );
            }
        }
    }

}
