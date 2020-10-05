package talkdraw.componet;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
/** <p>自訂義的 Custom Slider</p>  
 *  <p>注意！範圍只能正值</p>
 *  <p>繼承了 {@link #StackPane} 利用 {@link javafx.scene.shape.Rectangle} 來製作 Slider Bar</p> */
public class CustomSlider extends StackPane{
    /** 中心准位圖案 */
    private final Rectangle progressRect;
    /** 背景方塊 */
    private final Rectangle centRect;
    /** 最小值 */
    private final int minVal, maxVal;
    /** 偏移植 */
    private double offset;
    /** 數值的 {@link SimpleIntegerProperty} 可以用於監聽變化 */
    private SimpleIntegerProperty value;
    //---------------------------------------------------------------------
    /** Custom Slider  建構子 
     *  @param minVal 最小值(只能正值)
     *  @param maxVal 最大值(只能正值)
     *  @param curVal 預設值 */
    public CustomSlider(int minVal, int maxVal, int curVal){
        this.minVal         = minVal;
        this.maxVal         = maxVal;
        this.progressRect   = new Rectangle(10, 15, Color.gray(0.4) );
        this.centRect       = new Rectangle( 110, 15, Color.DARKGRAY );
        this.value          = new SimpleIntegerProperty( curVal );

        getChildren().addAll( centRect, progressRect );
        setAlignment(progressRect, Pos.CENTER_LEFT);
        setAlignment(centRect    , Pos.CENTER_LEFT);

        //匿名函式宣告：加入監聽器
        addEventHandler( MouseEvent.ANY, e -> {
            double x = e.getX();
            if( e.getButton() == MouseButton.PRIMARY ){
                if( x > 0 && x <= (centRect.getWidth() - progressRect.getWidth() + 1 ) )
                    progressRect.setTranslateX( x ); 
                else if( x <= 0 )
                    progressRect.setTranslateX( 0 );
                else 
                    progressRect.setTranslateX( 100 ); 

                //設定數值
                value.set( (int)(offset * progressRect.getTranslateX()) + minVal );
            }
            
        });

        //計算偏移值
        offset =  ((double)(maxVal - minVal) / (centRect.getWidth() - 10) ) ;  
    }
    //---------------------------------------------------------------------
    /** Custom Slider 建構子 
     *  @param minVal 最小值(只能正值)
     *  @param maxVal 最大值(只能正值)
     *  @param curVal 預設值 
     *  @param width 寬
     *  @param height 高*/
    public CustomSlider(int minVal, int maxVal, int curVal, int width, int height){
        this( minVal, maxVal, curVal);

        progressRect.resize( 10, height);;
        centRect.resize(width + 10, height);;
        resize(width, height);

        //計算偏移值
        offset = ((double)(maxVal - minVal) / (centRect.getWidth() - 10));
    }
    //=========================================================================================================
    //===================================        功能區       =================================================
    /** 設定數值
     *  @param value 欲設定的數值 */
    public void setValue(int value){
        this.value.set( value );
        int tempVal = ((value >= minVal && value <= maxVal) ? value : (( value < minVal ) ? minVal : maxVal) );
        progressRect.setTranslateX( (((double)(tempVal - minVal)) / offset) );
    }
    /**Defines the x coordinate of the translation that is added to this {@code Node}'s transform.
     * <p>The node's final translation will be computed as {@link #layoutXProperty layoutX} + {@code translateX},
     * where {@code layoutX} establishes the node's stable position and {@code translateX}
     * optionally makes dynamic adjustments to that position. <p>
     * This variable can be used to alter the location of a node without disturbing
     * its {@link #layoutBoundsProperty layoutBounds}, which makes it useful for animating a node's location.
     * @return the translateX for this {@code Node}
     * @defaultValue 0 */
    public DoubleProperty offsetProperty(){
        return progressRect.translateXProperty();
    }

    /** 取得數值的 {@link SimpleIntegerProperty} 可以用於監聽變化
     *  @return {@code [SimpleIntegerProperty]}*/
    public SimpleIntegerProperty getValueProperty(){
        return value;
    }
    //=========================================================================================================
    //==============================        功能區(Getter)       ===============================================
    /** 取得當前數值 @return {@code [Int]} */
    public int getValue(){ return value.get(); }
    /** 取得能設定的最小數值 @return {@code [Int]} */
    public int getMinValue(){ return minVal; }
    /** 取得能設定的最大數值 @return {@code [Int]} */
    public int getMaxValue(){ return maxVal; }
    //=========================================================================================================
    //==============================        功能區(Setter)       ===============================================
    /** 設定中心點的顏色 
     *  @param color 欲設定的顏色*/
    public void setIconColor(Paint color){
        progressRect.setFill( color );
    }
    //------------------------------------------------------
    /** 設定背景的顏色 
     *  @param color 欲設定的顏色*/
    public void setBackgroundColor(Paint color){
        progressRect.setFill( color );
    }
    //------------------------------------------------------
    /** 設定 Slider Bar 的大小 
     *  @param width 欲設定的寬
     *  @param height 愈設定的高*/
    public void setSize( int width, int height){
        resize(width, height);
        centRect.resize(width + 10, height);
        progressRect.resize( 10, height);
    }
}