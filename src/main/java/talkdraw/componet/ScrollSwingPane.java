package talkdraw.componet;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

/** 改寫 JavaFx 的 ScrollPane 
 * <p>原因是因為 ScrollBar 的 getValue 範圍是 [0, 1] {@code Double}</p> 
 * <p>這並不是我們想要的，所以把他轉成能取得位移量 {@code Pixel}</p> */
public final class ScrollSwingPane extends ScrollPane{
    private Pane content;
    /** ScrollSwingPane 建構子 
     *  @param content 內部物件 
     *  @param contentChildren 內部元件的子元件*/
    public ScrollSwingPane(Pane content){
        super();
        this.content = content;
        setHbarPolicy( ScrollBarPolicy.AS_NEEDED );
        setVbarPolicy( ScrollBarPolicy.AS_NEEDED );
        setContent(this.content);
    }
    /** 取得 Scroll Bar {@code 水平} 的偏移量 */
    final public double getHoffset(){
        return getHvalue() * (content.getWidth() - getViewportBounds().getWidth());
    }
    /** 取得 Scroll Bar {@code 垂直} 的偏移量 */
    final public double getVoffset(){
        return getVvalue() * (content.getHeight() - getViewportBounds().getHeight());
    }
    /** 取得目前的視窗大小 寬度 */
    final public double getViewWidth(){
        return getViewportBounds().getWidth();
    }
    /** 取得目前的視窗大小 高度*/
    final public double getViewHeight(){
        return getViewportBounds().getHeight();
    }
}