package talkdraw.event;

import javafx.event.EventType;
import talkdraw.imgobj.ImageObject;
import talkdraw.imgobj.ImageObjectClone;

/** <p>繼承了 {@link BehaviorEvent}</p>
 *  <p>負責提供給 {@code 所有圖片物件操作} 時會觸發的 {@link ImageEvent}</p> 
 *  <p>基本上這些觸發器都是在 {@code ImageObject(圖片物件)} or {@code ImageList(圖片物件串列)} <p>
 *  <p><ul>
 *  <li>{@link #ANY} 所有</li>
 *  <li>{@link #ADD} 新增</li>
 *  <li>{@link #DELETE} 刪除</li>
 *  <li>{@link #OPACITY} 透明度</li>
 *  <li>{@link #RENAME} 重新命名</li>
 *  <li>{@link #FILP} 翻轉</li>
 *  <li>{@link #FILP} 旋轉</li>
 *  <li>{@link #MOVING} 移動</li>
 *  </ul></p>*/
public class ImageEvent extends BehaviorEvent {

    /** <p>所有關於 {@code ImageObject}(圖片物件) 操作的 {@link BehaviorEvent}</p> 
   *  <p>所屬父 {@link BehaviorEvent}</p>*/
  public static final EventType<ImageEvent> ANY       = new EventType<>(BehaviorEvent.IMAGE_ACTION, "所有圖片物件事件");
    /** 當有 {@code 新增} 圖片物件時會有 {@link ImageEvent} 
    *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> ADD       = new EventType<>(ImageEvent.ANY, "新增");
    /** 當有 {@code 刪除} 圖片物件時會有 {@link ImageEvent} 
    *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> DELETE    = new EventType<>(ImageEvent.ANY, "刪除");
    /** 當設定 {@code 透明度} 圖片物件時會有 {@link ImageEvent} 
    *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> OPACITY   = new EventType<>(ImageEvent.ANY, "透明度");
    /** 當有 {@code 重新命名} 圖片物件時會有 {@link ImageEvent} 
    *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> RENAME    = new EventType<>(ImageEvent.ANY, "名稱");
    /** 當有 {@code 翻轉} 圖片物件時會有 {@link ImageEvent} 
    *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> FILP      = new EventType<>(ImageEvent.ANY, "翻轉");
    /** 當有 {@code 旋轉} 圖片物件時會有 {@link ImageEvent} 
    *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> ROTATION  = new EventType<>(ImageEvent.ANY, "旋轉");
  /** 當有 {@code 移動} 圖片物件時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> MOVING  = new EventType<>(ImageEvent.ANY, "移動");
  /** 當有 {@code 資訊更新} 圖片物件時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> INFO_UPDATE  = new EventType<>(ImageEvent.ANY, "圖片更新");
  /** 當有 {@code 圖片對齊} 圖片物件時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> ALIGN  = new EventType<>(ImageEvent.ANY, "圖片對齊");
  /** 當有 {@code 圖片順序改變} 圖片物件時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> VIEW_ORDER  = new EventType<>(ImageEvent.ANY, "圖片順序");
  /** 當有 {@code 圖片大小改變} 圖片物件時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> SIZE  = new EventType<>(ImageEvent.ANY, "圖片大小");

  /** 當有 {@code 更新} 圖片物件 {@code 標籤} 時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> TAG_UPDATE  = new EventType<>(ImageEvent.ANY, "標籤更動");
  /** 當有 {@code 新增} 圖片物件 {@code 標籤} 時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> TAG_ADD  = new EventType<>(ImageEvent.ANY, "標籤新增");
  /** 當有 {@code 刪除} 圖片物件 {@code 標籤} 時會有 {@link ImageEvent} 
  *  <p>所屬父 {@link ImageEvent}</p> */
  public static final EventType<ImageEvent> TAG_DELETE  = new EventType<>(ImageEvent.ANY, "標籤刪除");

  /**Construct a new {@code ImageEvent} with the specified event type. The source
   * and target of the event is set to {@code NULL_SOURCE_TARGET}.
   * @param eventType the event type 
   * @param actionNode 觸發此事件的 {@code Node}
   * @param clone 觸發此事件的 {@code 數值複製品}  */
  public ImageEvent(EventType<? extends BehaviorEvent> eventType, ImageObject actionNode, ImageObjectClone clone) {
      super(eventType,  String.join(" ", actionNode.getName(), eventType.getName()), actionNode, clone);
  }

  /**Construct a new {@code ImageEvent} with the specified event type. The source
   * and target of the event is set to {@code NULL_SOURCE_TARGET}.
   * @param eventType the event type 
   * @param actionMsg 事件訊息
   * @param actionNode 觸發此事件的 {@code Node}
   * @param clone 觸發此事件的 {@code 數值複製品}   */
  public ImageEvent(EventType<? extends BehaviorEvent> eventType, String actionMsg, ImageObject actionNode, ImageObjectClone clone) {
    super(eventType, String.join(" ", actionNode.getName(),eventType.getName(), actionMsg ), actionNode, clone);
    
  }

  /** {@inheritDoc}
  *  @return 事件操作節點 {@code [ImageObject]}*/
  @Override public ImageObject getActionNode(){ return (ImageObject)ACTION_NODE; }

 
  /** {@inheritDoc}
   *  @return 複製品 {@code [ImageObjectClone]}*/
  @Override public ImageObjectClone getClone(){
      return (ImageObjectClone)ACTION_NODE_CLONE;
  }
}