package talkdraw.componet.menuitem;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import talkdraw.componet.TextSliderBar;

/** <p>提供可以修改的 {@link #MenuItem} 繼承了 {@link LockedMenuItem}</p> 
 *  <p>滑鼠快速點擊兩下時，就可以修改裡面的值</p>
 *  <p>註1：推薦使用 {@link TextSliderMenuItemBuilder} 來建構此物件</p>
 *  <p>註2：此物件在使用上有些不同，假如要取得內部物件請使用以下的函式，盡量不要使用 {@code 原生函式}</p>
 *  <p>*************************************************************</p>
 *  {@code (重要！)}<pre>註：當建構完後請使用  void setHandler() 來設定當數值改變時的動作 </pre>
 *  <blockquote><pre> 
 * //設定函式
 * setContentText()  setContent()    setTitile()
 * //回傳函式
 * getContentText()  getContent()    getTitile()    getValue()
 *  </pre></blockquote> */
public class TextSliderMenuItem extends LockedMenuItem{
    /** {@link #TextSliderBar} 提供 Slider 與 TextField */
    private TextSliderBar textSlider; 

    /** 當使用 TextField 改動時要執行的動作 
     *  @see MenuItemHandler */
    private MenuItemHandler textFieldHandler;
    
    /** 當使用 Slider 數值改動時要執行的動作 
     *  @see MenuItemHandler */
    private MenuItemHandler sliderHandler;

    /** 判斷是否有修改過 */
    private int initVal;
    /** 建構子 
     *  @param title 標題
     *  @param minVal 最小值
     *  @param maxVal 最大值
     *  @param curVal 預設值*/
    public TextSliderMenuItem(String title, int minVal, int maxVal, int curVal){
        super( title );
        this.initVal = curVal;
        initialTextField( minVal, maxVal, curVal);
        createListener();
        setContent( textSlider );
    }
     /** 建構子 
     *  @param title 標題
     *  @param minVal 最小值
     *  @param maxVal 最大值
     *  @param curVal 預設值
     *  @param handler 當數值改動時要執行的動作 */
    public TextSliderMenuItem(String title, int minVal, int maxVal, int curVal, MenuItemHandler handler){
        this( title, minVal, maxVal, curVal );
        this.textFieldHandler = handler;
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Initializer)初始化區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 初始化 監聽器 */
    private void createListener(){
        //監聽器：當滑鼠快速點擊兩下時，把 TextSlider 打開
        context.addEventHandler( MouseEvent.MOUSE_RELEASED , e -> {
            if( e.getButton() == MouseButton.PRIMARY ){
                textSlider.setDisable( false );
                textSlider.requestFocus();
            }
        });
    }

    /** 初始化 TextSlider */
    private void initialTextField(int minVal, int maxVal, int curVal){

        //初始化 TextSlider
        textSlider = new TextSliderBar( minVal, maxVal, curVal );
        textSlider.setDisable( true );
        textSlider.getNumberTextField().getStyleClass().add("text-info-MenuItem");
        textSlider.getNumberTextField().setMaxWidth( 120 );


        //監聽器： 當 TextSlider 關閉值就執行 Function
        textSlider.getNumberTextField().disabledProperty().addListener( (obser, oldVal, newVal) -> {
            if( newVal.booleanValue() == true ){
                textSlider.setDisable( true );

                //執行
                if( textFieldHandler != null && (initVal != getValue()) ){
                    initVal = getValue();       //更新目前的值
                    textFieldHandler.execute();
                }
            }
        } );

        //監聽器： 當使用者沒有繼續在 Focus 時，把 TextSlider 關閉
        textSlider.getNumberTextField().focusedProperty().addListener( (obser, oldVal, newVal) -> {
            //判斷是否沒有再聚焦上了
            if( newVal.booleanValue() == false )textSlider.setDisable( true );
        } );

        //監聽器： 當 TextSlider 按下 Enter 時，將 TextField 關閉
        textSlider.getNumberTextField().setOnKeyReleased( e -> {
            //判斷是否按下 Enter
            if( e.getCode() == KeyCode.ENTER )textSlider.setDisable( true );
        });

         //監聽器： 滑鼠放開 SliderBar 的時候才執行
        textSlider.getSlider().setOnMouseDragged( e -> {
            if( sliderHandler != null )sliderHandler.execute();
        } );

    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Setter)設定區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>設定此 MenuItem 的內容物的文字</p> 
     *  <p>註：假如要設定內容物請用這個，不要用 {@link #MenuItem.setText()}</p> 
     *  @param text 欲設定的字串 */
    @Override public void setContentText( String ... text ){ 
        if( context.getGraphic() instanceof TextSliderBar ){
            ((TextSliderBar)context.getGraphic()).setValue( Integer.parseInt( text[0] ) ); 
        }
        else super.setContentText( text );
    }
    //-------------------------------------------------------------------------
    /** 設定當使用 TextField 數值改變時的動作 
     *  @param handler 動作(建議使用 Lambda)*/
    public void setTextFieldHandler( MenuItemHandler handler ){
        this.textFieldHandler = handler;
    }
    //-------------------------------------------------------------------------
    /** <p>設定當使用 Silder 數值改變時的動作</p> 
     *  <p>通常是在 當使用 Slider 時不要讓它頻繁更新值</p>
     *  @param handler 動作(建議使用 Lambda)*/
    public void setSliderHander( MenuItemHandler handler ){
        this.sliderHandler = handler;
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Getter)回傳區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>回傳此 MenuItem 的內容物的文字</p> 
     *  <p>註：假如要取得內容物請用這個，不要用 {@link #MenuItem.getText()}</p> 
     *  @return 回傳內容物文字 {@code [String]} */
    @Override public String getContentText(){ 
        if( context.getGraphic() instanceof TextSliderBar ){
            return ((TextSliderBar)context.getGraphic()).getText() ; 
        }
        return super.getContentText();
    }

    /** 取得 {@code minValue} ~ {@code maxValue} 之間的值 
     *  @return {@code [Int]}*/
    public int getValue(){ 
        if( textSlider.getText().equals("") ){
            textSlider.getNumberTextField().setText( Integer.toString( textSlider.getMinValue() ) );
        }
        return Integer.parseInt( textSlider.getText() ); 
    }
}

