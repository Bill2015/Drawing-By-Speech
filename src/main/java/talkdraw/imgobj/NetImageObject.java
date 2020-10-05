package talkdraw.imgobj;

import javafx.scene.Node;
import javafx.scene.image.Image;
import talkdraw.imgobj.base.ViewBox;

/** 網路圖片專用的 Class 用來存放 URL */
public class NetImageObject extends ViewBox{
    private final String imageURL;
    private final String infoURL;
    private final String keyword;
    /** 建構子 
     *  @param ID 編號
     *  @param IDnum 圖片編號
     *  @param imageURL 圖片網址位置
     *  @param infoURL 圖片資訊位置
     *  @param keyWord 搜尋圖片的關鍵字  */
    public NetImageObject(int ID, String name, String imageURL, String infoURL, String keyword){
        super(ID, name);
        this.imageURL = imageURL.replace("2x", "3x");
        this.infoURL = infoURL;
        this.keyword = keyword;
        System.out.println( this.imageURL );
        
        setImage( new Image( this.imageURL )  );
    }
    //取得圖片詳細資料
    public String getImageInfoURL(){ return infoURL; }
    //印出所有資訊
    public void printAll(){
        System.out.println( imageURL + "  " + infoURL );
    }
    //設定解析度
    /*public String encodingURL( String imgURL, SIZE size ){
        String temp = "";
        for( String s : imgURL.split("2x") ){
            temp = temp.concat(s + (temp.isBlank() ? (size.getSize()) : "") );
        }
        return temp;
    }
    enum SIZE{
        X1("1x"),X2("2x"),X3("3x"),X4("4x");
        String size;
        private SIZE(String size){ this.size = size; }
        //取得字串
        public String getSize(){ return size; }
    }*/
    /** 圖片大小 */
    final static String SMALL   = "2x";
    final static String NORMAL  = "4x";
    final static String LARGE   = "8x";
    final static String LARGER  = "16x";
    final static String HUGE    = "32x";

    
    /** 取得圖片網址 
     *  @return 圖片網址 {@code [String]}*/
    public String getImageURL( String size ){
        return imageURL.replace("2x", size );
    }

    @Override protected void updateThis() {}
    
    @Override public void showInfoList( Node viewNode ) {}

    @Override public void setAlpha(int opacity) {}

    @Override public void filp(short type) {}

    @Override public void rename(String name) {}

    /** 取得搜尋時的關鍵字 
     *  @return 關鍵字 {@code [String]}*/
    public String getKeyword(){
        return keyword;
    }
    /** 取得圖片網址 
     *  @param size 大小字串
     *  @return 圖片 {@code [Image]}*/
    public Image getImage( String size ){
        return new Image( imageURL.replace("2x", size ) );
    }

    @Override public void setImage(Image img) {
        imageView.setImage( img );

        double w = img.getWidth(), h = img.getHeight();        
        double scale = ((double)IMAGE_VIEW_BOX_SIZE / ((w > h) ? w : h));
        imageView.setFitWidth( (int)(w * scale) );
        imageView.setFitHeight( (int)(h * scale) );
    }

}