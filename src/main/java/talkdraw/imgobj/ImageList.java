package talkdraw.imgobj;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import talkdraw.imgobj.base.LevelList;

public class ImageList extends LevelList<ImageObject> {

    public ImageList() {

    }
    /**<p>利用 {@link ImageView} 取得 {@link ImageObject} 物件</p>
     * <p>目前用途只提供給在 {@link UpponDrawPane} 裡來查詢 {@link ImageObject} </p>
     * @param view 要查詢的 {@link ImageView}
     * @return 有找到回傳 {@link ImageObject} | 沒有則回傳 {@cde null}*/
    public ImageObject getImageObjectByView(ImageView view) {
        for (ImageObject obj : this) {
            if (obj.getMainImgView().equals(view)) {
                return obj;
            }
        }
        return null;
    }
    //---------------------------------------------------------------------------
    /** 使用圖片名稱 取得圖片物件
     *  @param name 圖片名稱
     *  @return 圖片物件 {@code 有找到 = object} | {@code 未找到 = null} */
    public final ImageObject getImageByName(String name){ 
        for( ImageObject object : this ){
            if( object.getName().equalsIgnoreCase( name ) )return object;
        }
        return null; 
    }

     //---------------------------------------------------------------------------
    /** 使用圖片名稱 取得圖片物件
     *  @param chiName 圖片中文名稱
     *  @param engName 圖片英文名稱
     *  @return 圖片物件串列湊圖 {@code 有找到 = object} | {@code 未找到 = null} */
    public final  HashMap<String, ImageObject> getImageNameOrTags(String chiName, String engName){ 
        HashMap<String, ImageObject> res = new HashMap<>();

        int alph = 0, num = 0;
        //編號
        for( ImageObject object : this ){
            String objName = object.getName();
            ArrayList<String> tags = object.getTags();
            //判斷是否名稱一樣，或是有包含標籤
            if( objName.contains( engName ) ||  //判斷英文名稱
                   tags.contains( engName ) ||  //判斷 Tags 裡有沒有包含此英文字
                objName.contains( chiName ) ||  //判斷中文名稱
                   tags.contains( chiName ) ){  //判斷 Tags 裡有沒有包含此中文字
                String SN = (char)(alph + 'A') + "" + (char)(num + '0') + "";
                res.put( SN, object );
                
                //設定 ID Label Text
                Platform.runLater(() -> object.setIDLabelText( SN ) );

                //編號上加
                alph += (num >= 10) ? 1 : 0;
                num = ((num + 1) % 10);
            }
        }
        return res; 
    }
}