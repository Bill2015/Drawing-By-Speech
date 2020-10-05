package talkdraw.componet;

import javafx.scene.control.TextField;

/** 中文輸入的 {@code Text Field} 解決了按下 Enter 鍵所產生的問題 */
public final class ChineseTextField extends TextField{
    /** 計算按下了幾次 Enter 鍵 */
    private short enterPressedCount = 0;
    /** 建構子 */
    public ChineseTextField(){}
    /** 建構子 
     *  @param text 預設的字串 */
    public ChineseTextField( String text ){
        super( text );
    }
    //複寫 replace Text
    @Override
    final public void replaceText(int start, int end, String text){
        //假如不是刪除鍵(因為刪除鍵會是空白)
        if( !text.isEmpty() ){
            //要送出文字
            enterPressedCount = 1;
            //輸入的文字等於 "help" 時就不要增加按下的計數器
            if( getText().concat(text) == "help" );
            //輸入的文字等於 "?"" 時就不要增加按下的計數器
            else if( text == "?" );
            //假如輸入的是數字或英文字母
            else if( text.matches("[a-zA-Z_0-9 ]") ){
                enterPressedCount = 2;
            }
        }
        super.replaceText(start, end, text);
    }
    /** 回傳是否按下 Enter 並且是送出文字 */
    final public boolean isSendOut(){
        enterPressedCount += 1;
        boolean temp = (enterPressedCount >= 3);
        if( temp )enterPressedCount = 0;
        return temp;
    }
}