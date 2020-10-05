package talkdraw.imgobj.base;

import java.util.ArrayList;

/** <p>繼承了 {@link ArrayList} </p>
 *  <p>提供了可以將元素上下移動或至頂至底的功能</p>
 *  <p>就像有層級一樣，所以叫做 Level List </p> */
public class LevelList<T extends ViewBox> extends ArrayList<T>{
    //=======================================================================================================
    //==================================     圖層處理     ====================================================
    /** 設定圖層至頂
     *  @param img 圖片 */
    public void setViewTop( T img ){
        int index = indexOf( img );
        T temp = remove( index );
        add(  temp);
    }
    // ----------------------------------------------
    /** 設定圖層至底 
     *  @param img 圖片*/
    public void setViewBottom( T img ){
        int index = indexOf( img );
        T temp = remove( index );
        add( 0 , temp);
    }
    //----------------------------------------------
    /** 設定圖片至上一層
     *  @param img 要設置往上一層的圖片 
     *  @return 回傳是否已經達到底 <p>[ {@code true} 還未達到頂 ]</p> <p>[ {@code false} 已經達到頂 ]</p>*/
     public boolean setViewUp(T img ){
        int index = indexOf( img );
        if( index + 1 < size() ){
            add(index + 2, get(index) );
            remove( index );
            return true;
        }
        return false;
    }
    //----------------------------------------------
    /** 設定圖片至上一層
     *  @param img 要設置往上一層的圖片 
     *  @return 回傳是否已經達到底 <p>[ {@code true} 還未達到底 ]</p> <p>[ {@code false} 已經達到底 ]</p>*/
     public boolean setViewDown(T img ){
        int index = indexOf( img );
        if( index - 1 >= 0 ){
            add(index - 1, get(index) );
            remove( index + 1 );
            return true;
        }
        return false;
    }
    //----------------------------------------------
    /** 設定圖片至上一層
     *  @param img 要設置往上一層的圖片 
     *  @return 回傳是否已經達到底 <p>[ {@code true} 還未達到底 ]</p> <p>[ {@code false} 已經達到底 ]</p>*/
    public void restoreOrder(T img, int oldIndex){
        remove( img );
        if( oldIndex >= size() )
            add( img);
        else 
            add( oldIndex, img);
    }
    //----------------------------------------------
    /** 利用{@code 名稱}取得元素 
     *  @param name 元素名稱
     *  @return 回傳元素，假如未找到回傳 {@code null} */
    public T getElementByName(String name){
        for (T element : this ) {
            if( element.getName().equalsIgnoreCase(name) )return element;
        }
        return null;
    }
    //----------------------------------------------
    /** 利用{@code 編號}取得元素 
     *  @param ID 元素編號
     *  @return 回傳元素，假如未找到回傳 {@code null} */
    public T getElementByID(int ID){
        for (T element : this ) {
            if( element.getID() == ID )return element;
        }
        return null;
    }
}

