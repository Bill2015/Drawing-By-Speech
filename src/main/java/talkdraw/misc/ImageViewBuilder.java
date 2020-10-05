package talkdraw.misc;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**<p>建構 {@link ImageView} 的 Class</p>
 * <p>就只是為了好用以及簡潔而已，不強迫使用</p>*/
public class ImageViewBuilder {
    /** {@link ImageView} */
    private ImageView view;
    /** 初始建構子 */
    public ImageViewBuilder(){
        view = new ImageView();
    }
    /** 建構子 
     *  @param img 欲設定的圖片*/
    public ImageViewBuilder(Image img){
        view = new ImageView( img );
    }
    /** 建構子 
     *  @param path 圖片檔案路徑 */
    public ImageViewBuilder(String path){
        view = new ImageView( path );
    }
    /** 設定圖片 {@link ImageView} 的大小 
     *  @param width 寬
     *  @param height 高 */
    public ImageViewBuilder reSize(double width, double height){
        view.setFitHeight( height );
        view.setFitWidth( width );
        return this;
    }
    /** 設定對其位置 
     *  @param pos 欲對齊的位置 */
    public ImageViewBuilder align(Pos pos){
        StackPane.setAlignment(view, pos);
        return this;
    }
    /** 建構完成 
     *  @return 回傳 {@link ImageView} */
    public ImageView build(){ return view; }
}