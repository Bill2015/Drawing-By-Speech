package talkdraw.event;

import javafx.event.EventType;
import talkdraw.imgobj.Layer;
import talkdraw.imgobj.LayerClone;

/** <p>繼承了 {@link BehaviorEvent}</p>
 *  <p>負責提供給 {@code 所有工具使用} 時會觸發的 {@link ToolEvent}</p> 
 *  <p>基本上這些觸發器都是在 {@code ToolBar(工具列)} or {@code Tool(工具裡)} <p>
 *  <p><ul>
 *  <li>{@link #ANY} 所有工具</li>
 *  <li>{@link #CIRCLE} 圓形工具</li>
 *  <li>{@link #ELLIPASE} 橢圓工具</li>
 *  <li>{@link #ERASER} 橡皮擦工具</li>
 *  <li>{@link #FILL} 填滿工具</li>
 *  <li>{@link #LINE} 直線工具</li>
 *  <li>{@link #PENCIL} 鉛筆工具</li>
 *  <li>{@link #RECTANGLE} 方形工具</li>
 *  <li>{@link #SELECT} 選擇工具</li>
 *  <li>{@link #TEXT} 文字工具</li>
 *  </ul></p>*/
 public class ToolEvent extends BehaviorEvent {
    //    TODO:
    /** <p>所有關於 {@code ImageObject}(圖片物件) 操作的 {@link BehaviorEvent}</p> 
     *  <p>所屬父 {@link BehaviorEvent}</p>*/
    public static final EventType<ToolEvent> ANY        = new EventType<>(BehaviorEvent.DRAWING_ACTION, "所有工具");
    /** 當使用 {@code 圓形} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> CIRCLE     = new EventType<>(ToolEvent.ANY, "圓形工具");
    /** 當使用 {@code 橢圓} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> ELLIPASE   = new EventType<>(ToolEvent.ANY, "橢圓工具");
    /** 當使用 {@code 橡皮擦} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> ERASER     = new EventType<>(ToolEvent.ANY, "橡皮擦工具");
    /** 當使用 {@code 填滿} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> FILL       = new EventType<>(ToolEvent.ANY, "填滿工具");
    /** 當使用 {@code 直線} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> LINE       = new EventType<>(ToolEvent.ANY, "直線工具");
    /** 當使用 {@code 鉛筆} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> PENCIL     = new EventType<>(ToolEvent.ANY, "鉛筆工具");
    /** 當使用 {@code 方形} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> RECTANGLE  = new EventType<>(ToolEvent.ANY, "方形工具");
    /** 當使用 {@code 選擇} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> SELECT     = new EventType<>(ToolEvent.ANY, "選擇工具");
    /** 當使用 {@code 文字} 工具時會有 {@link ToolEvent} 
    *  <p>所屬父 {@link ToolEvent}</p> */
    public static final EventType<ToolEvent> TEXT       = new EventType<>(ToolEvent.ANY, "文字工具");

    /**Construct a new {@code ToolEvent} with the specified event type. The source
     * and target of the event is set to {@code NULL_SOURCE_TARGET}.
     * @param eventType the event type 
     * @param actionNode 觸發此事件的 {@code Node}
     * @param clone 觸發此事件的 {@code 數值複製品}  */
    public ToolEvent(EventType<? extends BehaviorEvent> eventType, Layer actionNode, LayerClone clone) {
        super(eventType,  String.join(" ", actionNode.getName(), eventType.getName()), actionNode, clone);
        
    }

    /**Construct a new {@code ToolEvent} with the specified event type. The source
     * and target of the event is set to {@code NULL_SOURCE_TARGET}.
     * @param eventType the event type 
     * @param actionMsg 事件訊息
     * @param actionNode 觸發此事件的 {@code Node}
     * @param clone 觸發此事件的 {@code 數值複製品}   */
    public ToolEvent(EventType<? extends BehaviorEvent> eventType, String actionMsg, Layer actionNode, LayerClone clone) {
        super(eventType, String.join(" ", actionNode.getName(), eventType.getName(), actionMsg ), actionNode, clone);
        
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