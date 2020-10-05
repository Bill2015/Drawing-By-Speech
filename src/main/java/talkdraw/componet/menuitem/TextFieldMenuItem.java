package talkdraw.componet.menuitem;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/** <p>提供可以修改的 {@link #MenuItem} 繼承了 {@link LockedMenuItem}</p> 
 *  <p>滑鼠快速點擊兩下時，就可以修改裡面的值</p>
 *  <p>註1：推薦使用 {@link TextFieldMenuItemBuilder} 來建構此物件</p>
 *  <p>註2：此物件在使用上有些不同，假如要取得內部物件請使用以下的函式，盡量不要使用 {@code 原生函式}</p>
 *  <p>*************************************************************</p>
 *  {@code (重要！)}<pre>註：當建構完後請使用  void setHandler() 來設定當數值改變時的動作 </pre>
 *  <blockquote><pre> 
 *   //設定函式
 *   setContentText()  setContent()    setTitile()
 *   //回傳函式
 *   getContentText()  getContent()    getTitile()  getInputText()
 *  </pre></blockquote> */
public class TextFieldMenuItem extends LockedMenuItem{
    private TextField textField; 
    /** 當數值改動時要執行的動作 
     *  @see MenuItemHandler */
    private MenuItemHandler handler;
    /** {@link TextField} 的值是否有被修改 */
    private boolean isModify = false;
    /** 建構子 
     *  @param title 標題*/
    public TextFieldMenuItem(String title){
        super( title );
        initialTextField();
        createListener();
        setContent( textField );
    }
    /** 建構子 
     *  @param title 標題
     *  @param text 欲放入的 Text */
    public TextFieldMenuItem(String title, String ... text){
        this( title );
        setContentText( text );
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Initializer)初始化區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** 初始化 監聽器 */
    private void createListener(){
        //匿名函式宣告：當滑鼠快速點擊兩下時，把 TextField 打開
        context.addEventHandler( MouseEvent.MOUSE_RELEASED , e -> {
            if( e.getButton() == MouseButton.PRIMARY ){
                textField.setDisable( false );
                textField.requestFocus();
            }
        });

    }

    /** 初始化 TextField */
    private void initialTextField(){

        //初始化 TextField
        textField = new TextField( getContentText() );
        textField.setDisable( true );
        textField.setMaxWidth( 120 );
        textField.getStyleClass().add( "text-info-MenuItem" );

        //匿名函式宣告：當使用者沒有繼續在 Focus 時，把 TextField 關閉並執行 Function
        textField.focusedProperty().addListener( (obser, oldVal, newVal) -> {
            if( newVal.booleanValue() == false && isModify ){
                if(handler != null)handler.execute();
                isModify = false;
            }
        } );
        
        //匿名函式宣告：當 textField 按下 Enter 時，執行設定得 Function
        textField.setOnKeyReleased( e -> {
            //判斷是否按下 Enter
            if( e.getCode() == KeyCode.ENTER ){
                textField.setDisable( true );
                if(handler != null)handler.execute();
                isModify = false;
            }
            //假如輸入的是 "字母" or "刪除鍵" 就把 isModify 設成 true
            else if( e.getCode() == KeyCode.ALPHANUMERIC || e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE ) {
                isModify = true;
            }
        });

    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Setter)設定區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>設定此 MenuItem 的內容物的文字</p> 
     *  <p>註：假如要設定內容物請用這個，不要用 {@link #MenuItem.setText()}</p> 
     *  @param text 欲設定的字串 */
    @Override public void setContentText( String ... text ){ 
        if( context.getGraphic() instanceof TextField ){
            ((TextField)context.getGraphic()).setText( String.join(" ", text ) ); 
        }
        else super.setContentText( text );
    }
    //-------------------------------------------------------------------------
    /** 設定當數值改變時的動作 
     *  @param handler 動作(建議使用 Lambda)*/
    public void setHandler( MenuItemHandler handler ){
        this.handler = handler;
    }
    
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       (Getter)回傳區       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>回傳此 MenuItem 的內容物的文字</p> 
     *  <p>註：假如要取得內容物請用這個，不要用 {@link #MenuItem.getText()}</p> 
     *  @return 回傳內容物文字 {@code [String]} */
    @Override public String getContentText(){ 
        if( context.getGraphic() instanceof TextField ){
            return ((TextField)context.getGraphic()).getText(); 
        }
        return super.getContentText();
    }
    //-------------------------------------------------------------------------
    /** 回傳使用者輸入的字串 
     *  @return {@code [String]}*/
    public String getInputText(){ return textField.getText(); }
}

