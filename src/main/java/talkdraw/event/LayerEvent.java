package talkdraw.event;

import javafx.event.EventType;
import talkdraw.imgobj.Layer;
import talkdraw.imgobj.LayerClone;


/** <p>繼承了 {@link BehaviorEvent}</p>
 *  <p>負責提供給 {@code 所有圖層操作} 時會觸發的 {@link LayerEvent}</p> 
 *  <p>基本上這些觸發器都是在 {@code Layer(圖層)} or {@code LayerList(圖層串列)} <p>
 *  <p><ul>
 *  <li>{@link #ANY} 所有</li>
 *  <li>{@link #ADD} 新增</li>
 *  <li>{@link #DELETE} 刪除</li>
 *  <li>{@link #OPACITY} 透明度</li>
 *  <li>{@link #RENAME} 重新命名</li>
 *  <li>{@link #FILP} 翻轉</li>
 *  </ul></p>*/
public class LayerEvent extends BehaviorEvent {
    /** <p>所有關於 {@code Layer}(圖層) 操作的 {@link BehaviorEvent}</p> 
     *  <p>所屬父 {@link BehaviorEvent}</p>*/
    public static final EventType<LayerEvent> ANY       = new EventType<>(BehaviorEvent.LAYER_ACTION, "所有圖層事件");
    /** 當有 {@code 新增} 圖層時會有 {@link LayerEvent} 
     *  <p>所屬父 {@link LayerEvent}</p> */
    public static final EventType<LayerEvent> ADD       = new EventType<>(LayerEvent.ANY, "新增");
    /** 當有 {@code 刪除} 圖層時會有 {@link LayerEvent} 
     *  <p>所屬父 {@link LayerEvent}</p> */
    public static final EventType<LayerEvent> DELETE    = new EventType<>(LayerEvent.ANY, "刪除");
    /** 當設定 {@code 透明度} 圖層時會有 {@link LayerEvent} 
     *  <p>所屬父 {@link LayerEvent}</p> */
    public static final EventType<LayerEvent> OPACITY   = new EventType<>(LayerEvent.ANY, "透明度");
    /** 當有 {@code 重新命名} 圖層時會有 {@link LayerEvent} 
     *  <p>所屬父 {@link LayerEvent}</p> */
    public static final EventType<LayerEvent> RENAME    = new EventType<>(LayerEvent.ANY, "名稱");
    /** 當有 {@code 翻轉} 圖層時會有 {@link LayerEvent} 
     *  <p>所屬父 {@link LayerEvent}</p> */
    public static final EventType<LayerEvent> FILP      = new EventType<>(LayerEvent.ANY, "翻轉");
    /** 當有 {@code 圖層順序變動} 圖層時會有 {@link LayerEvent} 
     *  <p>所屬父 {@link LayerEvent}</p> */
     public static final EventType<LayerEvent> VIEW_ORDER      = new EventType<>(LayerEvent.ANY, "圖層順序");
    /** 當有 {@code 圖層清空} 圖層時會有 {@link LayerEvent} 
     *  <p>所屬父 {@link LayerEvent}</p> */
     public static final EventType<LayerEvent> CLEAR      = new EventType<>(LayerEvent.ANY, "圖層清空");

    /**Construct a new {@code LayerEvent} with the specified event type. The source
     * and target of the event is set to {@code NULL_SOURCE_TARGET}.
     * @param eventType the event type 
     * @param actionNode 觸發此事件的 {@code Node}
     * @param clone 觸發此事件的 {@code 數值複製品}  */
    public LayerEvent(EventType<? extends BehaviorEvent> eventType, Layer actionNode, LayerClone clone) {
        super(eventType,  String.join(" ", actionNode.getName(), eventType.getName()), actionNode, clone);
        
    }

    /**Construct a new {@code LayerEvent} with the specified event type. The source
     * and target of the event is set to {@code NULL_SOURCE_TARGET}.
     * @param eventType the event type 
     * @param actionMsg 事件訊息
     * @param actionNode 觸發此事件的 {@code Node}
     * @param clone 觸發此事件的 {@code 數值複製品}  */
    public LayerEvent(EventType<? extends BehaviorEvent> eventType, String actionMsg, Layer actionNode, LayerClone clone) {
    super(eventType, String.join(" ", actionNode.getName(),eventType.getName(), actionMsg ), actionNode, clone);
    
    }

    /** {@inheritDoc}
     *  @return 事件操作節點 {@code [Layer]}*/
    @Override public Layer getActionNode(){ return (Layer)ACTION_NODE; }

    /** {@inheritDoc}
     *  @return 複製品 {@code [LayerClone]}*/
    @Override public LayerClone getClone(){
        return (LayerClone)ACTION_NODE_CLONE;
    }
}

