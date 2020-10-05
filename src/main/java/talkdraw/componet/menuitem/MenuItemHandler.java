package talkdraw.componet.menuitem;

/** <p>用來設定當 {@link TextSliderMenuItem} 或 {@link TextFieldMenuItem} 動作時</p> 
 *  <p>所要做的動作</p>*/
public interface MenuItemHandler{
    /** 處理程序 */
    public void execute();
}