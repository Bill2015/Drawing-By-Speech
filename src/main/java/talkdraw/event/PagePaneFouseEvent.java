package talkdraw.event;

import javafx.event.Event;
import javafx.event.EventType;
import talkdraw.componet.PagePane;

/** <p>當有使用到 {@code PagePane} 的地方，就會 Fire 出這個 Event</p> 
 *  <p>都由 {@code Main} 來作為接收端</p>
 *  <p>而需要這個的用途是為了能當使用者操作於</p>
 *  <p>圖層 {@code PagePane} 能讓使用者能翻頁、跳頁等等 {@code PagePane} 操作</p>
 *  <p>目前有 Fire 出這個 Event 的有：  Date：2020/05/02
 *  <blockquote><pre>
 *class LayerViewPane     //圖層展示
 *class ImageViewPane     //圖片物件展示
 *class FileDialog        //檔案對話窗
 *  </pre></blockquote>*/
public class PagePaneFouseEvent extends Event {
    
    /** 觸發此事件的 {@link PagePane} */
    private PagePane newPagePane;

    /** 當發生改變時會觸發 */
    public static final EventType<PagePaneFouseEvent> CHANGE = new EventType<>(ANY, "Page Pane Fouce Change");
    /**Construct a new {@code Event} with the specified event type. The source
     * and target of the event is set to {@code NULL_SOURCE_TARGET}.
     * @param eventType the event type */
    public PagePaneFouseEvent(EventType<? extends Event> eventType, PagePane newPagePane) {
        super(eventType);
        this.newPagePane = newPagePane;
    }

    /** 取得此事件的目前 {@link PagePane} 
     *  @return 回傳目前的 {@link PagePane}  {@code [PagePane]} */
    public PagePane getNowPagePane(){
        return newPagePane;
    }
}