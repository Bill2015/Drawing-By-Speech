package talkdraw.imgobj;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.image.Image;
import talkdraw.imgobj.base.ViewBox;

/** 本地圖片專用的 Class 用來存放 URL */
public class FileImageObject extends ViewBox{
    /** 圖片位置 */
    private final String imageURL;
    /** 檔案名稱 */
    private final String imageName;
    /** 標籤 */
    private final ArrayList<String> tags;
    /** 搜尋時的關鍵字 */
    private final String keyword;
    /** 名稱 */
    /** 建構子 
     *  @param ID 編號
     *  @param IDnum 圖片編號
     *  @param imageURL 圖片位置
     *  @param imageName 圖片名稱
     *  @param tags 標籤矩陣  */
    public FileImageObject(int ID, String name, String keyword, String imageURL, String imageName, ArrayList<String> tags){
        super(ID, name);
        this.imageURL = imageURL;
        this.imageName = imageName;
        this.keyword = keyword;
        this.tags = tags;        
    
        setImage( new Image( imageURL )  );
        System.out.println(imageURL + " " + imageName );
    }
    //印出所有資訊
    public void printAll(){
        System.out.println( imageURL + "  " + imageName );
    }

    @Override protected void updateThis() {}
    
    @Override public void showInfoList( Node viewNode ) {}

    @Override public void setAlpha(int opacity) {}

    @Override public void filp(short type) {}

    @Override public void rename(String name) {}

    @Override public void setImage(Image img) {
        imageView.setImage( img );

        double w = img.getWidth(), h = img.getHeight();        
        double scale = ((double)IMAGE_VIEW_BOX_SIZE / ((w > h) ? w : h));
        imageView.setFitWidth( (int)(w * scale) );
        imageView.setFitHeight( (int)(h * scale) );
    }
    /** 取得標籤 */
    public ArrayList<String> getTags(){
        return tags;
    }
    /** 取得搜尋時的關鍵字 */
    public String getKeyword(){
        return keyword;
    }
}