package talkdraw.componet.menuitem;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/** <p>提供可以點擊的 {@link #MenuItem} 繼承了 {@link LockedMenuItem}</p> 
 *  <p>滑鼠快速點擊兩下時，就可以修改裡面的值</p>
 *  <p>註1：此物件在使用上有些不同，假如要取得內部物件請使用以下的函式，盡量不要使用 {@code 原生函式}</p>
 *  <blockquote><pre> 
 *   //設定函式
 *   setContentText()  setContent()    setTitile()
 *   //回傳函式
 *   getContentText()  getContent()    getTitile()  getInputText()
 *  </pre></blockquote> */
public class ButtonMenuItem extends LockedMenuItem{
    private Button[] buttons; 
    /** 建構子 
     *  @param title 標題*/
    public ButtonMenuItem(String title, Button ... button){
        super( title );
        this.buttons = button;
        initialButton();
        HBox buttonPane = new HBox( 5, buttons );
        setContent( buttonPane );
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Initializer)初始化區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 初始化 Button */
    private void initialButton(){
        for( Button btn : buttons ){
            btn.getStyleClass().add( "button-info-MenuItem" );
            btn.setFont( new Font("微軟正黑體", 15) );
        }
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Setter)設定區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>設定此 MenuItem 的內容物的文字</p> 
     *  <p>註：假如要設定內容物請用這個，不要用 {@link #MenuItem.setText()}</p> 
     *  @param text 欲設定的字串 */
    @Override public void setContentText( String ... text ){ 
        if( context.getGraphic() instanceof Button ){
            ((Button)context.getGraphic()).setText( String.join(" ", text ) ); 
        }
        else super.setContentText( text );
    }
    
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Getter)回傳區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>回傳此 MenuItem 的內容物的文字</p> 
     *  <p>註：假如要取得內容物請用這個，不要用 {@link #MenuItem.getText()}</p> 
     *  @return 回傳內容物文字 {@code [String]} */
    @Override public String getContentText(){ 
        if( context.getGraphic() instanceof Button ){
            return ((Button)context.getGraphic()).getText(); 
        }
        return super.getContentText();
    }
}

