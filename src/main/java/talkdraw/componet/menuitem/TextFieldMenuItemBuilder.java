package talkdraw.componet.menuitem;

/** <p>建構可以修改的 {@link #TextFieldMenuItem}</p> 
 *  <p>滑鼠快速點擊兩下時，就可以修改裡面的值</p>
 *  <p>註：建構後一定要呼叫下列 Function 來使建構完整</p>
 *  <blockquote><pre> 
 *   //建構所需的函式
 *   setExecute()  setContentText()    build()
 *  </pre></blockquote> */
public class TextFieldMenuItemBuilder {
    private TextFieldMenuItem textFieldMenuItem;
    /** 建構子 
     *  底下這些是必要執行的
     *  <blockquote><pre> //建構所需的函式
     *  setExecute()  setContentText()    build()</pre></blockquote> 
     * @param title 標題 */
    public TextFieldMenuItemBuilder(String title){
        textFieldMenuItem = new TextFieldMenuItem( title );
    }
    /** <p>設定此 MenuItem 的內容物的文字</p> 
     *  @param text 欲設定的字串 */
    public TextFieldMenuItemBuilder setText( String ... text ){
        textFieldMenuItem.setContentText( text );
        return this;
    }
    /** 建構完成 
     *  @return 回傳建構完成的 TextFieldMenuItem {@code [TextFieldMenuItem]}*/
    public TextFieldMenuItem build(){ return textFieldMenuItem; }
}