package talkdraw.componet.menuitem;

import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import talkdraw.componet.MyLabel;

/** <p>沒有功能的 {@link MenuItem} 只負責顯示文字與訊息</p>
 *  <p>繼承了 {@link MenuItem}</p>
 *  <p>沒有 Builder 直接歡告即可使用</p> */
public class LockedMenuItem extends MenuItem{
    /** 存放 Text 與 Graphic 的內文 */
    protected MyLabel context;
    /** 建構子 */
    private LockedMenuItem(){
        this.setDisable(true);
        this.getStyleClass().add("infoMenuItem");
        context = new MyLabel();
        context.getStyleClass().add("infoContent");
        context.setContentDisplay( ContentDisplay.RIGHT );
        setGraphic( context );
    }
    /** 建構子
     *  @param title 標題 */
    public LockedMenuItem(String title){ 
        this();
        context.setText( title );
        context.setGraphic( new MyLabel(){ { setTextFill( Color.WHITE );} } );
    }
    /** 建構子
     *  @param title 標題 
     *  @param text 文字 */
    public LockedMenuItem(String title, String ... text){
        this();
        context.setText( title );
        context.setGraphic( new MyLabel( String.join(" ", text) ){ { setTextFill( Color.WHITE );} } );
    }

    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       回傳區(Getter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>回傳此 MenuItem 的內容物</p> 
     *  <p>註：假如要取得內容物請用這個，不要用 {@link #MenuItem.getGraphic()}
     *  @return 回傳內容物 {@code [Node]} */
    public Node getContent(){ return context.getGraphic(); }
    /** <p>回傳此 MenuItem 的標題</p> 
     *  <p>註：假如要取得標題請用這個，不要用 {@link #MenuItem.getText()}
     *  @return 回傳標題 {@code [String]} */
    public String getTitile(){ return context.getText(); }
    /** <p>回傳此 MenuItem 的內容物的文字</p> 
     *  <p>假如目前內容物不是 {@link Label} 則回傳 Null</p> 
     *  <p>註：假如要取得內容物請用這個，不要用 {@link #MenuItem.getText()}
     *  @return 回傳內容物文字 {@code [String]} */
    public String getContentText(){ 
        if( context.getGraphic() instanceof Label ){
            return ((Label)context.getGraphic()).getText(); 
        }
        return null;
    }
    /** <p>判斷內容物的是否是 Label</p> 
     *  <p>假如目前內容物不是 {@link Label} 則回傳 false</p> 
     *  <p>註：假如要取得內容物請用這個，不要用 {@link #MenuItem.getText()}
     *  @return {@code 內容物是 Label = true} | {@code 內容物不是 Label = false} {@code [boolean]} */
    public boolean isContentLabel(){ 
        return ( context.getGraphic() instanceof Label ) ? true : false; 
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡       設定區(Setter)       ≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    /** <p>設定此 MenuItem 的內容物</p> 
     *  <p>註：假如要設定內容物請用這個，不要用 {@link #MenuItem.setGraphic()}
     *  @param node 欲設定的內容物*/
    public void setContent( Node node ){ context.setGraphic( node ); }
    /** <p>設定此 MenuItem 的標題</p> 
     *  <p>註：假如要設定標題請用這個，不要用 {@link #MenuItem.setText()}
     *  @param title 欲設定的標題*/
    public void setTitile( String title ){ context.setText( title ); }
    /** <p>設定此 MenuItem 的內容物的文字</p> 
     *  <p>假如目前內容物不是 {@link Label} 則回傳 Null</p> 
     *  <p>註：假如要設定內容物請用這個，不要用 {@link #MenuItem.setText()}</p> 
     *   @param text 欲設定的字串  */
    public void setContentText( String ... text ){ 
        if( context.getGraphic() instanceof Label ){
            ((Label)context.getGraphic()).setText( String.join(" ", text ) ); 
        }
    }
}
