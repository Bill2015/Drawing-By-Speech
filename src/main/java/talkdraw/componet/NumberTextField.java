package talkdraw.componet;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/** 只能輸入 數字 的 TextArea */
public final class NumberTextField extends TextField{
    /** 最大與最小值 */
    final private int MIN_VALUE, MAX_VALUE;
    final private ContextMenu alert;
    final private MenuItem errorMsg;
    /** 建構子
     *  @param minVal 最小值
     *  @param maxVal 最大值
     *  @param curVal 初始值 */
    public NumberTextField(int minVal, int maxVal, int curVal){
        MIN_VALUE = minVal;
        MAX_VALUE = maxVal;
        setText( Double.toString(curVal) );

        //提醒使用者輸入有誤
        errorMsg = new MenuItem("只能輸入數字！");
        errorMsg.getStyleClass().add("alertMenuItem");
 
        alert = new ContextMenu(errorMsg);
        alert.getStyleClass().add("alertMenu");

        //匿名函式宣告： 當焦點轉移時 檢查有無超過邊界
        focusedProperty().addListener( e -> {
            checkOutOfRange();
        } );

        //匿名函式宣告： 當按下 Enter 鍵時 檢查有無超過邊界
        addEventHandler(KeyEvent.KEY_RELEASED, e ->{
            if( e.getCode() == KeyCode.ENTER )
                checkOutOfRange();
        } );
    }


    @Override
    final public void replaceText(int start, int end, String text){
        if( !validate(text) ) return;
        super.replaceText(start, end, text);
    }

    /** 驗證是否輸入數字 */
    final private boolean validate(String text){
        if( !text.matches("[0-9]*")){
            alert( "只能輸入數字！" );return false;
        }
        if( getText().length() >= 6 && getSelectedText().isBlank() ){
            alert( "數值過大！" );return false;
        }
        return true;
    }
    /** 提示警告訊息 */
    final private void alert( String text ){
        errorMsg.setText( text );

        new Thread(new Runnable(){
            @Override
            public void run() {
                Platform.runLater( () -> { if( !alert.isShowing() ) alert.show(getThis(), Side.TOP, 0, 10); } );
                try{Thread.sleep(1000);}catch(Exception e){}
                Platform.runLater( () -> alert.hide());
            }
        }).start();
    }
    final private NumberTextField getThis(){
        return this;
    }
    /** 檢查是否超出範圍 */
    final private void checkOutOfRange(){
        int value = Integer.parseInt( getText() );
        if( value < MIN_VALUE || value > MAX_VALUE ) {
            alert( "數值只能界在" + MIN_VALUE + " 到 " + MAX_VALUE + "之間！" );
            setText(Integer.toString(value < MIN_VALUE ? MIN_VALUE : MAX_VALUE));
            alert.requestFocus();
        }
    }
}