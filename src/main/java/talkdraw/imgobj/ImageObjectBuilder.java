package talkdraw.imgobj;

import java.util.ArrayList;

import javafx.scene.image.Image;

public class ImageObjectBuilder {
    private int belongLayerID = -99;
    private int opacity = 100;
    private ImageObject imgObj;
    private Layer belongLayer;
    /** <p>一般用建構子</p>
     *  <p>沒有{@code 標籤(Tag)}、{@code 圖片(Image)}、{@code 所屬圖層(belongLayer)}預設 -99</p>
     *  @param name 圖片名稱*/
    public ImageObjectBuilder(String name){
        imgObj = new ImageObject( name );
        imgObj.setBelongLayer( Layer.NONE );
    }
    /** <p>一般用建構子</p>
     *  <p>沒有{@code 標籤(Tag)}、{@code 所屬圖層(belongLayer)}預設 -99</p>
     *  @param name 圖片名稱
     *  @param img 圖片*/
    public ImageObjectBuilder(String name, Image img){
        imgObj = new ImageObject( name, img );
        imgObj.setBelongLayer( Layer.NONE );
    }
     //--------------------------------------------------
    /** 設定所屬圖層 {@link Layer}
     *  @param belongLayer 所屬圖層  */
    public ImageObjectBuilder setBelongLayer(Layer belongLayer){
        this.belongLayer = belongLayer;
        imgObj.setBelongLayer( belongLayer );
        return this;
    } 
    //--------------------------------------------------
    /** <p>設定所屬圖層 {@link Layer}</p>
     *  <p>直接給串列，但是需要先給 {@code BelongLayerID}</p>
     *  <p>註：此功能給檔案處裡用的，一般 {@code 不建議使用}</p>
     *  @param layerList 圖層串列  */
    public ImageObjectBuilder setBelongLayer(LayerList layerList){
        Layer layer = layerList.getLayerByID( belongLayerID );
        if( layer != null )
            imgObj.setBelongLayer( layer );
        return this;
    } 
    //--------------------------------------------------
    /** 設定所屬圖層 {@link Layer} {@code ID}
     *  <p>給圖層ID，只有設定圖層的 ID {@code 沒有實際與圖層鏈結}</p>
     *  <p>註：此功能給檔案處裡用的，一般 {@code 不建議使用}</p>
     *  @param belongLayer 所屬圖層{@code ID}  */
    public ImageObjectBuilder setBelongLayerID(int belongLayerID){
        this.belongLayerID = belongLayerID;
        return this;
    } 
    //--------------------------------------------------
    /** 更改所有的 Tags
     *  @param tags 新的標籤 */
    public ImageObjectBuilder setTag( ArrayList<String> tags ){
        imgObj.setTag( tags );
        return this;
    }
    //--------------------------------------------------
    /** 設定圖片的透明度  {@code 範圍 [0 ~ 100]}
     *  @param opacity 圖片透明度 */
    public ImageObjectBuilder setOpactiy( int opacity ){
        this.opacity = opacity;
        return this;
    }
    //--------------------------------------------------
    /** 設定物件圖片
     *  @param img 欲設定的圖片 */
    public ImageObjectBuilder setImage( Image img ){
        imgObj.setImage( img );
        return this;
    }
    //--------------------------------------------------
    /** 設定物件圖片大小
     *  @param width 圖片寬度 
     *  @param height 圖片高度 */
    public ImageObjectBuilder setImageSize( double width, double height ){
        imgObj.setImageSize( width, height );
        return this;
    }
    //--------------------------------------------------
    /** 設定物件旋轉角度
     *  @param degree 角度 */
    public ImageObjectBuilder setRotation( int degree ){
        imgObj.setRotation( degree );
        return this;
    }
    //--------------------------------------------------
    /** 設定圖片座標位置 
     *  @param x X 座標
     *  @param y Y 座標 */
    public ImageObjectBuilder setLocation(double x, double y){
        imgObj.setLocation(x, y);
        return this;
    }
    //--------------------------------------------------
    /** <p>設定是否有無翻轉</p> 
     *  <p>{@code true = 要翻轉} | {@code false = 不翻轉}</p>
     *  @param horizFilp 水平翻轉 
     *  @param vertiFilp 垂直翻轉 */
    public ImageObjectBuilder setFilp(boolean horizFilp, boolean vertiFilp){
        if( horizFilp == true ) imgObj.filp( ImageObject.FILP_HORIZONTAL );
        if( vertiFilp == true ) imgObj.filp( ImageObject.FILP_VERTICAL );
        return this;
    }
    //--------------------------------------------------
    /** 建構 {@link ImageObject} 
     *  @return 回傳建構完成的 {@link ImageObject} */
    public ImageObject build(){ 
        imgObj.setAlpha( opacity );
        return imgObj; 
    }
}