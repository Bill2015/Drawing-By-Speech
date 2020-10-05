package talkdraw.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import talkdraw.imgobj.ViewBoxClone;

/** <p>負責提供給任何操作製作復原與重作的事件 </p>
 *  <p>基本主要的事件發生都是在 {@code Main(主要)}、{@code ImageObject}、{@code ImageList}、{@code Layer}、{@code LayerList} 裡，其監聽器在 App 裡</p>
 *  <p><ul>
 *  <li>{@link #ANY} 所有</li>
 *  <li>{@link #IMAGE_ACTION} 圖片物件事件</li>
 *  <li>{@link #LAYER_ACTION} 圖層事件</li>
 *  <li>{@link #DRAWING_ACTION} 工具事件</li>
 *  </ul></p>*/
 public class BehaviorEvent extends Event {
    /** 當有任何圖層更動時會觸發 */
    public static final EventType<BehaviorEvent> ANY = new EventType<>(Event.ANY, "所有動作");
    /** 當有 {@code 圖片物件} 發生更動時會有 {@link BehaviorEvent} 
    *  <p>所屬父 {@link BehaviorEvent}</p> */
    public static final EventType<BehaviorEvent> IMAGE_ACTION = new EventType<>(BehaviorEvent.ANY, "圖片物件動作");
    /** 當有 {@code 圖層} 發生更動時會有 {@link BehaviorEvent} 
    *  <p>所屬父 {@link BehaviorEvent}</p> */
    public static final EventType<BehaviorEvent> LAYER_ACTION = new EventType<>(BehaviorEvent.ANY, "圖層動作");
    /** 當有 {@code 使用工具} 時會有 {@link BehaviorEvent} 
    *  <p>所屬父 {@link BehaviorEvent}</p> */
    public static final EventType<BehaviorEvent> DRAWING_ACTION = new EventType<>(BehaviorEvent.ANY, "工具動作");



    /** 事件操作名稱 */
    protected final String ACTION_MESSAGE;

    /** 觸發此事件的 Node */
    protected final Node ACTION_NODE;

    /** 觸發此事件的 Node (舊的)*/
    protected final ViewBoxClone ACTION_NODE_CLONE;
    
    /**Construct a new {@code Event} with the specified event type. The source
     * and target of the event is set to {@code NULL_SOURCE_TARGET}.
     * @param eventType the event type 
     * @param actionMsg 事件訊息 
     * @param actionNode 觸發此事件的 {@code Node}
     * @param clone 觸發此事件的 {@code 數值複製品} */
    public BehaviorEvent(EventType<? extends Event> eventType, String actionMsg, Node actionNode, ViewBoxClone clone) {
        super(eventType);
        ACTION_MESSAGE      = actionMsg;
        ACTION_NODE         = actionNode;
        ACTION_NODE_CLONE   = clone;
    }

    /**Construct a new {@code Event} with the specified event type. The source
     * and target of the event is set to {@code NULL_SOURCE_TARGET}.
     * @param eventType the event type 
     * @param actionMsg 事件訊息  */
    public BehaviorEvent(EventType<? extends Event> eventType, String actionMsg) {
        super(eventType);
        ACTION_MESSAGE = actionMsg;
        ACTION_NODE = null;
        ACTION_NODE_CLONE = null;
    }

    /** 取得事件發生操作的動作名稱 
     *  @return 事件操作名稱 {@code [String]} */
    public String getActionName(){
        return ACTION_MESSAGE;
    }

    /** 取得事件發生操作的 {@link Node}
     *  @return 事件操作節點 {@code [Node]} */
    public Node getActionNode(){
        return ACTION_NODE;
    }

    /** 取得事件發生前的物件 {@code 複製品}  
     *  @return 複製品 {@code [ViewBoxClone]}*/
    public ViewBoxClone getClone(){
        return ACTION_NODE_CLONE;
    }

}