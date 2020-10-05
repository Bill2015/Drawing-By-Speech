package talkdraw.imgobj;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseButton;
import talkdraw.imgobj.base.LevelList;

public class LayerList extends LevelList<Layer>{
    /** 目前選擇的圖層 */
    private volatile SimpleObjectProperty<Layer> activeLayer;
    public LayerList(){
        //初始化 "選擇圖片" 的 Property
        activeLayer = new SimpleObjectProperty<Layer>();
        activeLayer.addListener( (obser, oldVal, newVal) -> {
            //將舊的 Layer 框框取消
            if( oldVal != null )oldVal.setStyle("-fx-border-color: blue;");

            //設定新的框框
            newVal.setStyle("-fx-border-color: red;");
        } );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡         新增區(New Zone)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>{@inheritDoc} </p>
     *  <p>重寫 add Function，能讓建立監聽器</p>
     *  <p>其他種類的 Add 沒有相同功能，不推薦使用</p>*/
    @Override public boolean add( Layer layer ){
        createListener( layer );
        //預設，沒有圖層時第一個為啟用
        if( size() == 0 ){
            activeLayer.set( layer );
        }
        return super.add( layer );
    }
    //=======================================================================================================
    //==================================        更新區      =================================================
    /** <p>更改目前選擇的 Layer</p> 
     *  <p>也就是說目前選擇的圖層能被畫圖或更改</p>
     *  @param layer 目前即將要更改的圖層 */
    public void changeActiveLayer(Layer layer){
        activeLayer.set( layer );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡         回傳區(Gatter)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得目前所選擇的圖層 @return 被選擇的圖層 {@code [Layer]}*/
    public Layer getActiveLayer(){ return activeLayer.get(); }

    /** 取得目前所選擇的圖層的監聽器 @return 被選擇的圖層監聽器 {@code [SimpleObjectProperty<Layer>]}*/
    public SimpleObjectProperty<Layer> getActiveLayerProperty(){ return activeLayer; }

    /** <p>使用 {@code ID} 取得這個串列的 圖層{@link Layer}</p>
     *  <p>沒找到就回傳 {@code Null}</p>
     *  @return 圖層 {@link Layer} {@code [Layer]}*/
    public Layer getLayerByID(int ID){ 
        for (Layer layer : this) {
            if( layer.getID() == ID )return layer;
        }
        return null;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡         功能區(Function Zone)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>取得這個串列的最大 ID 並回傳 +1</p>
     *  <p>用於產生下一個 {@link Layer} 的 ID</p>
     *  @return 最大ID+1 {@code [Int]}*/
    public int generateID(){ 
        int max = 0;
        for(int i = 0; i < size(); i++)
            if( max < get(i).getID() )max = get(i).getID();
        return (++max );
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡         建立監聽器(Create Listener)          ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    private void createListener(Layer layer){
        //匿名函式宣告： 當滑鼠點擊時 改變選擇的圖層外框 以及 設定目前啟動的圖層
        layer.setOnMouseClicked( e ->{  
            if( e.getButton() == MouseButton.PRIMARY ){
                changeActiveLayer(layer);
            }
        });

    }
}


