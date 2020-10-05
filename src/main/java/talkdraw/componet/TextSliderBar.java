package talkdraw.componet;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class TextSliderBar extends BorderPane{
    /** 輸入畫筆粗細的 Input Slider */
    private CustomSlider slider;
    /** 輸入線寬的 TextField */
    private NumberTextField numberTextField;
    /** 顯示 Slider 用得 ContextMenu */
    private ContextMenu contextMenu;
    /** 建構子  
     *  @param minVal 最小值 (只能正值)
     *  @param maxVal 最大值 (只能正值)
     *  @param curVal 預設值
     *  @param title 標題 */
    public TextSliderBar(int minVal, int maxVal, int curVal){

        numberTextField = new NumberTextField(minVal, maxVal, curVal);
        slider = new CustomSlider(minVal, maxVal, curVal);

        numberTextField.setText( Integer.toString( curVal ) );
        slider.setValue(curVal);

        contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("sliderMenu");

        contextMenu.getItems().add( new MenuItem("", slider){ {
            getStyleClass().add("sliderMenuItem");
            setDisable(true);
        } } );


        //匿名函式宣告：當 線段 的 TextField 變動時，Slider bar 跟著改變
        slider.getValueProperty().addListener((obser, oldVal, newVal) -> {
            numberTextField.setText( newVal.toString() );
        });

        //匿名函式宣告：當 線段 的 TextField 變動時，Slider bar 跟著改變
        numberTextField.addEventHandler( KeyEvent.KEY_RELEASED, e ->{
            if( e.getCode() == KeyCode.ENTER ){
                slider.setValue((numberTextField.getText().isBlank() ? slider.getMinValue() : Integer.parseInt(numberTextField.getText())));
            }
        } );

        //匿名函式宣告： 當滑鼠按下時 將 Slider Bar 彈出
        numberTextField.setOnMousePressed(e -> {
            contextMenu.show(numberTextField, Side.BOTTOM, 0, 0);
        });

        setCenter( numberTextField );
    }
    //------------------------------------------------------------------------------
    /** 建構子  
     *  @param minVal 最小值 (只能正值)
     *  @param maxVal 最大值 (只能正值)
     *  @param curVal 預設值
     *  @param title 標題 */
    public TextSliderBar(int minVal, int maxVal, int curVal, String title){
        this(minVal, maxVal, curVal);
        MyLabel label = new MyLabel( title );
        setMargin( label, new Insets(5, 10, 5, 10) );
        setLeft( label  );
        setAlignment(label, Pos.CENTER_LEFT);
    }
    //------------------------------------------------------------------------------
    /** 建構子  
     *  @param minVal 最小值 (只能正值)
     *  @param maxVal 最大值 (只能正值)
     *  @param curVal 預設值
     *  @param title 標題 */
    public TextSliderBar(int minVal, int maxVal, int curVal, String title, int fontSize){
        this(minVal, maxVal, curVal);
        MyLabel label = new MyLabel( title, fontSize );
        setMargin( label, new Insets(5, 10, 5, 10) );
        setLeft( label  );
        setAlignment(label, Pos.CENTER_LEFT);
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        回傳區(Getter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 取得輸入的 Slider @return {@code [Slider]} */
    public CustomSlider getSlider(){ return slider; }
    //------------------------------------------------------------------------------
    /** 取得輸入的 Number Text Field @return {@code [NumberTextField]} */
    public NumberTextField getNumberTextField(){ return numberTextField; }
    //------------------------------------------------------------------------------
    /** <p>取的目前設定的數值</p>
     *  <p>假如要時常監聽此數值的話用 {@link #getValueProperty()} 來做數值監聽</p>
     * @see #getValueProperty()
     * @return {@code [Int]}*/
    public int getValue(){ return slider.getValue();  }
    //------------------------------------------------------------------------------
    /** 取得 TextProperty @return {@code [StringProperty]}*/
    public StringProperty getTextProperty(){ return numberTextField.textProperty(); }

    //------------------------------------------------------------------------------
    /** 取得 數值 {@link SimpleIntegerProperty} 可用於監聽  @return {@code [SimpleIntegerProperty]}*/
    public SimpleIntegerProperty getValueProperty(){ return slider.getValueProperty(); }
    //------------------------------------------------------------------------------
    /** 取的數值 
     *  @return 數值 {@code [String]}*/
    public String getText(){ return numberTextField.getText(); }
    //------------------------------------------------------------------------------
    /** <p>取得 {@code 最大值} </p>
     * @return 最大值 {@code [Int]}*/
    public int getMaxValue(){ return slider.getMaxValue();  }
    //------------------------------------------------------------------------------
    /** <p>取得 {@code 最小值} </p>
     * @return 最小值 {@code [Int]}*/
    public int getMinValue(){ return slider.getMinValue();  }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        設定區(Setter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 設定數值 
     *  @param value  欲設定的數值，有邊界檢查 */
    public void setValue( int value ){
        numberTextField.setText( Integer.toString( value ) );
        slider.setValue( value );
    }
    //------------------------------------------------------------------------------
    /** 設定 NumberTextField 元件間隔 
     *  @param insets 間隔 */
    public void setMargin(Insets insets){
        setMargin( numberTextField, insets );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡        雜項區(Misc)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    @Override
    public void requestFocus(){
        numberTextField.requestFocus();
    }
    /** <p>關閉 Slider Bar</p>
     *  <p>通常是沒有自動關閉時，才需要呼叫</p> */
    public void closeSlider(){
        contextMenu.hide();
    }

    /** 設定當 Slider 滑鼠放開時事件 
     *  @param handler 事件 */
    public void setSliderOnMouseReleased(EventHandler<? super MouseEvent> handler){
        slider.setOnMouseReleased( handler );
    }

    /** 設定當 Slider 滑鼠拖曳時事件 
     *  @param handler 事件 */
    public void setSliderOnMouseDragged(EventHandler<? super MouseEvent> handler){
        slider.setOnMouseReleased( handler );
    }
}